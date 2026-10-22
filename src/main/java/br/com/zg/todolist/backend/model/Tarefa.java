package br.com.zg.todolist.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Tarefa {

    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATADOR_ALARME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private String nome;
    private String descricao;
    private LocalDate dataTermino;
    private int prioridade;
    private Categoria categoria;
    private Status status;
    private LocalDateTime horarioAlarme;

    private Tarefa(TarefaBuilder builder) {
        this.nome = builder.nome;
        this.descricao = builder.descricao;
        this.dataTermino = builder.dataTermino;
        this.categoria = builder.categoria;
        this.status = builder.status != null ? builder.status : Status.TODO;
        setPrioridade(builder.prioridade);
        this.horarioAlarme = builder.horarioAlarme;
    }

    // Getters e Setters originais mantidos...
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDate getDataTermino() { return dataTermino; }
    public void setDataTermino(LocalDate dataTermino) { this.dataTermino = dataTermino; }
    public int getPrioridade() { return prioridade; }
    public void setPrioridade(int prioridade) {
        validarPrioridade(prioridade);
        this.prioridade = prioridade;
    }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getHorarioAlarme() { return horarioAlarme; }
    public void setHorarioAlarme(LocalDateTime horarioAlarme) { this.horarioAlarme = horarioAlarme; }

    @Override
    public String toString() {
        String resultado = String.format(
                "[%s] Prioridade %d | %s (Categoria: %s) - %s | Prazo: %s",
                status, prioridade, nome, categoria.getNome(), descricao,
                dataTermino.format(FORMATADOR_DATA)
        );
        if (horarioAlarme != null) {
            resultado += " | Alarme: " + horarioAlarme.format(FORMATADOR_ALARME);
        }
        return resultado;
    }

    private void validarPrioridade(int prioridade) {
        if (prioridade < 1 || prioridade > 5) {
            throw new IllegalArgumentException("Erro: O número da prioridade deve ser entre 1 e 5");
        }
    }

    public static class TarefaBuilder {
        private String nome;
        private String descricao;
        private LocalDate dataTermino;
        private int prioridade;
        private Categoria categoria;
        private Status status = Status.TODO;
        private LocalDateTime horarioAlarme;

        public TarefaBuilder comNome(String nome) {
            this.nome = nome;
            return this;
        }

        public TarefaBuilder comDescricao(String descricao) {
            this.descricao = descricao;
            return this;
        }

        public TarefaBuilder comDataTermino(LocalDate dataTermino) {
            this.dataTermino = dataTermino;
            return this;
        }

        public TarefaBuilder comPrioridade(int prioridade) {
            this.prioridade = prioridade;
            return this;
        }

        public TarefaBuilder comCategoria(Categoria categoria) {
            this.categoria = categoria;
            return this;
        }

        public TarefaBuilder comStatus(Status status) {
            this.status = status;
            return this;
        }

        public TarefaBuilder comHorarioAlarme(LocalDateTime horarioAlarme) {
            this.horarioAlarme = horarioAlarme;
            return this;
        }

        public Tarefa build() {
            return new Tarefa(this);
        }
    }
}