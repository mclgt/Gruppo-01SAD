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

    /**
     * @brief Inizializza la coda di riproduzione e memorizza il riferimento al brano corrente.
     * @param queue La lista di brani che costituirà la coda.
     * @param currentTrack Il brano attualmente in esecuzione.
     */
    @Override
    public void setQueue(List<Track> queue, Track currentTrack){
        this.queue = queue != null ? queue : new ArrayList<>();
        this.currenTrack=currentTrack;
    }

    /**
     * @brief Aggiorna la lista dei brani nella coda a runtime.
     * @param updatedQueue La nuova lista di brani aggiornata.
     */
    @Override
    public void updateQueue(List<Track> updatedQueue){
        this.queue = updatedQueue!= null ? updatedQueue : new ArrayList<>();
        
    }
    
    /**
     * @brief Restituisce un brano casuale dalla coda, escludendo quello corrente.
     *
     * @param current Il brano attualmente in riproduzione.
     * @return Un Track scelto casualmente, il brano unico se la coda ha dimensione 1, oppure null se la coda è vuota.
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
     * @brief Restituisce un brano casuale delegando il comportamento a nextTrack.
     *
     * Non essendoci una cronologia dei brani passati memorizzata in questa strategia,
     * il comando precedente genera un nuovo brano casuale.
     *
     * @param current Il brano attualmente in riproduzione.
     * @return Un Track scelto casualmente.
     */
    @Override
    public Track previousTrack(Track current){
        return nextTrack(current);
        
    }

    /**
     * @brief Estrae un indice casuale valido all'interno della coda, escludendo l'indice specificato.
     *
     * @param queue La lista dalla quale estrarre l'indice.
     * @param excludeIndex L'indice da escludere (corrispondente al brano corrente).
     * @return Un valore intero che rappresenta l'indice estratto.
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
