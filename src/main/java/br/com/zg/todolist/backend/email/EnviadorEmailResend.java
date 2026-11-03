package br.com.zg.todolist.backend.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class EnviadorEmailResend implements EnviadorEmail {
    private static final URI ENDERECO_API = URI.create("https://api.resend.com/emails");

    private final HttpClient clienteHttp;
    private final ObjectMapper conversorJson;
    private final String chaveApi;
    private final String remetente;
    private final String destinatario;

    public EnviadorEmailResend(String chaveApi, String remetente, String destinatario) {
        this.chaveApi = chaveApi;
        this.remetente = remetente;
        this.destinatario = destinatario;
        this.clienteHttp = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.conversorJson = new ObjectMapper();
    }

    @Override
    public void enviar(NotificacaoEmail notificacao) throws IOException, InterruptedException {
        String conteudo = criarConteudo(notificacao);
        Map<String, Object> dadosEmail = Map.of(
                "from", remetente,
                "to", List.of(destinatario),
                "subject", "TODO List: " + notificacao.evento().getDescricao(),
                "text", conteudo
        );

        HttpRequest requisicao = HttpRequest.newBuilder(ENDERECO_API)
                .timeout(Duration.ofSeconds(15))
                .header("Authorization", "Bearer " + chaveApi)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(conversorJson.writeValueAsString(dadosEmail)))
                .build();

        HttpResponse<Void> resposta = clienteHttp.send(requisicao, HttpResponse.BodyHandlers.discarding());
        if (resposta.statusCode() < 200 || resposta.statusCode() >= 300) {
            throw new IOException("O provedor de e-mail retornou HTTP " + resposta.statusCode());
        }
    }

    private String criarConteudo(NotificacaoEmail notificacao) {
        StringBuilder conteudo = new StringBuilder()
                .append("Notificação do TODO List: ")
                .append(notificacao.evento().getDescricao())
                .append("\n\nAtividade: ")
                .append(notificacao.tituloTarefa());

        if (notificacao.status() != null) {
            conteudo.append("\nStatus: ").append(notificacao.status());
        }
        if (notificacao.dataTermino() != null) {
            conteudo.append("\nData de término: ").append(notificacao.dataTermino());
        }
        return conteudo.toString();
    }
}
