package com.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * @brief Definizione della classe di unit test per la rimozione delle tracce.
 */
public class RemoveTrackTest {

    @Test
    public void testRemoveTrackLibrary() {
        Library libTest = new Library();
        Track trackTest = new Track("Titolo Test", "Autore Test", 2024, "Genere Test", 289, "Albun Test", "Path");
        libTest.addTrack(trackTest);
        assertEquals(1, libTest.getTracksCount());
        libTest.removeTrack(trackTest);
        assertEquals(0, libTest.getTracksCount());
    }

}
