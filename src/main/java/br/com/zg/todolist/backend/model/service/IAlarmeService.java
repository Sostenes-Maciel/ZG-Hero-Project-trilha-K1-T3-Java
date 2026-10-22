package br.com.zg.todolist.backend.model.service;

import br.com.zg.todolist.backend.model.Tarefa;
import br.com.zg.todolist.backend.model.observer.AlarmeObserver;

public interface IAlarmeService {
    void configurarAlarme(Tarefa tarefa, long minutos);
    void encerrarAlarmes();
    void adicionarObservador(AlarmeObserver observador);
}