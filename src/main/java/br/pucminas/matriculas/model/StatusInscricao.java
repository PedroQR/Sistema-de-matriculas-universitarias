package br.pucminas.matriculas.model;

import java.io.Serializable;

/**
 * Representa os estados de uma inscrição de aluno em disciplina.
 */
public enum StatusInscricao implements Serializable {
    ATIVA,
    CANCELADA
}
