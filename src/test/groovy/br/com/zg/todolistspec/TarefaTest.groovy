package br.com.zg.todolistspec

import br.com.zg.todolist.backend.model.Status
import br.com.zg.todolist.backend.model.factory.CategoriaFactory
import br.com.zg.todolist.backend.model.factory.TarefaFactory

import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import static org.junit.jupiter.api.Assertions.*

class TarefaTest {

    @Test
    void deveCriarTarefaComCategoriaInjetadaComSucesso() {
        def categoria = CategoriaFactory.getCategoria("Estudos")
        def data = LocalDate.of(2026, 12, 31)

        def tarefa = TarefaFactory.criarTarefaSimples("Estudar Java", "Revisar SOLID", data, 5, categoria)

        assertEquals("Estudar Java", tarefa.getNome())
        assertEquals("Estudos", tarefa.getCategoria().getNome())
        assertEquals(Status.TODO, tarefa.getStatus())
        assertEquals(5, tarefa.getPrioridade())
    }

    @Test
    void deveLancarExcecaoQuandoPrioridadeForInvalida() {
        def categoria = CategoriaFactory.getCategoria("Trabalho")
        def data = LocalDate.now()

        assertThrows(IllegalArgumentException) {
            TarefaFactory.criarTarefaSimples("Reunião", "Apresentação", data, 6, categoria)
        }

        assertThrows(IllegalArgumentException) {
            TarefaFactory.criarTarefaSimples("Reunião", "Apresentação", data, 0, categoria)
        }
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaForVaziaOuNula() {
        assertThrows(IllegalArgumentException) {
            CategoriaFactory.getCategoria("")
        }

        assertThrows(IllegalArgumentException) {
            CategoriaFactory.getCategoria(null)
        }
    }

    @Test
    void deveFormatarToStringCorretamenteSemAlarme() {
        def categoria = CategoriaFactory.getCategoria("Casa")
        def data = LocalDate.of(2026, 10, 15)

        def tarefa = TarefaFactory.criarTarefaSimples("Limpeza", "Limpar o quarto", data, 2, categoria)

        def esperado = "[TODO] Prioridade 2 | Limpeza (Categoria: Casa) - Limpar o quarto | Prazo: 15/10/2026"
        assertEquals(esperado, tarefa.toString())
    }

    @Test
    void deveFormatarToStringCorretamenteComAlarme() {
        def categoria = CategoriaFactory.getCategoria("Casa")
        def data = LocalDate.of(2026, 10, 15)
        def alarme = LocalDateTime.of(2026, 10, 15, 14, 30)

        def tarefa = TarefaFactory.criarTarefaComAlarme("Limpeza", "Limpar o quarto", data, 2, categoria, alarme)

        def esperado = "[TODO] Prioridade 2 | Limpeza (Categoria: Casa) - Limpar o quarto | Prazo: 15/10/2026 | Alarme: 15/10/2026 14:30"
        assertEquals(esperado, tarefa.toString())
    }
}