package br.pucminas.matriculas;

import br.pucminas.matriculas.model.Aluno;
import br.pucminas.matriculas.model.BancoDeDados;
import br.pucminas.matriculas.model.CurriculoSemestre;
import br.pucminas.matriculas.model.Curso;
import br.pucminas.matriculas.model.Disciplina;
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
import br.pucminas.matriculas.service.ServicoNotificacaoCobranca;

import java.io.File;
import java.util.List;

/**
 * Suite de testes automatizados para validação de todos os Casos de Uso (UC01 a UC09)
 * e regras de negócio do Sistema de Matrículas Universitárias.
 */
public class SistemaMatriculasTest {

    private static int testesExecutados = 0;
    private static int testesAprovados = 0;

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("  INICIANDO BATERIA DE TESTES AUTOMATIZADOS (UC01 a UC09)");
        System.out.println("===============================================================");

        testarUC01Autenticacao();
        testarUC02CurriculoSemestre();
        testarUC03ManterCursosEDisciplinas();
        testarUC04ManterProfessoresEAlunos();
        testarUC05MatriculaELimites();
        testarUC06CancelamentoMatricula();
        testarUC07ConsultaProfessor();
        testarUC08NotificacaoCobranca();
        testarUC09FechamentoDisciplinas();
        testarPersistenciaArquivo();

        System.out.println("===============================================================");
        System.out.printf("RESULTADO FINAL: %d/%d testes aprovados com sucesso!%n",
                testesAprovados, testesExecutados);
        System.out.println("===============================================================");

