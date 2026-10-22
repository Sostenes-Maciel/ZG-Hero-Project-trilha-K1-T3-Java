package br.com.zg.todolistspec

import br.com.zg.todolist.backend.factory.CategoriaFactory
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class CategoriaTest {

    @Test
    void deveCriarCategoriaERetornarNomeNoToString() {
        def categoria = CategoriaFactory.getCategoria("Urgente")
        assertEquals("Urgente", categoria.getNome())
        assertEquals("Urgente", categoria.toString())
    }

    @Test
    void deveAtualizarNomeDaCategoriaComSucesso() {
        def categoria = CategoriaFactory.getCategoria("Normal")
        categoria.setNome("Baixa Prioridade")
        assertEquals("Baixa Prioridade", categoria.getNome())
    }

    @Test
    void deveRetornarMesmaInstanciaParaMesmoNome() {
        def categoria1 = CategoriaFactory.getCategoria("Estudos")
        def categoria2 = CategoriaFactory.getCategoria("estudos ")

        assertSame(categoria1, categoria2)
    }
}