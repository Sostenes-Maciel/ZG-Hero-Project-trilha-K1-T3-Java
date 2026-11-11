package novasuite

import br.com.zg.todolist.backend.factory.CategoriaFactory
import br.com.zg.todolist.backend.factory.TarefaFactory
import br.com.zg.todolist.backend.model.Categoria
import br.com.zg.todolist.backend.model.Status
import br.com.zg.todolist.backend.model.Tarefa
import br.com.zg.todolist.backend.service.GerenciadorTarefasService
import br.com.zg.todolist.backend.service.IAlarmeService
import spock.lang.Specification

import java.time.LocalDate

class FiltroTarefasSpec extends Specification {

    private GerenciadorTarefasService gerenciador

    def setup() {
        gerenciador = new GerenciadorTarefasService(Mock(IAlarmeService))
    }

    def cleanup() {
        gerenciador.desligarAlarmes()
    }

    def "Dado tarefas de categorias diferentes, quando filtro por categoria, então retorna somente a categoria informada"() {
        given:
        def tarefaEstudos = criarTarefa("Estudar", 3, "Estudos", Status.TODO, DATA_BASE)
        def tarefaTrabalho = criarTarefa("Reunião", 3, "Trabalho", Status.TODO, DATA_BASE)
        adicionar(tarefaEstudos, tarefaTrabalho)

        when:
        def resultado = gerenciador.filtrarTarefas { Tarefa tarefa ->
            tarefa.getCategoria().getNome() == "Estudos"
        }

        then:
        resultado*.nome == ["Estudar"]
    }

    def "Dado tarefas com prioridades diferentes, quando filtro por prioridade, então retorna somente a prioridade informada"() {
        given:
        def tarefaPrioridadeAlta = criarTarefa("Urgente", 5, "Trabalho", Status.TODO, DATA_BASE)
        def tarefaPrioridadeBaixa = criarTarefa("Rotina", 1, "Trabalho", Status.TODO, DATA_BASE)
        adicionar(tarefaPrioridadeAlta, tarefaPrioridadeBaixa)

        when:
        def resultado = gerenciador.filtrarTarefas { Tarefa tarefa ->
            tarefa.getPrioridade() == 5
        }

        then:
        resultado*.nome == ["Urgente"]
    }

    def "Dado tarefas com status diferentes, quando filtro por status, então retorna somente o status informado"() {
        given:
        def tarefaAFazer = criarTarefa("Pendente", 3, "Estudos", Status.TODO, DATA_BASE)
        def tarefaConcluida = criarTarefa("Concluída", 3, "Estudos", Status.DONE, DATA_BASE)
        adicionar(tarefaAFazer, tarefaConcluida)

        when:
        def resultado = gerenciador.filtrarTarefas { Tarefa tarefa ->
            tarefa.getStatus() == Status.DONE
        }

        then:
        resultado*.nome == ["Concluída"]
    }

    def "Dado tarefas com datas de término diferentes, quando filtro por data, então retorna somente a data informada"() {
        given:
        def tarefaNoPrazo = criarTarefa("Prazo selecionado", 3, "Estudos", Status.TODO, DATA_BASE)
        def tarefaOutroPrazo = criarTarefa("Outro prazo", 3, "Estudos", Status.TODO, DATA_BASE.plusDays(1))
        adicionar(tarefaNoPrazo, tarefaOutroPrazo)

        when:
        def resultado = gerenciador.filtrarTarefas { Tarefa tarefa ->
            tarefa.getDataTermino() == DATA_BASE
        }

        then:
        resultado*.nome == ["Prazo selecionado"]
    }

    def "Dado filtros de categoria, prioridade, status e data, quando combinados, então retorna apenas a tarefa que atende todos"() {
        given:
        def tarefaCorrespondente = criarTarefa("Certificado", 5, "Estudos", Status.TODO, DATA_BASE)
        def categoriaDiferente = criarTarefa("Projeto", 5, "Trabalho", Status.TODO, DATA_BASE)
        def prioridadeDiferente = criarTarefa("Leitura", 3, "Estudos", Status.TODO, DATA_BASE)
        def statusDiferente = criarTarefa("Concluída", 5, "Estudos", Status.DONE, DATA_BASE)
        def dataDiferente = criarTarefa("Amanhã", 5, "Estudos", Status.TODO, DATA_BASE.plusDays(1))
        adicionar(tarefaCorrespondente, categoriaDiferente, prioridadeDiferente, statusDiferente, dataDiferente)

        when:
        def resultado = gerenciador.filtrarTarefas { Tarefa tarefa ->
            tarefa.getCategoria().getNome() == "Estudos" &&
                    tarefa.getPrioridade() == 5 &&
                    tarefa.getStatus() == Status.TODO &&
                    tarefa.getDataTermino() == DATA_BASE
        }

        then:
        resultado*.nome == ["Certificado"]
    }

    def "Dado filtros de categoria e status, quando uma tarefa não atende um critério, então ela é excluída"() {
        given:
        def tarefaPendente = criarTarefa("Pendente", 4, "Estudos", Status.TODO, DATA_BASE)
        def tarefaConcluida = criarTarefa("Concluída", 4, "Estudos", Status.DONE, DATA_BASE)
        def tarefaOutraCategoria = criarTarefa("Trabalho", 4, "Trabalho", Status.TODO, DATA_BASE)
        adicionar(tarefaPendente, tarefaConcluida, tarefaOutraCategoria)

        when:
        def resultado = gerenciador.filtrarTarefas { Tarefa tarefa ->
            tarefa.getCategoria().getNome() == "Estudos" && tarefa.getStatus() == Status.TODO
        }

        then:
        resultado*.nome == ["Pendente"]
    }

    private static final LocalDate DATA_BASE = LocalDate.of(2026, 11, 11)

    private void adicionar(Tarefa... tarefas) {
        tarefas.each { gerenciador.adicionarTarefa(it) }
    }

    private static Tarefa criarTarefa(String nome, int prioridade, String nomeCategoria,
                                      Status status, LocalDate dataTermino) {
        Categoria categoria = CategoriaFactory.getCategoria(nomeCategoria)
        Tarefa tarefa = TarefaFactory.criarTarefaSimples(
                nome, "Descrição de teste", dataTermino, prioridade, categoria)
        tarefa.setStatus(status)
        return tarefa
    }
}