        if (testesAprovados != testesExecutados) {
            System.exit(1);
        }
    }

    private static void afirme(boolean condicao, String mensagem) {
        testesExecutados++;
        if (condicao) {
            testesAprovados++;
            System.out.println("  ✓ [PASSOU] " + mensagem);
        } else {
            System.err.println("  ❌ [FALHOU] " + mensagem);
        }
    }

    // --- UC01: Efetuar Login ---
    private static void testarUC01Autenticacao() {
        System.out.println("\n[UC01] Testando Autenticação e Login...");
        ServicoAutenticacao auth = new ServicoAutenticacao();
        BancoDeDados banco = new BancoDeDados();

        Secretaria sec = new Secretaria(1L, "Secretaria", "sec@puc.br", "admin", "pass123", "Depto");
        Aluno aluno = new Aluno(2L, "Aluno Teste", "aluno@puc.br", "aluno1", "senha123", "20261001");
        banco.getSecretarias().add(sec);
        banco.getAlunos().add(aluno);

        Usuario userAutenticado = auth.autenticar(banco.getTodosUsuarios(), "admin", "pass123");
        afirme(userAutenticado != null && userAutenticado.equals(sec), "Login de Secretaria com credenciais válidas");

        Usuario alunoAutenticado = auth.autenticar(banco.getTodosUsuarios(), "aluno1", "senha123");
        afirme(alunoAutenticado != null && alunoAutenticado.equals(aluno), "Login de Aluno com credenciais válidas");

        Usuario falhaLogin = auth.autenticar(banco.getTodosUsuarios(), "admin", "senhaErrada");
        afirme(falhaLogin == null, "Bloqueio de autenticação com senha incorreta");

        Usuario inexistente = auth.autenticar(banco.getTodosUsuarios(), "fantasma", "123");
        afirme(inexistente == null, "Bloqueio de autenticação com usuário inexistente");
    }

    // --- UC02: Gerar Currículo do Semestre ---
    private static void testarUC02CurriculoSemestre() {
        System.out.println("\n[UC02] Testando Geração e Controle de Currículo do Semestre...");
        Secretaria sec = new Secretaria(1L, "Secretaria", "sec@puc.br", "sec", "123", "Depto");
        ServicoCurriculo servico = new ServicoCurriculo();

        CurriculoSemestre curr = sec.gerarCurriculoSemestre("1º", 2026);
        afirme(curr != null && curr.getAno() == 2026, "Criação de currículo pela secretaria");
        afirme(!curr.isPeriodoMatriculaAberto(), "Currículo criado com período de matrícula inicialmente fechado");

        servico.abrirPeriodo(curr);
        afirme(curr.isPeriodoMatriculaAberto(), "Abertura do período de matrículas");

        servico.encerrarPeriodo(curr);
        afirme(!curr.isPeriodoMatriculaAberto(), "Encerramento do período de matrículas");
    }

    // --- UC03: Manter Cursos e Disciplinas ---
    private static void testarUC03ManterCursosEDisciplinas() {
        System.out.println("\n[UC03] Testando Manutenção de Cursos e Disciplinas...");
        BancoDeDados banco = new BancoDeDados();
        ServicoManutencao man = new ServicoManutencao();

        Curso curso = new Curso(10L, "Engenharia de Software", 200);
        man.cadastrarCurso(banco, curso);
        afirme(man.buscarCursoPorId(banco, 10L) != null, "Cadastro e busca de Curso");

        Disciplina disc = new Disciplina("ES200", "Arquitetura de Software", 4, curso);
        curso.adicionarDisciplina(disc);
        man.cadastrarDisciplina(banco, disc);

        afirme(man.buscarDisciplinaPorCodigo(banco, "ES200") != null, "Cadastro e busca de Disciplina");
        afirme(curso.getDisciplinas().contains(disc), "Associação da disciplina ao catálogo do curso");

        man.removerDisciplina(banco, "ES200");
        afirme(man.buscarDisciplinaPorCodigo(banco, "ES200") == null, "Remoção de disciplina");
    }

    // --- UC04: Manter Professores e Alunos ---
    private static void testarUC04ManterProfessoresEAlunos() {
        System.out.println("\n[UC04] Testando Manutenção de Professores e Alunos...");
        BancoDeDados banco = new BancoDeDados();
        ServicoManutencao man = new ServicoManutencao();

        Aluno aluno = new Aluno(1L, "Carlos", "carlos@email.com", "carlos", "123", "MAT001");
        man.cadastrarAluno(banco, aluno);
        afirme(man.buscarAlunoPorMatricula(banco, "MAT001") != null, "Cadastro e busca de Aluno");

        Professor prof = new Professor(2L, "Prof. Alan", "alan@email.com", "alan", "123", "DOC001");
        man.cadastrarProfessor(banco, prof);
        afirme(man.buscarProfessorPorRegistro(banco, "DOC001") != null, "Cadastro e busca de Professor");

        man.removerAluno(banco, "MAT001");
        afirme(man.buscarAlunoPorMatricula(banco, "MAT001") == null, "Remoção de Aluno");

        man.removerProfessor(banco, "DOC001");
        afirme(man.buscarProfessorPorRegistro(banco, "DOC001") == null, "Remoção de Professor");
    }

    // --- UC05: Matricular em Disciplinas e Regras ---
    private static void testarUC05MatriculaELimites() {
        System.out.println("\n[UC05] Testando Regras de Matrícula (Limites 4 Obrigatórias / 2 Optativas / Limite 60)...");
        Curso curso = new Curso(1L, "Engenharia de Software", 200);
        CurriculoSemestre curr = new CurriculoSemestre(1L, "1º", 2026);
        curr.abrirPeriodoMatricula();

        Disciplina d1 = new Disciplina("D1", "D1", 4, curso);
        Disciplina d2 = new Disciplina("D2", "D2", 4, curso);
        Disciplina d3 = new Disciplina("D3", "D3", 4, curso);
        Disciplina d4 = new Disciplina("D4", "D4", 4, curso);
        Disciplina d5 = new Disciplina("D5", "D5", 4, curso);
        Disciplina opt1 = new Disciplina("OPT1", "OPT1", 2, curso);
        Disciplina opt2 = new Disciplina("OPT2", "OPT2", 2, curso);
        Disciplina opt3 = new Disciplina("OPT3", "OPT3", 2, curso);

        for (Disciplina d : List.of(d1, d2, d3, d4, d5, opt1, opt2, opt3)) {
            curr.adicionarOfertaDisciplina(d);
        }

        MockCobrancaAdapter cobranca = new MockCobrancaAdapter();
        ServicoMatricula servico = new ServicoMatricula(cobranca);

        Aluno aluno = new Aluno(10L, "Estudante 1", "est1@email.com", "est1", "123", "MAT100");

        // Matricular em 4 obrigatórias
        afirme(servico.matricular(aluno, d1, TipoInscricao.OBRIGATORIA, curr), "1ª matrícula obrigatória");
        afirme(servico.matricular(aluno, d2, TipoInscricao.OBRIGATORIA, curr), "2ª matrícula obrigatória");
        afirme(servico.matricular(aluno, d3, TipoInscricao.OBRIGATORIA, curr), "3ª matrícula obrigatória");
        afirme(servico.matricular(aluno, d4, TipoInscricao.OBRIGATORIA, curr), "4ª matrícula obrigatória");

        // Tentativa da 5ª obrigatória deve falhar
        boolean quintaObrigatoria = servico.matricular(aluno, d5, TipoInscricao.OBRIGATORIA, curr);
        afirme(!quintaObrigatoria, "Bloqueio de 5ª matrícula obrigatória (limite é 4)");

        // Matricular em 2 optativas
        afirme(servico.matricular(aluno, opt1, TipoInscricao.OPTATIVA, curr), "1ª matrícula optativa");
        afirme(servico.matricular(aluno, opt2, TipoInscricao.OPTATIVA, curr), "2ª matrícula optativa");

        // Tentativa da 3ª optativa deve falhar
        boolean terceiraOptativa = servico.matricular(aluno, opt3, TipoInscricao.OPTATIVA, curr);
        afirme(!terceiraOptativa, "Bloqueio de 3ª matrícula optativa (limite é 2)");

        // Bloqueio de duplicidade na mesma disciplina
        boolean duplicada = servico.matricular(aluno, d1, TipoInscricao.OBRIGATORIA, curr);
        afirme(!duplicada, "Bloqueio de matrícula duplicada na mesma disciplina");

        // Bloqueio de matrícula quando período está fechado
        curr.encerrarPeriodoMatricula();
        Aluno outroAluno = new Aluno(20L, "Estudante 2", "est2@email.com", "est2", "123", "MAT200");
        boolean periodoFechado = servico.matricular(outroAluno, d5, TipoInscricao.OBRIGATORIA, curr);
        afirme(!periodoFechado, "Bloqueio de matrícula com período encerrado");
        curr.abrirPeriodoMatricula();

        // Teste de capacidade máxima de 60 alunos
        Disciplina turmaCheia = new Disciplina("FULL", "Turma Lotada", 4, curso);
        turmaCheia.setCapacidadeMaxima(2); // configurando teto 2 para teste unitário
        curr.adicionarOfertaDisciplina(turmaCheia);

        Aluno aA = new Aluno(31L, "A", "a@email.com", "a", "123", "M1");
        Aluno aB = new Aluno(32L, "B", "b@email.com", "b", "123", "M2");
        Aluno aC = new Aluno(33L, "C", "c@email.com", "c", "123", "M3");

        servico.matricular(aA, turmaCheia, TipoInscricao.OBRIGATORIA, curr);
        servico.matricular(aB, turmaCheia, TipoInscricao.OBRIGATORIA, curr);
        boolean matriculaExcedente = servico.matricular(aC, turmaCheia, TipoInscricao.OBRIGATORIA, curr);
        afirme(!matriculaExcedente, "Bloqueio de matrícula quando a capacidade máxima da turma é atingida");
    }

    // --- UC06: Cancelar Matrícula ---
    private static void testarUC06CancelamentoMatricula() {
        System.out.println("\n[UC06] Testando Cancelamento de Matrícula...");
        Curso curso = new Curso(1L, "Engenharia de Software", 200);
        CurriculoSemestre curr = new CurriculoSemestre(1L, "1º", 2026);
        curr.abrirPeriodoMatricula();

        Disciplina d = new Disciplina("ES300", "Testes de Software", 4, curso);
        curr.adicionarOfertaDisciplina(d);

        MockCobrancaAdapter cobranca = new MockCobrancaAdapter();
        ServicoMatricula servico = new ServicoMatricula(cobranca);

        Aluno aluno = new Aluno(10L, "Lucas", "lucas@email.com", "lucas", "123", "MAT300");
        servico.matricular(aluno, d, TipoInscricao.OBRIGATORIA, curr);
        afirme(d.getQtdInscritosAtivos() == 1, "Disciplina possui 1 inscrito ativo");

        boolean cancelou = servico.cancelar(aluno, d, curr);
        afirme(cancelou, "Cancelamento realizado com sucesso pelo aluno");
        afirme(d.getQtdInscritosAtivos() == 0, "Vaga liberada imediatamente na disciplina após cancelamento");
        afirme(aluno.getQtdObrigatoriasAtivas() == 0, "Aluno atualizado sem matrículas obrigatórias ativas");
    }

    // --- UC07: Consultar Alunos Matriculados (Professor) ---
    private static void testarUC07ConsultaProfessor() {
        System.out.println("\n[UC07] Testando Consulta de Alunos pelo Professor...");
        Curso curso = new Curso(1L, "Engenharia de Software", 200);
        Professor prof = new Professor(1L, "Prof. Gilberto", "gil@puc.br", "gil", "123", "P999");
        Disciplina d = new Disciplina("ES400", "Compiladores", 4, curso);
        prof.vincularDisciplina(d);

        Aluno a1 = new Aluno(10L, "Aluno 1", "a1@email.com", "a1", "123", "M01");
        Aluno a2 = new Aluno(20L, "Aluno 2", "a2@email.com", "a2", "123", "M02");

        a1.matricular(d, TipoInscricao.OBRIGATORIA);
        a2.matricular(d, TipoInscricao.OBRIGATORIA);

        List<Aluno> listaAlunos = prof.consultarAlunos(d);
        afirme(listaAlunos.size() == 2, "Professor visualiza exatamente os 2 alunos inscritos em sua disciplina");
        afirme(listaAlunos.contains(a1) && listaAlunos.contains(a2), "Alunos corretos retornados para o docente");

        // Teste de isolamento: disciplina de outro professor
        Disciplina outra = new Disciplina("OUTRA", "Outra Disc", 4, curso);
        afirme(prof.consultarAlunos(outra).isEmpty(), "Professor não tem acesso a disciplinas de outros docentes");
    }

    // --- UC08: Notificar Sistema de Cobrança ---
    private static void testarUC08NotificacaoCobranca() {
        System.out.println("\n[UC08] Testando Notificação Automática de Cobrança (<<include>>)...");
        Curso curso = new Curso(1L, "Engenharia", 200);
        CurriculoSemestre curr = new CurriculoSemestre(1L, "1º", 2026);
        curr.abrirPeriodoMatricula();

        Disciplina d = new Disciplina("ES500", "Sistemas Distribuídos", 4, curso);
        curr.adicionarOfertaDisciplina(d);

        MockCobrancaAdapter mock = new MockCobrancaAdapter();
        ServicoMatricula servico = new ServicoMatricula(mock);

        Aluno aluno = new Aluno(1L, "Marcos", "marcos@email.com", "marcos", "123", "MAT500");
        servico.matricular(aluno, d, TipoInscricao.OBRIGATORIA, curr);

        afirme(mock.notificacoesDisparadas == 1, "Sistema de Cobrança foi notificado após matrícula confirmada");
    }

    // --- UC09: Processar Fechamento de Disciplinas ---
    private static void testarUC09FechamentoDisciplinas() {
        System.out.println("\n[UC09] Testando Fechamento de Turmas (<3 cancela, >=3 ativa)...");
        Curso curso = new Curso(1L, "Engenharia", 200);
        CurriculoSemestre curr = new CurriculoSemestre(1L, "1º", 2026);

        Disciplina turmaComPoucos = new Disciplina("POUCOS", "Turma com 2", 4, curso);
        Disciplina turmaSuficiente = new Disciplina("SUFICIENTE", "Turma com 3", 4, curso);

        curr.adicionarOfertaDisciplina(turmaComPoucos);
        curr.adicionarOfertaDisciplina(turmaSuficiente);

        Aluno a1 = new Aluno(1L, "A1", "a1@email.com", "a1", "123", "M1");
        Aluno a2 = new Aluno(2L, "A2", "a2@email.com", "a2", "123", "M2");
        Aluno a3 = new Aluno(3L, "A3", "a3@email.com", "a3", "123", "M3");

        // Turma 1: apenas 2 alunos inscritos
        a1.matricular(turmaComPoucos, TipoInscricao.OBRIGATORIA);
        a2.matricular(turmaComPoucos, TipoInscricao.OBRIGATORIA);

        // Turma 2: 3 alunos inscritos (mínimo atingido)
        a1.matricular(turmaSuficiente, TipoInscricao.OBRIGATORIA);
        a2.matricular(turmaSuficiente, TipoInscricao.OBRIGATORIA);
        a3.matricular(turmaSuficiente, TipoInscricao.OBRIGATORIA);

        ServicoCurriculo servico = new ServicoCurriculo();
        servico.processarFechamento(curr);

        afirme(turmaComPoucos.getStatus() == StatusDisciplina.CANCELADA,
                "Turma com menos de 3 alunos é CANCELADA no fechamento");
        afirme(turmaSuficiente.getStatus() == StatusDisciplina.ATIVA,
                "Turma com 3 alunos ou mais torna-se ATIVA no fechamento");
    }

    // --- Persistência em Arquivo ---
    private static void testarPersistenciaArquivo() {
        System.out.println("\n[PERSISTÊNCIA] Testando Gravação e Leitura em Arquivo...");
        String caminhoTemp = "data/teste_sistema.dat";
        FileManager fm = new FileManager(caminhoTemp);

        BancoDeDados bancoOriginal = new BancoDeDados();
        Curso curso = new Curso(99L, "Sistemas de Informação", 180);
        bancoOriginal.getCursos().add(curso);

        Aluno aluno = new Aluno(999L, "Teste Persistência", "teste@email.com", "loginTeste", "senha99", "MAT999");
        bancoOriginal.getAlunos().add(aluno);

        fm.salvar(bancoOriginal);

        File f = new File(caminhoTemp);
        afirme(f.exists() && f.length() > 0, "Arquivo binário gerado no disco com sucesso");

        BancoDeDados bancoCarregado = fm.carregar();
        afirme(bancoCarregado != null, "Arquivo carregado com sucesso do disco");
        afirme(bancoCarregado.getCursos().size() == 1, "Cursos restaurados corretamente");
        afirme(bancoCarregado.getAlunos().size() == 1, "Alunos restaurados corretamente");
        afirme(bancoCarregado.getAlunos().get(0).getNome().equals("Teste Persistência"), "Dados de integridade validados");

        // Limpeza do arquivo de teste
        f.delete();
    }

    // Classe auxiliar Mock para teste isolado da cobrança
    private static class MockCobrancaAdapter implements ServicoNotificacaoCobranca {
        int notificacoesDisparadas = 0;

        @Override
        public boolean notificarMatricula(Aluno aluno, Disciplina disciplina) {
            notificacoesDisparadas++;
            return true;
        }
    }
}

