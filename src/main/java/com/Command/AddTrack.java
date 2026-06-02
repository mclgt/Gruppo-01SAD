package com.Command;

import com.Model.Library;
import com.Model.Track;

public class AddTrack implements ICommand {
    private final Library receiver;
    private Track track;

    public AddTrack(Library receiver, Track track) {
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