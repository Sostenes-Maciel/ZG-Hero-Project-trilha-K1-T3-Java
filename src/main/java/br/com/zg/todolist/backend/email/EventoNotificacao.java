package br.com.zg.todolist.backend.email;

public enum EventoNotificacao {
    TAREFA_CRIADA("Tarefa criada"),
    TAREFA_CONCLUIDA("Tarefa concluída"),
    PRAZO_PROXIMO("Prazo próximo"),
    TAREFA_ATRASADA("Tarefa atrasada");

    private final String descricao;

    EventoNotificacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
