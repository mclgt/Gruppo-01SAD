package com.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Test per l'entità Track.
 *        Verifica la corretta memorizzazione e recupero dei dati del brano.
 */
public class TrackTest {

    @Test
    public void testTrackConstructorAndGetters() {
        Track track = new Track("Bohemian Rhapsody", "Queen", 1975, "Rock", 355, "A Night at the Opera",
                "C:/audio.wav");

        assertEquals("Bohemian Rhapsody", track.getTitle());
        assertEquals("Queen", track.getAuthor());
        assertEquals(1975, track.getYear());
        assertEquals("Rock", track.getGenre());
        assertEquals(355, track.getDuration());
        assertEquals("A Night at the Opera", track.getAlbum());
        assertEquals("C:/audio.wav", track.getFilePath());
    }

    @Test
    public void testSetters() {
        Track track = new Track("A", "B", 0, "C", 0, "D", "E");

        track.setTitle("Creep");
        track.setAuthor("Radiohead");
        track.setYear(1992);
        track.setGenre("Alternative Rock");
        track.setDuration(238);
        track.setAlbum("Pablo Honey");
        track.setFilePath("C:/audio.wav");

        assertEquals("Creep", track.getTitle());
        assertEquals("Radiohead", track.getAuthor());
        assertEquals(1992, track.getYear());
        assertEquals("Alternative Rock", track.getGenre());
        assertEquals(238, track.getDuration());
        assertEquals("Pablo Honey", track.getAlbum());
        assertEquals("C:/audio.wav", track.getFilePath());
    }
}