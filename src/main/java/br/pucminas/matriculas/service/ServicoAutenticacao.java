package br.pucminas.matriculas.service;

import br.pucminas.matriculas.model.Usuario;

import java.util.List;

/**
 * Serviço responsável por autenticar usuários do sistema (UC01 / US01).
 */
public class ServicoAutenticacao {

    /**
     * Autentica o usuário no sistema a partir de uma lista de usuários cadastrados.
     *
     * @param usuarios lista de usuários
     * @param login identificador
     * @param senha senha
     * @return Usuario autenticado se credenciais forem corretas, ou null se inválidas.
     */
    public Usuario autenticar(List<Usuario> usuarios, String login, String senha) {
        if (usuarios == null || login == null || senha == null) {
            return null;
        }

        for (Usuario usuario : usuarios) {
            if (usuario.autenticar(login, senha)) {
                return usuario;
            }
        }

        return null;
    }
}