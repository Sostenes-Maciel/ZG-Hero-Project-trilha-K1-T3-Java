package br.com.zg.todolist.backend.telegram;

import java.io.IOException;

@FunctionalInterface
public interface EnviadorMensagemTelegram {
    void enviar(NotificacaoTelegram notificacao) throws IOException, InterruptedException;
}
