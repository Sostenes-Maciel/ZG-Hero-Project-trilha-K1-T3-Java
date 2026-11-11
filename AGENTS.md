# Contexto para Agentes de IA (ToDo List)

## Visão Geral
Aplicação de ToDo List com as seguintes características e integrações:
- **Funcionalidades Gerais:**
    - Cadastro, listagem, edição e remoção de tarefas.
    - Persistência das tarefas com LocalStorage.
    - Definição e alteração em lote de status (TODO, DOING, DONE).
    - Filtros por Categoria, Prioridade, Status e combinação entre eles.
    - Configuração de horário para alarme.
    - Mensagens de confirmação para as ações.
    - Formatação da data para o padrão brasileiro.
    - Modo de alto contraste com preferência salva no LocalStorage.
- **Integração com Resend (E-mail):** O destinatário é configurado no backend. A criação e as alterações das tarefas continuam salvas no navegador mesmo se o envio do e-mail falhar. Notificações por e-mail são enviadas ao criar uma tarefa ou concluí-la.
- **Integração com Telegram:** Recebimento de notificações automáticas sobre eventos das tarefas (Tarefa criada, Tarefa editada, e Prazo próximo - lembrete de vencimento para o dia seguinte).


O objetivo atual do projeto é implementar uma suíte de testes unitários abrangente para cobrir funcionalidades legadas sem cobertura (como filtros avançados, transições de status e regras de alarmes/notificações).

## Stack e Estrutura
- **Linguagens:** Java e Groovy(Backend) e JavaScript(Frontend).
- **Gerenciador de Build:** Gradle.
- **Arquitetura:** MVC (Model-View-Controller).
- **Design Patterns:** Builder (`TarefaBuilder`), Factory Method (`TarefaFactory` e `CategoriaFactory`), Flyweight (`CategoriaFactory`), State (`Status`), Strategy (`EstrategiaOrdenacao`) e Observer (`AlarmeObserver`).
- **Testes:** Spock Framework e JUnit 5.

## Como Executar e Testar
- **Executar a aplicação:** `./gradlew run` (ou via atalho na IDE - IntelliJ).
- **Rodar os testes:** `./gradlew test` (ou executando as especificações do Spock diretamente pelo painel do IntelliJ).

## Convenções
- Os testes devem ser escritos em Groovy usando o Spock, focando em testes descritivos e orientados a comportamento (BDD).
- O idioma padrão de variáveis, métodos de teste e documentação é o Português (BR).
- Casos de teste devem seguir a estrutura `Dado / Quando / Então` (given/when/then no Spock).
- **Organização de Diretórios:** Os novos testes criados nesta etapa devem ser colocados em um diretório/pacote à parte dos testes já existentes (ex: `src/test/groovy/novasuite`), garantindo o isolamento da nova feature.

## Restrições
- O foco exclusivo desta etapa é a criação de testes. Não alterar o código de produção das funcionalidades existentes, a menos que um bug real seja descoberto durante a escrita dos testes.
- Se um bug for encontrado, ele deve ser documentado e a correção deve ser incluída como uma tarefa separada no plano de ação.
- Nenhuma modificação deve ocorrer sem aprovação prévia da spec (`docs/spec.md`) e do plano (`docs/plano.md`).