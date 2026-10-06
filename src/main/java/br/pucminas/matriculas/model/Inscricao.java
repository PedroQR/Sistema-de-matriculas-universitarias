package br.pucminas.matriculas.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade associativa que registra a matrícula de um Aluno em uma Disciplina.
 */
public class Inscricao implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private LocalDateTime dataInscricao;
    private TipoInscricao tipo;
    private StatusInscricao status;
    private Aluno aluno;
    private Disciplina disciplina;

    public Inscricao() {
        this.status = StatusInscricao.ATIVA;
        this.dataInscricao = LocalDateTime.now();
    }

    public Inscricao(Long id, Aluno aluno, Disciplina disciplina, TipoInscricao tipo) {
        this.id = id;
        this.aluno = aluno;
        this.disciplina = disciplina;
        this.tipo = tipo;
        this.status = StatusInscricao.ATIVA;
        this.dataInscricao = LocalDateTime.now();
    }

    /**
     * Cancela a inscrição, liberando a vaga.
     */
    public void cancelar() {
        this.status = StatusInscricao.CANCELADA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(LocalDateTime dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    public TipoInscricao getTipo() {
        return tipo;
    }

    public void setTipo(TipoInscricao tipo) {
        this.tipo = tipo;
    }

    public StatusInscricao getStatus() {
        return status;
    }

    public void setStatus(StatusInscricao status) {
        this.status = status;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inscricao inscricao)) return false;
        return Objects.equals(id, inscricao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Inscricao{" +
                "id=" + id +
                ", aluno=" + (aluno != null ? aluno.getNome() : "null") +
                ", disciplina=" + (disciplina != null ? disciplina.getCodigo() : "null") +
                ", tipo=" + tipo +
                ", status=" + status +
                ", data=" + dataInscricao +
                '}';
    }
}
