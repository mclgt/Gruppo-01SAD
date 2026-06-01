package com.Strategy;

import com.Model.Track;
import java.util.List;

/**
 * @class LoopStrategy
 * @brief Strategia di riproduzione in loop.
 *
 * Al termine della coda il lettore torna al primo brano;
 * all'inizio della coda torna all'ultimo, garantendo una
 * riproduzione ciclica continua.
 */
public class LoopStrategy implements IPlaybackStrategy {

    /**
     * @brief Restituisce il brano successivo con wrap-around.
     *
     * Se il brano corrente è l'ultimo della coda (o non trovato),
     * restituisce il primo brano.
     *
     * @param queue   Lista ordinata dei brani nella coda di riproduzione.
     * @param current Brano attualmente in riproduzione.
     * @return Il brano successivo, oppure il primo brano se si è a fine coda.
     */
    @Override
    public Track nextTrack(List<Track> queue, Track current) {
        return null; // Implementazione da completare
    }

    /**
     * @brief Restituisce il brano precedente con wrap-around.
     *
     * Se il brano corrente è il primo della coda (o non trovato),
     * restituisce l'ultimo brano.
     *
     * @param queue   Lista ordinata dei brani nella coda di riproduzione.
     * @param current Brano attualmente in riproduzione.
     * @return Il brano precedente, oppure l'ultimo brano se si è a inizio coda.
     */
    @Override
    public Track previousTrack(List<Track> queue, Track current) {
        return null; // Implementazione da completare
    }
}
