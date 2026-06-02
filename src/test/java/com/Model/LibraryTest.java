package com.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LibraryTest {
    private Library l;
    private Track t;
    private Track track1;
    private Track track2;
    private Track track3;

    @BeforeEach
    public void setUp() {
        l = new Library();
        t = new Track("Bohemian Rhapsody", "Queen", 1975, "Rock", 355, "A Night at the Opera",
                "C:/audio.wav");
        track1 = new Track("Track 1", "Artist 1", 2020, "Genre 1", 240, "Album 1", "path/to/track1.mp3");
        track2 = new Track("Track 2", "Artist 2", 2021, "Genre 2", 300, "Album 2", "path/to/track2.mp3");
        track3 = new Track("Track 3", "Artist 3", 2022, "Genre 3", 180, "Album 3", "path/to/track3.mp3");
    }

    @Test
    public void testAddTrack_success() {
        l.addTrack(t);
        assertEquals(1, l.getTracksCount(), "La libreria dovrebbe contenere 1 brano");
        assertTrue(l.getLibrary().contains(t), "La libreria dovrebbe contenre il brano");
    }

    @Test
    public void testRemoveTrack_success() {
        l.addTrack(track1); // dim=1
        l.addTrack(track2); // dim=2
        l.addTrack(track3); // dim=3
        l.removeTrack(track1);
        assertEquals(2, l.getTracksCount());
    }

    @Test
    public void testUpdateTrack_success() {
        l.addTrack(track1);
        l.updateTrack(track1, "esempio1", "esempio2", 2020, "esempio3", 0, "esempio4", "esempio5.mp3");
        assertEquals("esempio1", t.getTitle(), "Il titolo dovrebbe essere cambiato");
        assertEquals("esempio2", t.getAuthor(), "L'autore dovrebbe essere cambiato");
        assertEquals(2020, t.getYear(), "L'anno dovrebbe essere cambiato");
        assertEquals("esempio3", t.getGenre(), "Il genere dovrebbe essere cambiato");
        assertEquals(0, t.getDuration(), "La durata dovrebbe essere cambiata");
        assertEquals("esempio4", t.getAlbum(), "L'album dovrebbe essere cambiato");
        assertEquals("esempio5.mp3", t.getFilePath(), "Il path dovrebbe essere cambiato");

    }

}
