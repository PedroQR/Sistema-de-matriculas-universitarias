package br.pucminas.matriculas.model;

import java.io.Serializable;

/**
 * Classificação da inscrição solicitada pelo aluno:
 * Obrigatória (máximo 4 ativas) ou Optativa (máximo 2 ativas).
 */
public enum TipoInscricao implements Serializable {
    OBRIGATORIA,
    OPTATIVA
}
