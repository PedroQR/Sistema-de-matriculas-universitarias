package main.java.br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;

public class Aluno extends Usuario {
    private static final long serialVersionUID = 1L;

    private String matricula;
    private List<Inscricao> inscricoes;

    public Aluno(Long id, String nome, String email, String login, String senha, String matricula) {
        super(id, nome, email, login, senha);
        this.matricula = matricula;
        this.inscricoes = new ArrayList<>();
    }

    public boolean matricular(Disciplina disciplina, TipoInscricao tipo) {
        if (disciplina == null || tipo == null) return false;
        if (disciplina.getStatus() != StatusDisciplina.ABERTA) return false;
        if (!disciplina.isVagasDisponiveis()) return false;

        for (Inscricao i : inscricoes) {
            if (i.getDisciplina().equals(disciplina) && i.getStatus() == StatusInscricao.ATIVA) {
                return false;
            }
        }

        if (tipo == TipoInscricao.OBRIGATORIA && getQtdObrigatoriasAtivas() >= 4) return false;
        if (tipo == TipoInscricao.OPTATIVA && getQtdOptativasAtivas() >= 2) return false;

        Inscricao inscricao = new Inscricao(
                System.currentTimeMillis(),
                this,
                disciplina,
                tipo
        );

        if (!disciplina.adicionarInscricao(inscricao)) return false;

        inscricoes.add(inscricao);
        return true;
    }

    public boolean cancelarMatricula(Disciplina disciplina) {
        for (Inscricao i : inscricoes) {
            if (i.getDisciplina().equals(disciplina) &&
                i.getStatus() == StatusInscricao.ATIVA) {
                i.cancelar();
                return true;
            }
        }
        return false;
    }

    public List<Inscricao> getInscricoesAtivas() {
        List<Inscricao> resultado = new ArrayList<>();
        for (Inscricao i : inscricoes) {
            if (i.getStatus() == StatusInscricao.ATIVA) {
                resultado.add(i);
            }
        }
        return resultado;
    }

    public int getQtdObrigatoriasAtivas() {
        int quantidade = 0;
        for (Inscricao i : getInscricoesAtivas()) {
            if (i.getTipo() == TipoInscricao.OBRIGATORIA) quantidade++;
        }
        return quantidade;
    }

    public int getQtdOptativasAtivas() {
        int quantidade = 0;
        for (Inscricao i : getInscricoesAtivas()) {
            if (i.getTipo() == TipoInscricao.OPTATIVA) quantidade++;
        }
        return quantidade;
    }

    public String getMatricula() { return matricula; }
    public List<Inscricao> getInscricoes() { return inscricoes; }
}
