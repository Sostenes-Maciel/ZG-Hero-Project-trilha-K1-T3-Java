package br.com.zg.todolist.backend.telegram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnviadorTelegramBotTest {
    private final AtomicReference<String> caminhoRecebido = new AtomicReference<>();
    private final AtomicReference<String> corpoRecebido = new AtomicReference<>();
    private HttpServer servidor;
    private ExecutorService executorServico;
    private URI enderecoApi;

    @BeforeEach
    void iniciarServidorTelegramFalso() throws Exception {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/", requisicao -> {
            caminhoRecebido.set(requisicao.getRequestURI().getPath());
            corpoRecebido.set(new String(requisicao.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] resposta = "{\"ok\":true}".getBytes(StandardCharsets.UTF_8);
            requisicao.getResponseHeaders().set("Content-Type", "application/json");
            requisicao.sendResponseHeaders(200, resposta.length);
            requisicao.getResponseBody().write(resposta);
            requisicao.close();
        });
        executorServico = Executors.newSingleThreadExecutor();
        servidor.setExecutor(executorServico);
        servidor.start();
        enderecoApi = URI.create("http://127.0.0.1:" + servidor.getAddress().getPort());
    }

    @AfterEach
    void pararServidorTelegramFalso() {
        servidor.stop(0);
        executorServico.shutdownNow();
    }

    @Test
    void enviaMensagemAoEndpointSendMessage() throws Exception {
        EnviadorTelegramBot enviador = new EnviadorTelegramBot(
                enderecoApi, "token-de-teste", "123456", HttpClient.newHttpClient());

        enviador.enviar(new NotificacaoTelegram(
                "PRAZO_PROXIMO", "Entregar certificado", null, "2026-11-04"));

        assertEquals("/bottoken-de-teste/sendMessage", caminhoRecebido.get());
        assertTrue(corpoRecebido.get().contains("\"chat_id\":\"123456\""));
        assertTrue(corpoRecebido.get().contains("Entregar certificado"));
        assertTrue(corpoRecebido.get().contains("04/11/2026"));
    }

    @Test
    void enviaMensagemNovaTarefaAoTelegram() throws Exception {
        EnviadorTelegramBot enviador = new EnviadorTelegramBot(
                enderecoApi, "token-de-teste", "123456", HttpClient.newHttpClient());

        enviador.enviar(new NotificacaoTelegram(
                "TAREFA_CRIADA", "Projeto ZG-HERO", null, "2026-11-05"));

        assertTrue(corpoRecebido.get().contains("Projeto ZG-HERO"));
        assertTrue(corpoRecebido.get().contains("criada com sucesso"));
    }

    @Test
    void enviaMensagemConclusaoAoTelegram() throws Exception {
        EnviadorTelegramBot enviador = new EnviadorTelegramBot(
                enderecoApi, "token-de-teste", "123456", HttpClient.newHttpClient());

        enviador.enviar(new NotificacaoTelegram(
                "TAREFA_CONCLUIDA", "Certificado de curso", null, "2026-11-04"));

        assertTrue(corpoRecebido.get().contains("Certificado de curso"));
        assertTrue(corpoRecebido.get().contains("concluída com sucesso"));
    }

    @Test
    void enviaMensagemAtrasadaAoTelegram() throws Exception {
        EnviadorTelegramBot enviador = new EnviadorTelegramBot(
                enderecoApi, "token-de-teste", "123456", HttpClient.newHttpClient());

        enviador.enviar(new NotificacaoTelegram(
                "TAREFA_ATRASADA", "Projeto introdutório", null, "2026-11-03"));

        assertTrue(corpoRecebido.get().contains("Projeto introdutório"));
        assertTrue(corpoRecebido.get().contains("venceu hoje"));
    }

    @Test
    void enviaMensagemRelatorioSemanal() throws Exception {
        EnviadorTelegramBot enviador = new EnviadorTelegramBot(
                enderecoApi, "token-de-teste", "123456", HttpClient.newHttpClient());

        enviador.enviar(new NotificacaoTelegram(
                "RELATORIO_SEMANAL", "Relatório Semanal", "Total de tarefas: 3", "2026-11-03"));

        assertTrue(corpoRecebido.get().contains("📊 Relatório Semanal"));
        assertTrue(corpoRecebido.get().contains("Total de tarefas: 3"));
    }
}
