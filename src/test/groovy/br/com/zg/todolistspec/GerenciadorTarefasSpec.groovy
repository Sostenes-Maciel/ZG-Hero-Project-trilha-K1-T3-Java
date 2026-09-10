package br.com.zg.todolistspec

import br.com.zg.todolist.model.Tarefa
import br.com.zg.todolist.service.GerenciadorTarefas
import spock.lang.Specification

import java.time.LocalDate

class GerenciadorTarefasSpec extends Specification {

    def "deve criar uma nova tarefa"() {

        given:
        GerenciadorTarefas gerenciador = new GerenciadorTarefas()

        Tarefa tarefa = new Tarefa(
                "Estudar Spock",
                "Criar testes unitários",
                LocalDate.of(2026, 9, 20),
                3,
                "Estudos"
        )

        when:
        gerenciador.adicionarTarefa(tarefa)

        then:
        gerenciador.ListarTarefa().size() == 1
        gerenciador.ListarTarefa().contains(tarefa)
    }

    def "deve listar as tarefas cadastradas"() {

        given:
        GerenciadorTarefas gerenciador = new GerenciadorTarefas()

        Tarefa tarefa1 = new Tarefa(
                "Estudar Java",
                "Revisar classes",
                LocalDate.of(2026, 9, 20),
                3,
                "Estudos"
        )

        Tarefa tarefa2 = new Tarefa(
                "Fazer exercício",
                "Resolver atividade",
                LocalDate.of(2026, 9, 21),
                2,
                "Faculdade"
        )

        gerenciador.adicionarTarefa(tarefa1)
        gerenciador.adicionarTarefa(tarefa2)

        when:
        def tarefas = gerenciador.ListarTarefa()

        then:
        tarefas.size() == 2
        tarefas.contains(tarefa1)
        tarefas.contains(tarefa2)
    }

    def "deve atualizar uma tarefa existente"() {

        given:
        GerenciadorTarefas gerenciador = new GerenciadorTarefas()

        Tarefa tarefaOriginal = new Tarefa(
                "Estudar Java",
                "Revisar classes",
                LocalDate.of(2026, 9, 20),
                3,
                "Estudos"
        )

        Tarefa tarefaAtualizada = new Tarefa(
                "Estudar Spock",
                "Criar testes",
                LocalDate.of(2026, 9, 25),
                5,
                "Testes"
        )

        gerenciador.adicionarTarefa(tarefaOriginal)

        when:
        boolean resultado = gerenciador.atualizarTarefa(0, tarefaAtualizada)

        then:
        resultado
        gerenciador.ListarTarefa().contains(tarefaAtualizada)
        !gerenciador.ListarTarefa().contains(tarefaOriginal)
    }

    def "deve excluir uma tarefa existente"() {

        given:
        GerenciadorTarefas gerenciador = new GerenciadorTarefas()

        Tarefa tarefa = new Tarefa(
                "Tarefa para excluir",
                "Teste de exclusão",
                LocalDate.of(2026, 9, 20),
                3,
                "Testes"
        )

        gerenciador.adicionarTarefa(tarefa)

        when:
        boolean resultado = gerenciador.removertarefa(0)

        then:
        resultado
        gerenciador.ListarTarefa().isEmpty()
    }
}