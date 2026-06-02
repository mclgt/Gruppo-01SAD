package com.Command;

import com.Model.Track;
import com.Model.Library;

public class ModifyTrack implements ICommand {
    private final Library receiver;
    private final Track trackToModify;

    private final String oldTitle, oldAuthor, oldGenre, oldAlbum, oldFilePath;
    private final int oldYear, oldDuration;

    private final String newTitle, newAuthor, newGenre, newAlbum, newFilePath;
    private final int newYear, newDuration;

    public ModifyTrack(Library receiver, Track trackToModify, String newTitle, String newAuthor, int newYear,
            String newGenre, int newDuration, String newAlbum, String newFilePath) {
        this.receiver = receiver;
        this.trackToModify = trackToModify;

        // Salvo i vecchi valori per poter eseguire l'undo
        this.oldTitle = trackToModify.getTitle();
        this.oldAuthor = trackToModify.getAuthor();
        this.oldYear = trackToModify.getYear();
        this.oldGenre = trackToModify.getGenre();
        this.oldDuration = trackToModify.getDuration();
        this.oldAlbum = trackToModify.getAlbum();
        this.oldFilePath = trackToModify.getFilePath();

        // Nuovi valori da impostare
        this.newTitle = newTitle;
        this.newAuthor = newAuthor;
        this.newYear = newYear;
        this.newGenre = newGenre;
        this.newDuration = newDuration;
        this.newAlbum = newAlbum;
        this.newFilePath = newFilePath;
    }

    @Override
    public void undo() {
        receiver.updateTrack(trackToModify, newTitle, newAuthor, newYear, newGenre, newDuration, newAlbum, newFilePath);
    }

    @Override
    public void execute() {
        receiver.updateTrack(trackToModify, oldTitle, oldAuthor, oldYear, oldGenre, oldDuration, oldAlbum, oldFilePath);
    }
}
