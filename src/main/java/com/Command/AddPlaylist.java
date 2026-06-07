package com.Command;

import com.Model.Playlist;
import com.Model.PlaylistCatalog;

public class AddPlaylist implements ICommand {
    private final PlaylistCatalog playlistCatalog;
    private final Playlist playlist;

    public AddPlaylist(PlaylistCatalog playlistCatalog, Playlist playlist) {
        this.playlistCatalog = playlistCatalog;
        this.playlist = playlist;
    }

    @Override
    public void execute() {
        playlistCatalog.addPlaylist(playlist);
    }

    @Override
    public void undo() {
        playlistCatalog.removePlaylist(playlist);
    }
}