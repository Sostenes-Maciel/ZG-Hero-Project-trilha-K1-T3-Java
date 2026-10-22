package br.com.zg.todolist.backend.strategy;

import br.com.zg.todolist.backend.model.Tarefa;
import java.util.List;

public interface EstrategiaOrdenacao {
    void ordenar(List<Tarefa> tarefas);
}