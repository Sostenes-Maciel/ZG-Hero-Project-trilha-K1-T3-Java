package br.com.zg.todolist.backend.model;

public enum Status {
    TODO("A Fazer"),
    DOING("Em Andamento"),
    DONE("Concluído");

    private final String descricao;

    Status(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return this.name();
    }
}