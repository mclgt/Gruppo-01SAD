package com.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * @brief Test per l'entità Track.
 *        Verifica la corretta memorizzazione e recupero dei dati del brano.
 */
public class TrackTest {

    @Test
    public void testTrackConstructorAndGetters() {
        Track track = new Track("Bohemian Rhapsody", "Queen", 1975, "Rock", 355, "A Night at the Opera",
                "C:/audio.wav", TrackTag.FAVOURITE);

        assertEquals("Bohemian Rhapsody", track.getTitle());
        assertEquals("Queen", track.getAuthor());
        assertEquals(1975, track.getYear());
        assertEquals("Rock", track.getGenre());
        assertEquals(355, track.getDuration());
        assertEquals("A Night at the Opera", track.getAlbum());
        assertEquals("C:/audio.wav", track.getFilePath());
        assertEquals(TrackTag.FAVOURITE, track.getTag());
    }

    @Test
    public void testSetters() {
        Track track = new Track("A", "B", 0, "C", 1, "D", "E", null);

        track.setTitle("Creep");
        track.setAuthor("Radiohead");
        track.setYear(1992);
        track.setGenre("Alternative Rock");
        track.setDuration(238);
        track.setAlbum("Pablo Honey");
        track.setFilePath("C:/audio.wav");
        track.setTag(TrackTag.NEW_RELEASE);

        assertEquals("Creep", track.getTitle(), "Il titolo dovrebbe essere modificato");
        assertEquals("Radiohead", track.getAuthor(), "L'Autore dovrebbe essere modificato");
        assertEquals(1992, track.getYear(), "L'anno dovrebbe essere modificato");
        assertEquals("Alternative Rock", track.getGenre(), "Il genere dovrebbe essere modificato");
        assertEquals(238, track.getDuration(), "La durata dovrebbe essere modificato");
        assertEquals("Pablo Honey", track.getAlbum(), "L'album dovrebbe essere modificato");
        assertEquals("C:/audio.wav", track.getFilePath(), "Il path dovrebbe essere modificato");
        assertEquals(TrackTag.NEW_RELEASE, track.getTag(), "Il tag dovrebbe essere modificato");
    }

    /**
     * @brief Assicura che l'inserimento di un titolo vuoto lanci l'eccezione
     *        IllegalArgument e non alteri lo stato dell'oggetto.
     * 
     */
    @Test
    public void testModifyTrack_noTitle() {
        Track track = new Track("A", "B", 0, "C", 1, "D", "E", null);
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
        Track track = new Track("A", "B", 0, "C", 1, "D", "E", null);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            track.setAuthor("  ");
        });
        assertTrue(ex.getMessage().contains("non può essere vuoto"));
        assertEquals("B", track.getAuthor(), "L'autore non doveva essere modificato!");
    }

    /**
     * @brief Assicura che l'inserimento di un autore contente solo punteggiatura
     *        lanci l'eccezione
     *        IllegalArgument e non alteri lo stato dell'oggetto.
     * 
     */
    @Test
    public void testModifyTrack_invalidAuthor() {
        Track track = new Track("A", "B", 0, "C", 1, "D", "E", null);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            track.setAuthor(";");
        });
        assertTrue(ex.getMessage().contains("punteggiatura"));
        assertEquals("B", track.getAuthor(), "L'autore non doveva essere modificato!");
    }

    /**
     * @brief Assicura che l'inserimento di una durata negativa lanci l'eccezione
     *        IllegalArgument e non alteri lo stato dell'oggetto.
     * 
     */
    @Test
    public void testModifyTrack_negativeDuration() {
        Track track = new Track("A", "B", 0, "C", 1, "D", "E", null);
        assertThrows(IllegalArgumentException.class, () -> {
            track.setDuration(-4);
        });
        assertEquals(1, track.getDuration(), "La durata non deve essere stata modificata");
    }

    @Test
    public void testModiftyTrack_zeroDuration() {
        Track track = new Track("A", "B", 0, "C", 1, "D", "E", null);
        assertThrows(IllegalArgumentException.class, () -> {
            track.setDuration(0);
        });
        assertEquals(1, track.getDuration(), "La durata non deve essere stata modificata");
    }

    /**
     * @brief Assicura che l'inserimento di un percorso vuoto lanci l'eccezione
     *        IllegalArgument e non alteri lo stato dell'oggetto.
     * 
     */
    @Test
    public void testModifyTrack_noPath() {
        Track track = new Track("A", "B", 0, "C", 1, "D", "E", null);
        assertThrows(IllegalArgumentException.class, () -> {
            track.setFilePath("  ");
        });
        assertEquals("E", track.getFilePath(), "Il percorso non deve essere modificato");
    }

}