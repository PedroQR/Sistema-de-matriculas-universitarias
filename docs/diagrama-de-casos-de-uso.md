# Projeto Comportamental: Diagrama de Casos de Uso (Versão Mermaid)

## 1. Visão Geral

Este documento apresenta a versão aprimorada e nativa em **Mermaid** do **Diagrama de Casos de Uso** do Sistema de Matrículas Universitárias, complementando o diagrama original em imagem e alinhando-se aos requisitos descritos no [README.md](../README.md).

### Principais Melhorias Implementadas:
- **Renderização Nativa e Versionável**: Totalmente compatível com Markdown e visualizadores do GitHub.
- **Herança de Atores**: Modelação explícita de `Secretaria`, `Aluno` e `Professor` como especializações do ator base `Usuario`, permitindo que todos herdem a capacidade de efetuar login (`UC01`).
- **Gatilho Temporal (UC09)**: Representação do término do período de matrículas como um evento temporal/agendado que dispara o fechamento de disciplinas.
- **Notas de Regras de Negócio**: Inclusão de notas visuais ligadas diretamente a `UC05` (limites de 4 obrigatórias + 2 optativas e teto de 60 alunos) e `UC09` (mínimo de 3 alunos para ativação de disciplina).

---

## 2. Diagrama de Casos de Uso em Mermaid

```mermaid
flowchart LR
    %% Atores Primários
    subgraph Atores["Atores Primários"]
        Usuario["👤 Usuário<br/>«abstrato»"]
        Secretaria["🏛️ Secretaria"]
        Aluno["🎓 Aluno"]
        Professor["👨‍🏫 Professor"]
    end

    %% Generalização dos Atores
    Secretaria -->|especializa| Usuario
    Aluno -->|especializa| Usuario
    Professor -->|especializa| Usuario

    %% Fronteira do Sistema
    subgraph Sistema[" Sistema de Matrículas Universitárias "]
        UC01(["UC01: Efetuar Login"])
        UC02(["UC02: Gerar Currículo do Semestre"])
        UC03(["UC03: Manter Cursos e Disciplinas"])
        UC04(["UC04: Manter Professores e Alunos"])
        UC05(["UC05: Matricular em Disciplinas"])
        UC06(["UC06: Cancelar Matrícula"])
        UC07(["UC07: Consultar Alunos Matriculados"])
        UC08(["UC08: Notificar Sistema de Cobrança"])
        UC09(["UC09: Processar Fechamento de Disciplinas"])
    end

    %% Atores Secundários e Gatilhos
    subgraph AtoresSecundarios["Atores Secundários / Eventos"]
        SistemaCobranca["🏦 Sistema de Cobranças<br/>«sistema externo»"]
        Temporizador["⏰ Fim do Prazo de Matrícula<br/>«evento temporal»"]
    end

    %% Associações Usuário
    Usuario --- UC01

    %% Associações Secretaria
    Secretaria --- UC02
    Secretaria --- UC03
    Secretaria --- UC04

    %% Associações Aluno
    Aluno --- UC05
    Aluno --- UC06

    %% Associações Professor
    Professor --- UC07

    %% Relacionamentos de Casos de Uso
    UC05 -. "<<include>>" .-> UC08
    UC08 --> SistemaCobranca

    Temporizador --> UC09

    %% Regras de Negócio e Notas
    NotaUC09["<b>Regra de Negócio (UC09):</b><br/>• Inscritos &lt; 3: Cancela disciplina<br/>• 3 &le; Inscritos &le; 60: Ativa disciplina"]
    NotaUC05["<b>Regra de Negócio (UC05):</b><br/>• Máx. 4 obrigatórias<br/>• Máx. 2 optativas<br/>• Máx. 60 alunos/turma"]

    NotaUC09 -.-> UC09
    NotaUC05 -.-> UC05

    %% Estilos
    classDef actorStyle fill:#eef2ff,stroke:#4338ca,stroke-width:2px,color:#1e1b4b;
    classDef usecaseStyle fill:#f0fdf4,stroke:#15803d,stroke-width:2px,color:#14532d;
    classDef externalStyle fill:#fef3c7,stroke:#b45309,stroke-width:2px,color:#78350f;
    classDef noteStyle fill:#fffbeb,stroke:#d97706,stroke-dasharray: 4 4,color:#78350f,font-size:11px;

    class Usuario,Secretaria,Aluno,Professor actorStyle;
    class UC01,UC02,UC03,UC04,UC05,UC06,UC07,UC08,UC09 usecaseStyle;
    class SistemaCobranca,Temporizador externalStyle;
    class NotaUC09,NotaUC05 noteStyle;
```

---

## 3. Matriz de Rastreabilidade (Atores x Casos de Uso)

| Caso de Uso | Ator(es) Principal(is) | Tipo de Relacionamento | Descrição / Regra |
| :--- | :--- | :--- | :--- |
| **UC01: Efetuar Login** | `Usuário` (`Secretaria`, `Aluno`, `Professor`) | Associação direta | Autenticação por credenciais (login e senha). |
| **UC02: Gerar Currículo do Semestre** | `Secretaria` | Associação direta | Criação do período letivo e oferta de disciplinas. |
| **UC03: Manter Cursos e Disciplinas** | `Secretaria` | Associação direta | Cadastro, edição e remoção de cursos e disciplinas. |
| **UC04: Manter Professores e Alunos** | `Secretaria` | Associação direta | Cadastro e gerenciamento de perfis de docentes e discentes. |
| **UC05: Matricular em Disciplinas** | `Aluno` | Associação direta | Inscrição em até 4 disciplinas obrigatórias e 2 optativas (teto de 60 alunos). |
| **UC06: Cancelar Matrícula** | `Aluno` | Associação direta | Cancelamento durante o período de alterações; libera vaga imediatamente. |
| **UC07: Consultar Alunos Matriculados** | `Professor` | Associação direta | Visualização exclusiva de turmas sob responsabilidade do docente. |
| **UC08: Notificar Sistema de Cobrança** | `Sistema de Cobranças` (secundário) | `<<include>>` de UC05 | Disparo automático à API financeira após confirmação de matrícula. |
| **UC09: Processar Fechamento de Disciplinas** | `Fim do Prazo de Matrícula` (temporal) | Disparo por evento | Ativação para $\ge 3$ inscritos e cancelamento para $< 3$ inscritos. |

