package br.com.zg.todolist.backend.model.observer;

import br.com.zg.todolist.backend.model.Tarefa;

public class ConsoleAlarmeObserver implements AlarmeObserver {
    @Override
    public void onAlarmeDisparado(Tarefa tarefa) {
        System.out.println("\n================================");
        System.out.println(" ⏰ ALARME: " + tarefa.getNome());
        System.out.println("================================");
        System.out.println("Descrição: " + tarefa.getDescricao());
        System.out.println("Prioridade: " + tarefa.getPrioridade());
        System.out.println("================================\n");
    }
}