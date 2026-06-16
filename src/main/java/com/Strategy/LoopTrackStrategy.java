package com.Strategy;

import java.util.List;

import com.Model.Track;

/**
 * @class LoopStrategy
 * @brief Strategia di riproduzione in loop sul brano corrente.
 *
 *        Il brano corrente viene ripetuto indefinitamente: sia il comando
 *        "successivo" che "precedente" restituiscono sempre lo stesso brano.
 */
public class LoopTrackStrategy implements IPlaybackStrategy {

    @Override
    public void setQueue(List<Track> queue, Track currentTrack){
        
    }

    @Override
    public void updateQueue(List<Track> updatedQueue){
        
    }
    /**
     * @brief Restituisce il brano corrente (loop sul singolo brano).
     *
     * @param queue   Lista ordinata dei brani nella coda di riproduzione.
     * @param current Brano attualmente in riproduzione.
     * @return Lo stesso brano corrente.
     */
    @Override
    public Track nextTrack(Track current) {
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
    public Track previousTrack(Track current) {
        return current;
    }

    
}
