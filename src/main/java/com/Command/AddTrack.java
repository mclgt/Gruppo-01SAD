package com.Command;

import com.DataLayer.DAO.Track.TrackDAO;
import com.Model.ITrackContainer;
import com.Model.Track;

public class AddTrack implements ICommand {
    private final ITrackContainer receiver;
    private Track track;
    private TrackDAO trackDAO;

    public AddTrack(ITrackContainer receiver, Track track, TrackDAO trackDAO) {
        this.receiver = receiver;
        this.track = track;
        this.trackDAO = trackDAO;
    }

    @Override
    public void execute() {
        this.receiver.addTrack(this.track);
        try {
            if (trackDAO != null) {
                trackDAO.save(track);
            }
        } catch (Exception e) {
            System.err.println("Errore DB in AddTrack (execute): " + e.getMessage());
        }
    }

    @Override
    public void undo() {
        this.receiver.removeTrack(this.track);
        try {
            if (trackDAO != null) {
                trackDAO.delete(track.getId());
            }
        } catch (Exception e) {
            System.err.println("Errore DB in AddTrack (undo): " + e.getMessage());
        }
    }
}