package br.com.zg.todolist.backend.email;

public enum EventoNotificacao {
    TAREFA_CRIADA("Tarefa criada"),
    TAREFA_EDITADA("Tarefa editada"),
    TAREFA_CONCLUIDA("Tarefa concluída"),
    PRAZO_PROXIMO("Prazo próximo"),
    TAREFA_A_FAZER_EM_TRES_DIAS("Tarefa a fazer em três dias"),
    PRAZO_HOJE("Tarefa com prazo para hoje"),
    TAREFA_ATRASADA("Tarefa atrasada"),
    RELATORIO_SEMANAL("Relatório semanal de tarefas");

    private final String descricao;

    EventoNotificacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
