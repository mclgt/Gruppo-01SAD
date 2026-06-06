package com.Strategy;

import com.Model.Track;
import java.util.List;
import java.util.Random;
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

    private final Random random = new Random();

    @Override
    public Track nextTrack(List<Track> queue, Track current) {
        if (queue.isEmpty()) return null;
        if (queue.size() == 1) return queue.get(0);
        //richiamo pickRandomIndex per ottenere un indice casuale diverso da quello del brano corrente
        return queue.get(pickRandomIndex(queue, queue.indexOf(current)));
    }

    @Override
    public Track previousTrack(List<Track> queue, Track current) {
        return nextTrack(queue, current);
    }

    /**
     * @brief Restituisce un indice casuale diverso da {@code excludeIndex}.
     *
     * @param queue        Lista da cui pescare l'indice.
     * @param excludeIndex Indice da escludere (il brano corrente).
     * @return Indice casuale valido, diverso da {@code excludeIndex}.
     */
    private int pickRandomIndex(List<Track> queue, int excludeIndex) {
        int index;
        do {
            index = random.nextInt(queue.size());
        } while (index == excludeIndex); 
        //specifico che non devo selezionare lo stesso brano corrente
        return index;
    }
}
