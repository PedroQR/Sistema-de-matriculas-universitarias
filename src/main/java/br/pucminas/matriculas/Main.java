package br.pucminas.matriculas;

import br.pucminas.matriculas.model.BancoDeDados;
import br.pucminas.matriculas.persistence.FileManager;
import br.pucminas.matriculas.ui.MenuPrincipal;

/**
 * Ponto de entrada da aplicação Sistema de Matrículas Universitárias.
 */
public class Main {
    public static void main(String[] args) {
        FileManager fileManager = new FileManager("data/sistema.dat");
        BancoDeDados banco = fileManager.carregar();

        MenuPrincipal menu = new MenuPrincipal(banco, fileManager);
        menu.iniciar();
    }
}
