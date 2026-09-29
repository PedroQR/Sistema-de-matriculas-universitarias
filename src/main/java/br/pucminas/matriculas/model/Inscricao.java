package main.java.br.pucminas.matriculas.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Inscricao implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private LocalDateTime dataInscricao;
    private TipoInscricao tipo;
    private StatusInscricao status;
    private Aluno aluno;
    private Disciplina disciplina;

    public Inscricao(Long id, Aluno aluno, Disciplina disciplina, TipoInscricao tipo) {
        this.id = id;
        this.aluno = aluno;
        this.disciplina = disciplina;
        this.tipo = tipo;
        this.status = StatusInscricao.ATIVA;
        this.dataInscricao = LocalDateTime.now();
    }

    public void cancelar() {
        status = StatusInscricao.CANCELADA;
    }

    public Long getId() { return id; }
    public LocalDateTime getDataInscricao() { return dataInscricao; }
    public TipoInscricao getTipo() { return tipo; }
    public StatusInscricao getStatus() { return status; }
    public Aluno getAluno() { return aluno; }
    public Disciplina getDisciplina() { return disciplina; }
}
