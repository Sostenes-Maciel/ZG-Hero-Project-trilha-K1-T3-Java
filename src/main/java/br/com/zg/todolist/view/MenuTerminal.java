package br.com.zg.todolist.view;

import br.com.zg.todolist.model.Tarefa;
import br.com.zg.todolist.service.GerenciadorTarefas;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

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

        while (opcao != 5) {
            System.out.println("-----------------------");
            System.out.println(" 1 - Cadastrar Tarefa");
            System.out.println(" 2 - Listar Tarefa");
            System.out.println(" 3 - Atualizar Tarefa");
            System.out.println(" 4 - Remover Tarefas");
            System.out.println(" 5 - Sair");
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
                    break;
                case 4:
                    System.out.println("Remoção em andamento...");
                    break;
                case 5:
                    System.out.println("Saindo. Até logo!");
                    break;
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
}
