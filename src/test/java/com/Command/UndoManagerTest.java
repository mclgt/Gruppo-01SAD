package com.Command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.Library;
import com.Model.Track;
import com.Model.TrackFactory;

import com.Model.MockTrackFactory;

/**
 * @class UndoManagerTest
 * @brief Unit test per la classe Invoker UndoManager.
 *        Verifica la corretta gestione dello stack LIFO dei comandi e della
 *        BooleanProperty di Binding (Slide 24, 27).
 */
public class UndoManagerTest {
    private UndoManager undoManager;
    private Library library;
    private Track track;
    private TrackFactory factory;

    @BeforeEach
    public void setUp() {
        factory = new MockTrackFactory();
        undoManager = new UndoManager();
        library = new Library();
        track = factory.createTrack("Fix You", "Coldplay", 2005, "Alternative", 295, "X&Y", "dummy.mp3",
                null);
    }

    /**
     * @brief Verifica lo stato iniziale disabilitato del manager.
     */
    @Test
    public void testInitialStateIsDisabled() {
        assertTrue(undoManager.undoDisabledProperty().get(), "Lo storico iniziale dovrebbe essere vuoto.");
    }

    /**
     * @brief Verifica che l'invocazione di executeCommand abiliti la history list
     *        dell'Undo.
     */
    @Test
    public void testExecuteCommandEnablesUndoAndRunsAction() {
        ICommand addCommand = new AddTrack(library, track);

        undoManager.executeCommand(addCommand);

        assertEquals(1, library.getTracksCount());
        assertFalse(undoManager.undoDisabledProperty().get(),
                "L'Undo Manager dovrebbe segnalare la presenza di azioni.");
    }

    /**
     * @brief Verifica il corretto scorrimento a ritroso ed estrazione (Pop LIFO)
     *        dello storico.
     */
    @Test
    public void testUndoPopsAndReversesAction() {
        ICommand addCommand = new AddTrack(library, track);

        undoManager.executeCommand(addCommand);
        undoManager.undo();

        assertEquals(0, library.getTracksCount());
        assertTrue(undoManager.undoDisabledProperty().get(), "Lo storico dovrebbe essere tornato vuoto.");
    }

    /**
     * @brief Verifica la pulizia completa forzata dello storico.
     */
    @Test
    public void testClearHistory() {
        ICommand addCommand = new AddTrack(library, track);
        undoManager.executeCommand(addCommand);

        undoManager.clear();

        assertTrue(undoManager.undoDisabledProperty().get());
    }
}