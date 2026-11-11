# Plano de Implementação: Suíte de Testes Unitários

## Premissas da etapa
- Testar apenas o comportamento já existente em `src/main`; não criar rotas, integrações ou regras novas.
- Criar as novas especificações em Groovy/Spock, em `src/test/groovy/novasuite`, usando nomes e descrições em português e a estrutura `Dado / Quando / Então`.
- Usar Mocks/Stubs para `EnviadorEmail` e `EnviadorMensagemTelegram`; nenhum teste pode acessar o Resend ou o Telegram.
- Não testar `localStorage`, interface web ou cenários E2E nesta suíte. O foco é o backend Java/Groovy, incluindo controllers, handlers e services existentes.
- Se um teste revelar um bug real e impeditivo, interromper a implementação daquele caso, registrar o problema e criar uma tarefa separada para eventual correção em produção.

## Tarefa 1: Mapear pontos de teste e contratos existentes
- **O que será feito:** Identificar as classes reais responsáveis por filtros, status, processamento em lote e notificações antes de criar as specs. Confirmar construtores, métodos e exceções disponíveis para evitar inventar APIs.
- **Arquivos criados/alterados:** Nenhum arquivo de produção; o resultado orientará as specs em `src/test/groovy/novasuite`.
- **Como validar:** Revisar os contratos encontrados e executar os testes existentes antes de adicionar a nova suíte.

## Tarefa 2: Testes de filtros combinados
- **O que será feito:** Cobrir Categoria, Prioridade, Status e Data isoladamente e em combinações. Toda combinação deverá aplicar lógica `AND`; não serão criados cenários `OR`.
- **Arquivo previsto:** `src/test/groovy/novasuite/FiltroTarefasSpec.groovy`.
- **Como validar:** Executar `./gradlew test --tests "*FiltroTarefasSpec*"`.

## Tarefa 3: Testes de transição individual de status
- **O que será feito:** Verificar transições válidas, inclusive a conclusão de uma tarefa atrasada quando os dados obrigatórios forem válidos, além da rejeição de dados inválidos conforme as validações normais do modelo.
- **Arquivo previsto:** `src/test/groovy/novasuite/MudancaStatusSpec.groovy`.
- **Como validar:** Executar `./gradlew test --tests "*MudancaStatusSpec*"`.

## Tarefa 4: Testes de alteração de status em lote
- **O que será feito:** Testar o comportamento individual das tarefas em uma operação em lote. O cenário de falha parcial só será implementado se o código existente expuser essa validação; deverá comprovar que tarefas válidas permanecem atualizadas e não há rollback das anteriores.
- **Arquivo previsto:** `src/test/groovy/novasuite/MudancaStatusLoteSpec.groovy`.
- **Como validar:** Executar `./gradlew test --tests "*MudancaStatusLoteSpec*"`.

## Tarefa 5: Testes isolados dos gatilhos de notificação
- **O que será feito:** Verificar os contratos existentes dos handlers e services para os eventos de e-mail via Resend e Telegram. Usar Mocks/Stubs de `EnviadorEmail` e `EnviadorMensagemTelegram`, validando payload, evento e parâmetros sem chamadas externas. Cobrir, conforme os contratos atuais, criação, edição, conclusão e prazo próximo.
- **Arquivo previsto:** `src/test/groovy/novasuite/NotificacaoSpec.groovy`.
- **Como validar:** Executar `./gradlew test --tests "*NotificacaoSpec*"`.

## Tarefa 6: Validar a suíte completa
- **O que será feito:** Executar todos os testes e confirmar que a nova suíte não altera configurações nem interfere nos testes existentes.
- **Arquivos criados/alterados:** Nenhum arquivo de produção.
- **Como validar:** Executar `./gradlew test`; o resultado esperado é sucesso completo. Qualquer falha preexistente deverá ser registrada, não mascarada.