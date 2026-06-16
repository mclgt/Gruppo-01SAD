package com.DataLayer;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * @brief Rappresenta la gestione fisica del file audio in memoria.
 *        Utilizza la libreria nativa javax.sound.sampled per caricare,
 *        riprodurre e fermare i flussi audio di tipo .wav.
 *
 * @see DataLayer.IAudioTrack
 */
public class RealTrack implements IAudioTrack {
    private String filePath; // Percorso audio
    private Clip audioClip; // Oggetto nativo di java che mantiene il buffer audio nella scheda audio

    /**
     * @brief Costruttore -> inizializza l'oggetto con il percorso del file.
     * @param filePath Percorso del file .wav da riprodurre.
     */
    public RealTrack(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @brief Apre lo stream del file e lo carica nel buffer della scheda audio.
     *        Cattura e gestisce le eccezioni di formato non supportato o file
     *        mancante.
     */
    /*
     * @Override
     * public void load() {
     * try {
     * File audioFile = new File(filePath);
     * AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
     * audioClip = AudioSystem.getClip();
     * audioClip.open(audioStream);
     * System.out.println("Audio caricato in memoria: " + filePath);
     * } catch (UnsupportedAudioFileException | IOException |
     * LineUnavailableException e) {
     * System.err.println("Errore caricamento file audio: " + e.getMessage());
     * }
     * }
     */
    @Override
    public void load() {
        try {
            // filePath DEVE essere il percorso assoluto (es.
            // "C:\Users\miche\Downloads\brano.wav")
            // NON un URI (file:/C:/...)
            File audioFile = new File(this.filePath);

            if (!audioFile.exists()) {
                throw new IOException("File non trovato: " + filePath);
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
            System.out.println("Audio caricato: " + filePath);

        } catch (UnsupportedAudioFileException e) {
            System.err.println("Formato non supportato (Javax Sound supporta solo WAV/AIFF): " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore caricamento: " + e.getMessage());
        }
    }

    /**
     * @brief Avvia la riproduzione musicale.
     */
    @Override
    public void startPlayback() {
        if (audioClip != null) {
            audioClip.setFramePosition(0);
            audioClip.start();
        }
    }

    /**
     * @brief Ferma istantaneamente la riproduzione audio se è in corso.
     */
    @Override
    public void stopPlayback() {
        if (audioClip != null && audioClip.isRunning()) {
            audioClip.stop();
        }
    }

    /**
     * @brief Mette in pausa preservando la posizione corrente nel frame buffer.
     *        La Clip di javax.sound.sampled mantiene il frame position dopo stop(),
     *        quindi una start() successiva riprenderà da quel punto.
     */
    @Override
    public void pausePlayback() {
        if (audioClip != null && audioClip.isRunning()) {
            audioClip.stop();
        }
    }

    /**
     * @brief Riprende la riproduzione dal frame in cui era stata fermata con
     *        pausePlayback().
     */
    @Override
    public void resumePlayback() {
        if (audioClip != null) {
            audioClip.start();
        }
    }

    /**
     * @brief Controlla se la clip audio sta emettendo suono.
     * @return boolean True se in riproduzione, False altrimenti.
     */
    @Override
    public boolean isPlaying() {
        return audioClip != null && audioClip.isRunning();
    }
}
