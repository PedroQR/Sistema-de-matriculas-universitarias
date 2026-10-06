package br.pucminas.matriculas.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa o currículo e oferta acadêmica de um semestre letivo (UC02, UC09).
 */
public class CurriculoSemestre implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String semestre;
    private int ano;
    private boolean periodoMatriculaAberto;
    private List<Disciplina> disciplinasOfertadas;

    public CurriculoSemestre() {
        this.periodoMatriculaAberto = false;
        this.disciplinasOfertadas = new ArrayList<>();
    }

    public CurriculoSemestre(Long id, String semestre, int ano) {
        this.id = id;
        this.semestre = semestre;
        this.ano = ano;
        this.periodoMatriculaAberto = false;
        this.disciplinasOfertadas = new ArrayList<>();
    }

    /**
     * Abre o período de matrículas.
     */
    public void abrirPeriodoMatricula() {
        this.periodoMatriculaAberto = true;
    }

    /**
     * Encerra o período de matrículas.
     */
    public void encerrarPeriodoMatricula() {
        this.periodoMatriculaAberto = false;
    }

    /**
     * Adiciona uma disciplina à grade ofertada no semestre.
     */
    public void adicionarOfertaDisciplina(Disciplina disciplina) {
        if (disciplina != null && !disciplinasOfertadas.contains(disciplina)) {
            disciplinasOfertadas.add(disciplina);
        }
    }

    /**
     * Remove uma disciplina da grade ofertada no semestre.
     */
    public void removerOfertaDisciplina(Disciplina disciplina) {
        if (disciplina != null) {
            disciplinasOfertadas.remove(disciplina);
        }
    }

    /**
     * Regra UC09: Processa fechamento de turmas ofertadas.
     * Disciplinas com menos de 3 alunos inscritos são canceladas;
     * Disciplinas com 3 a 60 alunos tornam-se ativas.
     */
    public void processarFechamentoDisciplinas() {
        for (Disciplina disciplina : disciplinasOfertadas) {
            disciplina.processarStatusFechamento();
        }
    }

    public boolean possuiDisciplina(Disciplina disciplina) {
        return disciplinasOfertadas.contains(disciplina);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public boolean isPeriodoMatriculaAberto() {
        return periodoMatriculaAberto;
    }

    public void setPeriodoMatriculaAberto(boolean periodoMatriculaAberto) {
        this.periodoMatriculaAberto = periodoMatriculaAberto;
    }

    public List<Disciplina> getDisciplinasOfertadas() {
        return disciplinasOfertadas;
    }

    public void setDisciplinasOfertadas(List<Disciplina> disciplinasOfertadas) {
        this.disciplinasOfertadas = disciplinasOfertadas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurriculoSemestre that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CurriculoSemestre{" +
                "id=" + id +
                ", período='" + semestre + "/" + ano + '\'' +
                ", matriculaAberta=" + periodoMatriculaAberto +
                ", qtdOfertadas=" + disciplinasOfertadas.size() +
                '}';
    }
}
