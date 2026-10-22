package br.com.zg.todolist;

import br.com.zg.todolist.backend.controller.CategoriaController;
import br.com.zg.todolist.backend.controller.TarefaController;
import br.com.zg.todolist.backend.model.service.AlarmeService;
import br.com.zg.todolist.backend.model.service.GerenciadorTarefasService;
import br.com.zg.todolist.backend.view.MenuTerminalView;

public class Main {
    public static void main(String[] args) {
        AlarmeService alarmeService = new AlarmeService();
        GerenciadorTarefasService gerenciador = new GerenciadorTarefasService(alarmeService);

        TarefaController tarefaController = new TarefaController(gerenciador);
        CategoriaController categoriaController = new CategoriaController();

        MenuTerminalView menu = new MenuTerminalView(tarefaController, categoriaController);
        menu.iniciar();
    }
}