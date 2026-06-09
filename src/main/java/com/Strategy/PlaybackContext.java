package com.Strategy;

import com.Model.Track;
import java.util.List;

/**
 * @class PlaybackContext
 * @brief Context del pattern Strategy per la riproduzione.
 *
 *        Mantiene un riferimento all'IPlaybackStrategy corrente e
 *        consente il cambio di strategia a runtime.
 *        Viene usato dal TrackProxy (DataLayer) quando una traccia finisce.
 */
public class PlaybackContext {

    private IPlaybackStrategy strategy;

    /**
     * @brief Inizializza il context con la strategia di riproduzione iniziale.
     * @param strategy Strategia da usare al momento della creazione.
     */
    public PlaybackContext(IPlaybackStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * @brief Sostituisce la strategia corrente a runtime.
     * @param strategy Nuova strategia da adottare (es. da sequenziale a shuffle).
     */
    public void setStrategy(IPlaybackStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * @brief Restituisce la strategia attualmente attiva.
     * @return Riferimento all'@ref IPlaybackStrategy corrente.
     */
    public IPlaybackStrategy getStrategy() {
        return strategy;
    }

    /**
     * @brief Delega alla strategia corrente il calcolo del brano successivo.
     * @param queue   Lista ordinata dei brani nella coda di riproduzione.
     * @param current Brano attualmente in riproduzione.
     * @return Il brano successivo secondo la strategia attiva, o {@code null} se la coda è terminata.
     */
    public Track nextTrack(List<Track> queue, Track current) {
        return strategy.nextTrack(queue, current);
    }

    /**
     * @brief Delega alla strategia corrente il calcolo del brano precedente.
     * @param queue   Lista ordinata dei brani nella coda di riproduzione.
     * @param current Brano attualmente in riproduzione.
     * @return Il brano precedente secondo la strategia attiva, o {@code null} se si è a inizio coda.
     */
    public Track previousTrack(List<Track> queue, Track current) {
        return strategy.previousTrack(queue, current);
    }
}