package br.com.zg.todolist.backend.model.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import br.com.zg.todolist.backend.model.Categoria;
import br.com.zg.todolist.backend.model.Tarefa;

class AlarmeServiceTest {

    @Test
    void deveConfigurarAlarmeComTempoValido() {
        AlarmeService service = new AlarmeService();
        Tarefa tarefa = new Tarefa.TarefaBuilder()
                .comNome("Estudar para prova")
                .comDescricao("Revisão final")
                .comDataTermino(LocalDate.of(2026, 11, 16))
                .comPrioridade(4)
                .comCategoria(new Categoria("Academico"))
                .build();

        service.configurarAlarme(tarefa, 5);

        assertAll(
                () -> assertNotNull(tarefa.getHorarioAlarme()),
                () -> assertTrue(tarefa.getHorarioAlarme().isAfter(LocalDateTime.now()))
        );

        service.encerrarAlarmes();
    }

    @Test
    void deveRejeitarAlarmesInvalidos() {
        AlarmeService service = new AlarmeService();
        Tarefa tarefaValida = new Tarefa.TarefaBuilder()
                .comNome("Tarefa válida")
                .comDescricao("Teste")
                .comDataTermino(LocalDate.of(2026, 11, 16))
                .comPrioridade(2)
                .comCategoria(new Categoria("Pessoal"))
                .build();

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> service.configurarAlarme(null, 5)),
                () -> assertThrows(IllegalArgumentException.class, () -> service.configurarAlarme(tarefaValida, 0)),
                () -> assertThrows(IllegalArgumentException.class, () -> service.configurarAlarme(tarefaValida, -1))
        );

        service.encerrarAlarmes();
    }
}
