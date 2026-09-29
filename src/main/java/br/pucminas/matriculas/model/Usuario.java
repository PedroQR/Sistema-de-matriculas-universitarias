package main.java.br.pucminas.matriculas.model;

import java.io.Serializable;

public abstract class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
    private String email;
    private String login;
    private String senha;

    protected Usuario(Long id, String nome, String email, String login, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.login = login;
        this.senha = senha;
    }

    public boolean autenticar(String login, String senha) {
        return this.login.equals(login) && this.senha.equals(senha);
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getLogin() { return login; }
    public String getSenha() { return senha; }

    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setLogin(String login) { this.login = login; }
    public void setSenha(String senha) { this.senha = senha; }
}
