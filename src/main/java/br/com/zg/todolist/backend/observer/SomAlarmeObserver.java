package br.com.zg.todolist.backend.observer;

import br.com.zg.todolist.backend.model.Tarefa;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class SomAlarmeObserver implements AlarmeObserver {
    @Override
    public void onAlarmeDisparado(Tarefa tarefa) {
        try {
            float sampleRate = 44100f;
            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            SourceDataLine line = AudioSystem.getSourceDataLine(format);

            line.open(format);
            line.start();

            byte[] buffer = new byte[1];

            for (int b = 0; b < 3; b++) {
                for (int i = 0; i < sampleRate * 0.2; i++) {
                    double angle = i / (sampleRate / 880.0) * 2.0 * Math.PI;
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