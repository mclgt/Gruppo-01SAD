package com.Strategy;

import java.util.ArrayList;
import java.util.List;

import com.Model.Track;

/**
 * @class SequentialStrategy
 * @brief Strategia di riproduzione sequenziale.
 *
 *        Riproduce i brani nell'ordine della coda senza ripetizioni.
 *        La riproduzione si arresta alla fine (o all'inizio) della coda.
 */
public class SequentialStrategy implements IPlaybackStrategy {

    private List<Track> queue = new ArrayList<>();
    private int currentIndex = -1;


    @Override
    public void setQueue(List<Track> queue, Track currentTrack){
        this.queue = queue != null ? queue : new ArrayList<>();
        if(currentTrack!=null){
            this.currentIndex=this.queue.indexOf(currentTrack);
        }
        else{
            this.currentIndex=0;
        }
    }

    @Override
    public void updateQueue(List<Track> updatedQueue){
        Track currentTrack = (currentIndex >= 0 && currentIndex < queue.size()) ? queue.get(currentIndex) : null;
        this.queue = updatedQueue!= null ? updatedQueue : new ArrayList<>();
        if (currentTrack != null){
            this.currentIndex=this.queue.indexOf(currentTrack);
        }
    }
    /**
     * @brief Restituisce il brano successivo nella coda.
     *
     *        Se il brano corrente è l'ultimo (o non trovato),
     *        restituisce {@code null} per segnalare la fine della riproduzione.
     *
     * @param queue   Lista ordinata dei brani nella coda di riproduzione.
     * @param current Brano attualmente in riproduzione.
     * @return Il brano successivo, oppure {@code null} se si è a fine coda.
     */
    

    @Override
    public Track nextTrack(Track current){
        if(queue.isEmpty()){
            return null;
        }
        if (current != null){
            if(!queue.contains(current)){
                return null;
            }
            this.currentIndex = queue.indexOf(current);
        }
        if(currentIndex < queue.size()-1){
            currentIndex++;
            return queue.get(currentIndex);
        }
        return null;
    }
    /**
     * @brief Restituisce il brano precedente nella coda.
     *
     *        Se il brano corrente è il primo (o non trovato),
     *        restituisce {@code null} per segnalare l'inizio della riproduzione.
     *
     * @param queue   Lista ordinata dei brani nella coda di riproduzione.
     * @param current Brano attualmente in riproduzione.
     * @return Il brano precedente, oppure {@code null} se si è a inizio coda.
     */
  
    @Override
    public Track previousTrack(Track current){
        if(queue.isEmpty()){
            return null;
        }
        if(current!=null){
            this.currentIndex=queue.indexOf(current);
        }
        if(currentIndex > 0){
            currentIndex--;
            return queue.get(currentIndex);
        }
        return null;
    }
}
