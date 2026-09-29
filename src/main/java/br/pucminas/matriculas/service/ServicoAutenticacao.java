package main.java.br.pucminas.matriculas.service;


import main.java.br.pucminas.matriculas.model.Usuario;

import java.util.List;

public class ServicoAutenticacao {

    public Usuario autenticar(
            List<Usuario> usuarios,
            String login,
            String senha) {

        for (Usuario usuario : usuarios) {

            if (usuario.autenticar(login, senha)) {
                return usuario;
            }
        }

        return null;
    }
}