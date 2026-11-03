package br.com.zg.todolist.backend.email;

import java.util.List;

public record NotificacaoEmail(
        EventoNotificacao evento,
        String tituloTarefa,
        String status,
        String dataTermino,
        List<AtividadeNotificacaoEmail> tarefas
) {
    public NotificacaoEmail {
        tarefas = tarefas == null ? List.of() : List.copyOf(tarefas);
    }
}
