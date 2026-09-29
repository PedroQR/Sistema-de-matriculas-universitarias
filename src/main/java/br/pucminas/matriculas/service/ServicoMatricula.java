package main.java.br.pucminas.matriculas.service;
import main.java.br.pucminas.matriculas.model.*;

public class ServicoMatricula {
    private final ServicoNotificacaoCobranca notificacao;

    public ServicoMatricula(ServicoNotificacaoCobranca notificacao) {
        this.notificacao = notificacao;
    }

    public boolean matricular(Aluno aluno, Disciplina disciplina,
                              TipoInscricao tipo, CurriculoSemestre curriculo) {
        if (aluno == null || disciplina == null || tipo == null || curriculo == null) {
            return false;
        }

        if (!curriculo.isPeriodoMatriculaAberto()) return false;
        if (!curriculo.possuiDisciplina(disciplina)) return false;

        boolean sucesso = aluno.matricular(disciplina, tipo);

        if (sucesso) {
            notificacao.notificarMatricula(aluno, disciplina);
        }

        return sucesso;
    }

    public boolean cancelar(Aluno aluno, Disciplina disciplina,
                            CurriculoSemestre curriculo) {
        if (aluno == null || disciplina == null || curriculo == null) return false;
        if (!curriculo.isPeriodoMatriculaAberto()) return false;

        return aluno.cancelarMatricula(disciplina);
    }
}
