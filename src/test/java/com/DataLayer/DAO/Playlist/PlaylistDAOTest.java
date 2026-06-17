package com.DataLayer.DAO.Playlist;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.DataLayer.DAO.DatabaseManager;
import com.DataLayer.DAO.Track.TrackDAO;
import com.Model.LocalTrackFactory;
import com.Model.Playlist;
import com.Model.Track;
import com.Model.TrackTag;

public class PlaylistDAOTest {
    private PlaylistDAO playlistDAO;
    private TrackDAO trackDAO;
    private LocalTrackFactory factory;

    private File dummy;

    @BeforeEach
    public void setUp() throws Exception {
        DatabaseManager.setTestMode(true);
        Connection c = DatabaseManager.getConnection();

        String createTrackTable = "CREATE TABLE tracks (id TEXT PRIMARY KEY, title TEXT, author TEXT, year INTEGER, genre TEXT, duration INTEGER, album TEXT, file_path TEXT, play_count INTEGER, tag TEXT);";
        String createPlaylistTable = "CREATE TABLE playlists (id TEXT PRIMARY KEY, name TEXT, play_count INTEGER);";
        String createTrackPlaylistTable = "CREATE TABLE playlist_tracks (playlist_id TEXT, track_id TEXT, FOREIGN KEY(playlist_id) REFERENCES playlists(id) ON DELETE CASCADE, FOREIGN KEY(track_id) REFERENCES tracks(id) ON DELETE CASCADE);";

        try (PreparedStatement st1 = c.prepareStatement(createTrackTable);
                PreparedStatement st2 = c.prepareStatement(createPlaylistTable);
                PreparedStatement st3 = c.prepareStatement(createTrackPlaylistTable)) {

            st1.executeUpdate();
            st2.executeUpdate();
            st3.executeUpdate();
        }

        factory = new LocalTrackFactory();
        trackDAO = new TrackDAO(factory);
        playlistDAO = new PlaylistDAO(trackDAO);

        File libreriaDir = new File("data/library_audio");
        if (!libreriaDir.exists())
            libreriaDir.mkdirs();

        dummy = new File(libreriaDir, "Test.wav");
        if (!dummy.exists())
            dummy.createNewFile();
    }

    @AfterEach
    public void tearDown() throws Exception {
        Connection c = DatabaseManager.getConnection();

        String dropRelations = "DROP TABLE IF EXISTS playlist_tracks;";
        String dropPlaylists = "DROP TABLE IF EXISTS playlists;";
        String dropTracks = "DROP TABLE IF EXISTS tracks;";

        try (PreparedStatement st1 = c.prepareStatement(dropRelations);
                PreparedStatement st2 = c.prepareStatement(dropPlaylists);
                PreparedStatement st3 = c.prepareStatement(dropTracks)) {

            st1.executeUpdate();
            st2.executeUpdate();
            st3.executeUpdate();
        }

        DatabaseManager.closeConnection();

        if (dummy != null && dummy.exists()) {
            dummy.delete();
        }
    }

    @Test
    public void testSavePlaylistWithTracks() throws Exception {
        String path = dummy.getAbsolutePath();
        Track track1 = factory.createTrack("Song 1", "Auth 1", 2020, "Pop", 100, "", path, TrackTag.NONE);
        trackDAO.save(track1);

        Playlist playlist = new Playlist("My playlist");
        playlist.addTrack(track1);

        playlistDAO.save(playlist);
        List<Playlist> savedPlaylists = playlistDAO.getAll();

        assertEquals(1, savedPlaylists.size());
        assertEquals("My playlist", savedPlaylists.get(0).getName());
        assertEquals(1, savedPlaylists.get(0).getTracks().size(),
                "La playlist recuperata dovrebbe contenere 1 traccia.");
        assertEquals("Song 1", savedPlaylists.get(0).getTracks().get(0).getTitle());
    }
}