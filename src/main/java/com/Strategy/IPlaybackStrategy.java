package com.Strategy;

import java.util.List;
import com.Model.Track;

/**
 * @interface IPlaybackStrategy
 * @brief Strategia di riproduzione per una coda di brani.
 *
 * Definisce il contratto per le strategie di navigazione tra i brani
 * (sequenziale, loop, shuffle, ecc.).
 */
public interface IPlaybackStrategy {

    /**
     * @brief Restituisce il brano successivo nella coda.
     *
     * @param queue   Lista ordinata dei brani nella coda di riproduzione.
     * @param current Brano attualmente in riproduzione.
     * @return Il brano successivo, o {@code null} se la riproduzione deve fermarsi.
     */
    Track nextTrack(List<Track> queue, Track current);

    /**
     * @brief Restituisce il brano precedente nella coda.
     *
     * @param queue   Lista ordinata dei brani nella coda di riproduzione.
     * @param current Brano attualmente in riproduzione.
     * @return Il brano precedente, o {@code null} se la riproduzione deve fermarsi.
     */
    Track previousTrack(List<Track> queue, Track current);
}
