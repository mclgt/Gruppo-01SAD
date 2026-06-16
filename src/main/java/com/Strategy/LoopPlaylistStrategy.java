package com.Strategy;

import java.util.ArrayList;
import java.util.List;

import com.Model.Track;

/**
 * @class LoopPlaylisrStrategy
 * @brief Strategia di riproduzione in loop sulla playlist corrente.
 *
 *        I brani vengono riprodotti sequenzialmente. Arrivati all'ultimo brano,
 *        il comando "successivo" riparte dal primo. Quando ci si trova nel
 *        primo brano il camndo "precedente" va all'ultimo.
 */
public class LoopPlaylistStrategy implements IPlaybackStrategy {
    private List<Track> queue = new ArrayList<>();
    private int currentIndex = -1;


    /**
     * @brief Inizializza la coda di riproduzione e imposta l'indice del brano corrente.
     * @param queue La lista di brani che costituirà la coda.
     * @param currentTrack Il brano attualmente in riproduzione da cui sincronizzare l'indice.
     */
    @Override
    public void setQueue(List<Track> queue, Track currentTrack){
        this.queue = queue /**
             * @brief Inizializza la coda di riproduzione e imposta l'indice del brano corrente.
             * * @param queue La lista di brani che costituirà la coda.
             * @param currentTrack Il brano attualmente in riproduzione da cui sincronizzare l'indice.
             */!= null ? queue : new ArrayList<>();
        if(currentTrack!=null){
            this.currentIndex=this.queue.indexOf(currentTrack);
        }
        else{
            this.currentIndex=0;
        }
    }

    /**
     * @brief Aggiorna la coda mantenendo la sincronizzazione sul brano attualmente in riproduzione.
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
     * @brief Restituisce il brano successivo all'interno della coda (con comportamento ad anello).
     *
     * @param current Il brano attualmente in riproduzione.
     * @return Il brano successivo, il primo brano se si è a fine coda, oppure null se la coda è vuota.
     */

    @Override
    public Track nextTrack(Track current) {
        if(queue.isEmpty()){
            return null;
        }
        if (current == null){
            currentIndex=0;
            return queue.get(currentIndex);
        }
        if(!queue.contains(current))
        {
            return null;
        }
        this.currentIndex=queue.indexOf(current);
        
        currentIndex = (currentIndex + 1) % queue.size();
        return queue.get(currentIndex);
       
    }

    /**
     * @brief Restituisce il brano precedente all'interno della coda (con comportamento ad anello).
     *
     * @param current Il brano attualmente in riproduzione.
     * @return Il brano precedente, l'ultimo brano se si è all'inizio della coda, oppure null se la coda è vuota.
     */
    
    @Override
    public Track previousTrack(Track current){
         if(queue.isEmpty()){
            return null;
        }
        if (current == null){
            currentIndex=0;
            return queue.get(currentIndex);
        }
        if(!queue.contains(current))
        {
            return null;
        }
        currentIndex=queue.indexOf(current);
        currentIndex=(currentIndex - 1 + queue.size()) % queue.size();
        return queue.get(currentIndex);
   
    }

}
