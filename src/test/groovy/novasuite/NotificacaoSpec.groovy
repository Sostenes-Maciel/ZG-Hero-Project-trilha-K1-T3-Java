package novasuite

import br.com.zg.todolist.backend.email.EventoNotificacao
import br.com.zg.todolist.backend.email.EnviadorEmail
import br.com.zg.todolist.backend.email.ManipuladorNotificacaoEmail
import br.com.zg.todolist.backend.email.NotificacaoEmail
import br.com.zg.todolist.backend.factory.CategoriaFactory
import br.com.zg.todolist.backend.factory.TarefaFactory
import br.com.zg.todolist.backend.model.Status
import br.com.zg.todolist.backend.model.Tarefa
import br.com.zg.todolist.backend.service.GerenciadorTarefasService
import br.com.zg.todolist.backend.service.IAlarmeService
import br.com.zg.todolist.backend.telegram.EnviadorMensagemTelegram
import br.com.zg.todolist.backend.telegram.NotificacaoTelegram
import com.sun.net.httpserver.HttpServer
import spock.lang.Specification

import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDate
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class NotificacaoSpec extends Specification {

    private static final String ORIGEM_PERMITIDA = "http://localhost:5500"
    private static final LocalDate DATA_TERMINO = LocalDate.of(2026, 11, 12)

    private GerenciadorTarefasService gerenciador
    private EnviadorMensagemTelegram enviadorTelegram
    private HttpServer servidorEmail
    private ExecutorService executorEmail
    private URI endpointEmail
    private EnviadorEmail enviadorEmail

    def setup() {
        enviadorTelegram = Mock(EnviadorMensagemTelegram)
        gerenciador = new GerenciadorTarefasService(Mock(IAlarmeService), enviadorTelegram)

        enviadorEmail = Mock(EnviadorEmail)
        servidorEmail = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0)
        servidorEmail.createContext(
                "/api/notificacoes/email",
                new ManipuladorNotificacaoEmail(enviadorEmail, ORIGEM_PERMITIDA))
        executorEmail = Executors.newSingleThreadExecutor()
        servidorEmail.setExecutor(executorEmail)
        servidorEmail.start()
        endpointEmail = URI.create(
                "http://127.0.0.1:${servidorEmail.address.port}/api/notificacoes/email")
    }

    def cleanup() {
        gerenciador?.desligarAlarmes()
        servidorEmail?.stop(0)
        executorEmail?.shutdownNow()
    }

    def "Dado uma tarefa válida, quando ela é criada, então o Telegram recebe o evento de criação sem envio real"() {
        given:
        def tarefa = criarTarefa("Entregar certificado", Status.TODO)

        when:
        gerenciador.adicionarTarefa(tarefa)

        then:
        1 * enviadorTelegram.enviar({
            NotificacaoTelegram notificacao ->
                notificacao.evento() == "TAREFA_CRIADA" &&
                        notificacao.tituloTarefa() == "Entregar certificado" &&
                        notificacao.dataTermino() == "2026-11-12"
        })
    }

    def "Dado uma tarefa existente, quando ela é concluída, então o Telegram recebe o evento de conclusão sem envio real"() {
        given:
        def tarefa = criarTarefa("Projeto introdutório", Status.TODO)
        gerenciador.adicionarTarefa(tarefa)
        def tarefaConcluida = criarTarefa("Projeto introdutório", Status.DONE)

        when:
        gerenciador.atualizarTarefa(0, tarefaConcluida)

        then:
        1 * enviadorTelegram.enviar({
            NotificacaoTelegram notificacao ->
                notificacao.evento() == "TAREFA_CONCLUIDA" &&
                        notificacao.tituloTarefa() == "Projeto introdutório"
        })
    }

    def "Dado uma notificação de criação, quando o endpoint de e-mail é acionado, então o mock recebe o evento correto"() {
        when:
        def resposta = enviarEmail("""
            {"evento":"TAREFA_CRIADA","tituloTarefa":"ZG-HERO","status":"TODO","dataTermino":"2026-11-12"}
        """)

        then:
        resposta.statusCode() == 202
        1 * enviadorEmail.enviar({
            it.evento() == EventoNotificacao.TAREFA_CRIADA &&
                    it.tituloTarefa() == "ZG-HERO" &&
                    it.status() == "TODO" &&
                    it.dataTermino() == "2026-11-12"
        })
    }

    def "Dado uma notificação de conclusão, quando o endpoint de e-mail é acionado, então o mock recebe o evento correto"() {
        when:
        def resposta = enviarEmail("""
            {"evento":"TAREFA_CONCLUIDA","tituloTarefa":"ZG-HERO","status":"DONE","dataTermino":"2026-11-12"}
        """)

        then:
        resposta.statusCode() == 202
        1 * enviadorEmail.enviar({
            it.evento() == EventoNotificacao.TAREFA_CONCLUIDA &&
                    it.tituloTarefa() == "ZG-HERO" &&
                    it.status() == "DONE"
        })
    }

    private static Tarefa criarTarefa(String nome, Status status) {
        def tarefa = TarefaFactory.criarTarefaSimples(
                nome,
                "Descrição de teste",
                DATA_TERMINO,
                3,
                CategoriaFactory.getCategoria("Estudos"))
        tarefa.setStatus(status)
        return tarefa
    }

    private HttpResponse<String> enviarEmail(String corpo) {
        def requisicao = HttpRequest.newBuilder(endpointEmail)
                .header("Origin", ORIGEM_PERMITIDA)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(corpo))
                .build()
        HttpClient.newHttpClient().send(requisicao, HttpResponse.BodyHandlers.ofString())
    }
}
