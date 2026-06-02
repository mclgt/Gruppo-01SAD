package com.Command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.Library;
import com.Model.Track;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Test per la modifica di un ogetto Track e l'aggiornamento in Library.
 *        Verifica che la modifica avvenga correttamente e non violi i vincoli
 *        logici associati all'oggetto.
 */

public class ModifyTrackTest {
    private Library library;
    private Track t;

    /**
     * @brief Inizializza un oggetto Library e un oggetto Track validi prima di ogni test.
     */

    @BeforeEach
    public void setUp() {
        library = new Library();
        t = new Track("Bohemian Rhapsody", "Queen", 1975, "Rock", 355, "A Night at the Opera", "C:/audio.wav");
        library.addTrack(t);
    }

    /**
     * @brief Verifica che il metodo updateTrack modifichi correttamente
     *        lo stato del brano.
     * 
     */
    @Test
    public void testLibraryUpdateTrack_executeAndUndo() {
        ICommand updateCommand = new ModifyTrack(library, t, "Bohemian Rhapsody Cover", "Queen", 1975, "Rock", 200, "A Night at the Opera", "C:/audio.wav");

        // Fase di execute
        updateCommand.execute();
        assertEquals("Bohemian Rhapsody Cover", t.getTitle(), "Titolo non modificato");
        assertEquals(200, t.getDuration(), "Durata non modificata");

        // Fase di undo
        updateCommand.undo();
        assertEquals("Bohemian Rhapsody", t.getTitle(), "Titolo non ripristinato");
        assertEquals(355, t.getDuration(), "Durata non ripristinata");
    }
}