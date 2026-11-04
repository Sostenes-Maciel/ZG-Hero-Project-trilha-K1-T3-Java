package br.com.zg.todolist.backend.telegram;

import br.com.zg.todolist.backend.model.Status;
import br.com.zg.todolist.backend.model.Tarefa;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public final class GeradorNotificacoesTelegram {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private GeradorNotificacoesTelegram() {
    }

    public static NotificacaoTelegram gerarNovaTarefa(Tarefa tarefa) {
        return new NotificacaoTelegram(
                "TAREFA_CRIADA",
                tarefa.getNome(),
                null,
                tarefa.getDataTermino().format(FORMATO_DATA)
        );
    }

    public static NotificacaoTelegram gerarConcluida(Tarefa tarefa) {
        return new NotificacaoTelegram(
                "TAREFA_CONCLUIDA",
                tarefa.getNome(),
                null,
                tarefa.getDataTermino().format(FORMATO_DATA)
        );
    }

    public static NotificacaoTelegram gerarAtrasada(Tarefa tarefa) {
        return new NotificacaoTelegram(
                "TAREFA_ATRASADA",
                tarefa.getNome(),
                null,
                tarefa.getDataTermino().format(FORMATO_DATA)
        );
    }

    public static NotificacaoTelegram gerarPrazoProximo(Tarefa tarefa) {
        return new NotificacaoTelegram(
                "PRAZO_PROXIMO",
                tarefa.getNome(),
                null,
                tarefa.getDataTermino().format(FORMATO_DATA)
        );
    }

    public static NotificacaoTelegram gerarRelatorioSemanal(List<Tarefa> tarefas) {
        List<Tarefa> pendentes = tarefas.stream()
                .filter(t -> t.getStatus() != Status.DONE)
                .toList();

        StringBuilder texto = new StringBuilder();
        texto.append("📊 Relatório Semanal do TODO List\n\n");
        texto.append("Total de tarefas cadastradas: ").append(tarefas.size()).append("\n");
        texto.append("Concluídas: ").append(tarefas.stream().filter(t -> t.getStatus() == Status.DONE).count()).append("\n");
        texto.append("Em andamento: ").append(tarefas.stream().filter(t -> t.getStatus() == Status.DOING).count()).append("\n");
        texto.append("A fazer: ").append(tarefas.stream().filter(t -> t.getStatus() == Status.TODO).count()).append("\n\n");

        if (!pendentes.isEmpty()) {
            texto.append("Tarefas pendentes:\n");
            for (int i = 0; i < pendentes.size(); i++) {
                Tarefa t = pendentes.get(i);
                texto.append(i + 1).append(". [").append(t.getStatus().name()).append("] ")
                        .append(t.getNome())
                        .append(" | Prazo: ")
                        .append(t.getDataTermino().format(FORMATO_DATA))
                        .append(" | Prioridade: ").append(t.getPrioridade()).append("/5")
                        .append(" | Categoria: ").append(t.getCategoria().getNome())
                        .append("\n");
            }
        } else {
            texto.append("✅ Todas as tarefas foram concluídas!");
        }

        return new NotificacaoTelegram(
                "RELATORIO_SEMANAL",
                "Relatório Semanal",
                texto.toString(),
                null
        );
    }
}
