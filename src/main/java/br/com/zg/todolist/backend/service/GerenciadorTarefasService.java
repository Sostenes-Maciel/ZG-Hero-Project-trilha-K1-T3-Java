package br.com.zg.todolist.backend.service;

import br.com.zg.todolist.backend.model.Status;
import br.com.zg.todolist.backend.model.Tarefa;
import br.com.zg.todolist.backend.strategy.EstrategiaOrdenacao;
import br.com.zg.todolist.backend.strategy.OrdenacaoPorPrioridadeEData;
import br.com.zg.todolist.backend.telegram.EnviadorMensagemTelegram;
import br.com.zg.todolist.backend.telegram.GeradorNotificacoesTelegram;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GerenciadorTarefasService implements IGerenciadorTarefas {

    private final List<Tarefa> tarefas;
    private final IAlarmeService alarmeService;
    private final EnviadorMensagemTelegram enviadorMensagem;
    private final ScheduledExecutorService scheduler;
    private EstrategiaOrdenacao estrategiaOrdenacao;

    public GerenciadorTarefasService(IAlarmeService alarmeService) {
        this(alarmeService, null);
    }

    public GerenciadorTarefasService(IAlarmeService alarmeService, EnviadorMensagemTelegram enviadorMensagem) {
        this.tarefas = new ArrayList<>();
        this.alarmeService = alarmeService;
        this.enviadorMensagem = enviadorMensagem;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.estrategiaOrdenacao = new OrdenacaoPorPrioridadeEData();
        iniciarAgendadorDiario();
    }

    @Override
    public void adicionarTarefa(Tarefa tarefa) {
        validarTarefa(tarefa);
        tarefas.add(tarefa);
        ordenarTarefas();
        enviarNotificacaoNovaTarefa(tarefa);
    }

    @Override
    public void atualizarTarefa(int indice, Tarefa tarefaAtualizada) {
        validarIndice(indice);
        validarTarefa(tarefaAtualizada);

        Tarefa tarefaAnterior = tarefas.get(indice);
        enviarNotificacaoConcluidaSeFinalizado(tarefaAnterior, tarefaAtualizada);

        tarefas.set(indice, tarefaAtualizada);
        ordenarTarefas();
    }

    @Override
    public void removerTarefa(int indice) {
        validarIndice(indice);
        tarefas.remove(indice);
    }

    @Override
    public void setEstrategiaOrdenacao(EstrategiaOrdenacao estrategiaOrdenacao) {
        this.estrategiaOrdenacao = estrategiaOrdenacao;
        ordenarTarefas();
    }

    @Override
    public List<Tarefa> listarTarefas() {
        return new ArrayList<>(tarefas);
    }

    @Override
    public List<Tarefa> filtrarTarefas(Predicate<Tarefa> criterio) {
        return tarefas.stream()
                .filter(criterio)
                .collect(Collectors.toList());
    }

    public void agendarAlarmeSeNecessario(Tarefa tarefa, long minutos) {
        if (minutos > 0) {
            alarmeService.configurarAlarme(tarefa, minutos);
            System.out.println("Alarme configurado para daqui a " + minutos + " minuto(s).");
        }
    }

    public void desligarAlarmes() {
        if (alarmeService != null) {
            alarmeService.encerrarAlarmes();
        }
        if (!scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    private void enviarNotificacaoNovaTarefa(Tarefa tarefa) {
        if (enviadorMensagem != null) {
            try {
                enviadorMensagem.enviar(GeradorNotificacoesTelegram.gerarNovaTarefa(tarefa));
            } catch (Exception e) {
                System.err.println("Falha ao enviar notificação de nova tarefa via Telegram: " + e.getMessage());
            }
        }
    }

    private void enviarNotificacaoConcluidaSeFinalizado(Tarefa tarefaAnterior, Tarefa tarefaAtual) {
        boolean anteriorConcluida = tarefaAnterior.getStatus() == Status.DONE;
        boolean statusVirouParaDone = tarefaAtual.getStatus() == Status.DONE && !anteriorConcluida;

        if (statusVirouParaDone && enviadorMensagem != null) {
            try {
                enviadorMensagem.enviar(GeradorNotificacoesTelegram.gerarConcluida(tarefaAtual));
            } catch (Exception e) {
                System.err.println("Falha ao enviar notificação de conclusão via Telegram: " + e.getMessage());
            }
        }
    }

    private void verificarAvisosDiariamente() {
        LocalDate hoje = LocalDate.now();
        LocalDate amanha = hoje.plusDays(1);

        for (Tarefa tarefa : tarefas) {
            if (tarefa.getStatus() == Status.DONE) {
                continue;
            }
            LocalDate dataTermino = tarefa.getDataTermino();
            if (dataTermino.equals(amanha)) {
                enviarNotificacaoPrazoProximo(tarefa);
            } else if (dataTermino.equals(hoje)) {
                enviarNotificacaoAtrasada(tarefa);
            }
        }

        enviarRelatorioSemanal();
    }

    private void enviarNotificacaoPrazoProximo(Tarefa tarefa) {
        if (enviadorMensagem != null) {
            try {
                enviadorMensagem.enviar(GeradorNotificacoesTelegram.gerarPrazoProximo(tarefa));
            } catch (Exception e) {
                System.err.println("Falha ao enviar aviso de prazo próximo via Telegram: " + e.getMessage());
            }
        }
    }

    private void enviarNotificacaoAtrasada(Tarefa tarefa) {
        if (enviadorMensagem != null) {
            try {
                enviadorMensagem.enviar(GeradorNotificacoesTelegram.gerarAtrasada(tarefa));
            } catch (Exception e) {
                System.err.println("Falha ao enviar notificação de atraso via Telegram: " + e.getMessage());
            }
        }
    }

    private void enviarRelatorioSemanal() {
        List<Tarefa> pendentes = tarefas.stream()
                .filter(t -> t.getStatus() != Status.DONE)
                .toList();

        if (pendentes.isEmpty()) {
            return;
        }

        try {
            enviadorMensagem.enviar(
                    GeradorNotificacoesTelegram.gerarRelatorioSemanal(tarefas)
            );
        } catch (Exception e) {
            System.err.println("Falha ao enviar relatório semanal via Telegram: " + e.getMessage());
        }
    }

    private void iniciarAgendadorDiario() {
        scheduler.scheduleAtFixedRate(
                this::verificarAvisosDiariamente,
                1L, 1L, TimeUnit.HOURS
        );
    }

    private void ordenarTarefas() {
        estrategiaOrdenacao.ordenar(this.tarefas);
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
}
