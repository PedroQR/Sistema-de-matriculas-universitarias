package br.pucminas.matriculas.service;

import br.pucminas.matriculas.model.Aluno;
import br.pucminas.matriculas.model.Disciplina;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Adaptador para integração com a API do Sistema de Cobranças (UC08).
 * Simula a chamada REST/Webhook para o serviço financeiro externo.
 */
public class SistemaCobrancaAdapter implements ServicoNotificacaoCobranca, Serializable {
    private static final long serialVersionUID = 1L;

    private String urlApi;
    private String apiKey;

    public SistemaCobrancaAdapter() {
        this.urlApi = "https://api.financeiro.pucminas.br/v1/cobranca/matriculas";
        this.apiKey = "sec_key_live_9a8b7c6d5e4f3a2b1c";
    }

    public SistemaCobrancaAdapter(String urlApi, String apiKey) {
        this.urlApi = urlApi;
        this.apiKey = apiKey;
    }

    @Override
    public boolean notificarMatricula(Aluno aluno, Disciplina disciplina) {
        if (aluno == null || disciplina == null) {
            return false;
        }

        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        System.out.println();
        System.out.println("┌────────────────────────────────────────────────────────┐");
        System.out.println("│      [UC08] NOTIFICAÇÃO AO SISTEMA DE COBRANÇAS        │");
        System.out.println("├────────────────────────────────────────────────────────┤");
        System.out.println("│ Endpoint : " + urlApi);
        System.out.println("│ Timestamp: " + dataHora);
        System.out.println("│ Aluno    : " + aluno.getNome() + " (Matrícula: " + aluno.getMatricula() + ")");
        System.out.println("│ E-mail   : " + aluno.getEmail());
        System.out.println("│ Cobrança : 1x Disciplina " + disciplina.getCodigo() + " - " + disciplina.getNome());
        System.out.println("│ Créditos : " + disciplina.getCreditos());
        System.out.println("│ Status   : 200 OK - Cobrança registrada e faturada!    │");
        System.out.println("└────────────────────────────────────────────────────────┘");

        return true;
    }

    public String getUrlApi() {
        return urlApi;
    }

    public void setUrlApi(String urlApi) {
        this.urlApi = urlApi;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
