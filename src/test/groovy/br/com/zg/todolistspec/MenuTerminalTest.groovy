package br.com.zg.todolistspec

import br.com.zg.todolist.backend.model.Tarefa
import br.com.zg.todolist.backend.controller.TarefaController
import br.com.zg.todolist.backend.controller.CategoriaController
import br.com.zg.todolist.backend.model.service.GerenciadorTarefasService
import br.com.zg.todolist.backend.model.service.IAlarmeService
import br.com.zg.todolist.backend.view.MenuTerminalView
import br.com.zg.todolist.backend.model.observer.AlarmeObserver
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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

        def alarmeMock = new AlarmeServiceParaMenuMock()
        def gerenciador = new GerenciadorTarefasService(alarmeMock)
        def tarefaController = new TarefaController(gerenciador)
        def categoriaController = new CategoriaController()
        def menu = new MenuTerminalView(tarefaController, categoriaController)

        menu.iniciar()

        assertTrue(outContent.toString().contains("Saindo. Até logo!"))
        assertTrue(alarmeMock.isEncerrado())
    }

    @Test
    void deveExibirMensagemQuandoNenhumaTarefaForEncontrada() {
        String input = "2\n6\n"
        System.setIn(new ByteArrayInputStream(input.getBytes()))

        // Montando o MVC no teste com os dois controllers
        def alarmeMock = new AlarmeServiceParaMenuMock()
        def gerenciador = new GerenciadorTarefasService(alarmeMock)
        def tarefaController = new TarefaController(gerenciador)
        def categoriaController = new CategoriaController()
        def menu = new MenuTerminalView(tarefaController, categoriaController)

        menu.iniciar()

        assertTrue(outContent.toString().contains("Nenhuma tarefa foi encontrada!"))
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