package main.java.br.pucminas.matriculas.service;

import main.java.br.pucminas.matriculas.model.*;


public class ServicoManutencao {

    public void cadastrarAluno(BancoDeDados banco, Aluno aluno) {
        if (buscarAlunoPorMatricula(banco, aluno.getMatricula()) != null) {
            throw new IllegalArgumentException("Já existe um aluno com essa matrícula.");
        }
        banco.getAlunos().add(aluno);
    }

    public void cadastrarProfessor(BancoDeDados banco, Professor professor) {
        if (buscarProfessorPorRegistro(banco, professor.getRegistroDocente()) != null) {
            throw new IllegalArgumentException("Já existe um professor com esse registro.");
        }
        banco.getProfessores().add(professor);
    }

    public void cadastrarCurso(BancoDeDados banco, Curso curso) {
        banco.getCursos().add(curso);
    }

    public void cadastrarDisciplina(BancoDeDados banco, Disciplina disciplina) {
        if (buscarDisciplinaPorCodigo(banco, disciplina.getCodigo()) != null) {
            throw new IllegalArgumentException("Já existe uma disciplina com esse código.");
        }
        banco.getDisciplinas().add(disciplina);
    }

    public void removerDisciplina(BancoDeDados banco, String codigo) {
        Disciplina disciplina = buscarDisciplinaPorCodigo(banco, codigo);
        if (disciplina == null) throw new IllegalArgumentException("Disciplina não encontrada.");
        banco.getDisciplinas().remove(disciplina);
        for (Curso curso : banco.getCursos()) curso.getDisciplinas().remove(disciplina);
    }

    public void removerAluno(BancoDeDados banco, String matricula) {
        Aluno aluno = buscarAlunoPorMatricula(banco, matricula);
        if (aluno == null) throw new IllegalArgumentException("Aluno não encontrado.");
        banco.getAlunos().remove(aluno);
    }

    public void removerProfessor(BancoDeDados banco, String registro) {
        Professor professor = buscarProfessorPorRegistro(banco, registro);
        if (professor == null) throw new IllegalArgumentException("Professor não encontrado.");
        banco.getProfessores().remove(professor);
    }

    public Aluno buscarAlunoPorMatricula(BancoDeDados banco, String matricula) {
        for (Aluno a : banco.getAlunos()) {
            if (a.getMatricula().equalsIgnoreCase(matricula)) return a;
        }
        return null;
    }

    public Professor buscarProfessorPorRegistro(BancoDeDados banco, String registro) {
        for (Professor p : banco.getProfessores()) {
            if (p.getRegistroDocente().equalsIgnoreCase(registro)) return p;
        }
        return null;
    }

    public Disciplina buscarDisciplinaPorCodigo(BancoDeDados banco, String codigo) {
        for (Disciplina d : banco.getDisciplinas()) {
            if (d.getCodigo().equalsIgnoreCase(codigo)) return d;
        }
        return null;
    }

    public Curso buscarCursoPorId(BancoDeDados banco, long id) {
        for (Curso c : banco.getCursos()) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public CurriculoSemestre getCurriculoAtual(BancoDeDados banco) {
        if (banco.getCurriculos().isEmpty()) return null;
        return banco.getCurriculos().get(banco.getCurriculos().size() - 1);
    }
}
