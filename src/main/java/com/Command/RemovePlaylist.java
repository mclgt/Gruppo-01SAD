package com.Command;

import com.Model.Playlist;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class RemovePlaylist implements ICommand {
    @FXML
    private TableView<Playlist> playlistList;
    private Playlist playlist;
    int index;
    public RemovePlaylist(TableView<Playlist> playlistList, Playlist playlist){
        this.playlistList = playlistList;
        this.playlist = playlist;
        this.index = playlistList.getItems().indexOf(playlist);
    }
    @Override
    public void execute() {
        if(index != -1)
            playlistList.getItems().remove(playlist);
    }

    @Override
    public void undo() {
        playlistList.getItems().add(index, playlist);
    }
    
}
