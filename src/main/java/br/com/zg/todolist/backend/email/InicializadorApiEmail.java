package br.com.zg.todolist.backend.email;

import br.com.zg.todolist.backend.telegram.EnviadorTelegramBot;
import br.com.zg.todolist.backend.telegram.ManipuladorNotificacaoTelegram;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InicializadorApiEmail {
    private InicializadorApiEmail() {
    }

    public static void main(String[] args) throws IOException {
        String tokenApi = obrigatorio("TODO_EMAIL_API_TOKEN");
        String enderecoServidor = System.getenv().getOrDefault("TODO_EMAIL_HOST", "127.0.0.1");
        int porta = Integer.parseInt(System.getenv().getOrDefault("TODO_EMAIL_PORT", "8081"));
        String origemPermitida = System.getenv().getOrDefault("TODO_EMAIL_ALLOWED_ORIGIN", "http://localhost:5500");

        String chaveApiEmail = System.getenv("RESEND_API_KEY");
        String remetenteEmail = System.getenv("TODO_EMAIL_FROM");
        String destinatarioEmail = System.getenv("TODO_EMAIL_TO");
        boolean configurarEmail = algumValorConfigurado(chaveApiEmail, remetenteEmail, destinatarioEmail);
        if (configurarEmail) {
            validarConfiguracaoEmail(chaveApiEmail, remetenteEmail, destinatarioEmail);
        }

        String tokenBotTelegram = System.getenv("TELEGRAM_BOT_TOKEN");
        String identificadorConversaTelegram = System.getenv("TELEGRAM_CHAT_ID");
        boolean configurarTelegram = algumValorConfigurado(tokenBotTelegram, identificadorConversaTelegram);
        if (configurarTelegram && (vazio(tokenBotTelegram) || vazio(identificadorConversaTelegram))) {
            throw new IllegalStateException(
                    "Configure TELEGRAM_BOT_TOKEN e TELEGRAM_CHAT_ID juntos para ativar o Telegram.");
        }
        if (!configurarEmail && !configurarTelegram) {
            throw new IllegalStateException(
                    "Configure o Resend ou TELEGRAM_BOT_TOKEN e TELEGRAM_CHAT_ID para iniciar a API.");
        }

        HttpServer servidor = HttpServer.create(new InetSocketAddress(enderecoServidor, porta), 0);
        if (configurarEmail) {
            servidor.createContext("/api/notificacoes/email",
                    new ManipuladorNotificacaoEmail(
                            new EnviadorEmailResend(chaveApiEmail, remetenteEmail, destinatarioEmail),
                            tokenApi,
                            origemPermitida));
        }
        if (configurarTelegram) {
            servidor.createContext("/api/notificacoes/telegram",
                    new ManipuladorNotificacaoTelegram(
                            new EnviadorTelegramBot(tokenBotTelegram, identificadorConversaTelegram),
                            tokenApi,
                            origemPermitida));
        }
        ExecutorService executorServico = Executors.newFixedThreadPool(4);
        servidor.setExecutor(executorServico);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            servidor.stop(1);
            executorServico.shutdownNow();
        }));
        servidor.start();
        System.out.printf("API de notificações escutando em %s:%d%n", enderecoServidor, porta);
    }

    private static void validarConfiguracaoEmail(String chaveApi, String remetente, String destinatario) {
        if (vazio(chaveApi) || vazio(remetente) || vazio(destinatario)) {
            throw new IllegalStateException(
                    "Configure RESEND_API_KEY, TODO_EMAIL_FROM e TODO_EMAIL_TO juntos para ativar o e-mail.");
        }
    }

    private static boolean algumValorConfigurado(String... valores) {
        for (String valor : valores) {
            if (!vazio(valor)) {
                return true;
            }
        }
        return false;
    }

    private static boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }

    private static String obrigatorio(String nome) {
        String valor = System.getenv(nome);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Configure a variável de ambiente " + nome + ".");
        }
        return valor;
    }
}
