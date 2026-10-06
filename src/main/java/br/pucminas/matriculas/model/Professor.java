package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um Professor no sistema de matrículas (UC07).
 */
public class Professor extends Usuario {
    private static final long serialVersionUID = 1L;

    private String registroDocente;
    private List<Disciplina> disciplinasLecionadas;

    public Professor() {
        super();
        this.disciplinasLecionadas = new ArrayList<>();
    }

    public Professor(Long id, String nome, String email, String login, String senha, String registroDocente) {
        super(id, nome, email, login, senha);
        this.registroDocente = registroDocente;
        this.disciplinasLecionadas = new ArrayList<>();
    }

    /**
     * Vincula a disciplina ao professor e vice-versa.
     */
    public void vincularDisciplina(Disciplina disciplina) {
        if (disciplina == null) return;

        if (!disciplinasLecionadas.contains(disciplina)) {
            disciplinasLecionadas.add(disciplina);
        }
        if (disciplina.getProfessor() != this) {
            disciplina.setProfessor(this);
        }
    }

    /**
     * Desvincula uma disciplina do professor.
     */
    public void desvincularDisciplina(Disciplina disciplina) {
        if (disciplina != null) {
            disciplinasLecionadas.remove(disciplina);
            if (disciplina.getProfessor() == this) {
                disciplina.setProfessor(null);
            }
        }
    }

    /**
     * Consulta alunos matriculados na disciplina lecionada (UC07).
     * Garante que o professor só consulta disciplinas sob sua responsabilidade.
     */
    public List<Aluno> consultarAlunos(Disciplina disciplina) {
        if (disciplina == null || !disciplinasLecionadas.contains(disciplina)) {
            return new ArrayList<>();
        }
        return disciplina.getAlunosMatriculados();
    }

    public String getRegistroDocente() {
        return registroDocente;
    }

    public void setRegistroDocente(String registroDocente) {
        this.registroDocente = registroDocente;
    }

    public List<Disciplina> getDisciplinasLecionadas() {
        return disciplinasLecionadas;
    }

    public void setDisciplinasLecionadas(List<Disciplina> disciplinasLecionadas) {
        this.disciplinasLecionadas = disciplinasLecionadas;
    }
}
