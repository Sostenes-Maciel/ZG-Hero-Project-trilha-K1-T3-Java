package br.com.zg.todolist.backend.telegram;

import br.com.zg.todolist.backend.telegram.EnviadorTelegramBot;
import br.com.zg.todolist.backend.telegram.ManipuladorNotificacaoTelegram;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InicializadorApiTelegram {
    private InicializadorApiTelegram() {
    }

    public static void main(String[] args) throws IOException {
        String tokenBotTelegram = obrigatorio("TELEGRAM_BOT_TOKEN");
        String identificadorConversaTelegram = obrigatorio("TELEGRAM_CHAT_ID");
        String enderecoServidor = System.getenv().getOrDefault("TODO_TELEGRAM_HOST", "127.0.0.1");
        int porta = Integer.parseInt(System.getenv().getOrDefault("TODO_TELEGRAM_PORT", "8082"));
        String origemPermitida = System.getenv().getOrDefault("TODO_TELEGRAM_ALLOWED_ORIGIN", "http://localhost:5500");

        HttpServer servidor = HttpServer.create(new InetSocketAddress(enderecoServidor, porta), 0);
        servidor.createContext("/api/notificacoes/telegram",
                new ManipuladorNotificacaoTelegram(
                        new EnviadorTelegramBot(tokenBotTelegram, identificadorConversaTelegram),
                        obrigatorio("TODO_EMAIL_API_TOKEN"),
                        origemPermitida));

        ExecutorService executorServico = Executors.newFixedThreadPool(4);
        servidor.setExecutor(executorServico);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            servidor.stop(1);
            executorServico.shutdownNow();
        }));
        servidor.start();
        System.out.printf("API de notificações Telegram escutando em %s:%d%n", enderecoServidor, porta);
    }

    private static String obrigatorio(String nomeVariavel) {
        String valor = System.getenv(nomeVariavel);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Configure a variável de ambiente " + nomeVariavel + ".");
        }
        return valor;
    }
}
