package main.java.br.pucminas.matriculas.service;
import main.java.br.pucminas.matriculas.model.*;

public interface ServicoNotificacaoCobranca {
    boolean notificarMatricula(Aluno aluno, Disciplina disciplina);
}
