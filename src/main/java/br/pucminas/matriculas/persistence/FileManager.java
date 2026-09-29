package main.java.br.pucminas.matriculas.persistence;

import java.io.*;

import main.java.br.pucminas.matriculas.model.BancoDeDados;

public class FileManager {
    private final File arquivo;

    public FileManager(String caminho) {
        this.arquivo = new File(caminho);
    }

    public void salvar(BancoDeDados banco) {
        File pasta = arquivo.getParentFile();
        if (pasta != null && !pasta.exists()) pasta.mkdirs();

        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(arquivo))) {
            out.writeObject(banco);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar os dados: " + e.getMessage(), e);
        }
    }

    public BancoDeDados carregar() {
        if (!arquivo.exists()) {
            return new BancoDeDados();
        }

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(arquivo))) {
            return (BancoDeDados) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Aviso: não foi possível carregar os dados existentes.");
            System.out.println("Um banco novo será criado.");
            return new BancoDeDados();
        }
    }
}
