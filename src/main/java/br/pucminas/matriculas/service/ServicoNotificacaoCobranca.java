package br.pucminas.matriculas.service;

import br.pucminas.matriculas.model.Aluno;
import br.pucminas.matriculas.model.Disciplina;

/**
 * Interface para desacoplamento da notificação ao sistema externo de cobranças (UC08).
 */
public interface ServicoNotificacaoCobranca {
    /**
     * Notifica o serviço financeiro sobre a efetivação de uma matrícula.
     *
     * @param aluno o aluno matriculado
     * @param disciplina a disciplina matriculada
     * @return true se a notificação foi processada com sucesso
     */
    boolean notificarMatricula(Aluno aluno, Disciplina disciplina);
}
