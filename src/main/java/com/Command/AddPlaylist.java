package com.Command;

import com.DataLayer.DAO.Playlist.PlaylistDAO;
import com.Model.Playlist;
import com.Model.PlaylistCatalog;

public class AddPlaylist implements ICommand {
    private final PlaylistCatalog playlistCatalog;
    private final Playlist playlist;
    private final PlaylistDAO playlistDAO;

    public AddPlaylist(PlaylistCatalog playlistCatalog, Playlist playlist, PlaylistDAO playlistDAO) {
        this.playlistCatalog = playlistCatalog;
        this.playlist = playlist;
        this.playlistDAO = playlistDAO;
    }

    @Override
    public void execute() {
        playlistCatalog.addPlaylist(playlist);
        try {
            if (playlistDAO != null)
                playlistDAO.save(playlist);
        } catch (Exception e) {
            System.err.println("DB Error in AddPlaylist(execute): " + e.getMessage());
        }
    }

    @Override
    public void undo() {
        playlistCatalog.removePlaylist(playlist);
        try {
            if (playlistDAO != null)
                playlistDAO.delete(playlist.getId());
        } catch (Exception e) {
            System.err.println("DB Error in AddPlaylist(undo): " + e.getMessage());
        }
    }
}