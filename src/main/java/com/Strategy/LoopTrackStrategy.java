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
     * @brief Restituisce il brano corrente per consentire la ripetizione infinita.
     *
     * @param current Il brano attualmente in riproduzione.
     * @return Lo stesso brano passato come parametro (current).
     */
    @Override
    public Track nextTrack(Track current) {
        return current;
    }

    
    /**
     * @brief Restituisce il brano corrente per consentire la ripetizione infinita.
     *
     * @param current Il brano attualmente in riproduzione.
     * @return Lo stesso brano passato come parametro (current).
     */
    @Override
    public Track previousTrack(Track current) {
        return current;
    }

    
}
