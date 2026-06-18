package com.Command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.ITrackContainer;
import com.Model.Library;
import com.Model.MockTrackFactory;
import com.Model.Track;
import com.Model.TrackFactory;

/**
 * @brief Classe di test unitari per la classe MoveDownTrack
 */
public class MoveDownTrackTest {
    private TrackFactory factory;
    private ITrackContainer receiver;
    private Track track1;
    private Track track2;
    private Track track3;
    /**
     * @brief Inizializzazione di una lista osservabile e alcune tracce simulate
     * prima di ogni singolo test.
     */
    @BeforeEach
    public void setUp() {
        factory=new MockTrackFactory();
        receiver = new Library();
        track1 = factory.createTrack("Track 1", "Artist 1", 2020, "Genre 1", 240, "Album 1", "path/to/track1.mp3",
                null);
        track2 = factory.createTrack("Track 2", "Artist 2", 2021, "Genre 2", 300, "Album 2", "path/to/track2.mp3",
                null);
        track3 = factory.createTrack("Track 3", "Artist 3", 2022, "Genre 3", 180, "Album 3", "path/to/track3.mp3",
                null);
        receiver.addTrack(track1);
        receiver.addTrack(track2);
        receiver.addTrack(track3);
    }

    /**
     * @brief Test dello spostamento verso il basso andato a buon fine.
     * * Verifica che invocando execute(), la traccia scenda di una posizione
     * scambiandosi con la successiva, e che invocando undo() la situazione originaria venga ripristinata.
     */
    @Test
    public void testExecuteAndUndoSuccess() {
        MoveDownTrack command = new MoveDownTrack(receiver, track1);
        command.execute();

        assertEquals(1, receiver.indexOf(track1), "La traccia 1 dovrebbe trovarsi all'indice 1 dopo l'execute.");
        assertEquals(0, receiver.indexOf(track2), "La traccia 2 dovrebbe essere salita all'indice 0.");
        assertEquals(2, receiver.indexOf(track3), "La traccia 3 non dovrebbe aver subito variazioni.");

        command.undo();

        assertEquals(0, receiver.indexOf(track1), "L'undo dovrebbe riportare la traccia 1 all'indice 0.");
        assertEquals(1, receiver.indexOf(track2), "L'undo dovrebbe riportare la traccia 2 all'indice 1.");
        assertEquals(2, receiver.indexOf(track3), "La traccia 3 deve rimanere invariata.");
    }

    /**
     * @brief Test dello spostamento di una traccia che si trova già all'ultima posizione.
     * * Verifica che se la traccia è l'ultima della lista, l'operazione di execute()
     * non modifichi l'ordine della lista e che l'undo() mantenga la lista intatta senza lanciare eccezioni.
     */
    @Test
    public void testExecuteWhenTrackIsAtTheBottom() {
        MoveDownTrack command = new MoveDownTrack(receiver, track3);

        command.execute();

        assertEquals(2, receiver.indexOf(track3), "La traccia 3 deve rimanere all'ultimo posto.");
        assertEquals(0, receiver.indexOf(track1), "L'ordine non deve cambiare.");
        assertEquals(1, receiver.indexOf(track2), "L'ordine non deve cambiare.");

        command.undo();
        assertEquals(2, receiver.indexOf(track3), "Anche dopo l'undo la posizione deve rimanere invariata.");
    }

    /**
     * @brief Test del comportamento nel caso in cui la traccia non sia presente nella lista.
     * * Verifica che se passiamo una traccia esterna (non contenuta nell'ObservableList), il comando
     * non effettui alcuna operazione fraudolenta o scambi non desiderati e che non sollevi eccezioni.
     */
    @Test
    public void testExecuteWithTrackNotFound() {
        Track track4 = factory.createTrack("Track 4", "Artist 4", 2023, "Genre 4", 320, "Album 4", "path/to/track4.mp3",
                null);
        MoveDownTrack command = new MoveDownTrack(receiver, track4);

        assertDoesNotThrow(() -> command.execute(), "L'esecuzione con traccia non presente non deve sollevare eccezioni.");

    
        assertEquals(0, receiver.indexOf(track1));
        assertEquals(1, receiver.indexOf(track2));
        assertEquals(2, receiver.indexOf(track3));

        assertDoesNotThrow(() -> command.undo(), "L'undo con traccia non trovata non deve sollevare eccezioni.");
    }
}
