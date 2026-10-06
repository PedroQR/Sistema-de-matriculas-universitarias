package br.pucminas.matriculas.model;

import java.io.Serializable;

/**
 * Representa os possíveis estados do ciclo de vida de uma disciplina.
 */
public enum StatusDisciplina implements Serializable {
    ABERTA,
    ATIVA,
    CANCELADA
}
