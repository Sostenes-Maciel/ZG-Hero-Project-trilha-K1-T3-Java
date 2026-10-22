package br.com.zg.todolist.backend.model.observer;

import br.com.zg.todolist.backend.model.Tarefa;
import javax.swing.JOptionPane;

public class PopupAlarmeObserver implements AlarmeObserver {
    @Override
    public void onAlarmeDisparado(Tarefa tarefa) {
        String mensagem = String.format("A tarefa '%s' precisa da sua atenção!\nDescrição: %s",
                tarefa.getNome(), tarefa.getDescricao());

        JOptionPane.showMessageDialog(
                null,
                mensagem,
                "⏰ Lembrete TODO-List",
                JOptionPane.WARNING_MESSAGE
        );
    }
}