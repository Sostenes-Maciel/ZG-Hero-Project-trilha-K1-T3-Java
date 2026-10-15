package br.com.zg.todolist.backend.service;

import br.com.zg.todolist.backend.model.Tarefa;

public interface IAlarmeService {
    void configurarAlarme(Tarefa tarefa, long minutos);
    void encerrarAlarmes();
}