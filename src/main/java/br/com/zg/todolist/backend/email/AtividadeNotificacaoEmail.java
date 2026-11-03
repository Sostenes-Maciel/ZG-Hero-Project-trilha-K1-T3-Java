package br.com.zg.todolist.backend.email;

public record AtividadeNotificacaoEmail(
        String titulo,
        String descricao,
        String status,
        String dataTermino,
        int prioridade,
        String categoria,
        String alarme
) {
}
