# Relatório Final — Etapa 8

## Consumo de Tokens (estimativa por etapa)

O repositório não registra métricas reais de tokens por etapa. Portanto, não são informados números exatos. A estimativa abaixo é qualitativa, baseada no volume de análise, edição e validação realizado:

| Etapa | Estimativa | Base da estimativa |
|---|---|---|
| Etapa 2 — Contexto para o agente | Baixa | Leitura das regras e convenções de `AGENTS.md`. |
| Etapa 3 — Especificação | Média | Análise do escopo, contratos existentes e critérios de aceite. |
| Etapa 4 — Plano | Média | Decomposição em tarefas, dependências e comandos de validação. |
| Etapa 5 — Implementação incremental | Alta | Criação de três specs Spock, ajustes após validação e commits individuais. |
| Etapa 6 — Validação | Baixa | Execução dos testes direcionados e da suíte completa. |
| Etapa 7 — Skill reutilizável | Média | Criação da documentação genérica com modelos e regras de execução. |
| Etapa 8 — Relatórios | Baixa | Consolidação da descrição do PR e deste relatório. |

## Divergências da IA e Correções

Durante o planejamento inicial, a especificação presumiu que a alteração de status em lote teria um contrato testável no backend. O mapeamento dos contratos reais mostrou que essa funcionalidade existe somente no frontend JavaScript, em `src/main/java/br/com/zg/todolist/frontend/js/tarefas.js`, usando `localStorage`; não há método equivalente em `IGerenciadorTarefas`, `GerenciadorTarefasService` ou `TarefaController`.

A implementação foi corretamente barrada para evitar a criação de uma API ou teste inexistente. Em seguida:

1. A Tarefa 4 foi marcada como cancelada em `docs/plano.md`.
2. As menções a processamento em lote, rollback e falha parcial foram removidas de `docs/spec.md`.
3. Nenhum teste backend foi criado para esse comportamento fora do escopo real.

As demais tarefas foram implementadas conforme os contratos existentes e validadas com `./gradlew test`.
