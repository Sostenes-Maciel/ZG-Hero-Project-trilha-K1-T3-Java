package br.com.zg.todolist.backend.strategy;

import br.com.zg.todolist.backend.model.Tarefa;
import java.util.Comparator;
import java.util.List;

public class OrdenacaoPorData implements EstrategiaOrdenacao {
    @Override
    public void ordenar(List<Tarefa> tarefas) {
        tarefas.sort(Comparator.comparing(Tarefa::getDataTermino));
    }
}