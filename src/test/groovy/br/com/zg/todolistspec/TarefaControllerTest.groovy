package br.com.zg.todolist.backend.controller


import br.com.zg.todolist.backend.model.service.GerenciadorTarefasService
import br.com.zg.todolist.backend.model.service.IAlarmeService
import br.com.zg.todolist.backend.model.factory.TarefaFactory
import br.com.zg.todolist.backend.model.factory.CategoriaFactory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.time.LocalDate
import java.util.function.Predicate
import static org.junit.jupiter.api.Assertions.*

class TarefaControllerTest {

    @Test
    void deveCriarListarFiltrarTarefasEDesligarAlarmesPeloController() {

        def alarmeServiceMock = [
                configurarAlarme: { tarefa, minutos -> },
                encerrarAlarmes: { -> }
        ] as IAlarmeService

        def service = new GerenciadorTarefasService(alarmeServiceMock)
        def controller = new TarefaController(service)

        def categoria = CategoriaFactory.getCategoria("Estudos")
        def dataVencimento = LocalDate.now().plusDays(1)

        def tarefa = TarefaFactory.criarTarefaSimples(
                "Estudar MVC",
                "Entender a camada de controle",
                dataVencimento,
                1,
                categoria
        )

        controller.criarTarefa(tarefa)
        controller.agendarAlarme(tarefa, 10L)

        def tarefas = controller.listarTodasTarefas()
        def tarefasFiltradas = controller.filtrarTarefas({ t -> t.getPrioridade() == 1 } as Predicate)

        assertEquals(1, tarefas.size())
        assertEquals(1, tarefasFiltradas.size())
        assertEquals("Estudar MVC", tarefas.get(0).getNome())

        assertDoesNotThrow({ controller.desligarAlarmes() } as Executable)
    }
}