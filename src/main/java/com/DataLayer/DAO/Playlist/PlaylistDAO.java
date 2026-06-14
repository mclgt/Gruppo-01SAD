package com.DataLayer.DAO.Playlist;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.DataLayer.DAO.DatabaseManager;
import com.DataLayer.DAO.Track.TrackDAO;
import com.Model.Playlist;
import com.Model.Track;

public class PlaylistDAO implements IPlaylistDAO {

    private final TrackDAO trackDAO;

    public PlaylistDAO(TrackDAO trackDAO) {
        this.trackDAO = trackDAO;
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
            setPlaylistParameters(st, playlist);
            st.executeUpdate();
        }

        //Salvo l'associazione di tutti i brani contenuti in questa playlist
        String sqlRelation = "INSERT INTO playlist_tracks (playlist_id, track_id) VALUES (?, ?);";
        try (PreparedStatement st = c.prepareStatement(sqlRelation)) {
            for (Track t : playlist.getTracks()) {
                st.setString(1, playlist.getId());
                st.setString(2, t.getId());
                st.addBatch();
            }

            st.executeBatch();
        }
    }

    @Override
    public void update(Playlist playlist) throws Exception {
        if (playlist == null) {
            return;
        }

        String sql = "UPDATE playlists SET id = ?, name = ?, play_count = ? WHERE id = ?;";
        Connection c = DatabaseManager.getConnection();
        try (PreparedStatement st = c.prepareStatement(sql)) {
            setPlaylistParameters(st, playlist);

            st.setString(4, playlist.getId());
            st.executeUpdate();
        }
    }

    @Override
    public void delete(String playlistId) throws Exception {
        String sql = "DELETE FROM playlists WHERE id = ?;";
        Connection c = DatabaseManager.getConnection();
        try (PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, playlistId);
            st.executeUpdate();
        }
    }

    @Override
    public List<Playlist> getAll() throws Exception {
        List<Playlist> playlists = new ArrayList<>();
        String query = "SELECT * FROM playlists;";

        Connection c = DatabaseManager.getConnection();
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                playlists.add(mapRowToPlaylist(rs));
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
                    topPlaylists.add(mapRowToPlaylist(rs));
                }
            }
        }
        return topPlaylists;
    }

    private void loadTracks(Playlist p) throws Exception {
        List<Track> playlistTracks = trackDAO.getTracksByPlaylist(p.getId());
        p.getTracks().addAll(playlistTracks);
    }

    private void setPlaylistParameters(PreparedStatement st, Playlist playlist) throws Exception {
        st.setString(1, playlist.getId());
        st.setString(2, playlist.getName());
        st.setInt(3, playlist.getPlayCount());
    }

    private Playlist mapRowToPlaylist(ResultSet rs) throws Exception {
        Playlist p = new Playlist(rs.getString("name"));

        Field idField = Playlist.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(p, rs.getString("id"));

        p.setPlayCount(rs.getInt("play_count"));

        loadTracks(p);

        return p;
    }
}
