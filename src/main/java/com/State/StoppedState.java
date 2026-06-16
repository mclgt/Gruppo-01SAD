package com.State;

import com.Model.Track;

public class StoppedState implements IPlayerState {

    public StoppedState(PlayerContext context) {}

    @Override
    public void play(Track track) {}

    @Override
    public void pause() {}

    @Override
    public void stop() {}

    @Override
    public void next() {}

    @Override
    public void previous() {}
}
