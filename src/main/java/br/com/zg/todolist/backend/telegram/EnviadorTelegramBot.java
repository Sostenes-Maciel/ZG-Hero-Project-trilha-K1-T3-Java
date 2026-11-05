package br.com.zg.todolist.backend.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class EnviadorTelegramBot implements EnviadorMensagemTelegram {
    private final URI enderecoApi;
    private final String tokenBot;
    private final String identificadorConversa;
    private final HttpClient clienteHttp;
    private final ObjectMapper conversorJson = new ObjectMapper();

    public EnviadorTelegramBot(String tokenBot, String identificadorConversa) {
        this(URI.create("https://api.telegram.org"), tokenBot, identificadorConversa, HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build());
    }

    EnviadorTelegramBot(
            URI enderecoApi,
            String tokenBot,
            String identificadorConversa,
            HttpClient clienteHttp
    ) {
        if (tokenBot == null || tokenBot.isBlank()) {
            throw new IllegalArgumentException("O token do bot do Telegram é obrigatório.");
        }
        if (identificadorConversa == null || identificadorConversa.isBlank()) {
            throw new IllegalArgumentException("O identificador da conversa do Telegram é obrigatório.");
        }
        this.enderecoApi = enderecoApi;
        this.tokenBot = tokenBot;
        this.identificadorConversa = identificadorConversa;
        this.clienteHttp = clienteHttp;
    }

    @Override
    public void enviar(NotificacaoTelegram notificacao) throws IOException, InterruptedException {
        Map<String, String> dadosMensagem = Map.of(
                "chat_id", identificadorConversa,
                "text", formatarMensagem(notificacao)
        );
        URI enderecoEnvio = enderecoApi.resolve("/bot" + tokenBot + "/sendMessage");
        HttpRequest requisicao = HttpRequest.newBuilder(enderecoEnvio)
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(conversorJson.writeValueAsString(dadosMensagem)))
                .build();

        HttpResponse<String> resposta = clienteHttp.send(requisicao, HttpResponse.BodyHandlers.ofString());
        if (resposta.statusCode() < 200 || resposta.statusCode() >= 300) {
            throw new IOException("A API do Telegram retornou HTTP " + resposta.statusCode() + ".");
        }
        JsonNode corpoResposta = conversorJson.readTree(resposta.body());
        if (!corpoResposta.path("ok").asBoolean(false)) {
            throw new IOException("A API do Telegram não confirmou o envio da mensagem.");
        }
    }

    private String formatarMensagem(NotificacaoTelegram notificacao) {
        return switch (notificacao.evento()) {
            case "PRAZO_PROXIMO" -> enviaLembreteProximo(notificacao);
            case "TAREFA_CRIADA" -> enviaNovaTarefa(notificacao);
            case "TAREFA_EDITADA" -> enviaEditada(notificacao);
            case "TAREFA_CONCLUIDA" -> enviaConcluida(notificacao);
            case "TAREFA_ATRASADA" -> enviaAtrasada(notificacao);
            case "RELATORIO_SEMANAL" -> "📊 Relatório Semanal\n\n" + notificacao.descricao();
            default -> "⚠️ Evento desconhecido.\n\n" + notificacao.tituloTarefa();
        };
    }

    private String enviaLembreteProximo(NotificacaoTelegram notificacao) {
        return "⏰ Lembrete do TODO List\n\n"
                + "A atividade \"" + notificacao.tituloTarefa() + "\" vence amanhã, "
                + dataFormatada(notificacao.dataTermino()) + ".";
    }

    private String enviaNovaTarefa(NotificacaoTelegram notificacao) {
        return "📋 Nova Tarefa criada!\n\n"
                + "\"" + notificacao.tituloTarefa() + "\" foi criada com sucesso!";
    }

    private String enviaConcluida(NotificacaoTelegram notificacao) {
        return "✅ Tarefa concluída!\n\n"
                + "\"" + notificacao.tituloTarefa() + "\" foi concluída com sucesso!";
    }

    private String enviaAtrasada(NotificacaoTelegram notificacao) {
        return "⚠️ ATENÇÃO!\n\n"
                + "\"" + notificacao.tituloTarefa() + "\" venceu hoje! Já deveria estar concluída.";
    }

    private String dataFormatada(String dataTermino) {
        try {
            LocalDate.parse(dataTermino).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return dataTermino;
        }
        return LocalDate.parse(dataTermino).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    private String enviaEditada(NotificacaoTelegram notificacao) {
        return "📝 Tarefa editada!\n\n"
                + "\"" + notificacao.tituloTarefa() + "\" foi atualizada com sucesso.";
    }
}
