package com.Command;

import com.Model.Playlist;
import com.Model.PlaylistCatalog;

public class ModifyPlaylist implements ICommand {
    private final PlaylistCatalog receiver;
    private final Playlist playlistToModify;

    private String oldName;
    private String newName;

    public ModifyPlaylist(PlaylistCatalog receiver, Playlist playlistToModify, String newName) {
        this.playlistToModify = playlistToModify;
        this.receiver = receiver;
        this.oldName = playlistToModify.getName();
        this.newName = newName;
    }

    @Override
    public void execute() {
        this.receiver.updatePlaylist(playlistToModify, newName);
    }

    @Override
    public void undo() {
        this.receiver.updatePlaylist(playlistToModify, oldName);
    }
}