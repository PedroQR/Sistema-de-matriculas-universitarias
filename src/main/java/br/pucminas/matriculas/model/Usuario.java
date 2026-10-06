package br.pucminas.matriculas.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Classe abstrata base para todos os usuários do sistema (UC01).
 * Implementa autenticação por login e senha.
 */
public abstract class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
    private String email;
    private String login;
    private String senha;

    public Usuario() {
    }

    public Usuario(Long id, String nome, String email, String login, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.login = login;
        this.senha = senha;
    }

    /**
     * Autentica o usuário comparando login e senha.
     *
     * @param login identificador de acesso
     * @param senha credencial de acesso
     * @return true se as credenciais forem válidas, false caso contrário
     */
    public boolean autenticar(String login, String senha) {
        if (this.login == null || this.senha == null) {
            return false;
        }
        return this.login.equals(login) && this.senha.equals(senha);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                '}';
    }
}
