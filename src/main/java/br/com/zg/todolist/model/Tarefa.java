package br.com.zg.todolist.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Tarefa {

    private String nome;
    private String descricao;
    private LocalDate dataTermino;
    private int prioridade;
    private String categoria;
    private Status status;

    public Tarefa(String nome, String descricao, LocalDate dataTermino, int prioridade, String categoria) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataTermino = dataTermino;
        this.prioridade = prioridade;
        this.categoria = categoria;
        this.status = Status.TODO;
    }

    public void setPrioridade(int prioridade) {
        if (prioridade < 1 || prioridade > 5) {
            throw new IllegalArgumentException("Erro: O número da prioridade deve ser entre 1 e 5");
        }
        this.prioridade = prioridade;
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

    @Override
    public String toString() {
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return String.format("[%s] Prioridade %d | %s (Categoria: %s) - %s | Prazo: %s",
                status, prioridade, nome, categoria, descricao, dataTermino);
    }

}