package com.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlaylistTest {
    private TrackFactory factory;

    @BeforeEach
    public void setUp() {
        factory = new MockTrackFactoryTest();
    }

    @Test
    public void testPlaylistInitialization() {
        Playlist playlist = new Playlist("Rock Anni 90");

        assertEquals("Rock Anni 90", playlist.getName());
        assertTrue(playlist.getTracks().isEmpty(), "La playlist appena creata deve essere vuota");
        assertEquals(0, playlist.getTotalDuration(), "La durata iniziale deve essere 0");
    }

    @Test
    public void testAddTrackAndDuration() {
        Playlist playlist = new Playlist("Preferite");
        Track t1 = factory.createTrack("Brano 1", "Autore", 2020, "Pop", 100, "Album 1", "path1.wav", null);
        Track t2 = factory.createTrack("Brano 2", "Autore", 2021, "Rock", 50, "Album 2", "path2.wav", null);

        playlist.addTrack(t1);
        playlist.addTrack(0, t2);
        List<Track> tracks = playlist.getTracks();

        assertEquals(2, playlist.getTracksCount(), "La playlist deve contenere 2 brani");
        assertEquals(t2, tracks.get(0), "Il brano 2 dovrebbe trovarsi all'indice 0");
        assertEquals(150, playlist.getTotalDuration(), "La durata totale deve essere la somma esatta (150s)");
        assertEquals("02:30", playlist.getFormattedTotalDuration(), "La formattazione del tempo deve essere 02:30");
    }

    @Test
    public void testRemoveTrack() {
        Playlist playlist = new Playlist("Test Remove");
        Track t1 = factory.createTrack("Brano 1", "Autore", 2020, "Pop", 100, "Album", "path1.wav", null);

        playlist.addTrack(t1);
        assertEquals(1, playlist.getTracksCount());

        boolean rimosso = playlist.removeTrack(t1);

        assertTrue(rimosso, "Il metodo deve restituire true");
        assertTrue(playlist.getTracks().isEmpty(), "La playlist deve essere di nuovo vuota");
        assertEquals(0, playlist.getTotalDuration());
    }

    @Test
    public void testPlaylistSetter_success() {
        Playlist playlist = new Playlist("Rock Anni 90");
        playlist.setName("Pop anni 2000");
        assertEquals("Pop anni 2000", playlist.getName());
    }

    @Test
    public void testPlaylistSetter_noName() {
        Playlist playlist = new Playlist("Rock Anni 90");
        try {
            playlist.setName(" ");
            fail("Ci si aspettava un'eccezione per nome vuoto");
        } catch (IllegalArgumentException ex) {
            assertEquals("Il nome della playlist non può essere vuoto!", ex.getMessage());
        }
    }
}