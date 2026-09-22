# Projeto Estrutural: Diagrama de Classes

## 1. Visão Geral

Este documento apresenta o **Projeto Estrutural (Diagrama de Classes UML)** do Sistema de Matrículas Universitárias, concebido com base nos requisitos especificados no [README.md](../README.md) e no **Diagrama de Casos de Uso** (UC01 a UC09).

---

## 2. Diagrama de Classes em Mermaid

```mermaid
classDiagram
    direction TB

    %% Herança de Usuários
    class Usuario {
        <<abstract>>
        -id: Long
        -nome: String
        -email: String
        -login: String
        -senha: String
        +autenticar(login: String, senha: String) boolean
        +getId() Long
        +getNome() String
    }

    class Aluno {
        -matricula: String
        -inscricoes: List~Inscricao~
        +matricular(disciplina: Disciplina, tipo: TipoInscricao) boolean
        +cancelarMatricula(disciplina: Disciplina) boolean
        +getInscricoesAtivas() List~Inscricao~
        +getQtdObrigatoriasAtivas() int
        +getQtdOptativasAtivas() int
    }

    class Professor {
        -registroDocente: String
        -disciplinasLecionadas: List~Disciplina~
        +consultarAlunos(disciplina: Disciplina) List~Aluno~
        +vincularDisciplina(disciplina: Disciplina) void
    }

    class Secretaria {
        -setor: String
        +gerarCurriculoSemestre(semestre: String, ano: int) CurriculoSemestre
        +cadastrarCurso(nome: String, creditos: int) Curso
        +cadastrarDisciplina(nome: String, creditos: int, curso: Curso) Disciplina
        +cadastrarProfessor(nome: String, email: String, registro: String) Professor
        +cadastrarAluno(nome: String, email: String, matricula: String) Aluno
    }

    Usuario <|-- Aluno
    Usuario <|-- Professor
    Usuario <|-- Secretaria

    %% Estrutura de Cursos e Currículo
    class Curso {
        -id: Long
        -nome: String
        -totalCreditos: int
        -disciplinas: List~Disciplina~
        +adicionarDisciplina(disciplina: Disciplina) void
        +getDisciplinas() List~Disciplina~
    }

    class CurriculoSemestre {
        -id: Long
        -semestre: String
        -ano: int
        -periodoMatriculaAberto: boolean
        -disciplinasOfertadas: List~Disciplina~
        +abrirPeriodoMatricula() void
        +encerrarPeriodoMatricula() void
        +adicionarOfertaDisciplina(disciplina: Disciplina) void
        +processarFechamentoDisciplinas() void
    }

    %% Disciplina e Inscrição
    class Disciplina {
        -codigo: String
        -nome: String
        -creditos: int
        -capacidadeMaxima: int
        -capacidadeMinima: int
        -status: StatusDisciplina
        -curso: Curso
        -professor: Professor
        -inscricoes: List~Inscricao~
        +adicionarInscricao(inscricao: Inscricao) boolean
        +removerInscricao(aluno: Aluno) boolean
        +isVagasDisponiveis() boolean
        +getQtdInscritosAtivos() int
        +processarStatusFechamento() void
        +getAlunosMatriculados() List~Aluno~
    }

    class Inscricao {
        -id: Long
        -dataInscricao: LocalDateTime
        -tipo: TipoInscricao
        -status: StatusInscricao
        -aluno: Aluno
        -disciplina: Disciplina
        +cancelar() void
        +getStatus() StatusInscricao
        +getTipo() TipoInscricao
    }

    %% Serviço Externo
    class ServicoNotificacaoCobranca {
        <<interface>>
        +notificarMatricula(aluno: Aluno, disciplina: Disciplina) boolean
    }

    class SistemaCobrancaAdapter {
        -urlApi: String
        -apiKey: String
        +notificarMatricula(aluno: Aluno, disciplina: Disciplina) boolean
    }

    ServicoNotificacaoCobranca <|.. SistemaCobrancaAdapter

    %% Enumerações
    class StatusDisciplina {
        <<enumeration>>
        ABERTA
        ATIVA
        CANCELADA
    }

    class TipoInscricao {
        <<enumeration>>
        OBRIGATORIA
        OPTATIVA
    }

    class StatusInscricao {
        <<enumeration>>
        ATIVA
        CANCELADA
    }

    %% Relacionamentos
    Curso "1" *-- "0..*" Disciplina : contém
    CurriculoSemestre "1" o-- "0..*" Disciplina : oferta
    Professor "0..1" --> "0..*" Disciplina : leciona
    Disciplina "1" --> "0..1" Professor : atribuída a

    Aluno "1" --> "0..*" Inscricao : realiza
    Inscricao "*" --> "1" Aluno : pertence a
    Disciplina "1" --> "0..*" Inscricao : possui
    Inscricao "*" --> "1" Disciplina : vinculada a

    Disciplina --> StatusDisciplina : possui status
    Inscricao --> TipoInscricao : classificada como
    Inscricao --> StatusInscricao : estado atual

    Aluno ..> ServicoNotificacaoCobranca : aciona (UC08)
    CurriculoSemestre ..> Disciplina : processa fechamento (UC09)
```

