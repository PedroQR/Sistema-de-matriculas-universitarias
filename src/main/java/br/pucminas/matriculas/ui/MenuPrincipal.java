package br.pucminas.matriculas.ui;

import br.pucminas.matriculas.model.Aluno;
import br.pucminas.matriculas.model.BancoDeDados;
import br.pucminas.matriculas.model.CurriculoSemestre;
import br.pucminas.matriculas.model.Curso;
import br.pucminas.matriculas.model.Disciplina;
import br.pucminas.matriculas.model.Inscricao;
import br.pucminas.matriculas.model.Professor;
import br.pucminas.matriculas.model.Secretaria;
import br.pucminas.matriculas.model.StatusDisciplina;
import br.pucminas.matriculas.model.TipoInscricao;
import br.pucminas.matriculas.model.Usuario;
import br.pucminas.matriculas.persistence.FileManager;
import br.pucminas.matriculas.service.ServicoAutenticacao;
import br.pucminas.matriculas.service.ServicoCurriculo;
import br.pucminas.matriculas.service.ServicoManutencao;
import br.pucminas.matriculas.service.ServicoMatricula;
import br.pucminas.matriculas.service.SistemaCobrancaAdapter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Interface em Linha de Comando (CLI) para interação com o Sistema de Matrículas Universitárias.
 * Implementa todos os Casos de Uso (UC01 a UC09).
 */
public class MenuPrincipal {
    private final Scanner scanner;
    private final BancoDeDados banco;
    private final FileManager fileManager;

    private final ServicoAutenticacao autenticacaoService;
    private final ServicoManutencao manutencao;
    private final ServicoCurriculo curriculoService;
    private final ServicoMatricula matriculaService;

    public MenuPrincipal(BancoDeDados banco, FileManager fileManager) {
        this.scanner = new Scanner(System.in);
        this.banco = banco;
        this.fileManager = fileManager;
        this.autenticacaoService = new ServicoAutenticacao();
        this.manutencao = new ServicoManutencao();
        this.curriculoService = new ServicoCurriculo();
        this.matriculaService = new ServicoMatricula(new SistemaCobrancaAdapter());
    }

