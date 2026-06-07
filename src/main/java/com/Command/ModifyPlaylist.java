package com.Command;

import com.Model.Library;
import com.Model.Playlist;

public class ModifyPlaylist implements ICommand {
    private final Library receiver;
    private final Playlist playlistToModify;

    private String oldName;
    private String newName;

    public ModifyPlaylist(Library receiver, Playlist playlistToModify, String newName) {
        this.playlistToModify = playlistToModify;
        this.receiver = receiver;
        this.oldName = playlistToModify.getName();
        this.newName = newName;
    }

    @Override
    public void undo() {

    }

    @Override
    public void execute() {

    }

}
