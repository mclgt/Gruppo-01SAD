package com.Model;

/**
 * @brief Classe di test per verificare l'aggiunta di un brano 
 */

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    public void testAddTrack_success() {
        l.addTrack(t);
        assertEquals(1, l.getTracksCount(), "La libreria dovrebbe contenere 1 brano");
        assertTrue(l.getLibrary().contains(t), "La libreria dovrebbe contenre il brano");
    }

}
