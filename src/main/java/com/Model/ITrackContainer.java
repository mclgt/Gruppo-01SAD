package com.Model;

public interface ITrackContainer {
    void addTrack(Track track);
    void addTrack(int index, Track track);
    boolean removeTrack(Track track);
    int indexOf(Track track);
}
