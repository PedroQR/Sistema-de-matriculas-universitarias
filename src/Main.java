

import main.java.br.pucminas.matriculas.model.Aluno;
import main.java.br.pucminas.matriculas.model.Curso;
import main.java.br.pucminas.matriculas.model.Disciplina;
import main.java.br.pucminas.matriculas.model.TipoInscricao;
import main.java.br.pucminas.matriculas.service.ServicoMatricula;
import main.java.br.pucminas.matriculas.service.SistemaCobrancaAdapter;

public class Main {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println(" SISTEMA DE MATRÍCULAS");
        System.out.println("=================================");

        Curso curso = new Curso(
                1L,
                "Engenharia de Software",
                200
        );

        Disciplina disciplina = new Disciplina(
                "ES101",
                "Projeto de Software",
                4,
                curso
        );

        curso.adicionarDisciplina(disciplina);

        Aluno aluno = new Aluno(
                1L,
                "Caio Santos",
                "caio@email.com",
                "caio",
                "123",
                "20260001"
        );

        CurriculoSemestre curriculo =
                new CurriculoSemestre(
                        1L,
                        "1º",
                        2026
                );

        curriculo.adicionarOfertaDisciplina(
                disciplina
        );

        curriculo.abrirPeriodoMatricula();

        ServicoMatricula servicoMatricula =
                new ServicoMatricula(
                        new SistemaCobrancaAdapter()
                );

        boolean resultado =
                servicoMatricula.matricular(
                        aluno,
                        disciplina,
                        TipoInscricao.OBRIGATORIA,
                        curriculo
                );

        if (resultado) {
            System.out.println(
                    "Matrícula realizada com sucesso!"
            );
        }

        System.out.println(
                "Alunos na disciplina: "
                + disciplina.getQtdInscritosAtivos()
        );
    }
}