package com.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlaylistCatalog {
    private final ObservableList<Playlist> playlists = FXCollections.observableArrayList();

    public ObservableList<Playlist> getPlaylists(){
        return playlists;
    }

    public void addPlaylist(Playlist playlist){
        if(!playlists.contains(playlist)){
            playlists.add(playlist);
        }
    }

    public void addPlaylist(int index, Playlist playlist){
        playlists.add(index, playlist);
    }

    public boolean removePlaylist(Playlist playlist){
        return this.playlists.remove(playlist);
    }

    public void updatePlaylist(Playlist playlist, String name){
        int index = this.playlists.indexOf(playlist);

        playlist.setName(name);

        this.playlists.set(index, playlist);
    }
}