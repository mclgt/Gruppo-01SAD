package com.State;

import java.util.List;
import com.Model.Track;

public interface IPlayerState {
    void play(Track track);

    void pause();

    void stop();

    void next(List<Track> queue, Track current);

    void previous(List<Track> queue, Track current);
}