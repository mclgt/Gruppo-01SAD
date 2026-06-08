package com.State;

import com.Model.Track;
import java.util.List;

/**
 * @class PlayingState
 * @brief Stato di riproduzione attiva nel pattern State del player.
 *
 *        Implementa @ref IPlayerState per il caso in cui il player stia
 *        riproducendo un brano. Gestisce avvio, pausa, navigazione e
 *        arresto delegando gli aggiornamenti di traccia a @ref PlayerContext.
 *        Quando non esiste una traccia successiva o precedente, la traccia
 *        corrente rimane invariata senza azzerare il context.
 *
 * @see IPlayerState
 * @see PlayerContext
 * @see PausedState
 */
public class PlayingState implements IPlayerState {

    private PlayerContext context;

    /**
     * @brief Costruisce lo stato di riproduzione associandolo al context.
     * @param context Il @ref PlayerContext che gestisce le transizioni di stato.
     */
    public PlayingState(PlayerContext context) {
        this.context = context;
    }

    /**
     * @brief Avvia la riproduzione della traccia specificata.
     *        Ferma l'eventuale traccia in corso, aggiorna la traccia corrente
     *        nel context e avvia il file audio tramite la sorgente audio (lazy load).
     * @param track La traccia da riprodurre.
     */
    @Override
    public void play(Track track) {
        Track previous = context.getCurrentTrack();
        if (previous != null && previous.getAudioSource() != null) {
            previous.getAudioSource().stopPlayback();
        }
        context.setCurrentTrack(track);
        if (track.getAudioSource() != null) {
            track.getAudioSource().startPlayback(); // lazy load: RealTrack creato solo qui
        }
        System.out.println("Playing: " + track.getTitle());
    }

    /**
     * @brief Mette in pausa la riproduzione transitando a @ref PausedState.
     *        Tutte le operazioni successive vengono delegate allo stato di pausa.
     */
    @Override
    public void pause() {
        context.setState(context.getPausedState());
    }

    /**
     * @brief Ferma la riproduzione. In PlayingState non richiede azioni aggiuntive.
     */
    @Override
    public void stop() {
    }

    /**
     * @brief Avanza alla traccia successiva usando la strategia di riproduzione attiva.
     *        Se la strategia non restituisce una traccia successiva (fine coda),
     *        la traccia corrente rimane invariata.
     * @param queue   Lista delle tracce disponibili nel container attivo.
     * @param current Traccia attualmente in riproduzione.
     */
    @Override
    public void next(List<Track> queue, Track current) {
        Track nextTrack = context.getPlaybackContext().nextTrack(queue, current);
        if (nextTrack != null) {
            System.out.println("Playing next track: " + nextTrack.getTitle());
            play(nextTrack);
        } else {
            System.out.println("No next track available.");
        }
    }

    /**
     * @brief Torna alla traccia precedente usando la strategia di riproduzione attiva.
     *        Se la strategia non restituisce una traccia precedente (inizio coda),
     *        la traccia corrente rimane invariata.
     * @param queue   Lista delle tracce disponibili nel container attivo.
     * @param current Traccia attualmente in riproduzione.
     */
    @Override
    public void previous(List<Track> queue, Track current) {
        Track previousTrack = context.getPlaybackContext().previousTrack(queue, current);
        if (previousTrack != null) {
            System.out.println("Playing previous track: " + previousTrack.getTitle());
            play(previousTrack);
        } else {
            System.out.println("No previous track available.");
        }
    }

}
