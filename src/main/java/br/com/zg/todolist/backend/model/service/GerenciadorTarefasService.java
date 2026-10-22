package br.com.zg.todolist.backend.model.service;

import br.com.zg.todolist.backend.model.Tarefa;
import br.com.zg.todolist.backend.model.strategy.EstrategiaOrdenacao;
import br.com.zg.todolist.backend.model.strategy.OrdenacaoPorPrioridadeEData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GerenciadorTarefasService implements IGerenciadorTarefas {

    private final List<Tarefa> tarefas;
    private final IAlarmeService alarmeService;
    private EstrategiaOrdenacao estrategiaOrdenacao;

    public GerenciadorTarefasService(IAlarmeService alarmeService) {
        this.tarefas = new ArrayList<>();
        this.alarmeService = alarmeService;
        this.estrategiaOrdenacao = new OrdenacaoPorPrioridadeEData();
    }

    public void setEstrategiaOrdenacao(EstrategiaOrdenacao estrategiaOrdenacao) {
        this.estrategiaOrdenacao = estrategiaOrdenacao;
        ordenarTarefas();
    }

    @Override
    public void adicionarTarefa(Tarefa tarefa) {
        validarTarefa(tarefa);
        tarefas.add(tarefa);
        ordenarTarefas();
    }

    @Override
    public void atualizarTarefa(int indice, Tarefa tarefaAtualizada) {
        validarIndice(indice);
        validarTarefa(tarefaAtualizada);

        tarefas.set(indice, tarefaAtualizada);
        ordenarTarefas();
    }

    @Override
    public void removerTarefa(int indice) {
        validarIndice(indice);
        tarefas.remove(indice);
    }

    @Override
    public List<Tarefa> listarTarefas() {
        return new ArrayList<>(tarefas);
    }

    @Override
    public List<Tarefa> filtrarTarefas(Predicate<Tarefa> criterio) {
        return tarefas.stream()
                .filter(criterio)
                .collect(Collectors.toList());
    }

    public void agendarAlarmeSeNecessario(Tarefa tarefa, long minutos) {
        if (minutos > 0) {
            alarmeService.configurarAlarme(tarefa, minutos);
            System.out.println("Alarme configurado para daqui a " + minutos + " minuto(s).");
        }
    }

    public void desligarAlarmes() {
        alarmeService.encerrarAlarmes();
    }

    private void ordenarTarefas() {
        estrategiaOrdenacao.ordenar(this.tarefas);
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= tarefas.size()) {
            throw new IndexOutOfBoundsException("Índice de tarefa inválido: " + indice);
        }
    }

    private void validarTarefa(Tarefa tarefa) {
        if (tarefa == null) {
            throw new IllegalArgumentException("A tarefa não pode ser nula.");
        }
    }
}