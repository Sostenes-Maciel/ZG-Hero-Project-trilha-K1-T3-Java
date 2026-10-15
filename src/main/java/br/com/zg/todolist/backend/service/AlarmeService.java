package br.com.zg.todolist.backend.service;

import br.com.zg.todolist.backend.model.Tarefa;

import javax.swing.JOptionPane;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AlarmeService implements IAlarmeService {

    private final ScheduledExecutorService scheduler;

    public AlarmeService() {
        // Aumentamos o pool para aguentar múltiplos alarmes simultâneos
        this.scheduler = Executors.newScheduledThreadPool(5);
    }

    @Override
    public void configurarAlarme(Tarefa tarefa, long minutos) {
        if (tarefa == null) {
            throw new IllegalArgumentException("A tarefa não pode ser nula.");
        }
        if (minutos <= 0) {
            throw new IllegalArgumentException("O tempo do alarme deve ser maior que zero.");
        }

        LocalDateTime horario = LocalDateTime.now().plusMinutes(minutos);
        tarefa.setHorarioAlarme(horario);

        scheduler.schedule(() -> dispararAlarme(tarefa), minutos, TimeUnit.MINUTES);

        System.out.println("⏰ [SISTEMA] Alarme ativado com sucesso para a tarefa: " + tarefa.getNome());
    }

    @Override
    public void encerrarAlarmes() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    private void dispararAlarme(Tarefa tarefa) {
        tocarSomDeDespertador();

        System.out.println("\n================================");
        System.out.println(" ⏰ ALARME: " + tarefa.getNome());
        System.out.println("================================");
        System.out.println("Descrição: " + tarefa.getDescricao());
        System.out.println("Prioridade: " + tarefa.getPrioridade());
        System.out.println("================================\n");

        String mensagem = String.format("A tarefa '%s' precisa da sua atenção!\nDescrição: %s",
                tarefa.getNome(), tarefa.getDescricao());

        JOptionPane.showMessageDialog(
                null,
                mensagem,
                "⏰ Lembrete TODO-List",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void tocarSomDeDespertador() {
        try {
            float sampleRate = 44100f;
            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            SourceDataLine line = AudioSystem.getSourceDataLine(format);

            line.open(format);
            line.start();

            byte[] buffer = new byte[1];

            // Toca 3 bipes curtos (como um relógio digital Casio)
            for (int b = 0; b < 3; b++) {
                // Cada bipe dura 200ms
                for (int i = 0; i < sampleRate * 0.2; i++) {
                    double angle = i / (sampleRate / 880.0) * 2.0 * Math.PI; // Frequência de 880Hz
                    buffer[0] = (byte) (Math.sin(angle) * 127.0);
                    line.write(buffer, 0, 1);
                }
                Thread.sleep(100);
            }

            line.drain();
            line.stop();
            line.close();
        } catch (Exception e) {
            System.out.println("(Aviso: Erro ao tocar som nativo: " + e.getMessage() + ")");
        }
    }
}