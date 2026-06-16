package com.State;

import com.Model.Track;

public interface IPlayerState {
    void play(Track track);

    void pause();

    void stop();

    void next();

    void previous();
}