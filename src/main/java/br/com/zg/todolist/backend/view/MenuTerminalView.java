package br.com.zg.todolist.backend.view;

import br.com.zg.todolist.backend.model.Categoria;
import br.com.zg.todolist.backend.model.Status;
import br.com.zg.todolist.backend.model.Tarefa;
import br.com.zg.todolist.backend.service.IAlarmeService;
import br.com.zg.todolist.backend.service.IGerenciadorTarefas;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class MenuTerminalView {

    private final IGerenciadorTarefas gerenciador;
    private final IAlarmeService alarmeService;
    private final Scanner sc;

    public MenuTerminalView(IGerenciadorTarefas gerenciador, IAlarmeService alarmeService) {
        this.gerenciador = gerenciador;
        this.alarmeService = alarmeService;
        this.sc = new Scanner(System.in);
    }

    public void iniciar() {
        int opcao = 0;
        System.out.println("Bem-Vindo ao TODO List!!");

        while (opcao != 6) {
            System.out.println("-----------------------");
            System.out.println(" 1 - Cadastrar Tarefa");
            System.out.println(" 2 - Listar Tarefas");
            System.out.println(" 3 - Atualizar Tarefas");
            System.out.println(" 4 - Remover Tarefas");
            System.out.println(" 5 - Filtrar tarefas");
            System.out.println(" 6 - Sair");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = sc.nextInt();
                sc.nextLine();

                switch (opcao) {
                    case 1:
                        System.out.println("Iniciando a criação da tarefa...");
                        adicionarNovaTarefa();
                        break;
                    case 2:
                        System.out.println("\n-- Suas tarefas --");
                        List<Tarefa> tarefas = gerenciador.listarTarefas();
                        if (tarefas.isEmpty()) {
                            System.out.println("Nenhuma tarefa foi encontrada!");
                        } else {
                            tarefas.forEach(System.out::println);
                        }
                        break;
                    case 3:
                        System.out.println("Atualização em andamento...");
                        atualizarTarefa();
                        break;
                    case 4:
                        System.out.println("Remoção em andamento...");
                        removerTarefa();
                        break;
                    case 5:
                        filtrarTarefa();
                        break;
                    case 6:
                        System.out.println("Saindo. Até logo!");
                        break;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida! Por favor, digite um número.");
                sc.nextLine();
            }
        }
        alarmeService.encerrarAlarmes();
        sc.close();
    }

    private void adicionarNovaTarefa() {
        System.out.println("-- Nova Tarefa --");

        try {
            System.out.print("Nome: ");
            String nome = sc.nextLine();

            System.out.print("Descrição: ");
            String descricao = sc.nextLine();

            System.out.print("Data de término (dd/MM/yyyy): ");
            String data = sc.nextLine();
            LocalDate dataTermino = converterData(data);

            System.out.print("Nível de prioridade (1 a 5): ");
            int prioridade = sc.nextInt();
            sc.nextLine();

            System.out.print("Categoria: ");
            String nomeCategoria = sc.nextLine();
            Categoria categoria = new Categoria(nomeCategoria);

            System.out.print("Deseja configurar um alarme? (1 - Sim / 2 - Não): ");
            int opcaoAlarme = sc.nextInt();
            sc.nextLine();

            long minutosAlarme = 0;
            if (opcaoAlarme == 1) {
                System.out.print("Em quantos minutos deseja ser avisado? ");
                minutosAlarme = sc.nextLong();
                sc.nextLine();

                if (minutosAlarme <= 0) {
                    System.out.println("Tempo inválido. O alarme não será configurado.");
                    minutosAlarme = 0;
                }
            }

            Tarefa novaTarefa = new Tarefa(nome, descricao, dataTermino, prioridade, categoria);
            gerenciador.adicionarTarefa(novaTarefa);

            if (minutosAlarme > 0) {
                alarmeService.configurarAlarme(novaTarefa, minutosAlarme);
            }

            System.out.println("Tarefa adicionada com sucesso!");

        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao criar tarefa: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro no formato de dados. Tente novamente.");
        }
    }

    private void atualizarTarefa() {
        System.out.println("-- Atualizando Tarefa --");
        List<Tarefa> tarefas = gerenciador.listarTarefas();

        if (tarefas.isEmpty()) {
            System.out.println("Não há tarefas para atualizar.");
            return;
        }

        imprimirTarefasComIndice(tarefas);

        try {
            System.out.print("Digite o número da tarefa que deseja atualizar: ");
            int indice = sc.nextInt();
            sc.nextLine();

            System.out.println("-- Digite os novos dados --");

            System.out.print("Novo Nome: ");
            String nome = sc.nextLine();

            System.out.print("Nova Descrição: ");
            String descricao = sc.nextLine();

            System.out.print("Nova Data de término (dd/MM/yyyy): ");
            String dataString = sc.nextLine();
            LocalDate dataTermino = converterData(dataString);

            System.out.print("Nova Prioridade (1 a 5): ");
            int prioridade = sc.nextInt();
            sc.nextLine();

            System.out.print("Nova Categoria: ");
            String nomeCategoria = sc.nextLine();
            Categoria categoria = new Categoria(nomeCategoria);

            System.out.print("Novo Status (1 - TODO, 2 - DOING, 3 - DONE): ");
            int opcaoStatus = sc.nextInt();
            sc.nextLine();

            Tarefa tarefaAtualizada = new Tarefa(nome, descricao, dataTermino, prioridade, categoria);
            tarefaAtualizada.setStatus(obterStatusPorOpcao(opcaoStatus));

            gerenciador.atualizarTarefa(indice, tarefaAtualizada);
            System.out.println("Tarefa atualizada com sucesso!");

        } catch (IndexOutOfBoundsException e) {
            System.out.println("Erro: Número de tarefa não encontrado!");
        } catch (Exception e) {
            System.out.println("Erro no formato de dados. Operação cancelada.");
        }
    }

    private void removerTarefa() {
        System.out.println("-- Removendo Tarefa --");
        List<Tarefa> tarefas = gerenciador.listarTarefas();

        if (tarefas.isEmpty()) {
            System.out.println("Não há tarefas para remover.");
            return;
        }

        imprimirTarefasComIndice(tarefas);

        try {
            System.out.print("Digite o número da tarefa a ser removida: ");
            int indice = sc.nextInt();
            sc.nextLine();

            gerenciador.removerTarefa(indice);
            System.out.println("Tarefa removida com sucesso!");

        } catch (IndexOutOfBoundsException e) {
            System.out.println("Erro: Número da tarefa inválido. Tente novamente.");
        } catch (Exception e) {
            System.out.println("Entrada inválida.");
            sc.nextLine();
        }
    }

    private void filtrarTarefa() {
        System.out.println("-- Filtrar tarefas --");
        System.out.println("1. Por Categoria");
        System.out.println("2. Por Prioridade");
        System.out.println("3. Por Status");
        System.out.print("Escolha como deseja listar: ");

        try {
            int opcaoFiltro = sc.nextInt();
            sc.nextLine();

            List<Tarefa> resultado;

            switch (opcaoFiltro) {
                case 1:
                    System.out.print("Digite o nome da Categoria: ");
                    String categoria = sc.nextLine();
                    resultado = gerenciador.filtrarTarefas(t -> t.getCategoria().getNome().equalsIgnoreCase(categoria));
                    break;
                case 2:
                    System.out.print("Digite o número da Prioridade (1 a 5): ");
                    int prioridade = sc.nextInt();
                    sc.nextLine();
                    resultado = gerenciador.filtrarTarefas(t -> t.getPrioridade() == prioridade);
                    break;
                case 3:
                    System.out.print("Qual status? (1 - TODO, 2 - DOING, 3 - DONE): ");
                    int opStatus = sc.nextInt();
                    sc.nextLine();
                    Status statusBusca = obterStatusPorOpcao(opStatus);
                    resultado = gerenciador.filtrarTarefas(t -> t.getStatus() == statusBusca);
                    break;
                default:
                    System.out.println("Opção de filtro inválida.");
                    return;
            }

            if (resultado.isEmpty()) {
                System.out.println("Nenhuma tarefa encontrada para este filtro.");
            } else {
                System.out.println("\n--- RESULTADO DA BUSCA ---");
                resultado.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Entrada inválida. Operação cancelada.");
            sc.nextLine();
        }
    }

    private void imprimirTarefasComIndice(List<Tarefa> tarefas) {
        for (int i = 0; i < tarefas.size(); i++) {
            System.out.println(i + " - " + tarefas.get(i));
        }
    }

    private Status obterStatusPorOpcao(int opcao) {
        if (opcao == 2) return Status.DOING;
        if (opcao == 3) return Status.DONE;
        return Status.TODO;
    }

    private LocalDate converterData(String data) {
        try {
            if (!data.contains("/")) {
                data = data.substring(0, 2) + "/" +
                        data.substring(2, 4) + "/" +
                        data.substring(4);
            }
            return LocalDate.parse(data, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Data inválida. Use o formato dd/MM/yyyy.");
        }
    }
}