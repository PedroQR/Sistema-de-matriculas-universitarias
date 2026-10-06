
package br.pucminas.matriculas.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Agregador em memória de todas as entidades do sistema para persistência.
 */
public class BancoDeDados implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Aluno> alunos = new ArrayList<>();
    private List<Professor> professores = new ArrayList<>();
    private List<Secretaria> secretarias = new ArrayList<>();
    private List<Curso> cursos = new ArrayList<>();
    private List<Disciplina> disciplinas = new ArrayList<>();
    private List<CurriculoSemestre> curriculos = new ArrayList<>();

    public BancoDeDados() {
    }

    /**
     * Retorna a união de todos os usuários cadastrados no sistema para fins de autenticação (UC01).
     */
    public List<Usuario> getTodosUsuarios() {
        List<Usuario> todos = new ArrayList<>();
        todos.addAll(secretarias);
        todos.addAll(professores);
        todos.addAll(alunos);
        return todos;
    }

    /**
     * Busca usuário por login único.
     */
    public Usuario buscarUsuarioPorLogin(String login) {
        if (login == null) return null;
        for (Usuario u : getTodosUsuarios()) {
            if (login.equalsIgnoreCase(u.getLogin())) {
                return u;
            }
        }
        return null;
    }

    public List<Aluno> getAlunos() {
        return alunos;
    }

    public void setAlunos(List<Aluno> alunos) {
        this.alunos = alunos;
    }

    public List<Professor> getProfessores() {
        return professores;
    }

    public void setProfessores(List<Professor> professores) {
        this.professores = professores;
    }

    public List<Secretaria> getSecretarias() {
        return secretarias;
    }

    public void setSecretarias(List<Secretaria> secretarias) {
        this.secretarias = secretarias;
    }

    public List<Curso> getCursos() {
        return cursos;
    }

    public void setCursos(List<Curso> cursos) {
        this.cursos = cursos;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public void setDisciplinas(List<Disciplina> disciplinas) {
        this.disciplinas = disciplinas;
    }

    public List<CurriculoSemestre> getCurriculos() {
        return curriculos;
    }

    public void setCurriculos(List<CurriculoSemestre> curriculos) {
        this.curriculos = curriculos;
    }
}
