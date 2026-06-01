package com.Strategy;

import com.Model.Track;
import java.util.List;

/**
 * @class ShuffleStrategy
 * @brief Strategia di riproduzione casuale (shuffle).
 *
 *        Seleziona un brano a caso dalla coda, escludendo sempre
 *        il brano corrente per evitare ripetizioni immediate.
 *        La navigazione "precedente" si comporta come "successivo"
 *        poiché non esiste una storia della riproduzione casuale.
 */
public class ShuffleStrategy implements IPlaybackStrategy {

    /**
     * @brief Restituisce un brano casuale dalla coda.
     *
     *        Garantisce che il brano restituito sia diverso da @p current,
     *        a meno che la coda non contenga un solo brano.
     *
     * @param queue   Lista dei brani disponibili nella coda.
     * @param current Brano attualmente in riproduzione.
     * @return Un brano scelto casualmente, diverso da @p current.
     *         Restituisce {@code null} se la coda è vuota.
     */
    @Override
    public Track nextTrack(List<Track> queue, Track current) {
        return null; // Implementazione da completare
    }

    /**
     * @brief Restituisce un brano casuale dalla coda (equivalente a nextTrack).
     *
     *        In modalità shuffle non esiste una direzione "indietro" definita,
     *        pertanto il comportamento è identico a {@link #nextTrack}.
     *
     * @param queue   Lista dei brani disponibili nella coda.
     * @param current Brano attualmente in riproduzione.
     * @return Un brano scelto casualmente, diverso da @p current.
     */
    @Override
    public Track previousTrack(List<Track> queue, Track current) {
        return null; // Implementazione da completare
    }
}
