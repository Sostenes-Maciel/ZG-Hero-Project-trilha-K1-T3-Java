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
    private String categoria;
    private Status status;
    private LocalDateTime horarioAlarme;

    public Tarefa(String nome, String descricao, LocalDate dataTermino, int prioridade, String categoria) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataTermino = dataTermino;
        this.categoria = categoria;
        this.status = Status.TODO;
        setPrioridade(prioridade);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(LocalDate dataTermino) {
        this.dataTermino = dataTermino;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        validarPrioridade(prioridade);
        this.prioridade = prioridade;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getHorarioAlarme() {
        return horarioAlarme;
    }

    public void setHorarioAlarme(LocalDateTime horarioAlarme) {
        this.horarioAlarme = horarioAlarme;
    }

    @Override
    public String toString() {
        String resultado = String.format(
                "[%s] Prioridade %d | %s (Categoria: %s) - %s | Prazo: %s",
                status,
                prioridade,
                nome,
                categoria,
                descricao,
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
}