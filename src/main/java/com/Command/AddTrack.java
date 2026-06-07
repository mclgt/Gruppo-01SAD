package com.Command;

import com.Model.ITrackContainer;
import com.Model.Track;

public class AddTrack implements ICommand {
    private final ITrackContainer receiver;
    private Track track;

    public AddTrack(ITrackContainer receiver, Track track) {
        this.receiver = receiver;
        this.track = track;
    }

    @Override
    public void execute() {
        this.receiver.addTrack(this.track);
    }

    @Override
    public void undo() {
        this.receiver.removeTrack(this.track);
    }
    
}