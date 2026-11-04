package br.com.zg.todolist;

import br.com.zg.todolist.backend.controller.CategoriaController;
import br.com.zg.todolist.backend.controller.TarefaController;
import br.com.zg.todolist.backend.observer.ConsoleAlarmeObserver;
import br.com.zg.todolist.backend.service.AlarmeService;
import br.com.zg.todolist.backend.service.GerenciadorTarefasService;
import br.com.zg.todolist.backend.telegram.EnviadorMensagemTelegram;
import br.com.zg.todolist.backend.telegram.EnviadorTelegramBot;
import br.com.zg.todolist.backend.view.MenuTerminalView;

public class Main {
    public static void main(String[] args) {
        String tokenBotTelegram = System.getenv("TELEGRAM_BOT_TOKEN");
        String identificadorConversaTelegram = System.getenv("TELEGRAM_CHAT_ID");

        if (vazio(tokenBotTelegram) || vazio(identificadorConversaTelegram)) {
            System.err.println("Configure TELEGRAM_BOT_TOKEN e TELEGRAM_CHAT_ID para enviar notificações automáticas.");
            System.err.println("Modo sem notificações automáticas — use a opção manual no menu.");
        }

        EnviadorMensagemTelegram enviadorTelegram = null;
        if (!vazio(tokenBotTelegram) && !vazio(identificadorConversaTelegram)) {
            enviadorTelegram = new EnviadorTelegramBot(tokenBotTelegram, identificadorConversaTelegram);
        }

        AlarmeService alarmeService = new AlarmeService();
        alarmeService.adicionarObservador(new ConsoleAlarmeObserver());
        GerenciadorTarefasService gerenciador = new GerenciadorTarefasService(alarmeService, enviadorTelegram);

        TarefaController tarefaController = new TarefaController(gerenciador);
        CategoriaController categoriaController = new CategoriaController();

        MenuTerminalView menu = new MenuTerminalView(tarefaController, categoriaController);
        menu.iniciar();
    }

    private static boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}