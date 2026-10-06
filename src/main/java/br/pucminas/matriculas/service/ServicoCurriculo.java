package br.pucminas.matriculas.service;

import br.pucminas.matriculas.model.CurriculoSemestre;
import br.pucminas.matriculas.model.Disciplina;

/**
 * Serviço que gerencia o ciclo de vida do currículo semestral e o fechamento de turmas (UC02, UC09).
 */
public class ServicoCurriculo {

    public void abrirPeriodo(CurriculoSemestre curriculo) {
        if (curriculo == null) {
            throw new IllegalArgumentException("Currículo não pode ser nulo.");
        }
        curriculo.abrirPeriodoMatricula();
    }

    public void encerrarPeriodo(CurriculoSemestre curriculo) {
        if (curriculo == null) {
            throw new IllegalArgumentException("Currículo não pode ser nulo.");
        }
        curriculo.encerrarPeriodoMatricula();
    }

    /**
     * Dispara o processamento de validação e fechamento de turmas (UC09).
     */
    public void processarFechamento(CurriculoSemestre curriculo) {
        if (curriculo == null) {
            throw new IllegalArgumentException("Currículo não pode ser nulo.");
        }
        curriculo.processarFechamentoDisciplinas();
    }

    public void adicionarDisciplina(CurriculoSemestre curriculo, Disciplina disciplina) {
        if (curriculo == null || disciplina == null) {
            throw new IllegalArgumentException("Parâmetros inválidos para adicionar disciplina ao currículo.");
        }
        curriculo.adicionarOfertaDisciplina(disciplina);
    }
}
