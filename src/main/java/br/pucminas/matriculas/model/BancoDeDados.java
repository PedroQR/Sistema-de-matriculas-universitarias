package main.java.br.pucminas.matriculas.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BancoDeDados implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Aluno> alunos = new ArrayList<>();
    private List<Professor> professores = new ArrayList<>();
    private List<Secretaria> secretarias = new ArrayList<>();
    private List<Curso> cursos = new ArrayList<>();
    private List<Disciplina> disciplinas = new ArrayList<>();
    private List<CurriculoSemestre> curriculos = new ArrayList<>();

    public List<Aluno> getAlunos() { return alunos; }
    public List<Professor> getProfessores() { return professores; }
    public List<Secretaria> getSecretarias() { return secretarias; }
    public List<Curso> getCursos() { return cursos; }
    public List<Disciplina> getDisciplinas() { return disciplinas; }
    public List<CurriculoSemestre> getCurriculos() { return curriculos; }
}
