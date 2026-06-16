package com.Strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.Model.Track;
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

    private List<Track> queue = new ArrayList<>();
    private Track currenTrack;
    private final Random random = new Random();


    @Override
    public void setQueue(List<Track> queue, Track currentTrack){
        this.queue = queue != null ? queue : new ArrayList<>();
        this.currenTrack=currentTrack;
    }

    @Override
    public void updateQueue(List<Track> updatedQueue){
        this.queue = updatedQueue!= null ? updatedQueue : new ArrayList<>();
        
    }
    /**
     * @brief Restituisce un brano casuale dalla coda, escludendo il brano corrente.
     *
     * @param queue   Lista ordinata dei brani nella coda di riproduzione.
     * @param current Brano attualmente in riproduzione.
     * @return Un @ref Track scelto casualmente, o {@code null} se la coda è vuota.
     */
    
    @Override
    public Track nextTrack(Track current){
     if (queue.isEmpty()) return null;
        if (queue.size() == 1) return queue.get(0);
        int excludeIndex=current != null ? queue.indexOf(current) : -1;
        int nextIndex=pickRandomIndex(queue, excludeIndex);
        currenTrack=queue.get(nextIndex);
        return currenTrack;
    }
    /**
     * @brief Equivalente a {@link #nextTrack}: restituisce un brano casuale.
     *        Non esiste una storia della riproduzione casuale, quindi "precedente"
     *        si comporta come "successivo".
     *
     * @param queue   Lista ordinata dei brani nella coda di riproduzione.
     * @param current Brano attualmente in riproduzione.
     * @return Un @ref Track scelto casualmente.
     */

    @Override
    public Track previousTrack(Track current){
        return nextTrack(current);
        
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
