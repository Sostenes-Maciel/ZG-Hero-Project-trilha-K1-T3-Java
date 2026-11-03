package br.com.zg.todolist.backend.email;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

public class ManipuladorNotificacaoEmail implements HttpHandler {
    private static final int TAMANHO_MAXIMO_REQUISICAO = 16_384;
    private static final String CAMINHO_ENDPOINT = "/api/notificacoes/email";

    private final EnviadorEmail enviadorEmail;
    private final byte[] tokenEsperado;
    private final String origemPermitida;
    private final ObjectMapper conversorJson = new ObjectMapper();

    public ManipuladorNotificacaoEmail(EnviadorEmail enviadorEmail, String tokenApi, String origemPermitida) {
        if (tokenApi == null || tokenApi.length() < 32) {
            throw new IllegalArgumentException("O token da API de e-mail deve ter pelo menos 32 caracteres.");
        }
        this.enviadorEmail = enviadorEmail;
        this.tokenEsperado = tokenApi.getBytes(StandardCharsets.UTF_8);
        this.origemPermitida = origemPermitida;
    }

    @Override
    public void handle(HttpExchange requisicaoHttp) throws IOException {
        try (requisicaoHttp) {
            if (!configurarCors(requisicaoHttp)) {
                responder(requisicaoHttp, 403, Map.of("erro", "Origem não permitida."));
                return;
            }

            if ("OPTIONS".equals(requisicaoHttp.getRequestMethod())) {
                requisicaoHttp.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
                requisicaoHttp.getResponseHeaders().set("Access-Control-Allow-Headers", "Authorization, Content-Type");
                requisicaoHttp.sendResponseHeaders(204, -1);
                return;
            }

            if (!CAMINHO_ENDPOINT.equals(requisicaoHttp.getRequestURI().getPath())) {
                responder(requisicaoHttp, 404, Map.of("erro", "Endpoint não encontrado."));
                return;
            }
            if (!"POST".equals(requisicaoHttp.getRequestMethod())) {
                requisicaoHttp.getResponseHeaders().set("Allow", "POST, OPTIONS");
                responder(requisicaoHttp, 405, Map.of("erro", "Método não permitido."));
                return;
            }
            if (!autenticado(requisicaoHttp)) {
                responder(requisicaoHttp, 401, Map.of("erro", "Token de API inválido ou ausente."));
                return;
            }
            String tipoConteudo = requisicaoHttp.getRequestHeaders().getFirst("Content-Type");
            if (tipoConteudo == null || !tipoConteudo.matches("(?i)application/json(?:\\s*;.*)?")) {
                responder(requisicaoHttp, 415, Map.of("erro", "Envie o conteúdo como application/json."));
                return;
            }

            byte[] corpoRequisicao = requisicaoHttp.getRequestBody().readNBytes(TAMANHO_MAXIMO_REQUISICAO + 1);
            if (corpoRequisicao.length > TAMANHO_MAXIMO_REQUISICAO) {
                responder(requisicaoHttp, 413, Map.of("erro", "A requisição excede o limite permitido."));
                return;
            }

            JsonNode corpoJson;
            try {
                corpoJson = conversorJson.readTree(corpoRequisicao);
            } catch (IOException excecao) {
                responder(requisicaoHttp, 400, Map.of("erro", "O corpo da requisição não contém JSON válido."));
                return;
            }

            NotificacaoEmail notificacao;
            try {
                notificacao = interpretarNotificacao(corpoJson);
            } catch (IllegalArgumentException excecao) {
                responder(requisicaoHttp, 400, Map.of("erro", excecao.getMessage()));
                return;
            }

            try {
                enviadorEmail.enviar(notificacao);
            } catch (InterruptedException excecao) {
                Thread.currentThread().interrupt();
                responder(requisicaoHttp, 503, Map.of("erro", "O envio do e-mail foi interrompido."));
                return;
            } catch (IOException excecao) {
                responder(requisicaoHttp, 502, Map.of("erro", "Não foi possível enviar o e-mail."));
                return;
            }

            responder(requisicaoHttp, 202, Map.of("mensagem", "Notificação enviada."));
        }
    }

    private boolean configurarCors(HttpExchange requisicaoHttp) {
        String origem = requisicaoHttp.getRequestHeaders().getFirst("Origin");
        if (origem == null) {
            return true;
        }
        if (origemPermitida == null || !origemPermitida.equals(origem)) {
            return false;
        }
        requisicaoHttp.getResponseHeaders().set("Access-Control-Allow-Origin", origemPermitida);
        requisicaoHttp.getResponseHeaders().set("Vary", "Origin");
        return true;
    }

    private boolean autenticado(HttpExchange requisicaoHttp) {
        String autorizacao = requisicaoHttp.getRequestHeaders().getFirst("Authorization");
        if (autorizacao == null || !autorizacao.startsWith("Bearer ")) {
            return false;
        }
        byte[] tokenRecebido = autorizacao.substring("Bearer ".length()).getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(tokenEsperado, tokenRecebido);
    }

    private NotificacaoEmail interpretarNotificacao(JsonNode corpo) {
        if (corpo == null || !corpo.isObject()) {
            throw new IllegalArgumentException("O corpo da requisição deve ser um objeto JSON.");
        }

        String valorEvento = textoObrigatorio(corpo, "evento", 40);
        EventoNotificacao evento;
        try {
            evento = EventoNotificacao.valueOf(valorEvento);
        } catch (IllegalArgumentException excecao) {
            throw new IllegalArgumentException("Tipo de notificação inválido.");
        }

        String tituloTarefa = textoObrigatorio(corpo, "tituloTarefa", 200);
        String status = textoOpcional(corpo, "status", 30);
        String dataTermino = textoOpcional(corpo, "dataTermino", 10);
        if (dataTermino != null) {
            try {
                LocalDate.parse(dataTermino);
            } catch (DateTimeParseException excecao) {
                throw new IllegalArgumentException("A data de término deve usar o formato AAAA-MM-DD.");
            }
        }
        return new NotificacaoEmail(evento, tituloTarefa, status, dataTermino);
    }

    private String textoObrigatorio(JsonNode corpo, String campo, int tamanhoMaximo) {
        String valor = textoOpcional(corpo, campo, tamanhoMaximo);
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O campo '" + campo + "' é obrigatório.");
        }
        return valor;
    }

    private String textoOpcional(JsonNode corpo, String campo, int tamanhoMaximo) {
        JsonNode valor = corpo.get(campo);
        if (valor == null || valor.isNull()) {
            return null;
        }
        if (!valor.isTextual() || valor.textValue().length() > tamanhoMaximo) {
            throw new IllegalArgumentException("O campo '" + campo + "' tem formato ou tamanho inválido.");
        }
        return valor.textValue();
    }

    private void responder(HttpExchange requisicaoHttp, int codigoStatus, Map<String, String> corpo)
            throws IOException {
        byte[] resposta = conversorJson.writeValueAsBytes(corpo);
        requisicaoHttp.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        requisicaoHttp.sendResponseHeaders(codigoStatus, resposta.length);
        requisicaoHttp.getResponseBody().write(resposta);
    }
}
