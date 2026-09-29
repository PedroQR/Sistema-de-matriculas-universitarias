package main.java.br.pucminas.matriculas.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Curso implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
    private int totalCreditos;
    private List<Disciplina> disciplinas;

    public Curso(Long id, String nome, int totalCreditos) {
        this.id = id;
        this.nome = nome;
        this.totalCreditos = totalCreditos;
        this.disciplinas = new ArrayList<>();
    }

    public void adicionarDisciplina(Disciplina disciplina) {
        if (disciplina != null && !disciplinas.contains(disciplina)) {
            disciplinas.add(disciplina);
        }
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public int getTotalCreditos() { return totalCreditos; }
    public List<Disciplina> getDisciplinas() { return disciplinas; }

    public void setNome(String nome) { this.nome = nome; }
    public void setTotalCreditos(int totalCreditos) { this.totalCreditos = totalCreditos; }
}
