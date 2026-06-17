package com.State;

import com.Model.Track;

/**
 * @class PausedState
 * @brief Stato di pausa nel pattern State del player musicale.
 *
 *        Rappresenta il player nella condizione in cui la riproduzione è
 *        momentaneamente sospesa. Da questo stato tutte le operazioni attive
 *        (play, next, previous) causano prima il ritorno a {@link PlayingState}
 *        e poi l'esecuzione dell'azione richiesta. La chiamata a pause() è un
 *        no-op poiché il player è già fermo.
 *
 * @see IPlayerState
 * @see PlayerContext
 * @see PlayingState
 */
public class PausedState implements IPlayerState {

    private final PlayerContext context;

    /**
     * @brief Costruisce lo stato di pausa associandolo al context.
     * @param context Il {@link PlayerContext} che gestisce le transizioni di stato.
     */
    public PausedState(PlayerContext context) {
        this.context = context;
    }

    /**
     * @brief Transisce a {@link PlayingState} e avvia la riproduzione del brano indicato.
     *        Gestisce il caso in cui l'utente cambi canzone mentre il player è in pausa.
     * @param track Il brano da riprodurre.
     */
    @Override
    public void play(Track track) {
        context.setState(context.getPlayingState());
        context.play(track);
    }

    /**
     * @brief No-op: il player è già in pausa, nessuna transizione necessaria.
     */
    @Override
    public void pause() {
        // già in pausa, no-op
    }

    /**
     * @brief Ferma la riproduzione tornando a {@link PlayingState}.
     *        La logica di arresto audio effettivo è delegata al {@link PlayerContext}.
     */
    @Override
    public void stop() {
        context.setState(context.getPlayingState());
    }

    /**
     * @brief Transisce a {@link PlayingState} e passa al brano successivo
     *        secondo la strategia di riproduzione attiva.
     */
    @Override
    public void next() {
        context.setState(context.getPlayingState());
        Track nextTrack = context.getPlaybackContext().getStrategy().nextTrack(context.getCurrentTrack());
        if(nextTrack!=null){
            context.setCurrentTrack(nextTrack);
        }
    }

    /**
     * @brief Transisce a {@link PlayingState} e torna al brano precedente
     *        secondo la strategia di riproduzione attiva.
     */
    @Override
    public void previous() {
        context.setState(context.getPlayingState());
        Track previousTrack = context.getPlaybackContext().getStrategy().previousTrack(context.getCurrentTrack());
        if(previousTrack!=null){
            context.setCurrentTrack(previousTrack);
        }
    }
}
