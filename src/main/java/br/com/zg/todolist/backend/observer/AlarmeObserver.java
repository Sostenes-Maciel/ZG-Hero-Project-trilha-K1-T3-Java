package br.com.zg.todolist.backend.observer;

import br.com.zg.todolist.backend.model.Tarefa;

public interface AlarmeObserver {
    void onAlarmeDisparado(Tarefa tarefa);
}