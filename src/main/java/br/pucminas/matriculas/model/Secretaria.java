package main.java.br.pucminas.matriculas.model;

public class Secretaria extends Usuario {
    private static final long serialVersionUID = 1L;

    private String setor;

    public Secretaria(Long id, String nome, String email, String login, String senha, String setor) {
        super(id, nome, email, login, senha);
        this.setor = setor;
    }

    public Curso cadastrarCurso(Long id, String nome, int creditos) {
        return new Curso(id, nome, creditos);
    }

    public Disciplina cadastrarDisciplina(String codigo, String nome, int creditos, Curso curso) {
        Disciplina disciplina = new Disciplina(codigo, nome, creditos, curso);
        if (curso != null) curso.adicionarDisciplina(disciplina);
        return disciplina;
    }

    public Professor cadastrarProfessor(Long id, String nome, String email,
                                        String login, String senha, String registro) {
        return new Professor(id, nome, email, login, senha, registro);
    }

    public Aluno cadastrarAluno(Long id, String nome, String email,
                                String login, String senha, String matricula) {
        return new Aluno(id, nome, email, login, senha, matricula);
    }

    public String getSetor() { return setor; }
}
