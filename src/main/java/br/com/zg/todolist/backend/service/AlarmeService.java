package br.com.zg.todolist.backend.service;

import br.com.zg.todolist.backend.model.Tarefa;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AlarmeService implements IAlarmeService {

    private final ScheduledExecutorService scheduler;

    public AlarmeService() {
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void configurarAlarme(Tarefa tarefa, long minutos) {
        if (tarefa == null) {
            throw new IllegalArgumentException("A tarefa não pode ser nula.");
        }
        if (minutos <= 0) {
            throw new IllegalArgumentException("O tempo do alarme deve ser maior que zero.");
        }

        LocalDateTime horario = LocalDateTime.now().plusMinutes(minutos);
        tarefa.setHorarioAlarme(horario);

        scheduler.schedule(() -> dispararAlarmeNoConsole(tarefa), minutos, TimeUnit.MINUTES);
    }

    @Override
    public void encerrarAlarmes() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    private void dispararAlarmeNoConsole(Tarefa tarefa) {
        System.out.println("\n================================");
        System.out.println("         ALARME DE TAREFA");
        System.out.println("================================");
        System.out.println("Tarefa: " + tarefa.getNome());
        System.out.println("Descrição: " + tarefa.getDescricao());
        System.out.println("Prioridade: " + tarefa.getPrioridade());
        System.out.println("================================\n");
    }
}