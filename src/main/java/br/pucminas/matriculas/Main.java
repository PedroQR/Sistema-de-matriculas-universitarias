package main.java.br.pucminas.matriculas;
import main.java.br.pucminas.matriculas.model.*;
import main.java.br.pucminas.matriculas.persistence.FileManager;

public class Main {
    public static void main(String[] args) {
        FileManager fileManager = new FileManager("data/sistema.dat");
        BancoDeDados banco = fileManager.carregar();

        MenuPrincipal menu = new MenuPrincipal(banco, fileManager);
        menu.iniciar();
    }
}
