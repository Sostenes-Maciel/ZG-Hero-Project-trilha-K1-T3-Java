package br.com.zg.todolist.backend.email;

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

class ManipuladorNotificacaoEmailTest {
    private static final String TOKEN_API = "0123456789abcdef0123456789abcdef";
    private static final String ORIGEM_PERMITIDA = "http://localhost:5500";

    private final AtomicReference<NotificacaoEmail> notificacaoEnviada = new AtomicReference<>();
    private final HttpClient clienteHttp = HttpClient.newHttpClient();
    private HttpServer servidor;
    private ExecutorService executorServico;
    private URI enderecoEndpoint;

    @BeforeEach
    void iniciarServidor() throws Exception {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/api/notificacoes/email",
                new ManipuladorNotificacaoEmail(notificacaoEnviada::set, TOKEN_API, ORIGEM_PERMITIDA));
        executorServico = Executors.newSingleThreadExecutor();
        servidor.setExecutor(executorServico);
        servidor.start();
        enderecoEndpoint = URI.create(
                "http://127.0.0.1:" + servidor.getAddress().getPort() + "/api/notificacoes/email");
    }

    @AfterEach
    void pararServidor() {
        servidor.stop(0);
        executorServico.shutdownNow();
    }

    @Test
    void enviaNotificacaoComTokenValido() throws Exception {
        HttpResponse<String> resposta = enviar(
                "Bearer " + TOKEN_API,
                ORIGEM_PERMITIDA,
                """
                {"evento":"PRAZO_PROXIMO","tituloTarefa":"Projeto ZG-HERO","status":"DOING","dataTermino":"2026-11-07"}
                """);

        assertEquals(202, resposta.statusCode());
        assertEquals(EventoNotificacao.PRAZO_PROXIMO, notificacaoEnviada.get().evento());
        assertEquals("Projeto ZG-HERO", notificacaoEnviada.get().tituloTarefa());
        assertEquals(ORIGEM_PERMITIDA,
                resposta.headers().firstValue("Access-Control-Allow-Origin").orElseThrow());
    }

    @Test
    void aceitaNovosEventosDeNotificacao() throws Exception {
        for (EventoNotificacao evento : new EventoNotificacao[]{
                EventoNotificacao.TAREFA_EDITADA,
                EventoNotificacao.TAREFA_A_FAZER_EM_TRES_DIAS,
                EventoNotificacao.PRAZO_HOJE
        }) {
            HttpResponse<String> resposta = enviar(
                    "Bearer " + TOKEN_API,
                    ORIGEM_PERMITIDA,
                    """
                    {"evento":"%s","tituloTarefa":"Tarefa de teste","status":"TODO","dataTermino":"2026-11-06"}
                    """.formatted(evento.name()));

            assertEquals(202, resposta.statusCode());
            assertEquals(evento, notificacaoEnviada.get().evento());
        }
    }

    @Test
    void rejeitaTokenInvalidoSemEnviarEmail() throws Exception {
        HttpResponse<String> resposta = enviar("Bearer token incorreto", ORIGEM_PERMITIDA,
                """
                {"evento":"TAREFA_CRIADA","tituloTarefa":"Tarefa"}
                """);

        assertEquals(401, resposta.statusCode());
        assertNull(notificacaoEnviada.get());
    }

    @Test
    void rejeitaOrigemNaoPermitida() throws Exception {
        HttpResponse<String> resposta = enviar("Bearer " + TOKEN_API, "http://site-nao-autorizado.example",
                """
                {"evento":"TAREFA_CRIADA","tituloTarefa":"Tarefa"}
                """);

        assertEquals(403, resposta.statusCode());
        assertNull(notificacaoEnviada.get());
    }

    @Test
    void rejeitaDataInvalida() throws Exception {
        HttpResponse<String> resposta = enviar("Bearer " + TOKEN_API, ORIGEM_PERMITIDA,
                """
                {"evento":"PRAZO_PROXIMO","tituloTarefa":"Tarefa","dataTermino":"amanhã"}
                """);

        assertEquals(400, resposta.statusCode());
        assertNull(notificacaoEnviada.get());
    }

    @Test
    void aceitaRelatorioSemanalComParametrosDasTarefas() throws Exception {
        HttpResponse<String> resposta = enviar("Bearer " + TOKEN_API, ORIGEM_PERMITIDA,
                """
                {"evento":"RELATORIO_SEMANAL","tarefas":[{"titulo":"Projeto ZG-HERO","descricao":"Finalizar tela","status":"DOING","dataTermino":"2026-11-07","prioridade":4,"categoria":"Estudo","alarme":"09:30"}]}
                """);

        assertEquals(202, resposta.statusCode());
        assertEquals(EventoNotificacao.RELATORIO_SEMANAL, notificacaoEnviada.get().evento());
        assertEquals(1, notificacaoEnviada.get().tarefas().size());
        assertEquals("Projeto ZG-HERO", notificacaoEnviada.get().tarefas().getFirst().titulo());
        assertEquals("Finalizar tela", notificacaoEnviada.get().tarefas().getFirst().descricao());
        assertEquals(4, notificacaoEnviada.get().tarefas().getFirst().prioridade());
    }

    @Test
    void rejeitaRelatorioSemTarefas() throws Exception {
        HttpResponse<String> resposta = enviar("Bearer " + TOKEN_API, ORIGEM_PERMITIDA,
                """
                {"evento":"RELATORIO_SEMANAL","tarefas":[]}
                """);

        assertEquals(400, resposta.statusCode());
        assertNull(notificacaoEnviada.get());
    }

    private HttpResponse<String> enviar(String autorizacao, String origem, String corpo) throws Exception {
        HttpRequest requisicao = HttpRequest.newBuilder(enderecoEndpoint)
                .header("Authorization", autorizacao)
                .header("Origin", origem)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(corpo))
                .build();
        return clienteHttp.send(requisicao, HttpResponse.BodyHandlers.ofString());
    }
}
