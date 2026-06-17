package com.Controller.playback;

import java.util.function.Consumer;

import com.Model.Track;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * @class PlaybackTimerManager
 * @brief Gestore dei timer per la riproduzione audio e l'aggiornamento
 *        dell'interfaccia.
 *        Incapsula la logica temporale legata all'esecuzione di un brano.
 */
public class PlaybackTimerManager {
    private PauseTransition playbackTimer;
    private Timeline progressTimeline;
    private int elapsedSeconds;

    /**
     * @brief Avvia i timer di riproduzione per una nuova traccia.
     *        Ferma eventuali timer in esecuzione, azzera il contatore dei secondi e
     *        inizializza due flussi temporali: una `Timeline` che scatta ogni
     *        secondo
     *        per aggiornare l'interfaccia, e una `PauseTransition` globale che
     *        scatta
     *        al termine esatto della durata del brano.
     *        * @param track Il brano da riprodurre, da cui estrarre la durata
     *        totale.
     * @param onTick     Callback invocata ogni secondo, utile per aggiornare slider
     *                   e label.
     * @param onFinished Callback invocata al termine naturale della traccia.
     */
    public void start(Track track, Consumer<Integer> onTick, Runnable onFinished) {
        stop();
        elapsedSeconds = 0;
        int total = track.getDuration();

        progressTimeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), e -> {
            elapsedSeconds++;
            onTick.accept(elapsedSeconds);
        }));
        progressTimeline.setCycleCount(total);
        progressTimeline.play();

        playbackTimer = new PauseTransition(Duration.seconds(total));
        playbackTimer.setOnFinished(e -> onFinished.run());
        playbackTimer.play();
    }

    /**
     * @brief Ferma e distrugge i timer correnti.
     */
    public void stop() {
        if (playbackTimer != null) {
            playbackTimer.stop();
        }
        if (progressTimeline != null) {
            progressTimeline.stop();
        }
    }

    /**
     * @brief Congela i timer di riproduzione preservando lo stato.
     *        Mette in pausa la `Timeline` e la `PauseTransition` senza resettarle.
     *        In questo modo, lo slider e le etichette temporali dell'interfaccia
     *        grafica
     *        rimangono fermi esattamente al secondo in cui è stata richiesta la
     *        pausa.
     */
    public void pause() {
        if (playbackTimer != null) {
            playbackTimer.pause();
        }
        if (progressTimeline != null) {
            progressTimeline.pause();
        }
    }

    /**
     * @brief Riprende il conteggio dei timer dallo stato di pausa.
     *        Riattiva i timer dal punto esatto in cui erano stati congelati tramite
     *        il metodo `pause()`, permettendo al conteggio e alla riproduzione di
     *        continuare fluidamente.
     */
    public void resume() {
        if (playbackTimer != null) {
            playbackTimer.play();
        }
        if (progressTimeline != null) {
            progressTimeline.play();
        }
    }

    /**
     * @brief Formatta una quantità di secondi in una stringa leggibile MM:SS.
     *        Utility per la conversione del tempo intero in un formato testuale
     *        standard adatto alla visualizzazione nell'interfaccia utente.
     *        * @param totalSeconds Il tempo totale espresso in secondi.
     * @return Una stringa formattata come "MM:SS" (es. "03:45").
     */
    public String getFormattedTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}