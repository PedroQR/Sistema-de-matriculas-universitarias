package main.java.br.pucminas.matriculas.service;
import main.java.br.pucminas.matriculas.model.*;


public class SistemaCobrancaAdapter implements ServicoNotificacaoCobranca {

    @Override
    public boolean notificarMatricula(Aluno aluno, Disciplina disciplina) {
        System.out.println();
        System.out.println("======================================");
        System.out.println(" NOTIFICAÇÃO AO SISTEMA DE COBRANÇAS");
        System.out.println("======================================");
        System.out.println("Aluno: " + aluno.getNome());
        System.out.println("Matrícula: " + aluno.getMatricula());
        System.out.println("Disciplina: " + disciplina.getNome());
        System.out.println("Código: " + disciplina.getCodigo());
        System.out.println("Status: NOTIFICADO");
        System.out.println("======================================");
        return true;
    }
}
