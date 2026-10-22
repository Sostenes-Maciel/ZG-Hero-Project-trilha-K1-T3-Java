package br.com.zg.todolist.backend.model.factory;

import br.com.zg.todolist.backend.model.Categoria;
import br.com.zg.todolist.backend.model.Tarefa;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TarefaFactory {

    public static Tarefa criarTarefaSimples(String nome, String descricao, LocalDate prazo, int prioridade, Categoria categoria) {
        return new Tarefa.TarefaBuilder()
                .comNome(nome)
                .comDescricao(descricao)
                .comDataTermino(prazo)
                .comPrioridade(prioridade)
                .comCategoria(categoria)
                .build();
    }

    public static Tarefa criarTarefaComAlarme(String nome, String descricao, LocalDate prazo, int prioridade, Categoria categoria, LocalDateTime horarioAlarme) {
        return new Tarefa.TarefaBuilder()
                .comNome(nome)
                .comDescricao(descricao)
                .comDataTermino(prazo)
                .comPrioridade(prioridade)
                .comCategoria(categoria)
                .comHorarioAlarme(horarioAlarme)
                .build();
    }
}