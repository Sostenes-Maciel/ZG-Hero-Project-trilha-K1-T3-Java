package br.com.zg.todolistspec

import br.com.zg.todolist.backend.model.Status
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class StatusTest {

    @Test
    void deveRetornarDescricoesCorretas() {
        assertEquals("A Fazer", Status.TODO.getDescricao())
        assertEquals("Em Andamento", Status.DOING.getDescricao())
        assertEquals("Concluído", Status.DONE.getDescricao())
    }

    @Test
    void deveRetornarONomeDoEnumNoToString() {
        assertEquals("TODO", Status.TODO.toString())
        assertEquals("DOING", Status.DOING.toString())
        assertEquals("DONE", Status.DONE.toString())
    }
}