package br.pucminas.matriculas.service;

import br.pucminas.matriculas.model.Aluno;
import br.pucminas.matriculas.model.BancoDeDados;
import br.pucminas.matriculas.model.CurriculoSemestre;
import br.pucminas.matriculas.model.Curso;
import br.pucminas.matriculas.model.Disciplina;
import br.pucminas.matriculas.model.Professor;

/**
 * Serviço responsável pela manutenção cadastral (UC03, UC04) no banco de dados em memória.
 */
public class ServicoManutencao {

    // ========== ALUNOS (UC04) ==========

    public void cadastrarAluno(BancoDeDados banco, Aluno aluno) {
        if (aluno == null) throw new IllegalArgumentException("Aluno não pode ser nulo.");
        if (buscarAlunoPorMatricula(banco, aluno.getMatricula()) != null) {
            throw new IllegalArgumentException("Já existe um aluno com a matrícula " + aluno.getMatricula() + ".");
        }
        if (banco.buscarUsuarioPorLogin(aluno.getLogin()) != null) {
            throw new IllegalArgumentException("Já existe um usuário com o login '" + aluno.getLogin() + "'.");
        }
        banco.getAlunos().add(aluno);
    }

    public void removerAluno(BancoDeDados banco, String matricula) {
        Aluno aluno = buscarAlunoPorMatricula(banco, matricula);
        if (aluno == null) {
            throw new IllegalArgumentException("Aluno não encontrado para a matrícula: " + matricula);
        }
        // Cancela inscrições ativas para liberar vagas
        for (Disciplina d : banco.getDisciplinas()) {
            d.removerInscricao(aluno);
        }
        banco.getAlunos().remove(aluno);
    }

    public Aluno buscarAlunoPorMatricula(BancoDeDados banco, String matricula) {
        if (matricula == null) return null;
        for (Aluno a : banco.getAlunos()) {
            if (matricula.trim().equalsIgnoreCase(a.getMatricula().trim())) {
                return a;
            }
        }
        return null;
    }

    // ========== PROFESSORES (UC04) ==========

    public void cadastrarProfessor(BancoDeDados banco, Professor professor) {
        if (professor == null) throw new IllegalArgumentException("Professor não pode ser nulo.");
        if (buscarProfessorPorRegistro(banco, professor.getRegistroDocente()) != null) {
            throw new IllegalArgumentException("Já existe um professor com o registro " + professor.getRegistroDocente() + ".");
        }
        if (banco.buscarUsuarioPorLogin(professor.getLogin()) != null) {
            throw new IllegalArgumentException("Já existe um usuário com o login '" + professor.getLogin() + "'.");
        }
        banco.getProfessores().add(professor);
    }

    public void removerProfessor(BancoDeDados banco, String registro) {
        Professor professor = buscarProfessorPorRegistro(banco, registro);
        if (professor == null) {
            throw new IllegalArgumentException("Professor não encontrado para o registro: " + registro);
        }
        // Desvincula professor das disciplinas
        for (Disciplina d : banco.getDisciplinas()) {
            if (professor.equals(d.getProfessor())) {
                d.setProfessor(null);
            }
        }
        banco.getProfessores().remove(professor);
    }

    public Professor buscarProfessorPorRegistro(BancoDeDados banco, String registro) {
        if (registro == null) return null;
        for (Professor p : banco.getProfessores()) {
            if (registro.trim().equalsIgnoreCase(p.getRegistroDocente().trim())) {
                return p;
            }
        }
        return null;
    }

    // ========== CURSOS (UC03) ==========

    public void cadastrarCurso(BancoDeDados banco, Curso curso) {
        if (curso == null) throw new IllegalArgumentException("Curso não pode ser nulo.");
        if (buscarCursoPorId(banco, curso.getId()) != null) {
            throw new IllegalArgumentException("Já existe um curso com o ID " + curso.getId() + ".");
        }
        banco.getCursos().add(curso);
    }

    public Curso buscarCursoPorId(BancoDeDados banco, long id) {
        for (Curso c : banco.getCursos()) {
            if (c.getId() != null && c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public void removerCurso(BancoDeDados banco, long id) {
        Curso curso = buscarCursoPorId(banco, id);
        if (curso == null) {
            throw new IllegalArgumentException("Curso não encontrado com o ID " + id);
        }
        banco.getCursos().remove(curso);
    }

    // ========== DISCIPLINAS (UC03) ==========

    public void cadastrarDisciplina(BancoDeDados banco, Disciplina disciplina) {
        if (disciplina == null) throw new IllegalArgumentException("Disciplina não pode ser nula.");
        if (buscarDisciplinaPorCodigo(banco, disciplina.getCodigo()) != null) {
            throw new IllegalArgumentException("Já existe uma disciplina com o código " + disciplina.getCodigo() + ".");
        }
        banco.getDisciplinas().add(disciplina);
    }

    public void removerDisciplina(BancoDeDados banco, String codigo) {
        Disciplina disciplina = buscarDisciplinaPorCodigo(banco, codigo);
        if (disciplina == null) {
            throw new IllegalArgumentException("Disciplina não encontrada com o código: " + codigo);
        }
        banco.getDisciplinas().remove(disciplina);
        for (Curso c : banco.getCursos()) {
            c.removerDisciplina(disciplina);
        }
        for (CurriculoSemestre cur : banco.getCurriculos()) {
            cur.removerOfertaDisciplina(disciplina);
        }
        if (disciplina.getProfessor() != null) {
            disciplina.getProfessor().desvincularDisciplina(disciplina);
        }
    }

    public Disciplina buscarDisciplinaPorCodigo(BancoDeDados banco, String codigo) {
        if (codigo == null) return null;
        for (Disciplina d : banco.getDisciplinas()) {
            if (codigo.trim().equalsIgnoreCase(d.getCodigo().trim())) {
                return d;
            }
        }
        return null;
    }

    public void vincularProfessorADisciplina(BancoDeDados banco, String registroDocente, String codigoDisciplina) {
        Professor prof = buscarProfessorPorRegistro(banco, registroDocente);
        if (prof == null) throw new IllegalArgumentException("Professor não encontrado.");
        Disciplina disc = buscarDisciplinaPorCodigo(banco, codigoDisciplina);
        if (disc == null) throw new IllegalArgumentException("Disciplina não encontrada.");

        prof.vincularDisciplina(disc);
    }

    // ========== CURRÍCULO DO SEMESTRE (UC02) ==========

    public CurriculoSemestre getCurriculoAtual(BancoDeDados banco) {
        if (banco.getCurriculos().isEmpty()) {
            return null;
        }
        return banco.getCurriculos().get(banco.getCurriculos().size() - 1);
    }
}
