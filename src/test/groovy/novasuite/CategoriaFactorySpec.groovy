package novasuite

import br.com.zg.todolist.backend.factory.CategoriaFactory
import br.com.zg.todolist.backend.model.Categoria
import spock.lang.Specification
import spock.lang.Unroll

class CategoriaFactorySpec extends Specification {

    @Unroll
    def "Dado nomes equivalentes, quando a categoria é solicitada, então a fábrica reutiliza a mesma instância"() {
        when:
        Categoria categoriaOriginal = CategoriaFactory.getCategoria(nomeOriginal)
        Categoria categoriaEquivalente = CategoriaFactory.getCategoria(nomeEquivalente)

        then:
        categoriaOriginal.is(categoriaEquivalente)
        categoriaOriginal.nome == "Estudos"

        where:
        nomeOriginal | nomeEquivalente
        "Estudos"    | "estudos"
        "Estudos"    | "  Estudos  "
        "  Estudos " | " ESTUDOS "
    }

    def "Dado um nome válido com espaços externos, quando a categoria é criada, então o nome armazenado é normalizado"() {
        when:
        def categoria = CategoriaFactory.getCategoria("  Trabalho  ")

        then:
        categoria.nome == "Trabalho"
    }

    @Unroll
    def "Dado um nome de categoria inválido, quando a categoria é solicitada, então ocorre IllegalArgumentException"() {
        when:
        CategoriaFactory.getCategoria(nomeInvalido)

        then:
        thrown(IllegalArgumentException)

        where:
        nomeInvalido << [null, "", "   "]
    }
}
