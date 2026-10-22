package br.com.zg.todolist.backend.controller;

import br.com.zg.todolist.backend.model.Categoria;
import br.com.zg.todolist.backend.model.factory.CategoriaFactory;

public class CategoriaController {

    public Categoria obterCategoria(String nome) {
        return CategoriaFactory.getCategoria(nome);
    }
}