---

## 3. Descrição Detalhada das Classes e Relacionamentos

### 3.1. Hierarquia de Usuários (UC01, UC04)
- **`Usuario` (Abstrata)**: Classe base para autenticação e dados cadastrais compartilhados (`id`, `nome`, `email`, `login`, `senha`). Possui o método `autenticar()`.
- **`Secretaria`**: Especialização que encapsula ações de administração acadêmica (UC02, UC03, UC04), como cadastrar cursos, ofertar disciplinas no semestre e gerenciar alunos e professores.
- **`Aluno`**: Especialização que realiza inscrições (UC05) e cancelamentos (UC06). Controla a regra de no máximo 4 disciplinas obrigatórias e 2 optativas.
- **`Professor`**: Especialização que leciona disciplinas e pode consultar a lista de alunos matriculados em suas turmas (UC07).

### 3.2. Estrutura Acadêmica (UC02, UC03)
- **`Curso`**: Representa um curso universitário (ex: Engenharia de Software) com seu total de créditos e catálogo de disciplinas vinculadas (composição).
- **`CurriculoSemestre`**: Representa o semestre letivo (ex: "2026/1"). Agrega as disciplinas ofertadas naquele período, controla a janela de abertura/encerramento de matrículas e dispara a rotina de validação e fechamento (UC09).
- **`Disciplina`**: Modela a disciplina/turma ofertada. Contém:
  - Capacidade mínima: 3 alunos.
  - Capacidade máxima: 60 alunos.
  - `status`: Controlado via `StatusDisciplina` (`ABERTA`, `ATIVA`, `CANCELADA`).

### 3.3. Associação e Inscrição (UC05, UC06)
- **`Inscricao`**: Classe associativa entre `Aluno` e `Disciplina`.
  - Registra a data da matrícula, o tipo (`OBRIGATORIA` ou `OPTATIVA`) e o status (`ATIVA` ou `CANCELADA`).
  - O cancelamento altera o status para `CANCELADA` e libera a vaga na disciplina imediatamente.

### 3.4. Regras de Fechamento de Turmas (UC09)
- Ao término do período de matrículas, `CurriculoSemestre.processarFechamentoDisciplinas()` percorre as disciplinas:
  - Se `inscritos >= 3`: status muda para `ATIVA`.
  - Se `inscritos < 3`: status muda para `CANCELADA`.

### 3.5. Integração com Sistema de Cobranças (UC08)
- **`ServicoNotificacaoCobranca` (Interface)** e **`SistemaCobrancaAdapter`**:
  - Implementa o padrão Adapter/Service para desacoplar o sistema acadêmico da API externa do Sistema de Cobranças.
  - É acionado automaticamente na confirmação da matrícula (UC05 `<<include>>` UC08).

