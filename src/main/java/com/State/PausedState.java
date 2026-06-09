package com.State;

import com.Model.Track;
import java.util.List;

//questo stato rappresenta il player in pausa, l'ho aggiunto per gestire correttamente
//la pausa all'interno del pattern State già esistente
//quando il player è in questo stato tutte le operazioni (play, next, prev)
//prima tornano a PlayingState e poi eseguono l'azione richiesta
public class PausedState implements IPlayerState {

    private final PlayerContext context;

    public PausedState(PlayerContext context) {
        this.context = context;
    }

    //se viene chiamato play mentre sono in pausa torno a PlayingState e avvio la traccia normalmente
    //questo gestisce il caso in cui si cambia canzone mentre si è in pausa
    @Override
    public void play(Track track) {
        context.setState(context.getPlayingState());
        context.play(track);
    }

    //se sono già in pausa e viene chiamato pause di nuovo non faccio nulla
    @Override
    public void pause() {
        // già in pausa, no-op
    }

    //in caso di stop torno semplicemente a PlayingState
    @Override
    public void stop() {
        context.setState(context.getPlayingState());
    }

    //se premo next mentre sono in pausa torno a PlayingState e passo alla traccia successiva
    //la traccia viene scelta dalla strategy corrente (loop, shuffle o sequential)
    @Override
    public void next(List<Track> queue, Track current) {
        context.setState(context.getPlayingState());
        context.next(queue, current);
    }

    //stesso comportamento di next ma per la traccia precedente
    @Override
    public void previous(List<Track> queue, Track current) {
        context.setState(context.getPlayingState());
        context.previous(queue, current);
    }
}
