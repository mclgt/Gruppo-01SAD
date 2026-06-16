package com.DataLayer.DAO.Track;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.DataLayer.DAO.DatabaseManager;
import com.Model.Track;
import com.Model.TrackFactory;
import com.Model.TrackTag;

public class TrackDAO implements ITrackDAO {

    private final TrackFactory factory;

    public TrackDAO(TrackFactory factory) {
        this.factory = factory;
    }

    @Override
    public void save(Track track) throws Exception {
        if (track == null) {
            return;
        }

        String sql = "INSERT INTO tracks (id, title, author, year, genre, duration, album, file_path, play_count, tag) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Connection c = DatabaseManager.getConnection();
        try (PreparedStatement st = c.prepareStatement(sql)) {
            setTrackParameters(st, track);
            st.executeUpdate();
        }
    }

    @Override
    public void update(Track track) throws Exception {
        if (track == null) {
            return;
        }

        String sql = "UPDATE tracks SET id = ?, title = ?, author = ?, year = ?, genre = ?, "
                + "duration = ?, album = ?, file_path = ?, play_count = ?, tag = ? "
                + "WHERE id = ?;";

        Connection c = DatabaseManager.getConnection();
        try (PreparedStatement st = c.prepareStatement(sql)) {
            setTrackParameters(st, track);

            st.setString(11, track.getId());
            st.executeUpdate();
        }
    }

    @Override
    public void delete(String trackId) throws Exception {
        String sql = "DELETE FROM tracks WHERE id = ?;";
        Connection c = DatabaseManager.getConnection();

        try (PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, trackId);
            st.executeUpdate();
        }
    }

    @Override
    public List<Track> getAll() throws Exception {
        List<Track> tracks = new ArrayList<>();
        String query = "SELECT * FROM tracks;";

        Connection c = DatabaseManager.getConnection();
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                tracks.add(mapRowToTrack(rs));
            }
        }
        return tracks;
    }

    @Override
    public List<Track> getFrequentlyPlayed(int limit) throws Exception {
        List<Track> topTracks = new ArrayList<>();
        String query = "SELECT * FROM tracks ORDER BY play_count DESC LIMIT ?;";

        Connection c = DatabaseManager.getConnection();
        try (PreparedStatement st = c.prepareStatement(query)) {
            st.setInt(1, limit);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    topTracks.add(mapRowToTrack(rs));
                }
            }
        }
        return topTracks;
    }

    private void setTrackParameters(PreparedStatement st, Track track) throws Exception {
        st.setString(1, track.getId());
        st.setString(2, track.getTitle());
        st.setString(3, track.getAuthor());
        st.setInt(6, track.getDuration());
        st.setString(8, track.getFilePath());
        st.setInt(9, track.getPlayCount());
        st.setString(10, track.getTag().name());

        if (track.getYear() > 0) {
            st.setInt(4, track.getYear());
        } else {
            st.setNull(4, Types.INTEGER);
        }

        if (track.getGenre() != null && !track.getGenre().trim().isEmpty()) {
            st.setString(5, track.getGenre());
        } else {
            st.setNull(5, Types.VARCHAR);
        }

        if (track.getAlbum() != null && !track.getAlbum().trim().isEmpty()) {
            st.setString(7, track.getAlbum());
        } else {
            st.setNull(7, Types.VARCHAR);
        }
    }

    public List<Track> getTracksByPlaylist(String playlistId) throws Exception {
        List<Track> tracks = new ArrayList<>();
        String query = "SELECT t.* FROM tracks t INNER JOIN playlist_tracks pt ON t.id = pt.track_id WHERE pt.playlist_id = ?;";

        Connection c = DatabaseManager.getConnection();
        try(PreparedStatement st = c.prepareStatement(query)){
            st.setString(1, playlistId);
            try(ResultSet rs = st.executeQuery()){

                while(rs.next()){
                    tracks.add(mapRowToTrack(rs));
                }
            }
        }
        return tracks;
    }

    private Track mapRowToTrack(ResultSet rs) throws Exception {
        String genre = rs.getString("genre");
        if (genre == null) {
            genre = "";
        }

        String album = rs.getString("album");
        if (album == null) {
            album = "";
        }

        String tagStr = rs.getString("tag");
        TrackTag currentTag = TrackTag.NONE;
        if (tagStr != null && !tagStr.trim().isEmpty()) {
            currentTag = TrackTag.valueOf(tagStr);
        }

        int year = rs.getInt("year");
        if (rs.wasNull()) {
            year = 0;
        }

        Track track = factory.createTrack(
                rs.getString("title"),
                rs.getString("author"),
                year,
                genre,
                rs.getInt("duration"),
                album,
                rs.getString("file_path"),
                currentTag);

        Field idField = Track.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(track, rs.getString("id"));
        track.setPlayCount(rs.getInt("play_count"));

        return track;
    }
}
