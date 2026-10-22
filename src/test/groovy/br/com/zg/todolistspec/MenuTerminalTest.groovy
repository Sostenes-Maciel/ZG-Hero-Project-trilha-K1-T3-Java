package br.com.zg.todolistspec

import br.com.zg.todolist.backend.model.Tarefa
import br.com.zg.todolist.backend.service.IAlarmeService
import br.com.zg.todolist.backend.service.IGerenciadorTarefas
import br.com.zg.todolist.backend.view.MenuTerminalView
import br.com.zg.todolist.backend.observer.AlarmeObserver
import br.com.zg.todolist.backend.strategy.EstrategiaOrdenacao
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.util.function.Predicate

import static org.junit.jupiter.api.Assertions.*

class MenuTerminalTest {

    private InputStream originalIn
    private PrintStream originalOut
    private ByteArrayOutputStream outContent

    @BeforeEach
    void setUp() {
        originalIn = System.in
        originalOut = System.out
        outContent = new ByteArrayOutputStream()
        System.setOut(new PrintStream(outContent))
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn)
        System.setOut(originalOut)
    }

    @Test
    void deveSairDoMenuAoEscolherOpcao6() {
        String input = "6\n"
        System.setIn(new ByteArrayInputStream(input.getBytes()))

        def gerenciadorMock = new GerenciadorTarefasMock()
        def alarmeMock = new AlarmeServiceParaMenuMock()
        def menu = new MenuTerminalView(gerenciadorMock, alarmeMock)

        menu.iniciar()

        assertTrue(outContent.toString().contains("Saindo. Até logo!"))
        assertTrue(alarmeMock.isEncerrado())
    }

    @Test
    void deveExibirMensagemQuandoNenhumaTarefaForEncontrada() {
        String input = "2\n6\n"
        System.setIn(new ByteArrayInputStream(input.getBytes()))

        def gerenciadorMock = new GerenciadorTarefasMock()
        def alarmeMock = new AlarmeServiceParaMenuMock()
        def menu = new MenuTerminalView(gerenciadorMock, alarmeMock)

        menu.iniciar()

        assertTrue(outContent.toString().contains("Nenhuma tarefa foi encontrada!"))
    }
}

class GerenciadorTarefasMock implements IGerenciadorTarefas {
    private List<Tarefa> tarefas = new ArrayList<>()

    @Override
    void adicionarTarefa(Tarefa tarefa) {
        tarefas.add(tarefa)
    }

    @Override
    void atualizarTarefa(int indice, Tarefa tarefaAtualizada) {
    }

    @Override
    void removerTarefa(int indice) {
    }

    @Override
    List<Tarefa> listarTarefas() {
        return tarefas
    }

    @Override
    List<Tarefa> filtrarTarefas(Predicate<Tarefa> criterio) {
        return []
    }

    void setEstrategiaOrdenacao(EstrategiaOrdenacao estrategia) {
    }
}

class AlarmeServiceParaMenuMock implements IAlarmeService {
    private boolean encerrado = false

    @Override
    void configurarAlarme(Tarefa tarefa, long minutos) {
    }

    @Override
    void encerrarAlarmes() {
        this.encerrado = true
    }

    void adicionarObservador(AlarmeObserver observador) {
    }

    void removerObservador(AlarmeObserver observador) {
    }

    boolean isEncerrado() {
        return encerrado
    }
}