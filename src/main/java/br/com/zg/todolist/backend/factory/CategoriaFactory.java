package br.com.zg.todolist.backend.factory;

import br.com.zg.todolist.backend.model.Categoria;

import java.util.HashMap;
import java.util.Map;

public class CategoriaFactory {

    private static final Map<String, Categoria> cacheCategorias = new HashMap<>();

    public static Categoria getCategoria(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria não pode ser vazio.");
        }

        String chave = nome.trim().toUpperCase();

        if (!cacheCategorias.containsKey(chave)) {
            cacheCategorias.put(chave, new Categoria(nome.trim()));
        }

        return cacheCategorias.get(chave);
    }
}