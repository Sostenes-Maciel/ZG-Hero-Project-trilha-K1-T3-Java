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


## 👨‍💻 Autor
**Sóstenes Marques Maciel**
