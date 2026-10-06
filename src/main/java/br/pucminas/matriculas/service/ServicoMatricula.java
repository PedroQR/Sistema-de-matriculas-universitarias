package br.pucminas.matriculas.service;

import br.pucminas.matriculas.model.Aluno;
import br.pucminas.matriculas.model.CurriculoSemestre;
import br.pucminas.matriculas.model.Disciplina;
import br.pucminas.matriculas.model.TipoInscricao;

/**
 * Orquestra as operações de matrícula e cancelamento do Aluno (UC05, UC06),
 * disparando automaticamente a notificação de cobrança (UC08).
 */
public class ServicoMatricula {
    private final ServicoNotificacaoCobranca notificacao;

    public ServicoMatricula(ServicoNotificacaoCobranca notificacao) {
        this.notificacao = notificacao;
    }

    /**
     * Realiza a matrícula do aluno em uma disciplina (UC05),
     * acionando o sistema de cobrança (UC08) após a confirmação.
     */
    public boolean matricular(Aluno aluno, Disciplina disciplina,
                              TipoInscricao tipo, CurriculoSemestre curriculo) {
        if (aluno == null || disciplina == null || tipo == null || curriculo == null) {
            return false;
        }

        if (!curriculo.isPeriodoMatriculaAberto()) {
            return false;
        }

        if (!curriculo.possuiDisciplina(disciplina)) {
            return false;
        }

        boolean sucesso = aluno.matricular(disciplina, tipo);

        if (sucesso && notificacao != null) {
            notificacao.notificarMatricula(aluno, disciplina);
        }

        return sucesso;
    }

    /**
     * Cancela a matrícula do aluno na disciplina durante período aberto (UC06).
     * Libera a vaga imediatamente.
     */
    public boolean cancelar(Aluno aluno, Disciplina disciplina,
                            CurriculoSemestre curriculo) {
        if (aluno == null || disciplina == null || curriculo == null) {
            return false;
        }

        if (!curriculo.isPeriodoMatriculaAberto()) {
            return false;
        }

        return aluno.cancelarMatricula(disciplina);
    }
}
