package br.com.zg.todolist.backend.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TarefaTest {

    @Test
    void deveCriarTarefaComValoresPadrao() {
        Categoria categoria = new Categoria("Estudo");

        Tarefa tarefa = new Tarefa.TarefaBuilder()
                .comNome("Estudar Java")
                .comDescricao("Revisar testes unitários")
                .comDataTermino(LocalDate.of(2026, 11, 15))
                .comPrioridade(3)
                .comCategoria(categoria)
                .comHorarioAlarme(LocalDateTime.of(2026, 11, 15, 9, 0))
                .build();

        assertAll(
                () -> assertEquals("Estudar Java", tarefa.getNome()),
                () -> assertEquals("Revisar testes unitários", tarefa.getDescricao()),
                () -> assertEquals(LocalDate.of(2026, 11, 15), tarefa.getDataTermino()),
                () -> assertEquals(3, tarefa.getPrioridade()),
                () -> assertEquals(categoria, tarefa.getCategoria()),
                () -> assertEquals(Status.TODO, tarefa.getStatus()),
                () -> assertEquals(LocalDateTime.of(2026, 11, 15, 9, 0), tarefa.getHorarioAlarme())
        );
    }

    @Test
    void deveLancarExcecaoQuandoPrioridadeForInvalida() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Tarefa.TarefaBuilder()
                        .comNome("Tarefa inválida")
                        .comDescricao("Prioridade fora do intervalo")
                        .comDataTermino(LocalDate.of(2026, 11, 15))
                        .comPrioridade(0)
                        .comCategoria(new Categoria("Trabalho"))
                        .build()
        );

        assertEquals("Erro: O número da prioridade deve ser entre 1 e 5", exception.getMessage());
    }

    @Test
    void deveAvancarStatusCorretamente() {
        assertAll(
                () -> assertEquals(Status.DOING, Status.TODO.avancar()),
                () -> assertEquals(Status.DONE, Status.DOING.avancar()),
                () -> assertEquals(Status.DONE, Status.DONE.avancar())
        );
    }
}
