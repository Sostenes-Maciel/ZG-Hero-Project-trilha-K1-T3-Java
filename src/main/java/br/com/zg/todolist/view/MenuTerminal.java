package br.com.zg.todolist.view;

import br.com.zg.todolist.model.Status;
import br.com.zg.todolist.model.Tarefa;
import br.com.zg.todolist.service.GerenciadorTarefas;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import java.util.List;

public class MenuTerminal {

    private GerenciadorTarefas gerenciador;
    private Scanner sc;


    public MenuTerminal() {
        this.gerenciador = new GerenciadorTarefas();
        this.sc = new Scanner(System.in);
    }


    public void iniciar() {
        int opcao = 0;
        System.out.println("Bem-Vindo ao TODO List!! ");

        while (opcao != 6) {
            System.out.println("-----------------------");
            System.out.println(" 1 - Cadastrar Tarefa");
            System.out.println(" 2 - Listar Tarefa");
            System.out.println(" 3 - Atualizar Tarefa");
            System.out.println(" 4 - Remover Tarefas");
            System.out.println(" 5 - Filtrar tarefas por Status");
            System.out.println(" 6 - Sair");
            System.out.print("Escolha uma opção: ");

            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1:
                    System.out.println("Iniciando a criação da tarefa...");
                    adiconarNovaTarefa();
                    break;
                case 2:
                    System.out.println("\n--Suas tarefas--");
                    if (gerenciador.ListarTarefa().isEmpty()) {
                        System.out.println("Nenhuma tarefa foi encontrada!");
                    } else {
                        gerenciador.ListarTarefa().forEach(System.out::println);
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


                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        }
        sc.close();
    }

    private void adiconarNovaTarefa() {
        System.out.println("-- Nova Tarefa --");

        System.out.print("Nome: ");
        String nome = sc.nextLine();

        System.out.print("Descrição: ");
        String descricao = sc.nextLine();

        System.out.print("Data de término(dd/mm/aaaa): ");
        String data = sc.nextLine();
        LocalDate dataTermino = LocalDate.parse(data, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        System.out.print("Nível de prioridade(1 a 5): ");
        int prioridade = sc.nextInt();
        sc.nextLine();

        System.out.print("Categoria: ");
        String categoria = sc.nextLine();

        try {
            Tarefa novaTarefa = new Tarefa(nome, descricao, dataTermino, prioridade, categoria);
            gerenciador.adicionarTarefa(novaTarefa);
            System.out.println("Tarefa adicionada com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao criar tarefa: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro no formato de dados. Tente novamente.");
        }

    }

    private void atualizarTarefa() {
        System.out.println("-- Atualizando Tarefa --");
        List<Tarefa> tarefas = gerenciador.ListarTarefa();

        if (tarefas.isEmpty()) {
            System.out.println("Não há tarefas para atualizar.");
            return;
        }

        for (int i = 0; i < tarefas.size(); i++) {
            System.out.println(i + " - " + tarefas.get(i));
        }

        System.out.print("Digite o número da tarefa que deseja atualizar: ");
        int indice = sc.nextInt();
        sc.nextLine();

        if (indice < 0 || indice >= tarefas.size()) {
            System.out.println("Número de tarefa não encontrado!");
            return;
        }

        System.out.println("-- Digite os novos dados --");

        try {
            System.out.print("Novo Nome: ");
            String nome = sc.nextLine();

            System.out.print("Nova Descrição: ");
            String descricao = sc.nextLine();

            System.out.print("Nova Data de término (dd/MM/yyyy): ");
            String dataString = sc.nextLine();
            LocalDate dataTermino = LocalDate.parse(dataString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            System.out.print("Nova Prioridade (1 a 5): ");
            int prioridade = sc.nextInt();
            sc.nextLine();

            System.out.print("Nova Categoria: ");
            String categoria = sc.nextLine();

            System.out.print("Novo Status (1- TODO, 2 - DOING, 3 - DONE): ");
            int opcaoStatus = sc.nextInt();
            sc.nextLine();

            Status novoStatus;
            switch (opcaoStatus) {
                case 2:
                    novoStatus = Status.DOING;
                    break;
                case 3:
                    novoStatus = Status.DONE;
                    break;
                default:
                    novoStatus = Status.TODO;
                    break;
            }

            Tarefa tarefaAtualizada = new Tarefa(nome, descricao, dataTermino, prioridade, categoria);
            tarefaAtualizada.setStatus(novoStatus);

            boolean sucesso = gerenciador.atualizarTarefa(indice, tarefaAtualizada);
            if (sucesso) {
                System.out.println("Tarefa atualizada com sucesso!");
            }
        } catch (Exception e) {
            System.out.println("Erro no formato de dados. Tente novamente.");
        }
    }

    public void removerTarefa() {
        System.out.println("-- Removendo Tarefa --");
        List<Tarefa> tarefas = gerenciador.ListarTarefa();

        if (tarefas.isEmpty()) {
            System.out.println("Não há tarefas para remover");
            return;
        }

        for (int i = 0; i < tarefas.size(); i++) {
            System.out.println(i + " - " + tarefas.get(i));
        }

        System.out.println("Digite o número da tarefa a ser removida: ");
        int indice = sc.nextInt();
        sc.nextLine();

        boolean sucesso = gerenciador.removertarefa(indice);
        if ((sucesso)) {
            System.out.println("Tarefa removida com sucesso!");
        } else {
            System.out.println("Erro no número da tarefa. Tente novamente.");
        }
    }

    private void filtrarTarefa() {
        System.out.println("-- Filtrando Tarefa --");
        System.out.println("Qual o Status deseja ver? (1 - TODO, 2 - DOING, 3 - DONE): ");
        int opcaoStatus = sc.nextInt();
        sc.nextLine();

        Status statusBuscar;
        switch (opcaoStatus) {
            case 2:
                statusBuscar = Status.DOING;
                break;
            case 3:
                statusBuscar = Status.DONE;
                break;
            default:
                statusBuscar = Status.TODO;
                break;
        }
        List<Tarefa> resultado = gerenciador.listarPorStatus(statusBuscar);
        if (resultado.isEmpty()) {
            System.out.println("Nenhuma tarefa encontrada com o status.");
        } else {
            System.out.println("-- Resultado da busca --");
            resultado.forEach(System.out::println);
        }
    }
}
