package br.com.zg.todolist.backend.model.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.zg.todolist.backend.model.Categoria;
import br.com.zg.todolist.backend.model.Status;
import br.com.zg.todolist.backend.model.Tarefa;

class GerenciadorTarefasServiceTest {

    @Test
    void deveAdicionarEOrdenarTarefasPorPrioridadeEDatas() {
        GerenciadorTarefasService service = new GerenciadorTarefasService(new AlarmeService());

        Tarefa tarefaComMenorPrioridade = criarTarefa("Urgência baixa", 1, LocalDate.of(2026, 11, 20), Status.TODO);
        Tarefa tarefaComMaiorPrioridade = criarTarefa("Urgência alta", 5, LocalDate.of(2026, 11, 18), Status.DOING);
        Tarefa tarefaComMesmaPrioridade = criarTarefa("Mesma prioridade", 5, LocalDate.of(2026, 11, 10), Status.TODO);

        service.adicionarTarefa(tarefaComMenorPrioridade);
        service.adicionarTarefa(tarefaComMaiorPrioridade);
        service.adicionarTarefa(tarefaComMesmaPrioridade);

        List<Tarefa> tarefas = service.listarTarefas();

        assertAll(
                () -> assertEquals(3, tarefas.size()),
                () -> assertEquals("Mesma prioridade", tarefas.getFirst().getNome()),
                () -> assertEquals("Urgência alta", tarefas.get(1).getNome()),
                () -> assertEquals("Urgência baixa", tarefas.get(2).getNome())
        );
    }

    @Test
    void deveFiltrarEAtualizarTarefa() {
        GerenciadorTarefasService service = new GerenciadorTarefasService(new AlarmeService());

        Tarefa tarefa1 = criarTarefa("Escrever relatório", 2, LocalDate.of(2026, 11, 12), Status.TODO);
        Tarefa tarefa2 = criarTarefa("Revisar código", 3, LocalDate.of(2026, 11, 13), Status.DONE);
        Tarefa tarefa3 = criarTarefa("Responder e-mails", 4, LocalDate.of(2026, 11, 14), Status.TODO);

        service.adicionarTarefa(tarefa1);
        service.adicionarTarefa(tarefa2);
        service.adicionarTarefa(tarefa3);

        List<Tarefa> concluidas = service.filtrarTarefas(tarefa -> tarefa.getStatus() == Status.DONE);
        assertEquals(1, concluidas.size());
        assertEquals("Revisar código", concluidas.getFirst().getNome());

        Tarefa tarefaAtualizada = criarTarefa("Revisar código final", 5, LocalDate.of(2026, 11, 15), Status.DONE);
        service.atualizarTarefa(1, tarefaAtualizada);

        List<Tarefa> tarefasAtualizadas = service.listarTarefas();
        Tarefa tarefaEncontrada = tarefasAtualizadas.stream()
                .filter(tarefa -> "Revisar código final".equals(tarefa.getNome()))
                .findFirst()
                .orElseThrow();

        assertAll(
                () -> assertEquals("Revisar código final", tarefaEncontrada.getNome()),
                () -> assertEquals(5, tarefaEncontrada.getPrioridade()),
                () -> assertEquals(Status.DONE, tarefaEncontrada.getStatus())
        );
    }

    @Test
    void deveLancarExcecaoParaIndiceInvalido() {
        GerenciadorTarefasService service = new GerenciadorTarefasService(new AlarmeService());
        service.adicionarTarefa(criarTarefa("Primeira tarefa", 2, LocalDate.of(2026, 11, 11), Status.TODO));

        assertAll(
                () -> assertThrows(IndexOutOfBoundsException.class, () -> service.atualizarTarefa(5, criarTarefa("Inválida", 3, LocalDate.of(2026, 11, 12), Status.TODO))),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> service.removerTarefa(-1)),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> service.removerTarefa(1))
        );
    }

    private Tarefa criarTarefa(String nome, int prioridade, LocalDate dataTermino, Status status) {
        return new Tarefa.TarefaBuilder()
                .comNome(nome)
                .comDescricao("Descrição de teste")
                .comDataTermino(dataTermino)
                .comPrioridade(prioridade)
                .comCategoria(new Categoria("Teste"))
                .comStatus(status)
                .build();
    }
}
