package com.DataLayer.DAO.Playlist;

import java.util.List;

import com.Model.Playlist;

public interface IPlaylistDAO {
    void save(Playlist playlist) throws Exception;
    void update(Playlist playlist) throws Exception;
    void delete(String playlistId) throws Exception;
    List<Playlist> getAll() throws Exception;
    List<Playlist> getFrequentlyPlayed(int limit) throws Exception;
}