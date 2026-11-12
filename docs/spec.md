# Especificação: Suíte de Testes Unitários

## Contexto e Objetivo
A aplicação ToDo List possui funcionalidades core no backend que atualmente carecem de testes automatizados. O objetivo desta feature é criar uma suíte de testes unitários em Groovy utilizando o framework Spock, a fim de garantir o comportamento correto dos filtros combinados, das transições individuais de status e dos gatilhos de notificações (e-mail via Resend e mensagens via Telegram).

## Regras de Negócio
1. **Filtros (Categoria, Prioridade, Status, Data):**
    - A combinação de múltiplos filtros obedece à lógica `AND` (a tarefa deve atender simultaneamente a todos os critérios informados).
    - Não há cenários de filtragem com lógica `OR`.
2. **Mudança de Status:**
    - Para ter o status alterado, a tarefa deve respeitar as validações normais do modelo (ex: título preenchido, data válida e demais campos obrigatórios).
    - Não há bloqueios restritivos para concluir uma tarefa que esteja com a data de vencimento atrasada.
3. **Alarmes e Notificações:**
    - O acionamento dos gatilhos para `EnviadorEmail` e `EnviadorMensagemTelegram` deve ser garantido pelos testes, mas a execução deve ser isolada através de Mocks/Stubs.
    - Nenhuma mensagem real (e-mail ou Telegram) deve ser enviada durante a execução da suíte de testes.
4. **Persistência e Frontend:**
    - O foco é validar o comportamento dos controllers, handlers e services do backend. O comportamento de leitura/gravação no `LocalStorage` pertence ao frontend e não faz parte desta suíte em Java/Groovy.

## Critérios de Aceite
- [X] **Dado** que múltiplos filtros são aplicados, **Quando** a busca é executada, **Então** o sistema deve retornar apenas as tarefas que satisfazem todos os filtros simultaneamente.
- [X] **Dado** uma requisição para concluir uma tarefa atrasada, **Quando** a alteração de status for acionada, **Então** o sistema deve permitir a mudança para `DONE` normalmente, se os dados obrigatórios forem válidos.
- [X] **Dado** a criação ou conclusão de uma tarefa, **Quando** a ação ocorrer, **Então** o teste deve verificar via Mocks se os serviços `EnviadorEmail` e `EnviadorMensagemTelegram` foram invocados com os parâmetros corretos.
- [X] Todos os novos testes devem rodar e passar com sucesso (`./gradlew test`).

## Impacto no que já existe
As funcionalidades de produção a serem testadas já estão implementadas em `src/main`, incluindo os filtros, as transições de status e as integrações de notificações por e-mail e Telegram. Nesta etapa, não serão feitas novas alterações nas regras de negócio nem nas integrações existentes. Serão criadas apenas novas classes de teste (`*Spec.groovy`) em um diretório separado, como `src/test/groovy/novasuite`, mantendo o isolamento da suíte. Correções em produção somente serão realizadas se os testes revelarem um bug real e impeditivo, que deverá ser documentado separadamente.

## Fora do Escopo
- Criação de novas rotas ou funcionalidades no ToDo List.
- Testes da camada visual, LocalStorage ou End-to-End (E2E) no front-end JS.
- Refatoração do código de produção (salvo correções mínimas caso os testes revelem bugs impeditivos).

## Ampliação de Cobertura — Issue #3

### Contexto e Objetivo
Ampliar a cobertura da funcionalidade legada `CategoriaFactory`, validando a normalização do nome e o reaproveitamento de instâncias implementado pelo padrão Flyweight.

### Escopo
- Testar nomes de categoria com diferenças de maiúsculas/minúsculas e espaços externos.
- Testar a rejeição de nomes nulos ou compostos apenas por espaços.
- Criar a cobertura em `src/test/groovy/novasuite/CategoriaFactorySpec.groovy`.

### Regras de Negócio
1. Nomes equivalentes após `trim` e conversão para maiúsculas devem retornar a mesma instância de `Categoria`.
2. A categoria armazenada deve preservar o nome normalizado sem espaços externos.
3. Nome nulo, vazio ou composto apenas por espaços deve lançar `IllegalArgumentException`.

### Critérios de Aceite
- [ ] Dado o mesmo nome em caixa diferente e com espaços externos, quando as categorias forem solicitadas, então a fábrica deve retornar a mesma instância.
- [ ] Dado um nome válido com espaços externos, quando a categoria for criada, então seu nome deve estar sem esses espaços.
- [ ] Dado um nome nulo, vazio ou somente com espaços, quando a categoria for solicitada, então deve ocorrer `IllegalArgumentException`.

### Impacto no que já existe
Somente uma nova especificação Spock será adicionada. Nenhum código de produção, contrato ou comportamento será alterado.