package main.java.br.pucminas.matriculas.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CurriculoSemestre implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String semestre;
    private int ano;
    private boolean periodoMatriculaAberto;
    private List<Disciplina> disciplinasOfertadas;

    public CurriculoSemestre(Long id, String semestre, int ano) {
        this.id = id;
        this.semestre = semestre;
        this.ano = ano;
        this.periodoMatriculaAberto = false;
        this.disciplinasOfertadas = new ArrayList<>();
    }

    public void abrirPeriodoMatricula() {
        periodoMatriculaAberto = true;
    }

    public void encerrarPeriodoMatricula() {
        periodoMatriculaAberto = false;
    }

    public void adicionarOfertaDisciplina(Disciplina disciplina) {
        if (disciplina != null && !disciplinasOfertadas.contains(disciplina)) {
            disciplinasOfertadas.add(disciplina);
        }
    }

    public void processarFechamentoDisciplinas() {
        for (Disciplina disciplina : disciplinasOfertadas) {
            disciplina.processarStatusFechamento();
        }
    }

    public boolean possuiDisciplina(Disciplina disciplina) {
        return disciplinasOfertadas.contains(disciplina);
    }

    public Long getId() { return id; }
    public String getSemestre() { return semestre; }
    public int getAno() { return ano; }
    public boolean isPeriodoMatriculaAberto() { return periodoMatriculaAberto; }
    public List<Disciplina> getDisciplinasOfertadas() { return disciplinasOfertadas; }
}
