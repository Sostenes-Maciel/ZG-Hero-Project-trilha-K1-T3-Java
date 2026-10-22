package br.com.zg.todolist.backend.service

import br.com.zg.todolist.backend.model.Tarefa
import br.com.zg.todolist.backend.observer.AlarmeObserver
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import static org.junit.jupiter.api.Assertions.*

class AlarmeServiceObserverTest {

    @Test
    void deveAdicionarERemoverObservadoresSemErros() {
        def alarmeService = new AlarmeService()

        def observadorEspiao = new AlarmeObserver() {
            boolean foiChamado = false

            @Override
            void onAlarmeDisparado(Tarefa tarefa) {
                foiChamado = true
            }
        }

        assertDoesNotThrow({
            alarmeService.adicionarObservador(observadorEspiao)
        } as Executable)

        assertDoesNotThrow({
            alarmeService.removerObservador(observadorEspiao)
        } as Executable)
    }
}