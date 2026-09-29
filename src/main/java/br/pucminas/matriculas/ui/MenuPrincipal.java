package main.java.br.pucminas.matriculas.ui;
import main.java.br.pucminas.matriculas.model.*;

import java.util.Scanner;

public class MenuPrincipal {
    private final Scanner scanner;
    private final BancoDeDados banco;
    private final FileManager fileManager;

    private final ServicoManutencao manutencao;
    private final ServicoCurriculo curriculoService;
    private final ServicoMatricula matriculaService;

    public MenuPrincipal(BancoDeDados banco, FileManager fileManager) {
        this.scanner = new Scanner(System.in);
        this.banco = banco;
        this.fileManager = fileManager;
        this.manutencao = new ServicoManutencao();
        this.curriculoService = new ServicoCurriculo();
        this.matriculaService = new ServicoMatricula(new SistemaCobrancaAdapter());
    }

    public void iniciar() {
        garantirDadosIniciais();

        boolean executando = true;
        while (executando) {
            System.out.println();
            System.out.println("=================================");
            System.out.println("     SISTEMA DE MATRÍCULAS");
            System.out.println("=================================");
            System.out.println("1 - Secretaria");
            System.out.println("2 - Aluno");
            System.out.println("3 - Professor");
            System.out.println("4 - Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = lerInt();

            switch (opcao) {
                case 1 -> menuSecretaria();
                case 2 -> menuAluno();
                case 3 -> menuProfessor();
                case 4 -> executando = false;
                default -> System.out.println("Opção inválida.");
            }
        }

        fileManager.salvar(banco);
        System.out.println("Dados salvos. Sistema encerrado.");
    }

    private void menuSecretaria() {
        boolean voltar = false;

        while (!voltar) {
            System.out.println();
            System.out.println("========== SECRETARIA ==========");
            System.out.println("1 - Gerar/visualizar currículo do semestre");
            System.out.println("2 - Abrir período de matrícula");
            System.out.println("3 - Encerrar período de matrícula");
            System.out.println("4 - Cadastrar curso");
            System.out.println("5 - Cadastrar disciplina");
            System.out.println("6 - Alterar disciplina");
            System.out.println("7 - Remover disciplina");
            System.out.println("8 - Cadastrar aluno");
            System.out.println("9 - Alterar aluno");
            System.out.println("10 - Remover aluno");
            System.out.println("11 - Cadastrar professor");
            System.out.println("12 - Alterar professor");
            System.out.println("13 - Remover professor");
            System.out.println("14 - Processar fechamento das disciplinas");
            System.out.println("15 - Listar dados");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");

            int op = lerInt();

            try {
                switch (op) {
                    case 1 -> visualizarCurriculo();
                    case 2 -> abrirPeriodo();
                    case 3 -> encerrarPeriodo();
                    case 4 -> cadastrarCurso();
                    case 5 -> cadastrarDisciplina();
                    case 6 -> alterarDisciplina();
                    case 7 -> removerDisciplina();
                    case 8 -> cadastrarAluno();
                    case 9 -> alterarAluno();
                    case 10 -> removerAluno();
                    case 11 -> cadastrarProfessor();
                    case 12 -> alterarProfessor();
                    case 13 -> removerProfessor();
                    case 14 -> processarFechamento();
                    case 15 -> listarDados();
                    case 0 -> voltar = true;
                    default -> System.out.println("Opção inválida.");
                }
                fileManager.salvar(banco);
            } catch (IllegalArgumentException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void menuAluno() {
        System.out.print("Informe a matrícula do aluno: ");
        Aluno aluno = manutencao.buscarAlunoPorMatricula(banco, scanner.nextLine());

        if (aluno == null) {
            System.out.println("Aluno não encontrado.");
            return;
        }

        boolean voltar = false;
        while (!voltar) {
            System.out.println();
            System.out.println("============ ALUNO =============");
            System.out.println("Aluno: " + aluno.getNome());
            System.out.println("Matrícula: " + aluno.getMatricula());
            System.out.println("1 - Consultar disciplinas");
            System.out.println("2 - Efetuar matrícula");
            System.out.println("3 - Consultar minhas matrículas");
            System.out.println("4 - Cancelar matrícula");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");

            int op = lerInt();

            switch (op) {
                case 1 -> listarDisciplinas();
                case 2 -> efetuarMatricula(aluno);
                case 3 -> consultarMatriculas(aluno);
                case 4 -> cancelarMatricula(aluno);
                case 0 -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }

            fileManager.salvar(banco);
        }
    }

    private void menuProfessor() {
        System.out.print("Informe o registro docente: ");
        Professor professor = manutencao.buscarProfessorPorRegistro(banco, scanner.nextLine());

        if (professor == null) {
            System.out.println("Professor não encontrado.");
            return;
        }

        boolean voltar = false;
        while (!voltar) {
            System.out.println();
            System.out.println("========== PROFESSOR ==========");
            System.out.println("Professor: " + professor.getNome());
            System.out.println("1 - Consultar minhas disciplinas");
            System.out.println("2 - Consultar alunos matriculados");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");

            int op = lerInt();

            switch (op) {
                case 1 -> listarDisciplinasProfessor(professor);
                case 2 -> consultarAlunosProfessor(professor);
                case 0 -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void visualizarCurriculo() {
        CurriculoSemestre curriculo = manutencao.getCurriculoAtual(banco);
        if (curriculo == null) {
            System.out.println("Nenhum currículo cadastrado.");
            return;
        }

        System.out.println();
        System.out.println("======= CURRÍCULO DO SEMESTRE =======");
        System.out.println("Semestre: " + curriculo.getSemestre() + "/" + curriculo.getAno());
        System.out.println("Período de matrícula: " +
                (curriculo.isPeriodoMatriculaAberto() ? "ABERTO" : "FECHADO"));

        for (Disciplina d : curriculo.getDisciplinasOfertadas()) {
            System.out.printf("%s - %s | %d créditos | %s | vagas: %d/%d%n",
                    d.getCodigo(), d.getNome(), d.getCreditos(), d.getStatus(),
                    d.getQtdInscritosAtivos(), Disciplina.CAPACIDADE_MAXIMA);
        }
    }

    private void abrirPeriodo() {
        CurriculoSemestre c = manutencao.getCurriculoAtual(banco);
        if (c == null) {
            System.out.println("Nenhum currículo cadastrado.");
            return;
        }
        curriculoService.abrirPeriodo(c);
        System.out.println("Período de matrícula aberto.");
    }

    private void encerrarPeriodo() {
        CurriculoSemestre c = manutencao.getCurriculoAtual(banco);
        if (c == null) {
            System.out.println("Nenhum currículo cadastrado.");
            return;
        }
        curriculoService.encerrarPeriodo(c);
        System.out.println("Período de matrícula encerrado.");
    }

    private void cadastrarCurso() {
        System.out.print("ID: ");
        long id = lerLong();
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Total de créditos: ");
        int creditos = lerInt();

        Curso curso = new Curso(id, nome, creditos);
        manutencao.cadastrarCurso(banco, curso);
        System.out.println("Curso cadastrado.");
    }

    private void cadastrarDisciplina() {
        System.out.print("Código: ");
        String codigo = scanner.nextLine();
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Créditos: ");
        int creditos = lerInt();
        System.out.print("ID do curso: ");
        long cursoId = lerLong();

        Curso curso = manutencao.buscarCursoPorId(banco, cursoId);
        if (curso == null) {
            System.out.println("Curso não encontrado.");
            return;
        }

        Disciplina disciplina = new Disciplina(codigo, nome, creditos, curso);
        curso.adicionarDisciplina(disciplina);
        manutencao.cadastrarDisciplina(banco, disciplina);

        CurriculoSemestre curriculo = manutencao.getCurriculoAtual(banco);
        if (curriculo != null) curriculo.adicionarOfertaDisciplina(disciplina);

        System.out.println("Disciplina cadastrada.");
    }

    private void alterarDisciplina() {
        System.out.print("Código da disciplina: ");
        Disciplina d = manutencao.buscarDisciplinaPorCodigo(banco, scanner.nextLine());

        if (d == null) {
            System.out.println("Disciplina não encontrada.");
            return;
        }

        System.out.print("Novo nome: ");
        d.setNome(scanner.nextLine());
        System.out.print("Novos créditos: ");
        d.setCreditos(lerInt());

        System.out.println("Disciplina alterada.");
    }

    private void removerDisciplina() {
        System.out.print("Código da disciplina: ");
        manutencao.removerDisciplina(banco, scanner.nextLine());
        System.out.println("Disciplina removida.");
    }

    private void cadastrarAluno() {
        System.out.print("ID: ");
        long id = lerLong();
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("E-mail: ");
        String email = scanner.nextLine();
        System.out.print("Matrícula: ");
        String matricula = scanner.nextLine();

        Aluno aluno = new Aluno(id, nome, email, matricula, "", matricula);
        manutencao.cadastrarAluno(banco, aluno);
        System.out.println("Aluno cadastrado.");
    }

    private void alterarAluno() {
        System.out.print("Matrícula: ");
        Aluno aluno = manutencao.buscarAlunoPorMatricula(banco, scanner.nextLine());

        if (aluno == null) {
            System.out.println("Aluno não encontrado.");
            return;
        }

        System.out.print("Novo nome: ");
        aluno.setNome(scanner.nextLine());
        System.out.print("Novo e-mail: ");
        aluno.setEmail(scanner.nextLine());

        System.out.println("Aluno alterado.");
    }

    private void removerAluno() {
        System.out.print("Matrícula: ");
        manutencao.removerAluno(banco, scanner.nextLine());
        System.out.println("Aluno removido.");
    }

    private void cadastrarProfessor() {
        System.out.print("ID: ");
        long id = lerLong();
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("E-mail: ");
        String email = scanner.nextLine();
        System.out.print("Registro docente: ");
        String registro = scanner.nextLine();

        Professor professor = new Professor(id, nome, email, registro, "", registro);
        manutencao.cadastrarProfessor(banco, professor);
        System.out.println("Professor cadastrado.");
    }

    private void alterarProfessor() {
        System.out.print("Registro docente: ");
        Professor professor = manutencao.buscarProfessorPorRegistro(banco, scanner.nextLine());

        if (professor == null) {
            System.out.println("Professor não encontrado.");
            return;
        }

        System.out.print("Novo nome: ");
        professor.setNome(scanner.nextLine());
        System.out.print("Novo e-mail: ");
        professor.setEmail(scanner.nextLine());

        System.out.println("Professor alterado.");
    }

    private void removerProfessor() {
        System.out.print("Registro docente: ");
        manutencao.removerProfessor(banco, scanner.nextLine());
        System.out.println("Professor removido.");
    }

    private void processarFechamento() {
        CurriculoSemestre c = manutencao.getCurriculoAtual(banco);
        if (c == null) {
            System.out.println("Nenhum currículo cadastrado.");
            return;
        }

        curriculoService.processarFechamento(c);
        System.out.println("Fechamento processado.");

        for (Disciplina d : c.getDisciplinasOfertadas()) {
            System.out.println(d.getCodigo() + " -> " + d.getStatus() +
                    " (" + d.getQtdInscritosAtivos() + " alunos)");
        }
    }

    private void listarDados() {
        System.out.println();
        System.out.println("Cursos: " + banco.getCursos().size());
        for (Curso c : banco.getCursos()) {
            System.out.println("- " + c.getId() + " | " + c.getNome());
        }

        System.out.println("Disciplinas: " + banco.getDisciplinas().size());
        for (Disciplina d : banco.getDisciplinas()) {
            System.out.println("- " + d.getCodigo() + " | " + d.getNome() + " | " + d.getStatus());
        }

        System.out.println("Alunos: " + banco.getAlunos().size());
        for (Aluno a : banco.getAlunos()) {
            System.out.println("- " + a.getMatricula() + " | " + a.getNome());
        }

        System.out.println("Professores: " + banco.getProfessores().size());
        for (Professor p : banco.getProfessores()) {
            System.out.println("- " + p.getRegistroDocente() + " | " + p.getNome());
        }
    }

    private void listarDisciplinas() {
        System.out.println();
        System.out.println("========== DISCIPLINAS ==========");
        for (Disciplina d : banco.getDisciplinas()) {
            System.out.printf("%s - %s | %s | %d/%d alunos%n",
                    d.getCodigo(), d.getNome(), d.getStatus(),
                    d.getQtdInscritosAtivos(), Disciplina.CAPACIDADE_MAXIMA);
        }
    }

    private void efetuarMatricula(Aluno aluno) {
        CurriculoSemestre curriculo = manutencao.getCurriculoAtual(banco);
        if (curriculo == null) {
            System.out.println("Nenhum currículo cadastrado.");
            return;
        }

        listarDisciplinas();
        System.out.print("Código da disciplina: ");
        Disciplina d = manutencao.buscarDisciplinaPorCodigo(banco, scanner.nextLine());

        if (d == null) {
            System.out.println("Disciplina não encontrada.");
            return;
        }

        System.out.println("1 - Obrigatória");
        System.out.println("2 - Optativa");
        System.out.print("Tipo: ");
        int tipo = lerInt();

        TipoInscricao tipoInscricao =
                tipo == 1 ? TipoInscricao.OBRIGATORIA :
                tipo == 2 ? TipoInscricao.OPTATIVA : null;

        if (tipoInscricao == null) {
            System.out.println("Tipo inválido.");
            return;
        }

        boolean sucesso = matriculaService.matricular(aluno, d, tipoInscricao, curriculo);
        System.out.println(sucesso
                ? "Matrícula realizada com sucesso!"
                : "Não foi possível realizar a matrícula.");
    }

    private void consultarMatriculas(Aluno aluno) {
        System.out.println();
        System.out.println("======= MINHAS MATRÍCULAS =======");

        if (aluno.getInscricoesAtivas().isEmpty()) {
            System.out.println("Nenhuma matrícula ativa.");
            return;
        }

        for (Inscricao i : aluno.getInscricoesAtivas()) {
            System.out.println(
                    i.getDisciplina().getCodigo() + " - " +
                    i.getDisciplina().getNome() + " | " +
                    i.getTipo()
            );
        }

        System.out.println("Obrigatórias: " + aluno.getQtdObrigatoriasAtivas() + "/4");
        System.out.println("Optativas: " + aluno.getQtdOptativasAtivas() + "/2");
    }

    private void cancelarMatricula(Aluno aluno) {
        CurriculoSemestre curriculo = manutencao.getCurriculoAtual(banco);
        if (curriculo == null) {
            System.out.println("Nenhum currículo cadastrado.");
            return;
        }

        consultarMatriculas(aluno);
        System.out.print("Código da disciplina para cancelar: ");
        Disciplina d = manutencao.buscarDisciplinaPorCodigo(banco, scanner.nextLine());

        if (d == null) {
            System.out.println("Disciplina não encontrada.");
            return;
        }

        boolean sucesso = matriculaService.cancelar(aluno, d, curriculo);
        System.out.println(sucesso
                ? "Matrícula cancelada com sucesso!"
                : "Não foi possível cancelar a matrícula.");
    }

    private void listarDisciplinasProfessor(Professor professor) {
        System.out.println();
        System.out.println("======= MINHAS DISCIPLINAS =======");

        if (professor.getDisciplinasLecionadas().isEmpty()) {
            System.out.println("Nenhuma disciplina vinculada.");
            return;
        }

        for (Disciplina d : professor.getDisciplinasLecionadas()) {
            System.out.println(d.getCodigo() + " - " + d.getNome());
        }
    }

    private void consultarAlunosProfessor(Professor professor) {
        listarDisciplinasProfessor(professor);

        if (professor.getDisciplinasLecionadas().isEmpty()) return;

        System.out.print("Código da disciplina: ");
        Disciplina d = manutencao.buscarDisciplinaPorCodigo(banco, scanner.nextLine());

        if (d == null || d.getProfessor() != professor) {
            System.out.println("Disciplina não pertence a este professor.");
            return;
        }

        System.out.println();
        System.out.println("======= ALUNOS MATRICULADOS =======");

        for (Aluno aluno : professor.consultarAlunos(d)) {
            System.out.println(aluno.getMatricula() + " - " + aluno.getNome());
        }
    }

    private void garantirDadosIniciais() {
        if (!banco.getCursos().isEmpty()) return;

        Secretaria secretaria = new Secretaria(
                1L, "Secretaria", "secretaria@pucminas.br",
                "secretaria", "", "Secretaria Acadêmica"
        );
        banco.getSecretarias().add(secretaria);

        Curso curso = new Curso(1L, "Engenharia de Software", 200);
        banco.getCursos().add(curso);

        Disciplina d1 = new Disciplina("ES101", "Projeto de Software", 4, curso);
        Disciplina d2 = new Disciplina("ES102", "Algoritmos e Estruturas de Dados", 4, curso);
        Disciplina d3 = new Disciplina("ES103", "Engenharia de Requisitos", 4, curso);

        curso.adicionarDisciplina(d1);
        curso.adicionarDisciplina(d2);
        curso.adicionarDisciplina(d3);

        banco.getDisciplinas().add(d1);
        banco.getDisciplinas().add(d2);
        banco.getDisciplinas().add(d3);

        Aluno aluno = new Aluno(
                1L, "Caio Santos", "caio@email.com",
                "20260001", "", "20260001"
        );
        banco.getAlunos().add(aluno);

        Professor professor = new Professor(
                1L, "Professor Exemplo", "professor@pucminas.br",
                "P001", "", "P001"
        );
        professor.vincularDisciplina(d1);
        banco.getProfessores().add(professor);

        CurriculoSemestre curriculo =
                new CurriculoSemestre(1L, "1º", 2026);

        curriculo.adicionarOfertaDisciplina(d1);
        curriculo.adicionarOfertaDisciplina(d2);
        curriculo.adicionarOfertaDisciplina(d3);
        curriculo.abrirPeriodoMatricula();

        banco.getCurriculos().add(curriculo);

        fileManager.salvar(banco);
        System.out.println("Dados iniciais criados.");
    }

    private int lerInt() {
        while (true) {
            try {
                int valor = Integer.parseInt(scanner.nextLine());
                return valor;
            } catch (NumberFormatException e) {
                System.out.print("Digite um número válido: ");
            }
        }
    }

    private long lerLong() {
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Digite um número válido: ");
            }
        }
    }
}
