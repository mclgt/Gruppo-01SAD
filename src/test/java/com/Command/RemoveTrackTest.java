package com.Command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.ITrackContainer;
import com.Model.Library;
import com.Model.Track;
import com.Model.TrackFactory;
import com.Model.MockTrackFactory;

/**
 * @brief Definizione della classe di unit test per la rimozione delle tracce.
 */
public class RemoveTrackTest {

    private ITrackContainer container;
    private UndoManager undoManager;
    private Track track1;
    private Track track2;
    private Track track3;
    private TrackFactory factory;

    @BeforeEach
    public void setUp() {
        factory = new MockTrackFactory();
        container = new Library();
        undoManager = new UndoManager();
        track1 = factory.createTrack("Track 1", "Artist 1", 2020, "Genre 1", 240, "Album 1", "dummy.mp3",
                null);
        track2 = factory.createTrack("Track 2", "Artist 2", 2021, "Genre 2", 300, "Album 2", "dummy.mp3",
                null);
        track3 = factory.createTrack("Track 3", "Artist 3", 2022, "Genre 3", 180, "Album 3", "dummy.mp3",
                null);
    }

    @Test
    public void testRemoveTrackCommand() {
        container.addTrack(track1);
        container.addTrack(track2);
        container.addTrack(track3);

        // Convertiamo il container in Library per usare metodi specifici
        // del test per accedere a metodi che non sono nell'interfaccia
        Library library = (Library) container;

        assertEquals(3, library.getTracksCount(), "La libreria dovrebbe contenere 3 tracce");
        assertEquals(track2, library.getTracks().get(1), "Il brano track2 dovrebbe essere in posizione 1");

        ICommand removeCommand = new RemoveTrack(container, track2);
        undoManager.executeCommand(removeCommand);

        assertEquals(2, library.getTracksCount(), "La libreria dovrebbe contenere 2 tracce dopo la rimozione");
        assertFalse(library.getTracks().contains(track2), "La traccia non è stata rimossa dalla libreria");

        undoManager.undo();
        assertEquals(3, library.getTracksCount(), "La libreria dovrebbe contenere di nuovo 3 tracce");
    }
}