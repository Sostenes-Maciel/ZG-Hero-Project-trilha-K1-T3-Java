package br.com.zg.todolist.backend.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;

public class ManipuladorNotificacaoTelegram implements HttpHandler {
    private static final int TAMANHO_MAXIMO_REQUISICAO = 4096;
    private static final String CAMINHO_ENDPOINT = "/api/notificacoes/telegram";
    private static final Set<String> EVENTOS_ACEITOS = Set.of(
            "PRAZO_PROXIMO", "TAREFA_CRIADA", "TAREFA_EDITADA",
            "TAREFA_CONCLUIDA", "TAREFA_ATRASADA", "RELATORIO_SEMANAL"
    );
    // Formato aceito: dd/MM/yyyy (corresponde à constante do GeradorNotificacoesTelegram)
    private static final DateTimeFormatter VALIDACAO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final EnviadorMensagemTelegram enviadorMensagem;
    private final String origemPermitida;
    private final ObjectMapper conversorJson = new ObjectMapper();

    public ManipuladorNotificacaoTelegram(
            EnviadorMensagemTelegram enviadorMensagem,
            String origemPermitida
    ) {
        this.enviadorMensagem = enviadorMensagem;
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
                // Headers CORS de preflight para impedir que o navegador aborta a leitura do recurso remoto
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

            NotificacaoTelegram notificacao;
            try {
                notificacao = interpretarNotificacao(corpoJson);
            } catch (IllegalArgumentException excecao) {
                responder(requisicaoHttp, 400, Map.of("erro", excecao.getMessage()));
                return;
            }

            try {
                enviadorMensagem.enviar(notificacao);
            } catch (InterruptedException excecao) {
                Thread.currentThread().interrupt();
                responder(requisicaoHttp, 503, Map.of("erro", "O envio ao Telegram foi interrompido."));
                return;
            } catch (IOException excecao) {
                responder(requisicaoHttp, 502, Map.of("erro", "Não foi possível enviar a mensagem ao Telegram."));
                return;
            }

            responder(requisicaoHttp, 202, Map.of("mensagem", "Mensagem enviada ao Telegram."));
        }
    }

    private boolean configurarCors(HttpExchange requisicaoHttp) {
        String origem = requisicaoHttp.getRequestHeaders().getFirst("Origin");
        System.out.println("Origem recebida: " + origem);

        if (origem == null) {
            return true;
        }

        if (origemPermitida != null && (origemPermitida.equals(origem) || origemPermitida.equals("*"))) {
            requisicaoHttp.getResponseHeaders().set("Access-Control-Allow-Origin", origem);
            requisicaoHttp.getResponseHeaders().set("Vary", "Origin");
            return true;
        }

        return false;
    }

    private NotificacaoTelegram interpretarNotificacao(JsonNode corpo) {
        if (corpo == null || !corpo.isObject()) {
            throw new IllegalArgumentException("O corpo da requisição não é um objeto JSON válido.");
        }
        if (!EVENTOS_ACEITOS.contains(textoObrigatorio(corpo, "evento", 40))) {
            throw new IllegalArgumentException("O evento '" + textoObrigatorio(corpo, "evento", 40) + "' não é suportado.");
        }
        String tituloTarefa = textoObrigatorio(corpo, "tituloTarefa", 200);
        String dataTermino = textoObrigatorio(corpo, "dataTermino", 10);
        try {
            LocalDate.parse(dataTermino);
        } catch (DateTimeParseException excecao) {
            throw new IllegalArgumentException("A data de término deve usar o formato AAAA-MM-DD.");
        }
        return new NotificacaoTelegram(
                textoObrigatorio(corpo, "evento", 40),
                tituloTarefa,
                null,
                dataTermino
        );
    }

    private String textoObrigatorio(JsonNode corpo, String campo, int tamanhoMaximo) {
        JsonNode valor = corpo.get(campo);
        if (valor == null || !valor.isTextual()
                || valor.textValue().isBlank() || valor.textValue().length() > tamanhoMaximo) {
            throw new IllegalArgumentException("O campo '" + campo + "' é obrigatório ou inválido.");
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
