package com.DataLayer.DAO.Playlist;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.DataLayer.DAO.DatabaseManager;
import com.Model.Playlist;
import com.Model.Track;
import com.Model.TrackFactory;
import com.Model.TrackTag;

public class PlaylistDAO implements IPlaylistDAO {
    private final TrackFactory factory;

    public PlaylistDAO(TrackFactory factory){
        this.factory = factory;
    }

    @Override
    public void save(Playlist playlist) throws Exception {
        if (playlist == null) {
            return;
        }

        String sql = "INSERT INTO playlists (id, name, play_count) VALUES (?, ?, ?);";

        //Salvo i dati della playlist
        Connection c = DatabaseManager.getConnection();
        try (PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, playlist.getId());
            st.setString(2, playlist.getName());
            st.setInt(3, playlist.getPlayCount());

            st.executeUpdate();
        }

        //Salvo l'associazione di tutti i brani contenuti in questa playlist
        String sqlRelation = "INSERT INTO playlist_tracks (playlist_id, track_id) VALUES (?, ?);";
        try(PreparedStatement st = c.prepareStatement(sqlRelation)){
            for(Track t : playlist.getTracks()){
                st.setString(1, playlist.getId());
                st.setString(2, t.getId());
                st.addBatch();
            }

            st.executeBatch();
        }
    }

    @Override
    public List<Playlist> getAll() throws Exception {
        List<Playlist> playlists = new ArrayList<>();
        String query = "SELECT * FROM playlists;";

        Connection c = DatabaseManager.getConnection();
        try (Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Playlist p = new Playlist(rs.getString("name"));

                Field idField = Playlist.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(p, rs.getString("id"));

                p.setPlayCount(rs.getInt("play_count"));

                loadTracks(c, p);
                playlists.add(p);
            }
        }
        return playlists;
    }

    @Override
    public List<Playlist> getFrequentlyPlayed(int limit) throws Exception {
        List<Playlist> topPlaylists = new ArrayList<>();
        String query = "SELECT * FROM playlists ORDER BY play_count DESC LIMIT ?;";

        Connection c = DatabaseManager.getConnection();
        try (PreparedStatement st = c.prepareStatement(query)) {
            st.setInt(1, limit);
            try (ResultSet rs = st.executeQuery()) {

                while (rs.next()) {
                    Playlist p = new Playlist(rs.getString("name"));

                    Field idField = Playlist.class.getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(p, rs.getString("id"));

                    p.setPlayCount(rs.getInt("play_count"));

                    loadTracks(c, p);
                    topPlaylists.add(p);
                }
            }
        }
        return topPlaylists;
    }

    private void loadTracks(Connection c, Playlist p) throws Exception {
        String queryTrack = "SELECT t.* FROM tracks t INNER JOIN playlist_tracks pt ON t.id = pt.track_id WHERE pt.playlist_id = ?;";

        try(PreparedStatement st = c.prepareStatement(queryTrack)){
            st.setString(1, p.getId());
            try(ResultSet rs = st.executeQuery()){

                while(rs.next()){
                    String genre = rs.getString("genre");
                    if (genre == null) genre = "";

                    String album = rs.getString("album");
                    if (album == null) album = "";

                    String tagStr = rs.getString("tag");
                    TrackTag currentTag = TrackTag.NONE;
                    if (tagStr != null && !tagStr.trim().isEmpty()) {
                        currentTag = TrackTag.valueOf(tagStr);
                    }

                    int year = rs.getInt("year");
                    if (rs.wasNull()) year = 0;

                    Track t = factory.createTrack(
                        rs.getString("title"),
                        rs.getString("author"),
                        year,
                        genre,
                        rs.getInt("duration"),
                        album,
                        rs.getString("file_path"),
                        currentTag
                    );

                    Field idField = Track.class.getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(t, rs.getString("id"));

                    t.setPlayCount(rs.getInt("play_count"));

                    p.getTracks().add(t);
                }
            }
        }
    }
}
