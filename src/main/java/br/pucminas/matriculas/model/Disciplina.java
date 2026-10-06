package br.pucminas.matriculas.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa uma disciplina no catálogo e em oferta.
 * Regras de negócio:
 * - Capacidade máxima: 60 alunos.
 * - Mínimo para abertura semestral: 3 alunos.
 */
public class Disciplina implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int CAPACIDADE_MAXIMA = 60;
    public static final int CAPACIDADE_MINIMA = 3;

    private String codigo;
    private String nome;
    private int creditos;
    private int capacidadeMaxima = CAPACIDADE_MAXIMA;
    private int capacidadeMinima = CAPACIDADE_MINIMA;
    private StatusDisciplina status;
    private Curso curso;
    private Professor professor;
    private List<Inscricao> inscricoes;

    public Disciplina() {
        this.status = StatusDisciplina.ABERTA;
        this.inscricoes = new ArrayList<>();
    }

    public Disciplina(String codigo, String nome, int creditos, Curso curso) {
        this.codigo = codigo;
        this.nome = nome;
        this.creditos = creditos;
        this.curso = curso;
        this.status = StatusDisciplina.ABERTA;
        this.capacidadeMaxima = CAPACIDADE_MAXIMA;
        this.capacidadeMinima = CAPACIDADE_MINIMA;
        this.inscricoes = new ArrayList<>();
    }

    /**
     * Adiciona uma nova inscrição à disciplina se houver vagas e estiver aberta.
     */
    public boolean adicionarInscricao(Inscricao inscricao) {
        if (status != StatusDisciplina.ABERTA) {
            return false;
        }
        if (!isVagasDisponiveis()) {
            return false;
        }
        if (inscricao != null && !inscricoes.contains(inscricao)) {
            inscricoes.add(inscricao);
            return true;
        }
        return false;
    }

    /**
     * Remove a inscrição ativa de um aluno, liberando a vaga imediatamente.
     */
    public boolean removerInscricao(Aluno aluno) {
        if (aluno == null) {
            return false;
        }
        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getAluno().equals(aluno) && inscricao.getStatus() == StatusInscricao.ATIVA) {
                inscricao.cancelar();
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se ainda há vagas disponíveis em relação à capacidade máxima.
     */
    public boolean isVagasDisponiveis() {
        return getQtdInscritosAtivos() < capacidadeMaxima;
    }

    /**
     * Retorna a quantidade de alunos atualmente matriculados com status ATIVA.
     */
    public int getQtdInscritosAtivos() {
        int count = 0;
        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getStatus() == StatusInscricao.ATIVA) {
                count++;
            }
        }
        return count;
    }

    /**
     * Regra UC09: Processa o fechamento ao término do período de matrículas.
     * - Se inscritos < 3: CANCELADA.
     * - Se 3 <= inscritos <= 60: ATIVA.
     */
    public void processarStatusFechamento() {
        int totalAtivos = getQtdInscritosAtivos();
        if (totalAtivos < capacidadeMinima) {
            this.status = StatusDisciplina.CANCELADA;
        } else {
            this.status = StatusDisciplina.ATIVA;
        }
    }

    /**
     * Retorna a lista de alunos matriculados ativos.
     */
    public List<Aluno> getAlunosMatriculados() {
        List<Aluno> alunos = new ArrayList<>();
        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getStatus() == StatusInscricao.ATIVA) {
                alunos.add(inscricao.getAluno());
            }
        }
        return alunos;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public int getCapacidadeMinima() {
        return capacidadeMinima;
    }

    public void setCapacidadeMinima(int capacidadeMinima) {
        this.capacidadeMinima = capacidadeMinima;
    }

    public StatusDisciplina getStatus() {
        return status;
    }

    public void setStatus(StatusDisciplina status) {
        this.status = status;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public List<Inscricao> getInscricoes() {
        return inscricoes;
    }

    public void setInscricoes(List<Inscricao> inscricoes) {
        this.inscricoes = inscricoes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Disciplina that)) return false;
        return Objects.equals(codigo, that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "Disciplina{" +
                "codigo='" + codigo + '\'' +
                ", nome='" + nome + '\'' +
                ", creditos=" + creditos +
                ", status=" + status +
                ", inscritos=" + getQtdInscritosAtivos() + "/" + capacidadeMaxima +
                '}';
    }
}
