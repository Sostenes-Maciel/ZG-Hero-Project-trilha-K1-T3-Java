package br.com.zg.todolist.backend.service;

import br.com.zg.todolist.backend.model.Status;
import br.com.zg.todolist.backend.model.Tarefa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GerenciadorTarefas {

    private final List<Tarefa> tarefas;
    private final ScheduledExecutorService scheduler;

    public GerenciadorTarefas() {
        this.tarefas = new ArrayList<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void adicionarTarefa(Tarefa tarefa) {
        validarTarefa(tarefa);
        tarefas.add(tarefa);
        ordenarTarefas();
    }

    public void atualizarTarefa(int indice, Tarefa tarefaAtualizada) {
        validarIndice(indice);
        validarTarefa(tarefaAtualizada);

        tarefas.set(indice, tarefaAtualizada);
        ordenarTarefas();
    }

    public void removerTarefa(int indice) {
        validarIndice(indice);
        tarefas.remove(indice);
    }

    public List<Tarefa> listarTarefas() {
        return new ArrayList<>(tarefas);
    }

    public List<Tarefa> listarPorStatus(Status statusProcurado) {
        return tarefas.stream()
                .filter(tarefa -> tarefa.getStatus() == statusProcurado)
                .collect(Collectors.toList());
    }

    public List<Tarefa> listarPorCategoria(String categoriaProcurada) {
        return tarefas.stream()
                .filter(tarefa -> tarefa.getCategoria().equalsIgnoreCase(categoriaProcurada))
                .collect(Collectors.toList());
    }

    public List<Tarefa> listarPorPrioridade(int prioridadeProcurada) {
        return tarefas.stream()
                .filter(tarefa -> tarefa.getPrioridade() == prioridadeProcurada)
                .collect(Collectors.toList());
    }

    public void configurarAlarme(Tarefa tarefa, long minutos) {
        validarTarefa(tarefa);
        if (minutos <= 0) {
            throw new IllegalArgumentException("O tempo do alarme deve ser maior que zero.");
        }

        LocalDateTime horario = LocalDateTime.now().plusMinutes(minutos);
        tarefa.setHorarioAlarme(horario);

        scheduler.schedule(() -> dispararAlarmeNoConsole(tarefa), minutos, TimeUnit.MINUTES);

        System.out.println("Alarme configurado para daqui a " + minutos + " minuto(s).");
    }

    public void encerrarAlarmes() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    private void ordenarTarefas() {
        tarefas.sort(
                Comparator.comparingInt(Tarefa::getPrioridade).reversed()
                        .thenComparing(Tarefa::getDataTermino)
        );
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= tarefas.size()) {
            throw new IndexOutOfBoundsException("Índice de tarefa inválido: " + indice);
        }
    }

    private void validarTarefa(Tarefa tarefa) {
        if (tarefa == null) {
            throw new IllegalArgumentException("A tarefa não pode ser nula.");
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