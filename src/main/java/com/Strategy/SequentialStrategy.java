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

    /**
     * @brief Inizializza la coda di riproduzione e imposta l'indice del brano corrente.
     * @param queue La lista di brani che costituirà la coda.
     * @param currentTrack Il brano da cui sincronizzare l'indice iniziale.
     */
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

    /**
     * @brief Aggiorna la coda mantenendo l'indice allineato al brano attualmente in riproduzione.
     * @param updatedQueue La nuova lista di brani aggiornata.
     */
    @Override
    public void updateQueue(List<Track> updatedQueue){
        Track currentTrack = (currentIndex >= 0 && currentIndex < queue.size()) ? queue.get(currentIndex) : null;
        this.queue = updatedQueue!= null ? updatedQueue : new ArrayList<>();
        if (currentTrack != null){
            this.currentIndex=this.queue.indexOf(currentTrack);
        }
    }
    

    /**
     * @brief Calcola il brano successivo procedendo linearmente nella coda.
     *
     * @param current Il brano attualmente in riproduzione.
     * @return Il brano successivo, oppure null se si è raggiunta la fine della coda.
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
     * @brief Calcola il brano precedente arretrando linearmente nella coda.
     *
     * @param current Il brano attualmente in riproduzione.
     * @return Il brano precedente, oppure null se si è raggiunto l'inizio della coda.
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