    /**
     * Ponto de entrada do loop de interação do usuário.
     */
    public void iniciar() {
        garantirDadosIniciais();

        boolean executando = true;
        while (executando) {
            System.out.println();
            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║          SISTEMA DE MATRÍCULAS UNIVERSITÁRIAS                ║");
            System.out.println("║                PUC MINAS - ENGENHARIA DE SOFTWARE            ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            System.out.println(" 1 - Efetuar Login (Secretaria, Aluno ou Professor) [UC01]");
            System.out.println(" 2 - Listar Credenciais de Teste / Demonstração Rápida");
            System.out.println(" 3 - Exportar Relatório Textual dos Dados Atualizados");
            System.out.println(" 0 - Encerrar Sistema");
            System.out.print("Escolha uma opção: ");

            int opcao = lerInt();

            switch (opcao) {
                case 1 -> fluxoLogin();
                case 2 -> exibirUsuariosCadastrados();
                case 3 -> {
                    fileManager.salvar(banco);
                    System.out.println("✓ Dados salvos e relatório 'data/relatorio_sistema.txt' atualizado!");
                }
                case 0 -> executando = false;
                default -> System.out.println("[!] Opção inválida. Tente novamente.");
            }
        }

        fileManager.salvar(banco);
        System.out.println("\n✓ Todos os dados foram salvos com sucesso em arquivo. Até logo!");
    }

    // =========================================================================
    // UC01: AUTENTICAÇÃO / LOGIN
    // =========================================================================

    private void fluxoLogin() {
        System.out.println("\n--- [UC01] AUTENTICAÇÃO DE USUÁRIO ---");
        System.out.print("Login: ");
        String login = scanner.nextLine().trim();
        System.out.print("Senha: ");
        String senha = scanner.nextLine().trim();

        Usuario usuario = autenticacaoService.autenticar(banco.getTodosUsuarios(), login, senha);

        if (usuario == null) {
            System.out.println("❌ Falha na autenticação: Login ou senha incorretos.");
            return;
        }

        System.out.println("\n✓ Autenticado com sucesso! Bem-vindo(a), " + usuario.getNome() + ".");

        if (usuario instanceof Secretaria secretaria) {
            menuSecretaria(secretaria);
        } else if (usuario instanceof Aluno aluno) {
            menuAluno(aluno);
        } else if (usuario instanceof Professor professor) {
            menuProfessor(professor);
        }
    }

    private void exibirUsuariosCadastrados() {
        System.out.println("\n═════════════ USUÁRIOS DISPONÍVEIS PARA TESTE ═════════════");
        System.out.println("PERFIL      | NOME               | LOGIN       | SENHA");
        System.out.println("------------+--------------------+-------------+----------");
        for (Secretaria s : banco.getSecretarias()) {
            System.out.printf("SECRETARIA  | %-18s | %-11s | %s%n", s.getNome(), s.getLogin(), s.getSenha());
        }
        for (Professor p : banco.getProfessores()) {
            System.out.printf("PROFESSOR   | %-18s | %-11s | %s (Reg: %s)%n",
                    p.getNome(), p.getLogin(), p.getSenha(), p.getRegistroDocente());
        }
        for (Aluno a : banco.getAlunos()) {
            System.out.printf("ALUNO       | %-18s | %-11s | %s (Mat: %s)%n",
                    a.getNome(), a.getLogin(), a.getSenha(), a.getMatricula());
        }
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    // =========================================================================
    // MENU SECRETARIA (UC02, UC03, UC04, UC09)
    // =========================================================================

    private void menuSecretaria(Secretaria secretaria) {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║                      PAINEL DA SECRETARIA                    ║");
            System.out.println("║ Usuário: " + formatarTamanho(secretaria.getNome(), 25) + " Setor: " + formatarTamanho(secretaria.getSetor(), 17) + " ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            System.out.println("--- CURRÍCULO DO SEMESTRE (UC02 / UC09) ---");
            System.out.println(" 1 - Visualizar Currículo do Semestre Atual");
            System.out.println(" 2 - Criar/Gerar Novo Currículo Semestral");
            System.out.println(" 3 - Abrir Período de Matrículas");
            System.out.println(" 4 - Encerrar Período de Matrículas");
            System.out.println(" 5 - [UC09] Processar Fechamento e Validação de Disciplinas");
            System.out.println("--- CURSOS E DISCIPLINAS (UC03) ---");
            System.out.println(" 6 - Cadastrar Curso");
            System.out.println(" 7 - Listar Cursos");
            System.out.println(" 8 - Cadastrar Disciplina (e Ofertá-la no Currículo)");
            System.out.println(" 9 - Listar Disciplinas");
            System.out.println("10 - Remover Disciplina");
            System.out.println("11 - Vincular Professor a Disciplina");
            System.out.println("--- GESTÃO DE USUÁRIOS (UC04) ---");
            System.out.println("12 - Cadastrar Aluno");
            System.out.println("13 - Listar Alunos Cadastrados");
            System.out.println("14 - Remover Aluno");
            System.out.println("15 - Cadastrar Professor");
            System.out.println("16 - Listar Professores Cadastrados");
            System.out.println("17 - Remover Professor");
            System.out.println(" 0 - Logout (Voltar)");
            System.out.print("Escolha uma opção: ");

            int opcao = lerInt();
            try {
                switch (opcao) {
                    case 1 -> visualizarCurriculo();
                    case 2 -> gerarNovoCurriculo(secretaria);
                    case 3 -> abrirPeriodo();
                    case 4 -> encerrarPeriodo();
                    case 5 -> processarFechamento();
                    case 6 -> cadastrarCurso(secretaria);
                    case 7 -> listarCursos();
                    case 8 -> cadastrarDisciplina(secretaria);
                    case 9 -> listarDisciplinasGeral();
                    case 10 -> removerDisciplina();
                    case 11 -> vincularProfessorADisciplina();
                    case 12 -> cadastrarAluno(secretaria);
                    case 13 -> listarAlunos();
                    case 14 -> removerAluno();
                    case 15 -> cadastrarProfessor(secretaria);
                    case 16 -> listarProfessores();
                    case 17 -> removerProfessor();
                    case 0 -> voltar = true;
                    default -> System.out.println("[!] Opção inválida.");
                }
                fileManager.salvar(banco);
            } catch (Exception e) {
                System.out.println("❌ Erro: " + e.getMessage());
            }
        }
    }

    private void visualizarCurriculo() {
        CurriculoSemestre curriculo = manutencao.getCurriculoAtual(banco);
        if (curriculo == null) {
            System.out.println("[!] Nenhum currículo cadastrado no sistema.");
            return;
        }

        System.out.println("\n══════════════════ CURRÍCULO DO SEMESTRE ══════════════════");
        System.out.printf("Período Letivo: %s/%d | Status Matrícula: %s%n",
                curriculo.getSemestre(), curriculo.getAno(),
                curriculo.isPeriodoMatriculaAberto() ? "🟢 ABERTO" : "🔴 FECHADO");
        System.out.println("───────────────────────────────────────────────────────────");

        if (curriculo.getDisciplinasOfertadas().isEmpty()) {
            System.out.println("Nenhuma disciplina ofertada neste semestre.");
            return;
        }

        for (Disciplina d : curriculo.getDisciplinasOfertadas()) {
            String professorStr = d.getProfessor() != null ? d.getProfessor().getNome() : "Sem docente";
            System.out.printf("• [%s] %-32s | %d créd. | Status: %-9s | Inscritos: %2d/%2d | Docente: %s%n",
                    d.getCodigo(), d.getNome(), d.getCreditos(), d.getStatus(),
                    d.getQtdInscritosAtivos(), Disciplina.CAPACIDADE_MAXIMA, professorStr);
        }
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    private void gerarNovoCurriculo(Secretaria secretaria) {
        System.out.print("Semestre (ex: 1º ou 2º): ");
        String sem = scanner.nextLine().trim();
        System.out.print("Ano (ex: 2026): ");
        int ano = lerInt();

        CurriculoSemestre novo = secretaria.gerarCurriculoSemestre(sem, ano);
        novo.abrirPeriodoMatricula();

        // Vincula as disciplinas cadastradas ao novo currículo
        for (Disciplina d : banco.getDisciplinas()) {
            novo.adicionarOfertaDisciplina(d);
        }

        banco.getCurriculos().add(novo);
        System.out.println("✓ Currículo " + sem + "/" + ano + " gerado com sucesso com "
                + novo.getDisciplinasOfertadas().size() + " disciplina(s) e período ABERTO!");
    }

    private void abrirPeriodo() {
        CurriculoSemestre c = manutencao.getCurriculoAtual(banco);
        if (c == null) {
            System.out.println("[!] Nenhum currículo encontrado.");
            return;
        }
        curriculoService.abrirPeriodo(c);
        System.out.println("✓ Período de matrículas para " + c.getSemestre() + "/" + c.getAno() + " foi ABERTO.");
    }

    private void encerrarPeriodo() {
        CurriculoSemestre c = manutencao.getCurriculoAtual(banco);
        if (c == null) {
            System.out.println("[!] Nenhum currículo encontrado.");
            return;
        }
        curriculoService.encerrarPeriodo(c);
        System.out.println("✓ Período de matrículas para " + c.getSemestre() + "/" + c.getAno() + " foi ENCERRADO.");
    }

    private void processarFechamento() {
        CurriculoSemestre c = manutencao.getCurriculoAtual(banco);
        if (c == null) {
            System.out.println("[!] Nenhum currículo cadastrado para processar fechamento.");
            return;
        }

        System.out.println("\n--- [UC09] PROCESSANDO FECHAMENTO DE TURMAS ---");
        curriculoService.processarFechamento(c);

        int ativas = 0;
        int canceladas = 0;
        for (Disciplina d : c.getDisciplinasOfertadas()) {
            if (d.getStatus() == StatusDisciplina.ATIVA) {
                ativas++;
                System.out.printf("  ✓ [%s] %-30s -> ATIVA (%d alunos matriculados >= %d)%n",
                        d.getCodigo(), d.getNome(), d.getQtdInscritosAtivos(), Disciplina.CAPACIDADE_MINIMA);
            } else if (d.getStatus() == StatusDisciplina.CANCELADA) {
                canceladas++;
                System.out.printf("  ❌ [%s] %-30s -> CANCELADA (%d alunos < mínimo de %d)%n",
                        d.getCodigo(), d.getNome(), d.getQtdInscritosAtivos(), Disciplina.CAPACIDADE_MINIMA);
            }
        }
        System.out.println("Resultado do Fechamento: " + ativas + " turma(s) ativada(s), " + canceladas + " cancelada(s).");
    }

    private void cadastrarCurso(Secretaria secretaria) {
        System.out.print("Nome do Curso (ex: Engenharia de Software): ");
        String nome = scanner.nextLine().trim();
        System.out.print("Total de Créditos do Curso: ");
        int creditos = lerInt();

        Curso curso = secretaria.cadastrarCurso(nome, creditos);
        banco.getCursos().add(curso);
        System.out.println("✓ Curso '" + curso.getNome() + "' cadastrado com sucesso (ID: " + curso.getId() + ").");
    }

    private void listarCursos() {
        System.out.println("\n--- CURSOS REGISTRADOS ---");
        if (banco.getCursos().isEmpty()) {
            System.out.println("Nenhum curso cadastrado.");
            return;
        }
        for (Curso c : banco.getCursos()) {
            System.out.printf("• ID: %d | Nome: %-30s | Créditos: %d | Disciplinas: %d%n",
                    c.getId(), c.getNome(), c.getTotalCreditos(), c.getDisciplinas().size());
        }
    }

    private void cadastrarDisciplina(Secretaria secretaria) {
        if (banco.getCursos().isEmpty()) {
            System.out.println("[!] Cadastre pelo menos um curso antes de cadastrar disciplinas.");
            return;
        }

        listarCursos();
        System.out.print("ID do Curso vinculado: ");
        long cursoId = lerLong();
        Curso curso = manutencao.buscarCursoPorId(banco, cursoId);
        if (curso == null) {
            System.out.println("[!] Curso não encontrado.");
            return;
        }

        System.out.print("Código da Disciplina (ex: ES104): ");
        String codigo = scanner.nextLine().trim().toUpperCase();
        if (manutencao.buscarDisciplinaPorCodigo(banco, codigo) != null) {
            System.out.println("[!] Já existe uma disciplina com esse código.");
            return;
        }

        System.out.print("Nome da Disciplina: ");
        String nome = scanner.nextLine().trim();
        System.out.print("Créditos (ex: 4): ");
        int creditos = lerInt();

        Disciplina disciplina = new Disciplina(codigo, nome, creditos, curso);
        curso.adicionarDisciplina(disciplina);
        banco.getDisciplinas().add(disciplina);

        CurriculoSemestre cur = manutencao.getCurriculoAtual(banco);
        if (cur != null) {
            cur.adicionarOfertaDisciplina(disciplina);
        }

        System.out.println("✓ Disciplina '" + disciplina.getNome() + "' (" + disciplina.getCodigo() + ") cadastrada e ofertada!");
    }

    private void listarDisciplinasGeral() {
        System.out.println("\n--- DISCIPLINAS DO SISTEMA ---");
        if (banco.getDisciplinas().isEmpty()) {
            System.out.println("Nenhuma disciplina cadastrada.");
            return;
        }
        for (Disciplina d : banco.getDisciplinas()) {
            String cursoNome = d.getCurso() != null ? d.getCurso().getNome() : "Sem curso";
            String profNome = d.getProfessor() != null ? d.getProfessor().getNome() : "Sem professor";
            System.out.printf("• [%s] %-28s | %d créd. | Status: %-9s | Inscritos: %2d/60 | Curso: %s | Docente: %s%n",
                    d.getCodigo(), d.getNome(), d.getCreditos(), d.getStatus(),
                    d.getQtdInscritosAtivos(), cursoNome, profNome);
        }
    }

    private void removerDisciplina() {
        listarDisciplinasGeral();
        System.out.print("Código da disciplina a remover: ");
        String cod = scanner.nextLine().trim().toUpperCase();
        manutencao.removerDisciplina(banco, cod);
        System.out.println("✓ Disciplina " + cod + " removida com sucesso.");
    }

    private void vincularProfessorADisciplina() {
        listarProfessores();
        System.out.print("Registro do Professor (ex: P001): ");
        String reg = scanner.nextLine().trim();

        listarDisciplinasGeral();
        System.out.print("Código da Disciplina: ");
        String cod = scanner.nextLine().trim().toUpperCase();

        manutencao.vincularProfessorADisciplina(banco, reg, cod);
        System.out.println("✓ Professor vinculado com sucesso à disciplina " + cod + "!");
    }

    private void cadastrarAluno(Secretaria secretaria) {
        System.out.print("Nome completo do aluno: ");
        String nome = scanner.nextLine().trim();
        System.out.print("E-mail: ");
        String email = scanner.nextLine().trim();
        System.out.print("Matrícula (ex: 20260005): ");
        String matricula = scanner.nextLine().trim();
        System.out.print("Login de acesso: ");
        String login = scanner.nextLine().trim();
        System.out.print("Senha: ");
        String senha = scanner.nextLine().trim();

        Aluno aluno = new Aluno(System.currentTimeMillis(), nome, email, login, senha, matricula);
        manutencao.cadastrarAluno(banco, aluno);
        System.out.println("✓ Aluno " + aluno.getNome() + " cadastrado com sucesso!");
    }

    private void listarAlunos() {
        System.out.println("\n--- ALUNOS CADASTRADOS ---");
        if (banco.getAlunos().isEmpty()) {
            System.out.println("Nenhum aluno cadastrado.");
            return;
        }
        for (Aluno a : banco.getAlunos()) {
            System.out.printf("• Matrícula: %-10s | Nome: %-22s | E-mail: %-20s | Ativas: %d (Obrig: %d, Opt: %d)%n",
                    a.getMatricula(), a.getNome(), a.getEmail(),
                    a.getInscricoesAtivas().size(), a.getQtdObrigatoriasAtivas(), a.getQtdOptativasAtivas());
        }
    }

    private void removerAluno() {
        listarAlunos();
        System.out.print("Matrícula do aluno a remover: ");
        String mat = scanner.nextLine().trim();
        manutencao.removerAluno(banco, mat);
        System.out.println("✓ Aluno com matrícula " + mat + " removido.");
    }

    private void cadastrarProfessor(Secretaria secretaria) {
        System.out.print("Nome completo do professor: ");
        String nome = scanner.nextLine().trim();
        System.out.print("E-mail: ");
        String email = scanner.nextLine().trim();
        System.out.print("Registro docente (ex: P003): ");
        String reg = scanner.nextLine().trim();
        System.out.print("Login de acesso: ");
        String login = scanner.nextLine().trim();
        System.out.print("Senha: ");
        String senha = scanner.nextLine().trim();

        Professor prof = new Professor(System.currentTimeMillis(), nome, email, login, senha, reg);
        manutencao.cadastrarProfessor(banco, prof);
        System.out.println("✓ Professor " + prof.getNome() + " cadastrado com sucesso!");
    }

    private void listarProfessores() {
        System.out.println("\n--- PROFESSORES CADASTRADOS ---");
        if (banco.getProfessores().isEmpty()) {
            System.out.println("Nenhum professor cadastrado.");
            return;
        }
        for (Professor p : banco.getProfessores()) {
            System.out.printf("• Registro: %-8s | Nome: %-25s | E-mail: %-25s | Disciplinas: %d%n",
                    p.getRegistroDocente(), p.getNome(), p.getEmail(), p.getDisciplinasLecionadas().size());
        }
    }

    private void removerProfessor() {
        listarProfessores();
        System.out.print("Registro docente do professor a remover: ");
        String reg = scanner.nextLine().trim();
        manutencao.removerProfessor(banco, reg);
        System.out.println("✓ Professor com registro " + reg + " removido.");
    }

    // =========================================================================
    // MENU ALUNO (UC05, UC06)
    // =========================================================================

    private void menuAluno(Aluno aluno) {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║                        PAINEL DO ALUNO                       ║");
            System.out.println("║ Aluno: " + formatarTamanho(aluno.getNome(), 27) + " Matrícula: " + formatarTamanho(aluno.getMatricula(), 15) + " ║");
            System.out.printf("║ Inscrições Ativas: Obrigatórias: %d/4 | Optativas: %d/2         ║%n",
                    aluno.getQtdObrigatoriasAtivas(), aluno.getQtdOptativasAtivas());
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            System.out.println(" 1 - Consultar Disciplinas Ofertadas no Semestre");
            System.out.println(" 2 - [UC05] Efetuar Matrícula em Disciplina (Dispara Cobrança [UC08])");
            System.out.println(" 3 - Consultar Minhas Matrículas Ativas");
            System.out.println(" 4 - [UC06] Cancelar Matrícula em Disciplina");
            System.out.println(" 0 - Logout (Voltar)");
            System.out.print("Escolha uma opção: ");

            int opcao = lerInt();
            switch (opcao) {
                case 1 -> listarDisciplinasOfertadasParaAluno();
                case 2 -> efetuarMatriculaAluno(aluno);
                case 3 -> consultarMatriculasAluno(aluno);
                case 4 -> cancelarMatriculaAluno(aluno);
                case 0 -> voltar = true;
                default -> System.out.println("[!] Opção inválida.");
            }
            fileManager.salvar(banco);
        }
    }

    private void listarDisciplinasOfertadasParaAluno() {
        CurriculoSemestre cur = manutencao.getCurriculoAtual(banco);
        if (cur == null) {
            System.out.println("[!] Nenhum currículo cadastrado no momento.");
            return;
        }

        System.out.println("\n════════════ DISCIPLINAS OFERTADAS (" + cur.getSemestre() + "/" + cur.getAno() + ") ════════════");
        System.out.println("Período de matrículas: " + (cur.isPeriodoMatriculaAberto() ? "🟢 ABERTO" : "🔴 FECHADO"));
        System.out.println("───────────────────────────────────────────────────────────");

        for (Disciplina d : cur.getDisciplinasOfertadas()) {
            int vagasRestantes = Disciplina.CAPACIDADE_MAXIMA - d.getQtdInscritosAtivos();
            String profNome = d.getProfessor() != null ? d.getProfessor().getNome() : "A definir";
            System.out.printf("• [%s] %-30s | %d créd. | Vagas: %2d/60 | Docente: %s%n",
                    d.getCodigo(), d.getNome(), d.getCreditos(), vagasRestantes, profNome);
        }
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    private void efetuarMatriculaAluno(Aluno aluno) {
        CurriculoSemestre cur = manutencao.getCurriculoAtual(banco);
        if (cur == null || !cur.isPeriodoMatriculaAberto()) {
            System.out.println("❌ Período de matrículas está FECHADO. Nenhuma matrícula pode ser realizada.");
            return;
        }

        listarDisciplinasOfertadasParaAluno();
        System.out.print("Informe o CÓDIGO da disciplina desejada: ");
        String codigo = scanner.nextLine().trim().toUpperCase();

        Disciplina disciplina = manutencao.buscarDisciplinaPorCodigo(banco, codigo);
        if (disciplina == null || !cur.possuiDisciplina(disciplina)) {
            System.out.println("❌ Disciplina não encontrada ou não ofertada neste semestre.");
            return;
        }

        if (disciplina.getStatus() != StatusDisciplina.ABERTA) {
            System.out.println("❌ Disciplina não está aberta para novas inscrições (Status: " + disciplina.getStatus() + ").");
            return;
        }

        if (!disciplina.isVagasDisponiveis()) {
            System.out.println("❌ Turma lotada! Limite máximo de 60 alunos atingido.");
            return;
        }

        System.out.println("Selecione o tipo de inscrição:");
        System.out.println(" 1 - Obrigatória (1ª Opção - Limite máximo: 4)");
        System.out.println(" 2 - Optativa (Alternativa - Limite máximo: 2)");
        System.out.print("Opção: ");
        int tipoOp = lerInt();

        TipoInscricao tipo = switch (tipoOp) {
            case 1 -> TipoInscricao.OBRIGATORIA;
            case 2 -> TipoInscricao.OPTATIVA;
            default -> null;
        };

        if (tipo == null) {
            System.out.println("[!] Tipo de inscrição inválido.");
            return;
        }

        if (tipo == TipoInscricao.OBRIGATORIA && aluno.getQtdObrigatoriasAtivas() >= Aluno.LIMITE_OBRIGATORIAS) {
            System.out.println("❌ Limite atingido: Você já possui o máximo de 4 disciplinas obrigatórias ativas.");
            return;
        }

        if (tipo == TipoInscricao.OPTATIVA && aluno.getQtdOptativasAtivas() >= Aluno.LIMITE_OPTATIVAS) {
            System.out.println("❌ Limite atingido: Você já possui o máximo de 2 disciplinas optativas ativas.");
            return;
        }

        boolean sucesso = matriculaService.matricular(aluno, disciplina, tipo, cur);
        if (sucesso) {
            System.out.println("✓ Matrícula realizada com sucesso na disciplina " + disciplina.getNome() + "!");
        } else {
            System.out.println("❌ Não foi possível efetuar a matrícula. Verifique se você já está matriculado nesta disciplina.");
        }
    }

    private void consultarMatriculasAluno(Aluno aluno) {
        System.out.println("\n════════════════ MINHAS MATRÍCULAS ATIVAS ════════════════");
        List<Inscricao> ativas = aluno.getInscricoesAtivas();
        if (ativas.isEmpty()) {
            System.out.println("Você ainda não possui matrículas ativas neste semestre.");
        } else {
            for (Inscricao i : ativas) {
                System.out.printf("• [%s] %-30s | Tipo: %-11s | Data: %s%n",
                        i.getDisciplina().getCodigo(), i.getDisciplina().getNome(),
                        i.getTipo(), i.getDataInscricao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
        }
        System.out.printf("Total: %d disciplina(s) [Obrigatórias: %d/4 | Optativas: %d/2]%n",
                ativas.size(), aluno.getQtdObrigatoriasAtivas(), aluno.getQtdOptativasAtivas());
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    private void cancelarMatriculaAluno(Aluno aluno) {
        CurriculoSemestre cur = manutencao.getCurriculoAtual(banco);
        if (cur == null || !cur.isPeriodoMatriculaAberto()) {
            System.out.println("❌ O período de alterações/cancelamento de matrículas está FECHADO.");
            return;
        }

        consultarMatriculasAluno(aluno);
        if (aluno.getInscricoesAtivas().isEmpty()) {
            return;
        }

        System.out.print("Informe o CÓDIGO da disciplina que deseja cancelar: ");
        String codigo = scanner.nextLine().trim().toUpperCase();

        Disciplina disciplina = manutencao.buscarDisciplinaPorCodigo(banco, codigo);
        if (disciplina == null) {
            System.out.println("❌ Disciplina não encontrada.");
            return;
        }

        boolean sucesso = matriculaService.cancelar(aluno, disciplina, cur);
        if (sucesso) {
            System.out.println("✓ Matrícula na disciplina " + disciplina.getNome() + " cancelada com sucesso!");
            System.out.println("✓ Vaga liberada imediatamente na turma.");
        } else {
            System.out.println("❌ Não foi possível cancelar: você não possui matrícula ativa nesta disciplina.");
        }
    }

    // =========================================================================
    // MENU PROFESSOR (UC07)
    // =========================================================================

    private void menuProfessor(Professor professor) {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║                      PAINEL DO PROFESSOR                     ║");
            System.out.println("║ Docente: " + formatarTamanho(professor.getNome(), 25) + " Reg: " + formatarTamanho(professor.getRegistroDocente(), 18) + " ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            System.out.println(" 1 - Consultar Minhas Disciplinas Lecionadas");
            System.out.println(" 2 - [UC07] Consultar Relação de Alunos Matriculados em Minha Turma");
            System.out.println(" 0 - Logout (Voltar)");
            System.out.print("Escolha uma opção: ");

            int opcao = lerInt();
            switch (opcao) {
                case 1 -> listarDisciplinasDoProfessor(professor);
                case 2 -> consultarAlunosDeTurma(professor);
                case 0 -> voltar = true;
                default -> System.out.println("[!] Opção inválida.");
            }
        }
    }

    private void listarDisciplinasDoProfessor(Professor professor) {
        System.out.println("\n════════════ MINHAS DISCIPLINAS ATRIBUÍDAS ════════════");
        if (professor.getDisciplinasLecionadas().isEmpty()) {
            System.out.println("Nenhuma disciplina atribuída a este professor.");
            return;
        }

        for (Disciplina d : professor.getDisciplinasLecionadas()) {
            System.out.printf("• [%s] %-32s | %d créditos | Inscritos: %2d/60 | Status: %s%n",
                    d.getCodigo(), d.getNome(), d.getCreditos(), d.getQtdInscritosAtivos(), d.getStatus());
        }
        System.out.println("═══════════════════════════════════════════════════════");
    }

    private void consultarAlunosDeTurma(Professor professor) {
        listarDisciplinasDoProfessor(professor);
        if (professor.getDisciplinasLecionadas().isEmpty()) {
            return;
        }

        System.out.print("Informe o CÓDIGO da disciplina para consultar alunos: ");
        String codigo = scanner.nextLine().trim().toUpperCase();

        Disciplina disciplina = null;
        for (Disciplina d : professor.getDisciplinasLecionadas()) {
            if (d.getCodigo().equalsIgnoreCase(codigo)) {
                disciplina = d;
                break;
            }
        }

        if (disciplina == null) {
            System.out.println("❌ A disciplina informada não pertence ao seu plano de aulas docente.");
            return;
        }

        List<Aluno> alunos = professor.consultarAlunos(disciplina);
        System.out.println("\n════════ ALUNOS MATRICULADOS EM " + disciplina.getNome() + " ════════");
        System.out.printf("Turma: %s | Total de inscritos: %d aluno(s)%n", disciplina.getCodigo(), alunos.size());
        System.out.println("───────────────────────────────────────────────────────────");

        if (alunos.isEmpty()) {
            System.out.println("Nenhum aluno matriculado nesta turma até o momento.");
        } else {
            int cont = 1;
            for (Aluno a : alunos) {
                System.out.printf("%2d. Matrícula: %-10s | Nome: %-25s | E-mail: %s%n",
                        cont++, a.getMatricula(), a.getNome(), a.getEmail());
            }
        }
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    // =========================================================================
    // CARGA DE DADOS INICIAIS (SEED)
    // =========================================================================

    private void garantirDadosIniciais() {
        if (!banco.getCursos().isEmpty()) {
            return;
        }

        System.out.println("Configurando base de dados demonstrativa inicial...");

        // 1. Secretaria
        Secretaria secretaria = new Secretaria(
                1L, "Secretaria Acadêmica", "secretaria@pucminas.br",
                "admin", "admin123", "Secretaria de Controle Acadêmico"
        );
        banco.getSecretarias().add(secretaria);

        // 2. Cursos
        Curso engSoft = new Curso(1L, "Engenharia de Software", 200);
        Curso cc = new Curso(2L, "Ciência da Computação", 210);
        banco.getCursos().add(engSoft);
        banco.getCursos().add(cc);

        // 3. Disciplinas
        Disciplina d1 = new Disciplina("ES101", "Projeto de Software", 4, engSoft);
        Disciplina d2 = new Disciplina("ES102", "Algoritmos e Estruturas de Dados", 4, engSoft);
        Disciplina d3 = new Disciplina("ES103", "Engenharia de Requisitos", 4, engSoft);
        Disciplina d4 = new Disciplina("ES104", "Testes e Qualidade de Software", 4, engSoft);
        Disciplina d5 = new Disciplina("OPT01", "Tópicos em Inteligência Artificial", 2, engSoft);
        Disciplina d6 = new Disciplina("OPT02", "Empreendedorismo Tecnológico", 2, engSoft);

        engSoft.adicionarDisciplina(d1);
        engSoft.adicionarDisciplina(d2);
        engSoft.adicionarDisciplina(d3);
        engSoft.adicionarDisciplina(d4);
        engSoft.adicionarDisciplina(d5);
        engSoft.adicionarDisciplina(d6);

        banco.getDisciplinas().add(d1);
        banco.getDisciplinas().add(d2);
        banco.getDisciplinas().add(d3);
        banco.getDisciplinas().add(d4);
        banco.getDisciplinas().add(d5);
        banco.getDisciplinas().add(d6);

        // 4. Professores
        Professor p1 = new Professor(101L, "Prof. Dr. Roberto Souza", "roberto@pucminas.br", "prof1", "123", "P001");
        Professor p2 = new Professor(102L, "Profa. Dra. Ana Paula", "anapaula@pucminas.br", "prof2", "123", "P002");

        p1.vincularDisciplina(d1);
        p1.vincularDisciplina(d3);
        p2.vincularDisciplina(d2);
        p2.vincularDisciplina(d4);

        banco.getProfessores().add(p1);
        banco.getProfessores().add(p2);

        // 5. Alunos
        Aluno a1 = new Aluno(201L, "Caio Santos", "caio@email.com", "aluno1", "123", "20260001");
        Aluno a2 = new Aluno(202L, "Anthony Santos", "anthony@email.com", "aluno2", "123", "20260002");
        Aluno a3 = new Aluno(203L, "Pedro Queiroz", "pedro@email.com", "aluno3", "123", "20260003");
        Aluno a4 = new Aluno(204L, "Maria Silva", "maria@email.com", "aluno4", "123", "20260004");

        banco.getAlunos().add(a1);
        banco.getAlunos().add(a2);
        banco.getAlunos().add(a3);
        banco.getAlunos().add(a4);

        // 6. Currículo do Semestre
        CurriculoSemestre curriculo = new CurriculoSemestre(1L, "1º", 2026);
        curriculo.adicionarOfertaDisciplina(d1);
        curriculo.adicionarOfertaDisciplina(d2);
        curriculo.adicionarOfertaDisciplina(d3);
        curriculo.adicionarOfertaDisciplina(d4);
        curriculo.adicionarOfertaDisciplina(d5);
        curriculo.adicionarOfertaDisciplina(d6);
        curriculo.abrirPeriodoMatricula();

        banco.getCurriculos().add(curriculo);

        // 7. Matrículas de demonstração inicial:
        // - d2 (Algoritmos) recebe a1, a2 e a3 (3 inscritos -> atinge mínimo >= 3 para fechar ATIVA)
        // - d1 (Projeto) recebe a1 e a2 (2 inscritos -> ideal para testar se mais 1 entra ou se cancela se fechar)
        // - d6 (Empreendedorismo - Optativa) recebe a1 (1 inscrito -> se fechar vira CANCELADA)
        a1.matricular(d1, TipoInscricao.OBRIGATORIA);
        a2.matricular(d1, TipoInscricao.OBRIGATORIA);

        a1.matricular(d2, TipoInscricao.OBRIGATORIA);
        a2.matricular(d2, TipoInscricao.OBRIGATORIA);
        a3.matricular(d2, TipoInscricao.OBRIGATORIA);

        a1.matricular(d6, TipoInscricao.OPTATIVA);

        fileManager.salvar(banco);
        System.out.println("✓ Base de dados inicial carregada e persistida.");
    }

    // =========================================================================
    // UTILITÁRIOS DE ENTRADA
    // =========================================================================

    private int lerInt() {
        while (true) {
            try {
                String linha = scanner.nextLine().trim();
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.print("[!] Digite um número inteiro válido: ");
            }
        }
    }

    private long lerLong() {
        while (true) {
            try {
                String linha = scanner.nextLine().trim();
                return Long.parseLong(linha);
            } catch (NumberFormatException e) {
                System.out.print("[!] Digite um número inteiro válido: ");
            }
        }
    }

    private String formatarTamanho(String texto, int largura) {
        if (texto == null) texto = "";
        if (texto.length() > largura) {
            return texto.substring(0, largura);
        }
        return String.format("%-" + largura + "s", texto);
    }
}
