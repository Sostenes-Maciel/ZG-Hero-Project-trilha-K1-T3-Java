package br.com.zg.todolistspec

import br.com.zg.todolist.backend.model.Status
import br.com.zg.todolist.backend.model.Tarefa
import br.com.zg.todolist.backend.model.factory.CategoriaFactory
import br.com.zg.todolist.backend.model.factory.TarefaFactory
import br.com.zg.todolist.backend.model.observer.AlarmeObserver
import br.com.zg.todolist.backend.model.service.GerenciadorTarefasService
import br.com.zg.todolist.backend.model.service.IAlarmeService
import br.com.zg.todolist.backend.model.strategy.OrdenacaoPorData

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
        def categoria = CategoriaFactory.getCategoria("Estudos")
        def t1 = TarefaFactory.criarTarefaSimples("Baixa", "Desc", LocalDate.now().plusDays(2), 1, categoria)
        def t2 = TarefaFactory.criarTarefaSimples("Alta", "Desc", LocalDate.now().plusDays(2), 5, categoria)
        def t3 = TarefaFactory.criarTarefaSimples("Alta Perto", "Desc", LocalDate.now().plusDays(1), 5, categoria)

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
    void deveAlterarEstrategiaDeOrdenacaoComSucesso() {
        def categoria = CategoriaFactory.getCategoria("Estudos")
        def t1 = TarefaFactory.criarTarefaSimples("Para Hoje", "Desc", LocalDate.now(), 1, categoria)
        def t2 = TarefaFactory.criarTarefaSimples("Para Amanha", "Desc", LocalDate.now().plusDays(1), 5, categoria)

        gerenciador.adicionarTarefa(t1)
        gerenciador.adicionarTarefa(t2)

        gerenciador.setEstrategiaOrdenacao(new OrdenacaoPorData())

        def tarefas = gerenciador.listarTarefas()

        assertEquals("Para Hoje", tarefas[0].getNome())
        assertEquals("Para Amanha", tarefas[1].getNome())
    }

    @Test
    void deveAtualizarTarefaComSucesso() {
        def categoria = CategoriaFactory.getCategoria("Estudos")
        def original = TarefaFactory.criarTarefaSimples("Original", "Desc", LocalDate.now(), 3, categoria)
        gerenciador.adicionarTarefa(original)

        def atualizada = TarefaFactory.criarTarefaSimples("Atualizada", "Desc", LocalDate.now(), 4, categoria)
        gerenciador.atualizarTarefa(0, atualizada)

        assertEquals("Atualizada", gerenciador.listarTarefas()[0].getNome())
        assertEquals(4, gerenciador.listarTarefas()[0].getPrioridade())
    }

    @Test
    void deveLancarExcecaoAoAtualizarOuRemoverIndiceInvalido() {
        def categoria = CategoriaFactory.getCategoria("Estudos")
        def tarefa = TarefaFactory.criarTarefaSimples("Teste", "Desc", LocalDate.now(), 3, categoria)

        assertThrows(IndexOutOfBoundsException) {
            gerenciador.atualizarTarefa(0, tarefa)
        }

        assertThrows(IndexOutOfBoundsException) {
            gerenciador.removerTarefa(0)
        }
    }

    @Test
    void deveRemoverTarefaComSucesso() {
        def categoria = CategoriaFactory.getCategoria("Estudos")
        def tarefa = TarefaFactory.criarTarefaSimples("Teste", "Desc", LocalDate.now(), 3, categoria)
        gerenciador.adicionarTarefa(tarefa)

        gerenciador.removerTarefa(0)

        assertTrue(gerenciador.listarTarefas().isEmpty())
    }

    @Test
    void deveFiltrarTarefasComPredicate() {
        def cat1 = CategoriaFactory.getCategoria("Trabalho")
        def cat2 = CategoriaFactory.getCategoria("Lazer")

        def t1 = TarefaFactory.criarTarefaSimples("T1", "Desc", LocalDate.now(), 3, cat1)
        def t2 = TarefaFactory.criarTarefaSimples("T2", "Desc", LocalDate.now(), 3, cat2)

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
        def categoria = CategoriaFactory.getCategoria("Estudos")
        def tarefa = TarefaFactory.criarTarefaSimples("Teste Alarme", "Desc", LocalDate.now(), 3, categoria)

        gerenciador.agendarAlarmeSeNecessario(tarefa, 15)

        assertTrue(alarmeServiceMock.isAlarmeChamado())
        assertEquals(15, alarmeServiceMock.getMinutosConfigurados())
    }

    @Test
    void naoDeveAgendarAlarmeSeTempoForZeroOuMenor() {
        def categoria = CategoriaFactory.getCategoria("Estudos")
        def tarefa = TarefaFactory.criarTarefaSimples("Teste Alarme", "Desc", LocalDate.now(), 3, categoria)

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
        def categoria = CategoriaFactory.getCategoria("Estudos")
        def tarefa = TarefaFactory.criarTarefaSimples("Valida", "Desc", LocalDate.now(), 3, categoria)
        gerenciador.adicionarTarefa(tarefa)

        assertThrows(IllegalArgumentException) {
            gerenciador.atualizarTarefa(0, null)
        }
    }

    @Test
    void deveProvarOPrincipioOCPCombinandoMultiplosFiltros() {
        def catEstudos = CategoriaFactory.getCategoria("Estudos")
        def catLazer = CategoriaFactory.getCategoria("Lazer")

        def t1 = TarefaFactory.criarTarefaSimples("T1", "Alta", LocalDate.now(), 5, catEstudos)
        def t2 = TarefaFactory.criarTarefaSimples("T2", "Alta feita", LocalDate.now(), 5, catEstudos)
        t2.setStatus(Status.DONE)

        def t3 = TarefaFactory.criarTarefaSimples("T3", "Baixa", LocalDate.now(), 1, catLazer)

        gerenciador.adicionarTarefa(t1)
        gerenciador.adicionarTarefa(t2)
        gerenciador.adicionarTarefa(t3)

        def resultado = gerenciador.filtrarTarefas { t ->
            t.getPrioridade() == 5 && t.getStatus() == Status.TODO
        }

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

    void adicionarObservador(AlarmeObserver observador) {
    }

    void removerObservador(AlarmeObserver observador) {
    }

    boolean isAlarmeChamado() {
        return alarmeChamado
    }

    long getMinutosConfigurados() {
        return minutosConfigurados
    }
}