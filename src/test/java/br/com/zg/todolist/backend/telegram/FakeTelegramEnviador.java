package br.com.zg.todolist.backend.telegram;

import java.util.ArrayList;
import java.util.List;

public class FakeTelegramEnviador implements EnviadorMensagemTelegram {

    private final List<NotificacaoTelegram> mensagensRecebidas = new ArrayList<>();

    @Override
    public void enviar(NotificacaoTelegram notificacao) {
        mensagensRecebidas.add(notificacao);
    }

    public List<NotificacaoTelegram> getMensagensRecebidas() {
        return mensagensRecebidas;
    }
}
