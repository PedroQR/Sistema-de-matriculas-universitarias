package main.java.br.pucminas.matriculas.service;
import main.java.br.pucminas.matriculas.model.*;
public class ServicoCurriculo {

    public void gerarCurriculo(CurriculoSemestre curriculo) {
        if (curriculo == null) throw new IllegalArgumentException("Currículo inválido.");
    }

    public void abrirPeriodo(CurriculoSemestre curriculo) {
        curriculo.abrirPeriodoMatricula();
    }

    public void encerrarPeriodo(CurriculoSemestre curriculo) {
        curriculo.encerrarPeriodoMatricula();
    }

    public void processarFechamento(CurriculoSemestre curriculo) {
        curriculo.processarFechamentoDisciplinas();
    }

    public void adicionarDisciplina(CurriculoSemestre curriculo, Disciplina disciplina) {
        curriculo.adicionarOfertaDisciplina(disciplina);
    }
}
