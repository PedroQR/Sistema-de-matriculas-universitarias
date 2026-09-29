package main.java.br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;

public class Professor extends Usuario {
    private static final long serialVersionUID = 1L;

    private String registroDocente;
    private List<Disciplina> disciplinasLecionadas;

    public Professor(Long id, String nome, String email, String login, String senha, String registroDocente) {
        super(id, nome, email, login, senha);
        this.registroDocente = registroDocente;
        this.disciplinasLecionadas = new ArrayList<>();
    }

    public void vincularDisciplina(Disciplina disciplina) {
        if (disciplina == null) return;

        if (!disciplinasLecionadas.contains(disciplina)) {
            disciplinasLecionadas.add(disciplina);
            disciplina.setProfessor(this);
        }
    }

    public List<Aluno> consultarAlunos(Disciplina disciplina) {
        if (!disciplinasLecionadas.contains(disciplina)) {
            return new ArrayList<>();
        }
        return disciplina.getAlunosMatriculados();
    }

    public String getRegistroDocente() { return registroDocente; }
    public List<Disciplina> getDisciplinasLecionadas() { return disciplinasLecionadas; }
}
