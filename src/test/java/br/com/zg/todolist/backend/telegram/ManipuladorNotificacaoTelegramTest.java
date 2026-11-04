package br.com.zg.todolist.backend.telegram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ManipuladorNotificacaoTelegramTest {
    private static final String TOKEN_API = "0123456789abcdef0123456789abcdef";
    private static final String ORIGEM_PERMITIDA = "http://localhost:5500";

    private final AtomicReference<NotificacaoTelegram> notificacaoRecebida = new AtomicReference<>();
    private final HttpClient clienteHttp = HttpClient.newHttpClient();
    private HttpServer servidor;
    private ExecutorService executorServico;
    private URI enderecoEndpoint;

    @BeforeEach
    void iniciarServidor() throws Exception {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/api/notificacoes/telegram",
                new ManipuladorNotificacaoTelegram(notificacaoRecebida::set, TOKEN_API, ORIGEM_PERMITIDA));
        executorServico = Executors.newSingleThreadExecutor();
        servidor.setExecutor(executorServico);
        servidor.start();
        enderecoEndpoint = URI.create(
                "http://127.0.0.1:" + servidor.getAddress().getPort() + "/api/notificacoes/telegram");
    }

    @AfterEach
    void pararServidor() {
        servidor.stop(0);
        executorServico.shutdownNow();
    }

    @Test
    void enviaLembreteDePrazoAoTelegram() throws Exception {
        HttpResponse<String> resposta = enviar("Bearer " + TOKEN_API,
                """
                {"evento":"PRAZO_PROXIMO","tituloTarefa":"Entregar certificado","dataTermino":"2026-11-04"}
                """);

        assertEquals(202, resposta.statusCode());
        assertEquals("Entregar certificado", notificacaoRecebida.get().tituloTarefa());
        assertEquals("PRAZO_PROXIMO", notificacaoRecebida.get().evento());
        assertEquals("2026-11-04", notificacaoRecebida.get().dataTermino());
    }

    @Test
    void enviaNovaTarefaAoTelegram() throws Exception {
        HttpResponse<String> resposta = enviar("Bearer " + TOKEN_API,
                """
                {"evento":"TAREFA_CRIADA","tituloTarefa":"Projeto ZG-HERO","dataTermino":"2026-11-05"}
                """);

        assertEquals(202, resposta.statusCode());
        assertEquals("Projeto ZG-HERO", notificacaoRecebida.get().tituloTarefa());
        assertEquals("TAREFA_CRIADA", notificacaoRecebida.get().evento());
    }

    @Test
    void enviaConclusaoAoTelegram() throws Exception {
        HttpResponse<String> resposta = enviar("Bearer " + TOKEN_API,
                """
                {"evento":"TAREFA_CONCLUIDA","tituloTarefa":"Certificado","dataTermino":"2026-11-04"}
                """);

        assertEquals(202, resposta.statusCode());
        assertEquals("Certificado", notificacaoRecebida.get().tituloTarefa());
        assertEquals("TAREFA_CONCLUIDA", notificacaoRecebida.get().evento());
    }

    @Test
    void enviaAtrasadaAoTelegram() throws Exception {
        HttpResponse<String> resposta = enviar("Bearer " + TOKEN_API,
                """
                {"evento":"TAREFA_ATRASADA","tituloTarefa":"Projeto introdutório","dataTermino":"2026-11-03"}
                """);

        assertEquals(202, resposta.statusCode());
        assertEquals("Projeto introdutório", notificacaoRecebida.get().tituloTarefa());
        assertEquals("TAREFA_ATRASADA", notificacaoRecebida.get().evento());
    }

    @Test
    void rejeitaEventoNaoSuportado() throws Exception {
        HttpResponse<String> resposta = enviar("Bearer " + TOKEN_API,
                """
                {"evento":"ERRO_IMPOSSIVEL","tituloTarefa":"Tarefa","dataTermino":"2026-11-04"}
                """);

        assertEquals(400, resposta.statusCode());
        assertNull(notificacaoRecebida.get());
    }

    @Test
    void rejeitaTokenInvalido() throws Exception {
        HttpResponse<String> resposta = enviar("Bearer token-incorreto",
                """
                {"evento":"PRAZO_PROXIMO","tituloTarefa":"Tarefa","dataTermino":"2026-11-04"}
                """);

        assertEquals(401, resposta.statusCode());
        assertNull(notificacaoRecebida.get());
    }

    @Test
    void rejeitaDataFormatadaInvalida() throws Exception {
        HttpResponse<String> resposta = enviar("Bearer " + TOKEN_API,
                """
                {"evento":"PRAZO_PROXIMO","tituloTarefa":"Tarefa","dataTermino":"2026/11/04"}
                """);

        assertEquals(400, resposta.statusCode());
        assertNull(notificacaoRecebida.get());
    }

    private HttpResponse<String> enviar(String autorizacao, String corpo) throws Exception {
        HttpRequest requisicao = HttpRequest.newBuilder(enderecoEndpoint)
                .header("Authorization", autorizacao)
                .header("Origin", ManipuladorNotificacaoTelegramTest.ORIGEM_PERMITIDA)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(corpo))
                .build();
        return clienteHttp.send(requisicao, HttpResponse.BodyHandlers.ofString());
    }
}
