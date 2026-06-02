package com.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Command.ICommand;
import com.Command.RemoveTrack;
import com.Command.UndoManager;
/**
 * @brief Definizione della classe di unit test per la rimozione delle tracce.
 */
public class RemoveTrackTest {

    private Library library;   
    private UndoManager undoManager; 
    private Track track1;
    private Track track2;
    private Track track3;

    @BeforeEach
    public void setUp() {
        library = new Library();
        undoManager = new UndoManager();
        track1 = new Track("Track 1", "Artist 1", 2020, "Genre 1", 240, "Album 1", "path/to/track1.mp3");
        track2 = new Track("Track 2", "Artist 2", 2021, "Genre 2", 300, "Album 2", "path/to/track2.mp3");
        track3 = new Track("Track 3", "Artist 3", 2022, "Genre 3", 180, "Album 3", "path/to/track3.mp3");
    }

    @Test
    public void testRemoveTrackCommand() {
        library.addTrack(track1);
        assertEquals(1, library.getTracksCount());

        ICommand removeCommand = new RemoveTrack(library, track1);
        removeCommand.execute();
        assertEquals(0, library.getTracksCount());

        removeCommand.undo();
        assertEquals(1, library.getTracksCount());
    }

}
