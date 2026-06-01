package com.Strategy;

import com.Model.Track;
import java.util.List;

/**
 * @class SequentialStrategy
 * @brief Strategia di riproduzione sequenziale.
 *
 * Riproduce i brani nell'ordine della coda senza ripetizioni.
 * La riproduzione si arresta alla fine (o all'inizio) della coda.
 */
public class SequentialStrategy implements IPlaybackStrategy {

    /**
     * @brief Restituisce il brano successivo nella coda.
     *
     * Se il brano corrente è l'ultimo (o non trovato),
     * restituisce {@code null} per segnalare la fine della riproduzione.
     *
     * @param queue   Lista ordinata dei brani nella coda di riproduzione.
     * @param current Brano attualmente in riproduzione.
     * @return Il brano successivo, oppure {@code null} se si è a fine coda.
     */
    @Override
    public Track nextTrack(List<Track> queue, Track current) {
        int currentIndex = queue.indexOf(current);
        if (currentIndex == -1 || currentIndex == queue.size() - 1) {
            return null;
        }
        return queue.get(currentIndex + 1);
    }

    /**
     * @brief Restituisce il brano precedente nella coda.
     *
     * Se il brano corrente è il primo (o non trovato),
     * restituisce {@code null} per segnalare l'inizio della riproduzione.
     *
     * @param queue   Lista ordinata dei brani nella coda di riproduzione.
     * @param current Brano attualmente in riproduzione.
     * @return Il brano precedente, oppure {@code null} se si è a inizio coda.
     */
    @Override
    public Track previousTrack(List<Track> queue, Track current) {
        int currentIndex = queue.indexOf(current);
        if (currentIndex <= 0) {
            return null;
        }
        return queue.get(currentIndex - 1);
    }
}
