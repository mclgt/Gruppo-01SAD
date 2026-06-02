package com.Command;

/**
 * @brief Classe di test per verificare l'aggiunta di un brano 
 */

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.Library;
import com.Model.Track;

public class AddTrackTest {
    private Library l;
    private Track t;

    @BeforeEach
    public void setUp() {
        l = new Library();
        t = new Track("Bohemian Rhapsody", "Queen", 1975, "Rock", 355, "A Night at the Opera",
                "C:/audio.wav");
    }

    @Test
    public void testAddTrack_executeAndUndo() {
        ICommand addCommand = new AddTrack(l, t);

        // Fase di execute
        addCommand.execute();
        assertEquals(1, l.getTracksCount());
        assertTrue(l.getLibrary().contains(t));

        // Fase di undo
        addCommand.undo();
        assertEquals(0, l.getTracksCount());
        assertFalse(l.getLibrary().contains(t));
    }
}