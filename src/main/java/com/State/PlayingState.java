package com.State;

import com.Model.Track;
import java.util.List;

public class PlayingState implements IPlayerState {

    private PlayerContext context;

    public PlayingState(PlayerContext context) {
        this.context = context;
    }

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

    //ho implementato pause() che prima era vuoto: cambia lo stato corrente a PausedState
    //così tutte le operazioni successive vengono delegate allo stato di pausa
    @Override
    public void pause() {
        context.setState(context.getPausedState());
    }

    @Override
    public void stop() {
    }

    @Override
    public void next(List<Track> queue, Track current) {
        Track nextTrack = context.getPlaybackContext().nextTrack(queue, current);
        if (nextTrack != null) {
            System.out.println("Playing next track: " + nextTrack.getTitle());
            play(nextTrack);
        } else {
            System.out.println("No next track available.");
            context.setCurrentTrack(null);
            stop();
        }
    }

    @Override
    public void previous(List<Track> queue, Track current) {
        Track previousTrack = context.getPlaybackContext().previousTrack(queue, current);
        if (previousTrack != null) {
            System.out.println("Playing previous track: " + previousTrack.getTitle());
            play(previousTrack);
        } else {
            System.out.println("No previous track available.");
            context.setCurrentTrack(null);
            stop();
        }
    }

}
