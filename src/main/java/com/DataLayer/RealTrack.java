package com.DataLayer;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * @brief Rappresenta la gestione fisica del file audio in memoria.
 * Utilizza la libreria nativa javax.sound.sampled per caricare, 
 * riprodurre e fermare i flussi audio di tipo .wav.
 *
 * @see DataLayer.IAudioTrack
 */
public class RealTrack implements IAudioTrack {
    private String filePath;    //Percorso audio
    private Clip audioClip;     //Oggetto nativo di java che mantiene il buffer audio nella scheda audio

    /**
     * @brief Costruttore -> inizializza l'oggetto con il percorso del file.
     * @param filePath Percorso del file .wav da riprodurre.
     */
    public RealTrack(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @brief Apre lo stream del file e lo carica nel buffer della scheda audio.
     * Cattura e gestisce le eccezioni di formato non supportato o file mancante.
     */
    @Override
    public void load() {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
            System.out.println("Audio caricato in memoria: " + filePath);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Errore caricamento file audio: " + e.getMessage());
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
     * @brief Controlla se la clip audio sta emettendo suono.
     * @return boolean True se in riproduzione, False altrimenti.
     */
    @Override
    public boolean isPlaying() {
        return audioClip != null && audioClip.isRunning();
    }
}
