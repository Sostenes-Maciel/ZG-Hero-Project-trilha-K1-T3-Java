package br.com.zg.todolist.backend.service;

import br.com.zg.todolist.backend.model.Tarefa;
import br.com.zg.todolist.backend.strategy.EstrategiaOrdenacao;

import java.util.List;
import java.util.function.Predicate;

public interface IGerenciadorTarefas {
    void adicionarTarefa(Tarefa tarefa);
    void atualizarTarefa(int indice, Tarefa tarefaAtualizada);
    void removerTarefa(int indice);
    void setEstrategiaOrdenacao(EstrategiaOrdenacao estrategia);
    List<Tarefa> listarTarefas();
    List<Tarefa> filtrarTarefas(Predicate<Tarefa> criterio); // Aplicação do Aberto/Fechado (OCP)
}