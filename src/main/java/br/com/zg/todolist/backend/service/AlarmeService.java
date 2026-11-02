package br.com.zg.todolist.backend.service;

import br.com.zg.todolist.backend.model.Tarefa;
import br.com.zg.todolist.backend.observer.AlarmeObserver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AlarmeService implements IAlarmeService {

    private final ScheduledExecutorService scheduler;
    private final List<AlarmeObserver> observadores;

    public AlarmeService() {
        this.scheduler = Executors.newScheduledThreadPool(5);
        this.observadores = new ArrayList<>();
    }

    public void adicionarObservador(AlarmeObserver observador) {
        this.observadores.add(observador);
    }

    public void removerObservador(AlarmeObserver observador) {
        this.observadores.remove(observador);
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

        scheduler.schedule(() -> dispararAlarme(tarefa), minutos, TimeUnit.MINUTES);

    }

    @Override
    public void encerrarAlarmes() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    private void dispararAlarme(Tarefa tarefa) {
        for (AlarmeObserver observador : observadores) {
            observador.onAlarmeDisparado(tarefa);
        }
    }
}