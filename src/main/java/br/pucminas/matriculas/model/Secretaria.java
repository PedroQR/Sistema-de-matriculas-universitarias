package br.pucminas.matriculas.model;

/**
 * Representa um membro da Secretaria acadêmica (UC02, UC03, UC04).
 */
public class Secretaria extends Usuario {
    private static final long serialVersionUID = 1L;

    private String setor;

    public Secretaria() {
        super();
    }

    public Secretaria(Long id, String nome, String email, String login, String senha, String setor) {
        super(id, nome, email, login, senha);
        this.setor = setor;
    }

    /**
     * Cria e retorna uma nova instância de CurriculoSemestre (UC02).
     */
    public CurriculoSemestre gerarCurriculoSemestre(String semestre, int ano) {
        return new CurriculoSemestre(System.currentTimeMillis(), semestre, ano);
    }

    /**
     * Cria e retorna uma nova instância de Curso (UC03).
     */
    public Curso cadastrarCurso(String nome, int creditos) {
        return new Curso(System.currentTimeMillis(), nome, creditos);
    }

    /**
     * Cria e retorna uma nova instância de Disciplina vinculada ao curso (UC03).
     */
    public Disciplina cadastrarDisciplina(String nome, int creditos, Curso curso) {
        String codigoSugerido = "DISC" + (int)(Math.random() * 900 + 100);
        Disciplina disciplina = new Disciplina(codigoSugerido, nome, creditos, curso);
        if (curso != null) {
            curso.adicionarDisciplina(disciplina);
        }
        return disciplina;
    }

    /**
     * Cria e retorna uma nova instância de Professor (UC04).
     */
    public Professor cadastrarProfessor(String nome, String email, String registro) {
        return new Professor(System.currentTimeMillis(), nome, email, registro, "123456", registro);
    }

    /**
     * Cria e retorna uma nova instância de Aluno (UC04).
     */
    public Aluno cadastrarAluno(String nome, String email, String matricula) {
        return new Aluno(System.currentTimeMillis(), nome, email, matricula, "123456", matricula);
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }
}
