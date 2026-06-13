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

public class PlaylistDAO implements IPlaylistDAO {

    @Override
    public void save(Playlist playlist) throws Exception {
        if (playlist == null) {
            return;
        }

        String sql = "INSERT INTO playlists (id, name, play_count) VALUES (?, ?, ?);";

        Connection c = DatabaseManager.getConnection();
        try (PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, playlist.getId());
            st.setString(2, playlist.getName());
            st.setInt(3, playlist.getPlayCount());

            st.executeUpdate();
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
                    topPlaylists.add(p);
                }
            }
        }
        return topPlaylists;
    }
}
