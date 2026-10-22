package br.com.zg.todolist.backend.controller;

import br.com.zg.todolist.backend.model.Tarefa;
import br.com.zg.todolist.backend.model.service.GerenciadorTarefasService;
import java.util.List;
import java.util.function.Predicate;

public class TarefaController {

    private final GerenciadorTarefasService tarefaService;

    public TarefaController(GerenciadorTarefasService tarefaService) {
        this.tarefaService = tarefaService;
    }

    public void criarTarefa(Tarefa tarefa) {
        tarefaService.adicionarTarefa(tarefa);
    }

    public List<Tarefa> listarTodasTarefas() {
        return tarefaService.listarTarefas();
    }

    public void atualizarTarefa(int indice, Tarefa tarefaAtualizada) {
        tarefaService.atualizarTarefa(indice, tarefaAtualizada);
    }

    public void removerTarefa(int indice) {
        tarefaService.removerTarefa(indice);
    }

    public List<Tarefa> filtrarTarefas(Predicate<Tarefa> criterio) {
        return tarefaService.filtrarTarefas(criterio);
    }

    public void agendarAlarme(Tarefa tarefa, long minutos) {
        tarefaService.agendarAlarmeSeNecessario(tarefa, minutos);
    }

    public void desligarAlarmes() {
        tarefaService.desligarAlarmes();
    }
}