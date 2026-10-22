package br.com.zg.todolist.backend.model;

public enum Status {
    TODO("A Fazer") {
        @Override
        public Status avancar() {
            return DOING;
        }
    },
    DOING("Em Andamento") {
        @Override
        public Status avancar() {
            return DONE;
        }
    },
    DONE("Concluído") {
        @Override
        public Status avancar() {
            return this;
        }
    };

    private final String descricao;

    Status(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public abstract Status avancar();

    @Override
    public String toString() {
        return this.name();
    }
}