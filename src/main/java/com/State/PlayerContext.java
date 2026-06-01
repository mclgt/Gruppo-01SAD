package com.State;

import com.Strategy.PlaybackContext;
import com.Model.Track;
import java.util.List;

public class PlayerContext {

    private IPlayerState playingState;
    private IPlayerState currentState;

    private PlaybackContext playbackContext;
    private Track currentTrack;

    public PlayerContext(PlaybackContext playbackContext) {
        this.playbackContext = playbackContext;
        this.playingState = new PlayingState(this);
        this.currentState = playingState;
    }

    public void setState(IPlayerState state) {
        this.currentState = state;
    }

    public IPlayerState getPlayingState() {
        return playingState;
    }

    public PlaybackContext getPlaybackContext() {
        return playbackContext;
    }

    public void setCurrentTrack(Track track) {
        this.currentTrack = track;
    }

    public Track getCurrentTrack() {
        return currentTrack;
    }

    public boolean isPlaying() {
        return currentState == playingState;
    }

    public void play(Track track) {
        currentState.play(track);
    }

    public void pause() {
        currentState.pause();
    }

    public void stop() {
        currentState.stop();
    }

    public void next(List<Track> queue, Track current) {
        currentState.next(queue, current);
    }

    public void previous(List<Track> queue, Track current) {
        currentState.previous(queue, current);
    }
}
