package br.com.zg.todolistspec

import br.com.zg.todolist.backend.model.Categoria
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class CategoriaTest {

    @Test
    void deveCriarCategoriaERetornarNomeNoToString() {
        def categoria = new Categoria("Urgente")
        assertEquals("Urgente", categoria.getNome())
        assertEquals("Urgente", categoria.toString())
    }

    @Test
    void deveAtualizarNomeDaCategoriaComSucesso() {
        def categoria = new Categoria("Normal")
        categoria.setNome("Baixa Prioridade")
        assertEquals("Baixa Prioridade", categoria.getNome())
    }
}