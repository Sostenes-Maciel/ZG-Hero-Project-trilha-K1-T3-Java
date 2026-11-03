package br.com.zg.todolist.backend.email;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InicializadorApiEmail {
    private InicializadorApiEmail() {
    }

    public static void main(String[] args) throws IOException {
        String chaveApi = obrigatorio("RESEND_API_KEY");
        String remetente = obrigatorio("TODO_EMAIL_FROM");
        String destinatario = obrigatorio("TODO_EMAIL_TO");
        String tokenApi = obrigatorio("TODO_EMAIL_API_TOKEN");
        String enderecoServidor = System.getenv().getOrDefault("TODO_EMAIL_HOST", "127.0.0.1");
        int porta = Integer.parseInt(System.getenv().getOrDefault("TODO_EMAIL_PORT", "8081"));
        String origemPermitida = System.getenv().getOrDefault("TODO_EMAIL_ALLOWED_ORIGIN", "http://localhost:5500");

        HttpServer servidor = HttpServer.create(new InetSocketAddress(enderecoServidor, porta), 0);
        servidor.createContext("/api/notificacoes/email",
                new ManipuladorNotificacaoEmail(
                        new EnviadorEmailResend(chaveApi, remetente, destinatario), tokenApi, origemPermitida));
        ExecutorService executorServico = Executors.newFixedThreadPool(4);
        servidor.setExecutor(executorServico);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            servidor.stop(1);
            executorServico.shutdownNow();
        }));
        servidor.start();
        System.out.printf("API de notificações de e-mail escutando em %s:%d%n", enderecoServidor, porta);
    }

    private static String obrigatorio(String nome) {
        String valor = System.getenv(nome);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Configure a variável de ambiente " + nome + ".");
        }
        return valor;
    }
}
