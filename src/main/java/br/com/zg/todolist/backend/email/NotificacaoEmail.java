package br.com.zg.todolist.backend.email;

public record NotificacaoEmail(EventoNotificacao evento, String tituloTarefa, String status, String dataTermino) {
}
