package com.DataLayer.DAO.Track;

import java.util.List;

import com.Model.Track;

public interface ITrackDAO {
    void save(Track track) throws Exception;
    List<Track> getAll() throws Exception;
    List<Track> getFrequentlyPlayed(int limit) throws Exception;
}