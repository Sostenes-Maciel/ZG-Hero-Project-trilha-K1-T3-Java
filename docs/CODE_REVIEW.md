# Code Review — Suíte de Testes

## Escopo revisado

Foram revisados:

- `src/test/groovy/novasuite/FiltroTarefasSpec.groovy`
- `src/test/groovy/novasuite/MudancaStatusSpec.groovy`
- `src/test/groovy/novasuite/NotificacaoSpec.groovy`

A comparação foi feita com `docs/spec.md`, considerando legibilidade, aderência aos requisitos e casos de borda.

## Apontamentos

| Severidade | Arquivo | Apontamento | Ação |
|---|---|---|---|
| Baixa | `FiltroTarefasSpec.groovy` | Os testes cobrem filtros isolados e combinações `AND`, conforme a spec. Não há cenário `OR`, corretamente excluído do escopo. | Nenhuma correção necessária. |
| Baixa | `MudancaStatusSpec.groovy` | São cobertas transições válidas, conclusão atrasada e exceções reais do modelo/serviço. O modelo não valida título ou data, portanto não foram inventadas expectativas para essas regras. | Nenhuma correção necessária. |
| Baixa | `NotificacaoSpec.groovy` | Os testes verificavam o payload recebido pelo mock, mas não explicitavam a cardinalidade do envio de e-mail. | Corrigido com `1 * enviadorEmail.enviar(...)` nos cenários de criação e conclusão. |

## Segurança e isolamento

Os testes utilizam `Mock(IAlarmeService)`, `Mock(EnviadorMensagemTelegram)` e `Mock(EnviadorEmail)`. O endpoint de e-mail é executado em um servidor HTTP local de teste, sem uso de Resend ou Telegram reais.

Não foram identificadas chamadas externas, credenciais, alterações de produção ou desvios de escopo.

## Validação após a revisão

Executar:

```bash
./gradlew test --tests "*FiltroTarefasSpec*"
./gradlew test --tests "*MudancaStatusSpec*"
./gradlew test --tests "*NotificacaoSpec*"
./gradlew test
```

Resultado esperado: todos os testes aprovados.
