package br.com.zg.todolist;

import br.com.zg.todolist.backend.service.AlarmeService;
import br.com.zg.todolist.backend.service.GerenciadorTarefasService;
import br.com.zg.todolist.backend.service.IAlarmeService;
import br.com.zg.todolist.backend.service.IGerenciadorTarefas;
import br.com.zg.todolist.backend.view.MenuTerminalView;

public class Main {
    public static void main(String[] args) {
        IAlarmeService alarmeService = new AlarmeService();
        IGerenciadorTarefas gerenciadorTarefas = new GerenciadorTarefasService(alarmeService);

        MenuTerminalView menu = new MenuTerminalView(gerenciadorTarefas, alarmeService);
        menu.iniciar();
    }
}