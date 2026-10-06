package br.pucminas.matriculas.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa um curso universitário (ex: Engenharia de Software).
 */
public class Curso implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
    private int totalCreditos;
    private List<Disciplina> disciplinas;

    public Curso() {
        this.disciplinas = new ArrayList<>();
    }

    public Curso(Long id, String nome, int totalCreditos) {
        this.id = id;
        this.nome = nome;
        this.totalCreditos = totalCreditos;
        this.disciplinas = new ArrayList<>();
    }

    public void adicionarDisciplina(Disciplina disciplina) {
        if (disciplina != null && !disciplinas.contains(disciplina)) {
            disciplinas.add(disciplina);
            if (disciplina.getCurso() != this) {
                disciplina.setCurso(this);
            }
        }
    }

    public void removerDisciplina(Disciplina disciplina) {
        if (disciplina != null) {
            disciplinas.remove(disciplina);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getTotalCreditos() {
        return totalCreditos;
    }

    public void setTotalCreditos(int totalCreditos) {
        this.totalCreditos = totalCreditos;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public void setDisciplinas(List<Disciplina> disciplinas) {
        this.disciplinas = disciplinas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Curso curso)) return false;
        return Objects.equals(id, curso.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Curso{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", totalCreditos=" + totalCreditos +
                ", qtdDisciplinas=" + disciplinas.size() +
                '}';
    }
}
