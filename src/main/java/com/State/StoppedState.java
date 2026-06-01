package com.State;

import com.Model.Track;
import java.util.List;

public class StoppedState implements IPlayerState {

    public StoppedState(PlayerContext context) {}

    @Override
    public void play(Track track) {}

    @Override
    public void pause() {}

    @Override
    public void stop() {}

    @Override
    public void next(List<Track> queue, Track current) {}

    @Override
    public void previous(List<Track> queue, Track current) {}
}
