package com.Command;

import com.DataLayer.DAO.Track.TrackDAO;
import com.Model.Library;
import com.Model.Track;
import com.Model.TrackTag;

public class ModifyTrack implements ICommand {
    private final Library receiver;
    private final Track trackToModify;
    private final TrackDAO trackDAO;

    private final String oldTitle, oldAuthor, oldGenre, oldAlbum, oldFilePath;
    private final int oldYear, oldDuration;
    private final TrackTag oldTag;
    private final String newTitle, newAuthor, newGenre, newAlbum, newFilePath;
    private final int newYear, newDuration;
    private final TrackTag newTag;

    public ModifyTrack(Library receiver, Track trackToModify, String newTitle, String newAuthor, int newYear,
            String newGenre, int newDuration, String newAlbum, String newFilePath, TrackTag newTag, TrackDAO trackDAO) {
        this.receiver = receiver;
        this.trackToModify = trackToModify;
        this.trackDAO = trackDAO;

        // Salvo i vecchi valori per poter eseguire l'undo
        this.oldTitle = trackToModify.getTitle();
        this.oldAuthor = trackToModify.getAuthor();
        this.oldYear = trackToModify.getYear();
        this.oldGenre = trackToModify.getGenre();
        this.oldDuration = trackToModify.getDuration();
        this.oldAlbum = trackToModify.getAlbum();
        this.oldFilePath = trackToModify.getFilePath();
        this.oldTag = trackToModify.getTag();

        // Nuovi valori da impostare
        this.newTitle = newTitle;
        this.newAuthor = newAuthor;
        this.newYear = newYear;
        this.newGenre = newGenre;
        this.newDuration = newDuration;
        this.newAlbum = newAlbum;
        this.newFilePath = newFilePath;
        this.newTag = newTag;
    }

    @Override
    public void undo() {
        receiver.updateTrack(trackToModify, oldTitle, oldAuthor, oldYear, oldGenre, oldDuration, oldAlbum, oldFilePath,
                oldTag);

        try {
            if (trackDAO != null)
                trackDAO.update(trackToModify);
        } catch (Exception e) {
            System.err.println("Errore DB in ModifyTrack (execute): " + e.getMessage());
        }
    }

    @Override
    public void execute() {
        receiver.updateTrack(trackToModify, newTitle, newAuthor, newYear, newGenre, newDuration, newAlbum, newFilePath,
                newTag);

        try {
            if (trackDAO != null)
                trackDAO.update(trackToModify);
        } catch (Exception e) {
            System.err.println("Errore DB in ModifyTrack (undo): " + e.getMessage());
        }
    }
}
