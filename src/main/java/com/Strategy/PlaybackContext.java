package com.Strategy;

import java.util.List;

import com.Model.Track;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
    private ObservableList<Track> currentQueue;
    
    /**
     * @brief Listener per intercettare le modifiche in tempo reale sulla coda JavaFX (es. canzoni aggiunte/rimosse).
     */
    private final ListChangeListener<Track> queueListener = change -> {
        if(strategy != null && currentQueue != null){
            strategy.updateQueue(currentQueue);
        }
    };
    /**
     * @brief Inizializza il context con la strategia di riproduzione iniziale.
     * @param strategy Strategia da usare al momento della creazione.
     */
    public PlaybackContext(IPlaybackStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * @brief Cambia la strategia a runtime senza specificare un brano di partenza.
     * @param strategy La nuova strategia da adottare.
     */
    public void setStrategy(IPlaybackStrategy strategy) {
        setStrategy(strategy, null);
    }
    /**
     * @brief Cambia la strategia a runtime sincronizzandola sul brano attualmente in riproduzione.
     * @param strategy La nuova strategia da adottare.
     * @param currentTrack Il brano attualmente in esecuzione per mantenere l'indice coerente.
     */
    public void setStrategy(IPlaybackStrategy strategy, Track currentTrack) {
        this.strategy = strategy;
        if(currentQueue != null && this.strategy != null){
            this.strategy.setQueue(currentQueue, currentTrack);
        }
    }

    /**
     * @brief Imposta una nuova coda di riproduzione osservabile (JavaFX), gestendo l'aggancio del listener.
     * @param newQueue La nuova ObservableList di brani.
     * @param startTrack Il brano dal quale avviare la riproduzione nella nuova coda.
     */
    public void setCurrentQueue(ObservableList<Track> newQueue, Track startTrack){
        if(currentQueue!=null){
            currentQueue.removeListener(queueListener);
        }

        this.currentQueue=newQueue;

        if(currentQueue!=null){
            currentQueue.addListener(queueListener);
            if(strategy!=null){
                strategy.setQueue(currentQueue, startTrack);
            }
        }
    }

    /**
     * @brief Restituisce la strategia di riproduzione attualmente attiva.
     * @return Il riferimento all'oggetto IPlaybackStrategy corrente.
     */
    public IPlaybackStrategy getStrategy() {
        return strategy;
    }

    /**
     * @brief Calcola il brano successivo delegando la decisione alla strategia attiva.
     * @param queue Lista ordinata di supporto aggiornata dei brani.
     * @param current Il brano attualmente in riproduzione.
     * @return Il brano successivo configurato dalla strategia, o null se la coda è terminata.
     */
    public Track nextTrack(List<Track> queue, Track current) {
        if(strategy!=null && current!=null){
            strategy.updateQueue(queue);
        }
        return strategy.nextTrack(current);
    }

   /**
     * @brief Calcola il brano precedente delegando la decisione alla strategia attiva.
     * @param queue Lista ordinata di supporto aggiornata dei brani.
     * @param current Il brano attualmente in riproduzione.
     * @return Il brano precedente configurato dalla strategia, o null se si è a inizio coda.
     */
    public Track previousTrack(List<Track> queue, Track current) {
        return strategy.previousTrack(current);
    }
}