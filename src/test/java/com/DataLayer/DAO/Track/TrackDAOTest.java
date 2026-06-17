package com.DataLayer.DAO.Track;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.DataLayer.DAO.DatabaseManager;
import com.Model.LocalTrackFactory;
import com.Model.Track;
import com.Model.TrackFactory;
import com.Model.TrackTag;

public class TrackDAOTest {
    private TrackDAO trackDAO;
    private TrackFactory factory;

    private File dummy1;
    private File dummy2;

    @BeforeEach
    public void setUp() throws Exception {
        DatabaseManager.setTestMode(true);
        Connection c = DatabaseManager.getConnection();

        String createTableSql = "CREATE TABLE tracks (id TEXT PRIMARY KEY, title TEXT, author TEXT, year INTEGER, genre TEXT, duration INTEGER, album TEXT, file_path TEXT, play_count TEXT, tag TEXT);";

        try (PreparedStatement st = c.prepareStatement(createTableSql)) {
            st.executeUpdate();
        }

        factory = new LocalTrackFactory();
        trackDAO = new TrackDAO(factory);

        File libraryAudio = new File("data/library_audio");
        if (!libraryAudio.exists())
            libraryAudio.mkdir();

        dummy1 = new File(libraryAudio, "Bohemian.wav");
        if (!dummy1.exists())
            dummy1.createNewFile();

        dummy2 = new File(libraryAudio, "Test.wav");
        if (!dummy2.exists())
            dummy2.createNewFile();
    }

    @AfterEach
    public void tearDown() throws Exception {
        Connection c = DatabaseManager.getConnection();

        String dropTableSql = "DROP TABLE IF EXISTS tracks;";
        try (PreparedStatement st = c.prepareStatement(dropTableSql)) {
            st.executeUpdate();
        }

        DatabaseManager.closeConnection();

        if (dummy1 != null && dummy1.exists())
            dummy1.delete();

        if (dummy2 != null && dummy2.exists())
            dummy2.delete();
    }

    @Test
    public void testSaveAndGetAll() throws Exception {
        Track track = factory.createTrack("Bohemian Rapsody", "Queen", 1975, "Rock", 354, "A Night at the Opera",
                dummy1.getAbsolutePath(), TrackTag.FAVOURITE);

        trackDAO.save(track);
        List<Track> savedTracks = trackDAO.getAll();

        assertEquals(1, savedTracks.size(), "Dovrebbe esserci esattamente una traccia");
        assertEquals("Bohemian Rapsody", savedTracks.get(0).getTitle());
        assertEquals("Queen", savedTracks.get(0).getAuthor());
    }

    @Test
    public void testUpdatePlayCount() throws Exception {
        Track track = factory.createTrack("Test", "Author", 2020, "Pop", 200, "", dummy2.getAbsolutePath(),
                TrackTag.NONE);
        trackDAO.save(track);

        track.setPlayCount(3);
        trackDAO.update(track);

        Track updatedTrack = trackDAO.getAll().get(0);
        assertEquals(3, updatedTrack.getPlayCount(), "Il play count dovrebbe essere aggiornato a 3");
    }
}
