package br.com.zg.todolist.service;

import br.com.zg.todolist.model.Tarefa;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GerenciadorTarefas {
    private List<Tarefa> tarefas;

    public GerenciadorTarefas() {

        this.tarefas = new ArrayList<>();
    }

    public void adicionarTarefa(Tarefa tarefa) {

        tarefas.add(tarefa);
        rebalancearTarefa();
    }

    private void rebalancearTarefa() {

        tarefas.sort(
                Comparator.comparingInt(Tarefa::getPrioridade).reversed()
                        .thenComparing(Tarefa::getDataTermino)
        );
    }

    public boolean atualizarTarefa(int indice, Tarefa tarefaAtualizada) {
        if (indice >= 0 && indice < tarefas.size()) {
            tarefas.set(indice, tarefaAtualizada);
            rebalancearTarefa();
            return true;
        }
        return false;
    }

    public List<Tarefa> ListarTarefa() {
        return tarefas;
    }

    public boolean removertarefa(int indice) {
        if (indice >= 0 && indice < tarefas.size()) {
            tarefas.remove(indice);
            return true;
        }
        return false;
    }
}
