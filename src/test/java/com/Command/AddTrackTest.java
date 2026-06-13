package com.Command;

/**
 * @brief Classe di test per verificare l'aggiunta di un brano 
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.Library;
import com.Model.Track;
import com.Model.TrackFactory;

import com.Model.MockTrackFactoryTest;

public class AddTrackTest {
    private Library l;
    private Track t;
    private TrackFactory factory;

    @BeforeEach
    public void setUp() {
        factory = new MockTrackFactoryTest();
        l = new Library();
        t = factory.createTrack("Bohemian Rhapsody", "Queen", 1975, "Rock", 355, "A Night at the Opera",
                "dummy.mp3", null);
    }

    @Test
    public void testAddTrack_executeAndUndo() {
        ICommand addCommand = new AddTrack(l, t);

        // Fase di execute
        addCommand.execute();
        assertEquals(1, l.getTracksCount());
        assertTrue(l.getTracks().contains(t));

        // Fase di undo
        addCommand.undo();
        assertEquals(0, l.getTracksCount());
        assertFalse(l.getTracks().contains(t));
    }
}