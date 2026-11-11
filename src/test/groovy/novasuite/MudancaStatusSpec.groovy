package novasuite

import br.com.zg.todolist.backend.factory.CategoriaFactory
import br.com.zg.todolist.backend.factory.TarefaFactory
import br.com.zg.todolist.backend.model.Status
import br.com.zg.todolist.backend.model.Tarefa
import br.com.zg.todolist.backend.service.GerenciadorTarefasService
import br.com.zg.todolist.backend.service.IAlarmeService
import spock.lang.Specification

import java.time.LocalDate

class MudancaStatusSpec extends Specification {

    private GerenciadorTarefasService gerenciador

    def setup() {
        gerenciador = new GerenciadorTarefasService(Mock(IAlarmeService))
    }

    def cleanup() {
        gerenciador.desligarAlarmes()
    }

    def "Dado uma tarefa válida, quando seu status é alterado para DONE, então a tarefa fica concluída"() {
        given:
        def tarefa = criarTarefa("Finalizar atividade", LocalDate.now(), Status.TODO)
        gerenciador.adicionarTarefa(tarefa)
        def atualizada = criarTarefa("Finalizar atividade", LocalDate.now(), Status.DONE)

        when:
        gerenciador.atualizarTarefa(0, atualizada)

        then:
        gerenciador.listarTarefas()[0].status == Status.DONE
    }

    def "Dado uma tarefa atrasada com dados válidos, quando seu status é alterado para DONE, então a conclusão é permitida"() {
        given:
        def dataAtrasada = LocalDate.now().minusDays(1)
        def tarefa = criarTarefa("Atividade atrasada", dataAtrasada, Status.TODO)
        gerenciador.adicionarTarefa(tarefa)
        def concluida = criarTarefa("Atividade atrasada", dataAtrasada, Status.DONE)

        when:
        gerenciador.atualizarTarefa(0, concluida)

        then:
        gerenciador.listarTarefas()[0].status == Status.DONE
        gerenciador.listarTarefas()[0].dataTermino == dataAtrasada
    }

    def "Dado uma tarefa TODO, quando seu status avança pelo modelo, então passa para DOING e depois DONE"() {
        given:
        def tarefa = criarTarefa("Atividade em andamento", LocalDate.now(), Status.TODO)

        when:
        tarefa.setStatus(tarefa.status.avancar())
        tarefa.setStatus(tarefa.status.avancar())

        then:
        tarefa.status == Status.DONE
    }

    def "Dado uma atualização nula, quando a mudança de status é solicitada, então falha com IllegalArgumentException"() {
        given:
        def tarefa = criarTarefa("Atividade válida", LocalDate.now(), Status.TODO)
        gerenciador.adicionarTarefa(tarefa)

        when:
        gerenciador.atualizarTarefa(0, null)

        then:
        def excecao = thrown(IllegalArgumentException)
        excecao.message == "A tarefa não pode ser nula."
        gerenciador.listarTarefas()[0].status == Status.TODO
    }

    def "Dado uma prioridade fora do intervalo permitido, quando a tarefa é criada, então falha com IllegalArgumentException"() {
        when:
        TarefaFactory.criarTarefaSimples(
                "Atividade inválida", "Descrição", LocalDate.now(), 6,
                CategoriaFactory.getCategoria("Estudos"))

        then:
        def excecao = thrown(IllegalArgumentException)
        excecao.message == "Erro: O número da prioridade deve ser entre 1 e 5"
    }

    def "Dado um índice inexistente, quando a mudança de status é solicitada, então falha com IndexOutOfBoundsException"() {
        given:
        def tarefa = criarTarefa("Atividade válida", LocalDate.now(), Status.TODO)
        gerenciador.adicionarTarefa(tarefa)
        def atualizada = criarTarefa("Atividade válida", LocalDate.now(), Status.DONE)

        when:
        gerenciador.atualizarTarefa(1, atualizada)

        then:
        thrown(IndexOutOfBoundsException)
    }

    private static Tarefa criarTarefa(String nome, LocalDate dataTermino, Status status) {
        def tarefa = TarefaFactory.criarTarefaSimples(
                nome,
                "Descrição de teste",
                dataTermino,
                3,
                CategoriaFactory.getCategoria("Estudos"))
        tarefa.setStatus(status)
        return tarefa
    }
}
