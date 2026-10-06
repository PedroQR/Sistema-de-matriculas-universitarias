package br.pucminas.matriculas.persistence;

import br.pucminas.matriculas.model.Aluno;
import br.pucminas.matriculas.model.BancoDeDados;
import br.pucminas.matriculas.model.CurriculoSemestre;
import br.pucminas.matriculas.model.Curso;
import br.pucminas.matriculas.model.Disciplina;
import br.pucminas.matriculas.model.Inscricao;
import br.pucminas.matriculas.model.Professor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

/**
 * Responsável pelo gerenciamento de persistência de dados em arquivos (armazenamento local).
 */
public class FileManager {
    private final File arquivo;

    public FileManager(String caminho) {
        this.arquivo = new File(caminho);
    }

    /**
     * Salva o estado atual do banco de dados em arquivo binário serializado
     * e exporta espelho legível em arquivo de texto.
     */
    public void salvar(BancoDeDados banco) {
        File pasta = arquivo.getParentFile();
        if (pasta != null && !pasta.exists()) {
            pasta.mkdirs();
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(arquivo))) {
            out.writeObject(banco);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao persistir dados no arquivo: " + e.getMessage(), e);
        }

        // Exporta também uma versão legível para conferência de persistência
        exportarEspelhoTexto(banco, new File(pasta, "relatorio_sistema.txt"));
    }

    /**
     * Carrega os dados serializados do arquivo. Retorna um novo banco se o arquivo não existir.
     */
    public BancoDeDados carregar() {
        if (!arquivo.exists()) {
            return new BancoDeDados();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (BancoDeDados) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Aviso: arquivo de dados prévio incompatível ou corrompido (" + e.getMessage() + ").");
            System.out.println("Inicializando novo repositório limpo...");
            return new BancoDeDados();
        }
    }

    /**
     * Gera um espelho textual dos dados salvos no sistema.
     */
    private void exportarEspelhoTexto(BancoDeDados banco, File destino) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(destino))) {
            writer.println("===============================================================");
            writer.println("        RELATÓRIO DO SISTEMA DE MATRÍCULAS (ESPELHO)");
            writer.println("===============================================================");
            writer.println();

            writer.println("--- CURSOS CADASTRADOS (" + banco.getCursos().size() + ") ---");
            for (Curso c : banco.getCursos()) {
                writer.printf("ID: %d | Nome: %s | Total Créditos: %d%n",
                        c.getId(), c.getNome(), c.getTotalCreditos());
            }
            writer.println();

            writer.println("--- DISCIPLINAS CADASTRADAS (" + banco.getDisciplinas().size() + ") ---");
            for (Disciplina d : banco.getDisciplinas()) {
                String profNome = d.getProfessor() != null ? d.getProfessor().getNome() : "Sem professor";
                writer.printf("Código: %s | Nome: %s | Créditos: %d | Status: %s | Inscritos: %d/%d | Docente: %s%n",
                        d.getCodigo(), d.getNome(), d.getCreditos(), d.getStatus(),
                        d.getQtdInscritosAtivos(), Disciplina.CAPACIDADE_MAXIMA, profNome);
            }
            writer.println();

            writer.println("--- ALUNOS CADASTRADOS (" + banco.getAlunos().size() + ") ---");
            for (Aluno a : banco.getAlunos()) {
                writer.printf("Matrícula: %s | Nome: %s | E-mail: %s | Obrigatórias: %d/4 | Optativas: %d/2%n",
                        a.getMatricula(), a.getNome(), a.getEmail(),
                        a.getQtdObrigatoriasAtivas(), a.getQtdOptativasAtivas());
                for (Inscricao i : a.getInscricoesAtivas()) {
                    writer.printf("   -> %s (%s) [%s em %s]%n",
                            i.getDisciplina().getNome(), i.getDisciplina().getCodigo(),
                            i.getTipo(), i.getDataInscricao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                }
            }
            writer.println();

            writer.println("--- PROFESSORES CADASTRADOS (" + banco.getProfessores().size() + ") ---");
            for (Professor p : banco.getProfessores()) {
                writer.printf("Registro: %s | Nome: %s | E-mail: %s | Qtd Disciplinas: %d%n",
                        p.getRegistroDocente(), p.getNome(), p.getEmail(), p.getDisciplinasLecionadas().size());
            }
            writer.println();

            CurriculoSemestre curr = banco.getCurriculos().isEmpty() ? null : banco.getCurriculos().get(banco.getCurriculos().size() - 1);
            if (curr != null) {
                writer.printf("--- CURRÍCULO ATUAL: %s/%d (Período: %s) ---%n",
                        curr.getSemestre(), curr.getAno(), curr.isPeriodoMatriculaAberto() ? "ABERTO" : "FECHADO");
            }
        } catch (IOException ignored) {
            // Se falhar a gravação do log em texto, não interrompe o fluxo principal
        }
    }
}
