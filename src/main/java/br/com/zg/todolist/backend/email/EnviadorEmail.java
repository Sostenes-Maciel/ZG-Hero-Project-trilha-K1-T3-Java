package br.com.zg.todolist.backend.email;

import java.io.IOException;

@FunctionalInterface
public interface EnviadorEmail {
    void enviar(NotificacaoEmail notificacao) throws IOException, InterruptedException;
}
