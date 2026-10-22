package br.com.zg.todolist.backend.factory

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class CategoriaFactoryTest {

    @Test
    void deveRetornarMesmaInstanciaParaMesmaCategoriaFlyweight() {
        def categoria1 = CategoriaFactory.getCategoria("Estudos")
        def categoria2 = CategoriaFactory.getCategoria("Estudos")

        def categoria3 = CategoriaFactory.getCategoria("Trabalho")

        assertSame(categoria1, categoria2, "O Flyweight falhou: gerou objetos diferentes para a mesma categoria!")

        assertNotSame(categoria1, categoria3, "O Flyweight falhou: categorias diferentes apontam para o mesmo objeto!")
    }
}