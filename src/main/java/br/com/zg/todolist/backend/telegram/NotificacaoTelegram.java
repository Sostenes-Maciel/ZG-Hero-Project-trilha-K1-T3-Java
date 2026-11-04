package br.com.zg.todolist.backend.telegram;

public record NotificacaoTelegram(String evento, String tituloTarefa, String descricao, String dataTermino) {
}
