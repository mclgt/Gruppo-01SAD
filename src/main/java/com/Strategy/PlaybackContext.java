package com.Strategy;

import com.Model.Track;
import java.util.List;

/**
 * @class PlaybackContext
 * @brief Context del pattern Strategy per la riproduzione.
 *
 *        Mantiene un riferimento all'IPlaybackStrategy corrente e
 *        consente il cambio di strategia a runtime.
 *        Viene usato dal TrackProxy (DataLayer) quando una traccia finisce.
 */
public class PlaybackContext {

    private IPlaybackStrategy strategy;

    public PlaybackContext(IPlaybackStrategy strategy) {
        this.strategy = strategy;
    }

    // Cambia la strategia a runtime (es. dall'interfaccia grafica )
    public void setStrategy(IPlaybackStrategy strategy) {
        this.strategy = strategy;
    }

    public IPlaybackStrategy getStrategy() {
        return strategy;
    }

    public Track nextTrack(List<Track> queue, Track current) {
        return strategy.nextTrack(queue, current);
    }

    public Track previousTrack(List<Track> queue, Track current) {
        return strategy.previousTrack(queue, current);
    }
}