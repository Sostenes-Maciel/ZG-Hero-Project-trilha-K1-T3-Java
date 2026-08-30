package br.com.zg.todolist.view;

import br.com.zg.todolist.service.GerenciadorTarefas;

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
            System.out.println("\n-----------------------");
            System.out.println(" 1 - Cadastrar Tarefa");
            System.out.println(" 2 - Listar Tarefa");
            System.out.println(" 3 - Atualizar Tarefa");
            System.out.println(" 4 - Remover Tarefas");
            System.out.println(" 5 - Sair");

            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1:
                    System.out.println("Iniciando a criação da tarefa...");
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
                case 5:
                    System.out.println("Saindo. Até logo!");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        }
        sc.close();
    }
}
