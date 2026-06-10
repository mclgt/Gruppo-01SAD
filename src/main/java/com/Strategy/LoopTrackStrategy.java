package com.Strategy;

import com.Model.Track;
import java.util.List;

/**
 * @class LoopStrategy
 * @brief Strategia di riproduzione in loop sul brano corrente.
 *
 *        Il brano corrente viene ripetuto indefinitamente: sia il comando
 *        "successivo" che "precedente" restituiscono sempre lo stesso brano.
 */
public class LoopTrackStrategy implements IPlaybackStrategy {

    /**
     * @brief Restituisce il brano corrente (loop sul singolo brano).
     *
     * @param queue   Lista ordinata dei brani nella coda di riproduzione.
     * @param current Brano attualmente in riproduzione.
     * @return Lo stesso brano corrente.
     */
    @Override
    public Track nextTrack(List<Track> queue, Track current) {
        return current;
    }

    /**
     * @brief Restituisce il brano corrente (loop sul singolo brano).
     *
     * @param queue   Lista ordinata dei brani nella coda di riproduzione.
     * @param current Brano attualmente in riproduzione.
     * @return Lo stesso brano corrente.
     */
    @Override
    public Track previousTrack(List<Track> queue, Track current) {
        return current;
    }

}
