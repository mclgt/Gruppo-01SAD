package com.Model;

import javafx.collections.ObservableList;
public interface ITrackContainer {
    void addTrack(Track track);

    void addTrack(int index, Track track);

    boolean removeTrack(Track track);

    int indexOf(Track track);
    ObservableList<Track> getTracks();

    public boolean moveTrackUp(Track track);
    
    public boolean moveTrackDown(Track track);
}
