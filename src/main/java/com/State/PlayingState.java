package com.State;



import com.Model.Track;
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
        context.setCurrentTrack(track);
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
     * @brief Avanza alla traccia successiva sfruttando la strategia di riproduzione attiva.
     *
     * Calcola la traccia successiva tramite il PlaybackContext. Se disponibile, aggiorna 
     * il brano corrente del player; se la coda è terminata (restituisce null), invoca lo stop del player.
     */
    @Override
    public void next() {
        Track nextTrack = context.getPlaybackContext().getStrategy().nextTrack(context.getCurrentTrack());
        if (nextTrack != null) {
            context.setCurrentTrack(nextTrack);
            System.out.println("Next track set: " + nextTrack.getTitle());
        } else {
            context.stop();
            System.out.println("No next track available.");
        }
    }

    /**
     * @brief Ritorna alla traccia precedente sfruttando la strategia di riproduzione attiva.
     *
     * Calcola la traccia precedente tramite il PlaybackContext. Se disponibile, aggiorna 
     * il brano corrente del player; se l'inizio della coda è stato raggiunto (restituisce null), invoca lo stop del player.
     */
    @Override
    public void previous() {
        Track previousTrack = context.getPlaybackContext().getStrategy().previousTrack(context.getCurrentTrack());
        if (previousTrack != null) {
            context.setCurrentTrack(previousTrack);
            System.out.println("Previous track set: " + previousTrack.getTitle());
        } else {
            context.stop();
            System.out.println("No previous track available.");
        }
    }

}
