package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um Aluno no sistema de matrículas (UC05, UC06).
 * Regras:
 * - Até 4 disciplinas obrigatórias ativas.
 * - Até 2 disciplinas optativas ativas.
 */
public class Aluno extends Usuario {
    private static final long serialVersionUID = 1L;

    public static final int LIMITE_OBRIGATORIAS = 4;
    public static final int LIMITE_OPTATIVAS = 2;

    private String matricula;
    private List<Inscricao> inscricoes;

    public Aluno() {
        super();
        this.inscricoes = new ArrayList<>();
    }

    public Aluno(Long id, String nome, String email, String login, String senha, String matricula) {
        super(id, nome, email, login, senha);
        this.matricula = matricula;
        this.inscricoes = new ArrayList<>();
    }

    /**
     * Efetua a matrícula do aluno na disciplina informada, validando regras de negócio.
     */
    public boolean matricular(Disciplina disciplina, TipoInscricao tipo) {
        if (disciplina == null || tipo == null) {
            return false;
        }
        if (disciplina.getStatus() != StatusDisciplina.ABERTA) {
            return false;
        }
        if (!disciplina.isVagasDisponiveis()) {
            return false;
        }

        // Verifica se já está matriculado ativamente na disciplina
        for (Inscricao inscricao : getInscricoesAtivas()) {
            if (inscricao.getDisciplina().equals(disciplina)) {
                return false;
            }
        }

        // Validação dos limites de matrícula
        if (tipo == TipoInscricao.OBRIGATORIA && getQtdObrigatoriasAtivas() >= LIMITE_OBRIGATORIAS) {
            return false;
        }
        if (tipo == TipoInscricao.OPTATIVA && getQtdOptativasAtivas() >= LIMITE_OPTATIVAS) {
            return false;
        }

        Inscricao inscricao = new Inscricao(
                System.currentTimeMillis() + (long)(Math.random() * 1000),
                this,
                disciplina,
                tipo
        );

        if (!disciplina.adicionarInscricao(inscricao)) {
            return false;
        }

        inscricoes.add(inscricao);
        return true;
    }

    /**
     * Cancela a matrícula em uma disciplina ativa, liberando a vaga imediatamente.
     */
    public boolean cancelarMatricula(Disciplina disciplina) {
        if (disciplina == null) {
            return false;
        }
        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getDisciplina().equals(disciplina) && inscricao.getStatus() == StatusInscricao.ATIVA) {
                inscricao.cancelar();
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna apenas as inscrições com status ATIVA.
     */
    public List<Inscricao> getInscricoesAtivas() {
        List<Inscricao> ativas = new ArrayList<>();
        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getStatus() == StatusInscricao.ATIVA) {
                ativas.add(inscricao);
            }
        }
        return ativas;
    }

    /**
     * Contagem de disciplinas obrigatórias ativas.
     */
    public int getQtdObrigatoriasAtivas() {
        int count = 0;
        for (Inscricao inscricao : getInscricoesAtivas()) {
            if (inscricao.getTipo() == TipoInscricao.OBRIGATORIA) {
                count++;
            }
        }
        return count;
    }

    /**
     * Contagem de disciplinas optativas ativas.
     */
    public int getQtdOptativasAtivas() {
        int count = 0;
        for (Inscricao inscricao : getInscricoesAtivas()) {
            if (inscricao.getTipo() == TipoInscricao.OPTATIVA) {
                count++;
            }
        }
        return count;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public List<Inscricao> getInscricoes() {
        return inscricoes;
    }

    public void setInscricoes(List<Inscricao> inscricoes) {
        this.inscricoes = inscricoes;
    }
}
