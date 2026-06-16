package com.Command;

import com.DataLayer.DAO.Playlist.PlaylistDAO;
import com.Model.Playlist;
import com.Model.PlaylistCatalog;

public class ModifyPlaylist implements ICommand {
    private final PlaylistCatalog receiver;
    private final Playlist playlistToModify;
    private final PlaylistDAO playlistDAO;

    private String oldName;
    private String newName;

    public ModifyPlaylist(PlaylistCatalog receiver, Playlist playlistToModify, String newName,
            PlaylistDAO playlistDAO) {
        this.playlistToModify = playlistToModify;
        this.receiver = receiver;
        this.playlistDAO = playlistDAO;
        this.oldName = playlistToModify.getName();
        this.newName = newName;
    }

    @Override
    public void execute() {
        this.receiver.updatePlaylist(playlistToModify, newName);
        try {
            if (playlistDAO != null)
                playlistDAO.update(playlistToModify);
        } catch (Exception e) {
            System.err.println("DB Error in ModifyPlaylist(execute): " + e.getMessage());
        }
    }

    @Override
    public void undo() {
        this.receiver.updatePlaylist(playlistToModify, oldName);
        try {
            if (playlistDAO != null)
                playlistDAO.update(playlistToModify);
        } catch (Exception e) {
            System.err.println("DB Error in ModifyPlaylist(undo): " + e.getMessage());
        }
    }
}
