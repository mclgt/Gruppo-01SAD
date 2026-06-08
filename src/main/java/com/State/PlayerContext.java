package com.State;

import com.Strategy.PlaybackContext;
import com.Model.Track;
import java.util.List;

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

    /** @brief Stato attualmente attivo; le operazioni vengono delegate a questo. */
    private IPlayerState currentState;

    /** @brief Context della strategia di navigazione tra le tracce. */
    private PlaybackContext playbackContext;

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
        this.currentState = playingState;
    }

    /**
     * @brief Cambia lo stato corrente del player.
     * @param state Il nuovo stato (@ref IPlayerState) da impostare.
     */
    public void setState(IPlayerState state) {
        this.currentState = state;
    }

    /**
     * @brief Restituisce l'istanza dello stato PlayingState.
     * @return L'oggetto @ref IPlayerState corrispondente a PlayingState.
     */
    public IPlayerState getPlayingState() {
        return playingState;
    }

    /**
     * @brief Restituisce il context della strategia di riproduzione.
     * @return L'oggetto @ref PlaybackContext associato a questo player.
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
     * @return La @ref Track corrente, o {@code null} se nessuna traccia è stata
     *         impostata.
     */
    public Track getCurrentTrack() {
        return currentTrack;
    }

    /**
     * @brief Indica se il player si trova nello stato di riproduzione attiva.
     * @return {@code true} se lo stato corrente è @ref PlayingState, {@code false}
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
        currentState.stop();
    }

    /**
     * @brief Passa alla traccia successiva delegando allo stato corrente.
     * @param queue   La lista completa delle tracce disponibili.
     * @param current La traccia attualmente in riproduzione.
     */
    public void next(List<Track> queue, Track current) {
        Track nexTrack = this.playbackContext.nextTrack(queue, current);
        if (nexTrack != null) {
            this.setCurrentTrack(nexTrack);
        }
        if (currentState != null) {
            currentState.next(queue, current);
        }
    }

    /**
     * @brief Torna alla traccia precedente delegando allo stato corrente.
     * @param queue   La lista completa delle tracce disponibili.
     * @param current La traccia attualmente in riproduzione.
     */
    public void previous(List<Track> queue, Track current) {
        Track prevTrack = this.playbackContext.previousTrack(queue, current);
        if (prevTrack != null) {
            this.setCurrentTrack(prevTrack);
        }
        currentState.previous(queue, current);
    }
}
