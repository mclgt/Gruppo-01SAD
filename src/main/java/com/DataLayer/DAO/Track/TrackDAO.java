package com.DataLayer.DAO.Track;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Connection c = DatabaseManager.getConnection();
        try (PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, track.getId());
            st.setString(2, track.getTitle());
            st.setString(3, track.getAuthor());
            st.setInt(4, track.getDuration());
            st.setString(5, track.getAlbum());
            st.setString(6, track.getFilePath());
            st.setInt(9, track.getPlayCount());
            st.setString(10, track.getTag().name());

            st.executeUpdate();
        }
    }

    @Override
    public List<Track> getAll() throws Exception {
        List<Track> tracks = new ArrayList<>();
        String query = "SELECT * FROM tracks;";

        Connection c = DatabaseManager.getConnection();
        try (Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                String tagStr = rs.getString("tag");
                TrackTag currentTag = TrackTag.NONE;
                if (tagStr != null && !tagStr.trim().isEmpty()) {
                    currentTag = TrackTag.valueOf(tagStr);
                }

                Track track = factory.createTrack(
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("yeat"),
                        rs.getString("genre"),
                        rs.getInt("duration"),
                        rs.getString("album"),
                        rs.getString("file_path"),
                        currentTag);

                Field idField = Track.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(track, rs.getInt("play_count"));

                tracks.add(track);
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
                    String tagStr = rs.getString("tag");
                    TrackTag currentTag = TrackTag.NONE;
                    if (tagStr != null && !tagStr.trim().isEmpty()) {
                        currentTag = TrackTag.valueOf(tagStr);
                    }

                    Track track = factory.createTrack(
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("year"),
                            rs.getString("genre"),
                            rs.getInt("duration"),
                            rs.getString("album"),
                            rs.getString("file_path"),
                            currentTag);

                    Field idField = Track.class.getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(track, rs.getString("id"));
                    track.setPlayCount(rs.getInt("play_count"));

                    topTracks.add(track);
                }
            }
        }
        return topTracks;
    }
}