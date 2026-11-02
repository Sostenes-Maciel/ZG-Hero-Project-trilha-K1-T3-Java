package br.com.zg.todolist.backend.model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.zg.todolist.backend.controller.CategoriaController;
import br.com.zg.todolist.backend.controller.TarefaController;
import br.com.zg.todolist.backend.factory.CategoriaFactory;
import br.com.zg.todolist.backend.factory.TarefaFactory;
import br.com.zg.todolist.backend.observer.ConsoleAlarmeObserver;
import br.com.zg.todolist.backend.service.AlarmeService;
import br.com.zg.todolist.backend.service.GerenciadorTarefasService;
import br.com.zg.todolist.backend.strategy.OrdenacaoPorData;

class CoberturaExtraTest {

    @Test
    void categoriaDeveValidarNomeEPermitirAlteracao() {
        Categoria categoria = new Categoria("Casa");

        assertAll(
                () -> assertEquals("Casa", categoria.getNome()),
                () -> assertThrows(IllegalArgumentException.class, () -> new Categoria("   ")),
                () -> assertThrows(IllegalArgumentException.class, () -> new Categoria(null))
        );

        categoria.setNome("Trabalho");
        assertEquals("Trabalho", categoria.getNome());
    }

    @Test
    void categoriaFactoryDeveCachearCategoriaMesmoNomeComDiferentesCasos() {
        Categoria categoria1 = CategoriaFactory.getCategoria(" estudo ");
        Categoria categoria2 = CategoriaFactory.getCategoria("ESTUDO");

        assertAll(
                () -> assertSame(categoria1, categoria2),
                () -> assertEquals("estudo", categoria1.getNome()),
                () -> assertThrows(IllegalArgumentException.class, () -> CategoriaFactory.getCategoria("   "))
        );
    }

    @Test
    void tarefaFactoryDeveCriarTarefaSimplesEComAlarme() {
        Categoria categoria = new Categoria("Pessoal");

        Tarefa simples = TarefaFactory.criarTarefaSimples(
                "Comprar pão",
                "Comprar 2 pães",
                LocalDate.of(2026, 11, 25),
                2,
                categoria
        );

        Tarefa comAlarme = TarefaFactory.criarTarefaComAlarme(
                "Reunião",
                "Apresentar progresso",
                LocalDate.of(2026, 11, 26),
                5,
                categoria,
                LocalDateTime.of(2026, 11, 26, 9, 0)
        );

        assertAll(
                () -> assertEquals("Comprar pão", simples.getNome()),
                () -> assertEquals("Comprar 2 pães", simples.getDescricao()),
                () -> assertEquals(2, simples.getPrioridade()),
                () -> assertEquals(categoria, simples.getCategoria()),
                () -> assertEquals("Reunião", comAlarme.getNome()),
                () -> assertEquals(LocalDateTime.of(2026, 11, 26, 9, 0), comAlarme.getHorarioAlarme())
        );
    }

    @Test
    void ordenacaoPorDataDeveOrdenarTarefasCronologicamente() {
        Tarefa tarefa3 = new Tarefa.TarefaBuilder()
                .comNome("Tarefa 3")
                .comDescricao("descrição")
                .comDataTermino(LocalDate.of(2026, 12, 5))
                .comPrioridade(2)
                .comCategoria(new Categoria("Casa"))
                .build();

        Tarefa tarefa1 = new Tarefa.TarefaBuilder()
                .comNome("Tarefa 1")
                .comDescricao("descrição")
                .comDataTermino(LocalDate.of(2026, 12, 1))
                .comPrioridade(3)
                .comCategoria(new Categoria("Casa"))
                .build();

        Tarefa tarefa2 = new Tarefa.TarefaBuilder()
                .comNome("Tarefa 2")
                .comDescricao("descrição")
                .comDataTermino(LocalDate.of(2026, 12, 3))
                .comPrioridade(2)
                .comCategoria(new Categoria("Casa"))
                .build();

        List<Tarefa> tarefas = new ArrayList<>(List.of(tarefa3, tarefa1, tarefa2));
        new OrdenacaoPorData().ordenar(tarefas);

        assertAll(
                () -> assertEquals("Tarefa 1", tarefas.getFirst().getNome()),
                () -> assertEquals("Tarefa 2", tarefas.get(1).getNome()),
                () -> assertEquals("Tarefa 3", tarefas.get(2).getNome())
        );
    }

    @Test
    void tarefaControllerDeveDelegarOperacoesAoService() {
        GerenciadorTarefasService service = new GerenciadorTarefasService(new AlarmeService());
        TarefaController controller = new TarefaController(service);
        CategoriaController categoriaController = new CategoriaController();

        Categoria categoria = categoriaController.obterCategoria("Academico");
        Tarefa tarefa1 = TarefaFactory.criarTarefaSimples("Ler capítulo", "ler 20 páginas", LocalDate.of(2026, 11, 20), 1, categoria);
        Tarefa tarefa2 = TarefaFactory.criarTarefaSimples("Responder e-mail", "revisar 5 pendências", LocalDate.of(2026, 11, 21), 4, categoria);

        controller.criarTarefa(tarefa1);
        controller.criarTarefa(tarefa2);

        List<Tarefa> tarefasAntes = controller.listarTodasTarefas();
        int indiceTarefa1 = tarefasAntes.indexOf(tarefa1);

        assertEquals(2, tarefasAntes.size());

        controller.atualizarTarefa(indiceTarefa1, TarefaFactory.criarTarefaSimples("Ler capítulo final", "ler 30 páginas", LocalDate.of(2026, 11, 22), 5, categoria));
        assertEquals("Ler capítulo final", controller.listarTodasTarefas().stream()
                .filter(t -> t.getPrioridade() == 5)
                .findFirst()
                .orElseThrow()
                .getNome());

        List<Tarefa> filtradas = controller.filtrarTarefas(t -> t.getPrioridade() >= 4);
        assertEquals(2, filtradas.size());

        controller.agendarAlarme(tarefa2, 1);
        assertNotNull(tarefa2.getHorarioAlarme());

        controller.removerTarefa(0);
        assertEquals(1, controller.listarTodasTarefas().size());
        controller.desligarAlarmes();
    }

    @Test
    void consoleAlarmeObserverDeveImprimirDadosDaTarefa() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));

        try {
            Tarefa tarefa = TarefaFactory.criarTarefaSimples(
                    "Fazer revisão",
                    "Estudar patterns",
                    LocalDate.of(2026, 11, 30),
                    3,
                    new Categoria("Estudo")
            );

            new ConsoleAlarmeObserver().onAlarmeDisparado(tarefa);

            String saida = buffer.toString(StandardCharsets.UTF_8);
            assertAll(
                    () -> assertTrue(saida.contains("Fazer revisão")),
                    () -> assertTrue(saida.contains("Estudar patterns")),
                    () -> assertTrue(saida.contains("Prioridade: 3"))
            );
        } finally {
            System.setOut(originalOut);
        }
    }
}
