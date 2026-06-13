package com.State;

import com.Strategy.PlaybackContext;
import com.Model.Track;
import com.Observer.IPlayerSubscriber;

import java.util.List;
import java.util.ArrayList;

/**
 * @class PlayerContext
 * @brief Context del pattern State per il player musicale.
 *
 *        Mantiene il riferimento allo stato corrente (@ref IPlayerState) e
 *        delega ad esso tutte le operazioni di riproduzione (play, pause, stop,
 *        next, previous). Lo stato iniziale è @ref PlayingState e può essere
 *        cambiato a runtime tramite @ref setState.
 *
 *        Collabora con @ref PlaybackContext (pattern Strategy) per determinare
 *        la traccia successiva o precedente in base alla strategia attiva.
 *
 * @see IPlayerState
 * @see PlayingState
 * @see PlaybackContext
 */
public class PlayerContext {

    /**
     * @brief Stato "in riproduzione"; usato come riferimento fisso per isPlaying().
     */
    private IPlayerState playingState;

    /**
     * @brief Stato "in pausa"; serve per gestire la transizione tra in riproduzione
     *        e pausa.
     */
    private IPlayerState pausedState;

    /** @brief Stato attualmente attivo; le operazioni vengono delegate a questo. */
    private IPlayerState currentState;

    /** @brief Context della strategia di navigazione tra le tracce. */
    private PlaybackContext playbackContext;

    /**
     * @brief Lista dei sottoscrittori che ricevono notifiche sui cambiamenti di
     *        riproduzione
     */
    private List<IPlayerSubscriber> listeners = new ArrayList<>();

    /** @brief Traccia attualmente selezionata/riprodotta. */
    private Track currentTrack;

    /**
     * @brief Costruisce il context inizializzando lo stato corrente a @ref
     *        PlayingState.
     * @param playbackContext Il context della strategia di riproduzione da
     *                        utilizzare.
     */
    public PlayerContext(PlaybackContext playbackContext) {
        this.playbackContext = playbackContext;
        this.playingState = new PlayingState(this);
        this.pausedState = new PausedState(this);
        this.currentState = playingState;
    }

    /**
     * @brief Cambia lo stato corrente del player.
     * @param state Il nuovo stato da impostare.
     */
    public void setState(IPlayerState state) {
        this.currentState = state;
    }

    /**
     * @brief Restituisce l'istanza dello stato PlayingState.
     * @return L'oggetto IPlayerState di tipo PlayingState.
     */
    public IPlayerState getPlayingState() {
        return playingState;
    }

    /**
     * @brief Restituisce il riferimento a PausedState
     * @return L'oggetto IPlayerState di tipo PausedState
     */
    public IPlayerState getPausedState() {
        return pausedState;
    }

    /**
     * @brief Verifica se il player si trova nello stato di pausa
     * @return true se lo stato è PausedState, false altrimenti
     */
    public boolean isPaused() {
        return currentState == pausedState;
    }

    /**
     * @brief Restituisce il context della strategia di riproduzione.
     * @return L'oggetto PlaybackContext usato per il calcolo della traccia
     *         successiva/precedente
     */
    public PlaybackContext getPlaybackContext() {
        return playbackContext;
    }

    /**
     * @brief Imposta la traccia corrente senza avviare la riproduzione.
     * @param track La traccia da impostare come corrente.
     */
    public void setCurrentTrack(Track track) {
        this.currentTrack = track;
    }

    /**
     * @brief Restituisce la traccia attualmente impostata come corrente.
     * @return La Track corrente, o null se nessuna traccia è stata
     *         impostata.
     */
    public Track getCurrentTrack() {
        return currentTrack;
    }

    /**
     * @brief Indica se il player si trova nello stato di riproduzione attiva.
     * @return true se lo stato corrente è PlayingState, false
     *         altrimenti.
     */
    public boolean isPlaying() {
        return currentState == playingState;
    }

    /**
     * @brief Avvia la riproduzione della traccia specificata delegando allo stato
     *        corrente.
     * @param track La traccia da riprodurre.
     */
    public void play(Track track) {
        currentState.play(track);
        this.currentTrack = track;
        notifySubscribers();
    }

    /**
     * @brief Mette in pausa la riproduzione delegando allo stato corrente.
     */
    public void pause() {
        currentState.pause();
    }

    /**
     * @brief Ferma la riproduzione delegando allo stato corrente.
     */
    public void stop() {
        this.currentTrack = null;
        currentState.stop();
        notifySubscribers();
    }

    /**
     * @brief Calcola e passa alla traccia successiva utilizzando la trategia
     *        attiva, delengando allo stato corrente l'esecuzione.
     * @param queue   La lista completa delle tracce disponibili.
     * @param current La traccia attualmente in riproduzione.
     */
    public void next(List<Track> queue, Track current) {
        Track nexTrack = this.playbackContext.nextTrack(queue, current);
        if (nexTrack != null) {
            this.setCurrentTrack(nexTrack);
            notifySubscribers();
        }
        if (currentState != null) {
            currentState.next(queue, current);
        }
    }

    /**
     * @brief Calcola e torna alla traccia precedente utilizzando la strategia
     *        attiva, delegando allo stato corrente l'esecuzione.
     * @param queue   La lista completa delle tracce disponibili.
     * @param current La traccia attualmente in riproduzione.
     */
    public void previous(List<Track> queue, Track current) {
        Track prevTrack = this.playbackContext.previousTrack(queue, current);
        if (prevTrack != null) {
            this.setCurrentTrack(prevTrack);
            notifySubscribers();
        }
        currentState.previous(queue, current);
    }

    /**
     * @brief Registra un nuovo subscriber per le notifiche del playback
     * @param s oggetto che implementa l'interfaccia subscriber
     */
    public void subscribe(IPlayerSubscriber s) {
        listeners.add(s);
    }

    /**
     * @brief Rimuove il subscriber dalla ricezione di notifiche del playback
     * @param s oggetto che implementa l'interfaccia subscriber
     */
    public void unsubscribe(IPlayerSubscriber s) {
        listeners.remove(s);
    }

    /**
     * @brief Notifica tutti i subscriber del cambiamento della traccia corrente
     */
    private void notifySubscribers() {
        for (IPlayerSubscriber s : listeners) {
            s.onPlaybackChanged(this.currentTrack);
        }
    }

}
