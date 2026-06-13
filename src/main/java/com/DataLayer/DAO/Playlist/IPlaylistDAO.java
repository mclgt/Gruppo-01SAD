package com.DataLayer.DAO.Playlist;

import java.util.List;

import com.Model.Playlist;

public interface IPlaylistDAO {
    void save(Playlist playlist) throws Exception;
    List<Playlist> getAll() throws Exception;
    List<Playlist> getFrequentlyPlayed(int limit) throws Exception;
}