package br.com.zg.todolistspec

import br.com.zg.todolist.backend.model.Categoria
import br.com.zg.todolist.backend.model.Status
import br.com.zg.todolist.backend.model.Tarefa

import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import static org.junit.jupiter.api.Assertions.*

class TarefaTest {

    @Test
    void deveCriarTarefaComCategoriaInjetadaComSucesso() {
        def categoria = new Categoria("Estudos")
        def data = LocalDate.of(2026, 12, 31)

        def tarefa = new Tarefa("Estudar Java", "Revisar SOLID", data, 5, categoria)

        assertEquals("Estudar Java", tarefa.getNome())
        assertEquals("Estudos", tarefa.getCategoria().getNome())
        assertEquals(Status.TODO, tarefa.getStatus())
        assertEquals(5, tarefa.getPrioridade())
    }

    @Test
    void deveLancarExcecaoQuandoPrioridadeForInvalida() {
        def categoria = new Categoria("Trabalho")
        def data = LocalDate.now()

        assertThrows(IllegalArgumentException) {
            new Tarefa("Reunião", "Apresentação", data, 6, categoria)
        }

        assertThrows(IllegalArgumentException) {
            new Tarefa("Reunião", "Apresentação", data, 0, categoria)
        }
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaForVaziaOuNula() {
        assertThrows(IllegalArgumentException) {
            new Categoria("")
        }

        assertThrows(IllegalArgumentException) {
            new Categoria(null)
        }
    }

    @Test
    void deveFormatarToStringCorretamenteSemAlarme() {
        def categoria = new Categoria("Casa")
        def data = LocalDate.of(2026, 10, 15)
        def tarefa = new Tarefa("Limpeza", "Limpar o quarto", data, 2, categoria)

        def esperado = "[TODO] Prioridade 2 | Limpeza (Categoria: Casa) - Limpar o quarto | Prazo: 15/10/2026"
        assertEquals(esperado, tarefa.toString())
    }

    @Test
    void deveFormatarToStringCorretamenteComAlarme() {
        def categoria = new Categoria("Casa")
        def data = LocalDate.of(2026, 10, 15)
        def tarefa = new Tarefa("Limpeza", "Limpar o quarto", data, 2, categoria)

        tarefa.setHorarioAlarme(LocalDateTime.of(2026, 10, 15, 14, 30))

        def esperado = "[TODO] Prioridade 2 | Limpeza (Categoria: Casa) - Limpar o quarto | Prazo: 15/10/2026 | Alarme: 15/10/2026 14:30"
        assertEquals(esperado, tarefa.toString())
    }
}