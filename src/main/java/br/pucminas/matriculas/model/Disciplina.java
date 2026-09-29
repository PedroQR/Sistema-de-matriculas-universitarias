package main.java.br.pucminas.matriculas.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Disciplina implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int CAPACIDADE_MAXIMA = 60;
    public static final int CAPACIDADE_MINIMA = 3;

    private String codigo;
    private String nome;
    private int creditos;
    private StatusDisciplina status;
    private Curso curso;
    private Professor professor;
    private List<Inscricao> inscricoes;

    public Disciplina(String codigo, String nome, int creditos, Curso curso) {
        this.codigo = codigo;
        this.nome = nome;
        this.creditos = creditos;
        this.curso = curso;
        this.status = StatusDisciplina.ABERTA;
        this.inscricoes = new ArrayList<>();
    }

    public boolean adicionarInscricao(Inscricao inscricao) {
        if (status != StatusDisciplina.ABERTA) return false;
        if (!isVagasDisponiveis()) return false;
        inscricoes.add(inscricao);
        return true;
    }

    public boolean isVagasDisponiveis() {
        return getQtdInscritosAtivos() < CAPACIDADE_MAXIMA;
    }

    public int getQtdInscritosAtivos() {
        int quantidade = 0;
        for (Inscricao i : inscricoes) {
            if (i.getStatus() == StatusInscricao.ATIVA) quantidade++;
        }
        return quantidade;
    }

    public void processarStatusFechamento() {
        int quantidade = getQtdInscritosAtivos();
        if (quantidade < CAPACIDADE_MINIMA) {
            status = StatusDisciplina.CANCELADA;
        } else {
            status = StatusDisciplina.ATIVA;
        }
    }

    public List<Aluno> getAlunosMatriculados() {
        List<Aluno> alunos = new ArrayList<>();
        for (Inscricao i : inscricoes) {
            if (i.getStatus() == StatusInscricao.ATIVA) {
                alunos.add(i.getAluno());
            }
        }
        return alunos;
    }

    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public int getCreditos() { return creditos; }
    public StatusDisciplina getStatus() { return status; }
    public Curso getCurso() { return curso; }
    public Professor getProfessor() { return professor; }
    public List<Inscricao> getInscricoes() { return inscricoes; }

    public void setNome(String nome) { this.nome = nome; }
    public void setCreditos(int creditos) { this.creditos = creditos; }
    public void setCurso(Curso curso) { this.curso = curso; }
    public void setProfessor(Professor professor) { this.professor = professor; }
}
