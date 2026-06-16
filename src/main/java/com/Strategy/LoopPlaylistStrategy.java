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
     * @brief Restituisce il brano successivo (con loop all'inizio se si è alla
     *        fine).
     *
     * @param queue   Lista ordinata dei brani nella coda di riproduzione.
     * @param current Brano attualmente in riproduzione.
     * @return Il brano successivo, o il primo se current è l'ultimo.
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
     * @brief Restituisce il brano precedente, con loop alla fine se si è
     *        all'inizio.
     *
     * @param queue   Lista ordinata dei brani nella coda di riproduzione.
     * @param current Brano attualmente in riproduzione.
     * @return IL brano precedente, o l'ultimo se current è il primo.
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
