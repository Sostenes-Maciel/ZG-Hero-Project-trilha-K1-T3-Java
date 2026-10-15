package br.com.zg.todolist.backend.service

import br.com.zg.todolist.backend.model.Categoria
import br.com.zg.todolist.backend.model.Status
import br.com.zg.todolist.backend.model.Tarefa
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import static org.junit.jupiter.api.Assertions.*

class GerenciadorTarefasTest {

    private GerenciadorTarefasService gerenciador
    private AlarmeServiceMock alarmeServiceMock

    @BeforeEach
    void setUp() {
        alarmeServiceMock = new AlarmeServiceMock()
        gerenciador = new GerenciadorTarefasService(alarmeServiceMock)
    }

    @Test
    void deveAdicionarEOrdenarTarefasCorretamente() {
        def categoria = new Categoria("Estudos")
        def t1 = new Tarefa("Baixa", "Desc", LocalDate.now().plusDays(2), 1, categoria)
        def t2 = new Tarefa("Alta", "Desc", LocalDate.now().plusDays(2), 5, categoria)
        def t3 = new Tarefa("Alta Perto", "Desc", LocalDate.now().plusDays(1), 5, categoria)

        gerenciador.adicionarTarefa(t1)
        gerenciador.adicionarTarefa(t2)
        gerenciador.adicionarTarefa(t3)

        def tarefas = gerenciador.listarTarefas()

        assertEquals(3, tarefas.size())
        assertEquals("Alta Perto", tarefas[0].getNome())
        assertEquals("Alta", tarefas[1].getNome())
        assertEquals("Baixa", tarefas[2].getNome())
    }

    @Test
    void deveAtualizarTarefaComSucesso() {
        def categoria = new Categoria("Estudos")
        def original = new Tarefa("Original", "Desc", LocalDate.now(), 3, categoria)
        gerenciador.adicionarTarefa(original)

        def atualizada = new Tarefa("Atualizada", "Desc", LocalDate.now(), 4, categoria)
        gerenciador.atualizarTarefa(0, atualizada)

        assertEquals("Atualizada", gerenciador.listarTarefas()[0].getNome())
        assertEquals(4, gerenciador.listarTarefas()[0].getPrioridade())
    }

    @Test
    void deveLancarExcecaoAoAtualizarOuRemoverIndiceInvalido() {
        def categoria = new Categoria("Estudos")
        def tarefa = new Tarefa("Teste", "Desc", LocalDate.now(), 3, categoria)

        assertThrows(IndexOutOfBoundsException) {
            gerenciador.atualizarTarefa(0, tarefa)
        }

        assertThrows(IndexOutOfBoundsException) {
            gerenciador.removerTarefa(0)
        }
    }

    @Test
    void deveRemoverTarefaComSucesso() {
        def categoria = new Categoria("Estudos")
        def tarefa = new Tarefa("Teste", "Desc", LocalDate.now(), 3, categoria)
        gerenciador.adicionarTarefa(tarefa)

        gerenciador.removerTarefa(0)

        assertTrue(gerenciador.listarTarefas().isEmpty())
    }

    @Test
    void deveFiltrarTarefasComPredicate() {
        def cat1 = new Categoria("Trabalho")
        def cat2 = new Categoria("Lazer")

        def t1 = new Tarefa("T1", "Desc", LocalDate.now(), 3, cat1)
        def t2 = new Tarefa("T2", "Desc", LocalDate.now(), 3, cat2)

        t2.setStatus(Status.DONE)

        gerenciador.adicionarTarefa(t1)
        gerenciador.adicionarTarefa(t2)

        def resultadoCategoria = gerenciador.filtrarTarefas { t -> t.getCategoria().getNome() == "Trabalho" }
        assertEquals(1, resultadoCategoria.size())
        assertEquals("T1", resultadoCategoria[0].getNome())

        def resultadoStatus = gerenciador.filtrarTarefas { t -> t.getStatus() == Status.DONE }
        assertEquals(1, resultadoStatus.size())
        assertEquals("T2", resultadoStatus[0].getNome())
    }

    @Test
    void deveAgendarAlarmeAtravesDoServicoInjetado() {
        def categoria = new Categoria("Estudos")
        def tarefa = new Tarefa("Teste Alarme", "Desc", LocalDate.now(), 3, categoria)

        gerenciador.agendarAlarmeSeNecessario(tarefa, 15)

        assertTrue(alarmeServiceMock.isAlarmeChamado())
        assertEquals(15, alarmeServiceMock.getMinutosConfigurados())
    }

    @Test
    void naoDeveAgendarAlarmeSeTempoForZeroOuMenor() {
        def categoria = new Categoria("Estudos")
        def tarefa = new Tarefa("Teste Alarme", "Desc", LocalDate.now(), 3, categoria)

        gerenciador.agendarAlarmeSeNecessario(tarefa, 0)

        assertFalse(alarmeServiceMock.isAlarmeChamado())
    }
    @Test
    void deveLancarExcecaoAoTentarAdicionarTarefaNula() {
        assertThrows(IllegalArgumentException) {
            gerenciador.adicionarTarefa(null)
        }
    }

    @Test
    void deveLancarExcecaoAoTentarAtualizarComTarefaNula() {
        def categoria = new Categoria("Estudos")
        def tarefa = new Tarefa("Valida", "Desc", LocalDate.now(), 3, categoria)
        gerenciador.adicionarTarefa(tarefa)

        assertThrows(IllegalArgumentException) {
            gerenciador.atualizarTarefa(0, null)
        }
    }
    @Test
    void deveProvarOPrincipioOCPCombinandoMultiplosFiltros() {
        def catEstudos = new Categoria("Estudos")
        def catLazer = new Categoria("Lazer")

        def t1 = new Tarefa("T1", "Alta", LocalDate.now(), 5, catEstudos)
        def t2 = new Tarefa("T2", "Alta feita", LocalDate.now(), 5, catEstudos)
        t2.setStatus(Status.DONE) // T2 está concluída

        def t3 = new Tarefa("T3", "Baixa", LocalDate.now(), 1, catLazer)

        gerenciador.adicionarTarefa(t1)
        gerenciador.adicionarTarefa(t2)
        gerenciador.adicionarTarefa(t3)

        // MÁGICA DO OCP: Criamos um filtro complexo (Prioridade 5 + Status TODO) dinamicamente!
        def resultado = gerenciador.filtrarTarefas { t ->
            t.getPrioridade() == 5 && t.getStatus() == Status.TODO
        }

        // Deve encontrar apenas a T1, ignorando a T2 (que é DONE) e a T3 (Prioridade 1)
        assertEquals(1, resultado.size())
        assertEquals("T1", resultado[0].getNome())
    }
}

class AlarmeServiceMock implements IAlarmeService {

    private boolean alarmeChamado = false
    private long minutosConfigurados = 0

    @Override
    void configurarAlarme(Tarefa tarefa, long minutos) {
        this.alarmeChamado = true
        this.minutosConfigurados = minutos
    }

    @Override
    void encerrarAlarmes() {
    }

    boolean isAlarmeChamado() {
        return alarmeChamado
    }

    long getMinutosConfigurados() {
        return minutosConfigurados
    }

}