package br.com.zg.todolist;

import br.com.zg.todolist.backend.service.AlarmeService;
import br.com.zg.todolist.backend.service.GerenciadorTarefasService;
import br.com.zg.todolist.backend.service.IGerenciadorTarefas;
import br.com.zg.todolist.backend.view.MenuTerminalView;
import br.com.zg.todolist.backend.observer.ConsoleAlarmeObserver;
import br.com.zg.todolist.backend.observer.PopupAlarmeObserver;
import br.com.zg.todolist.backend.observer.SomAlarmeObserver;

public class Main {
    public static void main(String[] args) {
        AlarmeService alarmeService = new AlarmeService();

        alarmeService.adicionarObservador(new ConsoleAlarmeObserver());
        alarmeService.adicionarObservador(new PopupAlarmeObserver());
        alarmeService.adicionarObservador(new SomAlarmeObserver());

        IGerenciadorTarefas gerenciadorTarefas = new GerenciadorTarefasService(alarmeService);

        MenuTerminalView menu = new MenuTerminalView(gerenciadorTarefas, alarmeService);
        menu.iniciar();
    }
}