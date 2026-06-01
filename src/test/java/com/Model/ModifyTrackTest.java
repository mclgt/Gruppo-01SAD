package com.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Test per la modifica di un ogetto Track
 *        Verifica che la modifica avvenga correttamente e non violi i vincoli
 *        logici associati all'oggetto.
 */

public class ModifyTrackTest {
    private Track t;

    /**
     * @bried Inizializza un oggetto Traccia valido prima di ogni test.
     */

    @BeforeEach
    public void setUp() {
        t = new Track("Bohemian Rhapsody", "Queen", 1975, "Rock", 355, "A Night at the Opera", "C:/audio.wav");
    }

    /**
     * @brief Verifica che la modifica dei dati di un brano avvenga correttamente
     *        quando vengono forniti input validi.
     * 
     */

    @Test
    public void testModifyTrack_success() {
        t.setTitle("Bohemian Rhapsody Cover");
        t.setDuration(200);
        assertEquals("Bohemian Rhapsody Cover", t.getTitle(), "Titolo non modificato");
        assertEquals(200, t.getDuration(), "Durata non modificata");

    }

    /**
     * @brief Assicura che l'inserimento di un titolo vuoto lanci l'eccezione
     *        IllegalArgument e non alteri lo stato dell'oggetto.
     * 
     */
    @Test
    public void testModifyTrack_noTitle() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            t.setTitle("  ");
        });
        assertTrue(ex.getMessage().contains("non può essere vuoto"));
        assertEquals("Bohemian Rhapsody", t.getTitle(), "Il titolo non doveva essere modificato!");
    }

    /**
     * @brief Assicura che l'inserimento di un autore vuoto lanci l'eccezione
     *        IllegalArgument e non alteri lo stato dell'oggetto.
     * 
     */
    @Test
    public void testModifyTrack_noAuthor() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            t.setAuthor("  ");
        });
        assertTrue(ex.getMessage().contains("non può essere vuoto"));
        assertEquals("Queen", t.getAuthor(), "L'autore non doveva essere modificato!");
    }

    /**
     * @brief Assicura che l'inserimento di una durata negativa lanci l'eccezione
     *        IllegalArgument e non alteri lo stato dell'oggetto.
     * 
     */
    @Test
    public void testModifyTrack_negativeDuration() {
        assertThrows(IllegalArgumentException.class, () -> {
            t.setDuration(-4);
        });
        assertEquals(355, t.getDuration(), "La durata non deve essere stata modificata");
    }

    /**
     * @brief Assicura che l'inserimento di un percorso vuoto lanci l'eccezione
     *        IllegalArgument e non alteri lo stato dell'oggetto.
     * 
     */
    @Test
    public void testModifyTrack_noPath() {
        assertThrows(IllegalArgumentException.class, () -> {
            t.setFilePath("  ");
        });
        assertEquals("C:/audio.wav", t.getFilePath(), "Il percorso non deve essere modificato");
    }

}
