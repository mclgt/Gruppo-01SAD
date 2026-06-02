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

    /**
     * @brief Assicura che l'inserimento di un titolo vuoto lanci l'eccezione
     *        IllegalArgument e non alteri lo stato dell'oggetto.
     * 
     */
    @Test
    public void testModifyTrack_noTitle() {
        Track track = new Track("A", "B", 0, "C", 0, "D", "E");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            track.setTitle("  ");
        });
        assertTrue(ex.getMessage().contains("non può essere vuoto"));
        assertEquals("A", track.getTitle(), "Il titolo non doveva essere modificato!");
    }

    /**
     * @brief Assicura che l'inserimento di un autore vuoto lanci l'eccezione
     *        IllegalArgument e non alteri lo stato dell'oggetto.
     * 
     */
    @Test
    public void testModifyTrack_noAuthor() {
        Track track = new Track("A", "B", 0, "C", 0, "D", "E");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            track.setAuthor("  ");
        });
        assertTrue(ex.getMessage().contains("non può essere vuoto"));
        assertEquals("B", track.getAuthor(), "L'autore non doveva essere modificato!");
    }

    /**
     * @brief Assicura che l'inserimento di una durata negativa lanci l'eccezione
     *        IllegalArgument e non alteri lo stato dell'oggetto.
     * 
     */
    @Test
    public void testModifyTrack_negativeDuration() {
        Track track = new Track("A", "B", 0, "C", 0, "D", "E");
        assertThrows(IllegalArgumentException.class, () -> {
            track.setDuration(-4);
        });
        assertEquals(0, track.getDuration(), "La durata non deve essere stata modificata");
    }

    /**
     * @brief Assicura che l'inserimento di un percorso vuoto lanci l'eccezione
     *        IllegalArgument e non alteri lo stato dell'oggetto.
     * 
     */
    @Test
    public void testModifyTrack_noPath() {
        Track track = new Track("A", "B", 0, "C", 0, "D", "E");
        assertThrows(IllegalArgumentException.class, () -> {
            track.setFilePath("  ");
        });
        assertEquals("E", track.getFilePath(), "Il percorso non deve essere modificato");
    }

}