# TODO List - Acelera ZG Trilha Java

Projeto desenvolvido como parte do desafio técnico do Acelera ZG. Consiste em um gerenciador de tarefas interativo executado via terminal (CLI). O sistema foi projetado com foco em código limpo, separação de responsabilidades (Clean Architecture) e versionamento semântico.


##  Tecnologias Utilizadas
* **Java** (Vanilla, sem uso de frameworks externos)
* **Gradle** (Gerenciamento de dependências e build)
* **Git e GitHub** (Controle de versão seguindo o padrão SemVer)

##  Funcionalidades (CRUD Completo)
* **Adicionar Tarefa:** Criação de tarefas (nascem com status `TODO` por padrão).
* **Listar Tarefas:** Visualização de todas as tarefas cadastradas.
* **Atualizar Tarefa:** Alteração de atributos e evolução do status (`TODO`, `DOING`, `DONE`).
* **Remover Tarefa:** Exclusão de tarefas da memória.
* **Rebalanceamento Automático:** Tarefas são ordenadas automaticamente por prioridade (1 a 5) e, em caso de empate, pela data de término mais próxima.
* **Filtros Avançados:** Busca de tarefas específicas por **Categoria**, **Prioridade** ou **Status**.

##  Como Executar

### Via IDE (Recomendado)
1. Abra a pasta do projeto em sua IDE de preferência (IntelliJ IDEA, Eclipse, etc.).
2. Aguarde a sincronização do Gradle.
3. Navegue até a classe principal localizada em `src/main/java/.../Main.java`.
4. Execute o método `main`.
5. Interaja com o menu do sistema diretamente pelo console da IDE.

### Via Terminal
1. Abra o terminal na raiz do projeto.
2. Compile e execute o projeto utilizando o Gradle Wrapper:
   * **Windows:** `gradlew run`
   * **Linux/Mac:** `./gradlew run`

##  Arquitetura e Solução

Para garantir um código escalável e de fácil manutenção, o projeto foi estruturado seguindo o princípio de Separação de Preocupações (inspirado no padrão MVC):

* **View (`MenuTerminal`):** Responsável exclusivamente pela interação com o usuário, leitura de dados via teclado (`Scanner`) e exibição de mensagens no console. Não contém regras de negócio.
* **Service/Controller (`GerenciadorDeTarefas`):** O "cérebro" da aplicação. Isola toda a lógica de negócio, manipulação da lista, rebalanceamento por prioridade e filtros.
* **Model (`Tarefa`, `Status`):** Representam os dados e o estado da aplicação.

* ## Atualização: Testes Unitários do CRUD

Foram implementados testes unitários utilizando **Spock Framework** e **Groovy** para validar as operações de **CRUD (Create, Read, Update e Delete)** do sistema de gerenciamento de tarefas.

### Principais Alterações

* **Create:** Teste para verificar a criação e inserção de novas tarefas na lista.
* **Read:** Teste para validar a recuperação das tarefas cadastradas.
* **Update:** Teste para verificar a atualização de uma tarefa existente.
* **Delete:** Teste para validar a remoção de uma tarefa cadastrada.
* **Testes Unitários com Spock:** As operações foram testadas de forma independente, verificando o comportamento esperado de cada ação do CRUD.

* ## Atualização: Sistema de Alarmes para Tarefas

O projeto foi atualizado com a implementação de um sistema de alarmes para as tarefas, permitindo configurar um período de aviso durante a criação de cada tarefa.

### Principais Alterações

* **Configuração de Alarme:** Adicionada a opção de configurar um alarme durante o cadastro de uma nova tarefa, permitindo definir o tempo para o aviso.
* **Alarmes Personalizados:** Cada tarefa pode possuir seu próprio horário de alarme, de acordo com o período definido pelo usuário.
* **Agendamento de Avisos:** Implementado o uso de `ScheduledExecutorService` no `GerenciadorTarefas` para disparar os avisos automaticamente quando o período configurado chegar.
* **Exibição do Alarme:** O horário configurado para o alarme passou a ser exibido junto com os dados da tarefa ao listar as tarefas.
* **Tratamento de Datas:** Melhorado o tratamento das datas de término, permitindo a entrada no formato `dd/MM/yyyy` e também sem as barras.


## Atualização: Frontend desenvolvido para a trilha de JavaScript.

A interface foi criada para permitir o gerenciamento das tarefas de forma simples e organizada. Como a comunicação com o backend ainda não foi implementada, os dados são armazenados localmente no navegador.

## Tecnologias utilizadas

* **HTML5** — estrutura da aplicação
* **CSS3** — estilização e responsividade
* **JavaScript** — lógica e interação da aplicação
* **LocalStorage** — persistência dos dados no navegador

## Funcionalidades

* Cadastro de tarefas
* Listagem de tarefas
* Edição de tarefas
* Remoção de tarefas
* Persistência das tarefas com LocalStorage
* Definição de status:

  * `TODO`
  * `DOING`
  * `DONE`
* Alteração de status para múltiplas tarefas
* Filtros por:

  * Categoria
  * Prioridade
  * Status
* Combinação de filtros
* Configuração de horário para alarme
* Mensagens de confirmação para as ações
* Formatação da data para o padrão brasileiro
* Modo de alto contraste com preferência salva no LocalStorage

## Estrutura

```text
Frontend/
├── index.html
├── css/
│   └── style.css
├── js/
│   ├── app.js
│   ├── tarefas.js
│   ├── filtros.js
│   └── ui.js
└── README.md
```

## Organização do JavaScript

O JavaScript foi dividido em módulos para facilitar a manutenção:

### `app.js`

Responsável por inicializar e integrar os módulos da aplicação.

### `tarefas.js`

Responsável pelas funcionalidades relacionadas às tarefas:

* Cadastro
* Listagem
* Edição
* Remoção
* LocalStorage
* Alarmes
* Alteração de status em múltiplas tarefas

### `filtros.js`

Responsável pelos filtros de:

* Categoria
* Prioridade
* Status

Também realiza a combinação dos filtros e informa a quantidade de tarefas encontradas.

### `ui.js`

Responsável pelos elementos gerais da interface:

* Navegação entre as seções
* Mensagens de sucesso
* Modo de alto contraste

## Atualização: 

1. Persistência de tarefas no LocalStorage

As tarefas cadastradas são armazenadas no LocalStorage do navegador, permitindo que os dados permaneçam salvos mesmo após recarregar ou fechar a página.

2. Alteração de status de múltiplas tarefas

É possível selecionar várias tarefas ao mesmo tempo e definir um novo status para todas as tarefas selecionadas. Os status disponíveis são:

TODO
DOING
DONE

Após a alteração, o novo status é atualizado e salvo no LocalStorage.


## 👨‍💻 Autor
**Sóstenes Marques Maciel**

