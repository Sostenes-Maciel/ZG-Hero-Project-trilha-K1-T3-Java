# Descrição do Pull Request

## O que foi feito

Foi criada uma suíte de testes unitários em Groovy com Spock para as funcionalidades backend existentes do TODO List:

- filtros de categoria, prioridade, status e data, isolados e combinados com lógica `AND`;
- transições individuais de status, incluindo a conclusão de tarefas atrasadas;
- gatilhos de notificação de criação e conclusão usando mocks para Telegram e e-mail;
- organização dos novos testes em `src/test/groovy/novasuite`.

O processamento de status em lote não foi testado no backend porque a funcionalidade existe exclusivamente no frontend JavaScript e não há contrato correspondente em Java/Groovy.

## Como testar

Executar os testes direcionados:

```bash
./gradlew test --tests "*FiltroTarefasSpec*"
./gradlew test --tests "*MudancaStatusSpec*"
./gradlew test --tests "*NotificacaoSpec*"
```

Executar a suíte completa:

```bash
./gradlew test
```

## Critérios de aceite atendidos

- [x] Filtros combinados retornam somente tarefas que atendem simultaneamente a todos os critérios.
- [x] Tarefas atrasadas podem ser concluídas quando seus dados são válidos.
- [x] Criação e conclusão de tarefas acionam os notificadores por meio de mocks, sem envio real.
- [x] A suíte completa de testes executa com sucesso.

Closes #1
