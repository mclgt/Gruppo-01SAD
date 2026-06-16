package com.Command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.MockTrackFactory;
import com.Model.Track;
import com.Model.TrackFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @brief Classe di test unitari per la classe MoveUpTrack
 */
public class MoveUpTrackTest {
    private TrackFactory factory;
    private ObservableList<Track> trackList;
    private Track track1;
    private Track track2;
    private Track track3;

    /**
     * @brief Inizializzazione di una lista osservabile e alcune tracce simulate
     * tramite il factory prima di ogni singolo test.
     */
    @BeforeEach
    public void setUp() {
        factory = new MockTrackFactory();
        
        track1 = factory.createTrack("Track 1", "Artist 1", 2020, "Genre 1", 240, "Album 1", "path/to/track1.mp3", null);
        track2 = factory.createTrack("Track 2", "Artist 2", 2021, "Genre 2", 300, "Album 2", "path/to/track2.mp3", null);
        track3 = factory.createTrack("Track 3", "Artist 3", 2022, "Genre 3", 180, "Album 3", "path/to/track3.mp3", null);
        
        trackList = FXCollections.observableArrayList(track1, track2, track3);
    }

    /**
     * @brief Test dello spostamento verso l'alto andato a buon fine.
     * * Verifica che invocando execute(), la traccia salga di una posizione
     * scambiandosi con la precedente, e che invocando undo() la situazione originaria venga ripristinata.
     */
    @Test
    public void testExecuteAndUndoSuccess() {
        MoveUpTrack command = new MoveUpTrack(trackList, track2);
        command.execute();

        assertEquals(0, trackList.indexOf(track2), "La traccia 2 dovrebbe trovarsi all'indice 0 dopo l'execute.");
        assertEquals(1, trackList.indexOf(track1), "La traccia 1 dovrebbe essere scesa all'indice 1.");
        assertEquals(2, trackList.indexOf(track3), "La traccia 3 non dovrebbe aver subito variazioni.");

        command.undo();

        assertEquals(0, trackList.indexOf(track1), "L'undo dovrebbe riportare la traccia 1 all'indice 0.");
        assertEquals(1, trackList.indexOf(track2), "L'undo dovrebbe riportare la traccia 2 all'indice 1.");
        assertEquals(2, trackList.indexOf(track3), "La traccia 3 deve rimanere invariata al suo posto.");
    }

    /**
     * @brief Test dello spostamento di una traccia che si trova già all'inizio della lista.
     * * Verifica che se la traccia è la prima della lista, l'operazione di execute()
     * venga bloccata dalle condizioni di guardia, mantenendo intatto l'ordine e garantendo che
     * l'undo() successivo non corrompa la struttura dati.
     */
    @Test
    public void testExecuteWhenTrackIsAtTheTop() {
        MoveUpTrack command = new MoveUpTrack(trackList, track1);

        command.execute();

        assertEquals(0, trackList.indexOf(track1), "La traccia 1 deve rimanere al primo posto.");
        assertEquals(1, trackList.indexOf(track2), "L'ordine complessivo non deve cambiare.");
        assertEquals(2, trackList.indexOf(track3), "L'ordine complessivo non deve cambiare.");

        command.undo();
        
        assertEquals(0, trackList.indexOf(track1), "L'ordine deve rimanere invariato anche dopo l'undo.");
    }

    /**
     * @brief Test del comportamento nel caso in cui la traccia non sia presente nella lista.
     * * Verifica che se passiamo al comando una traccia esterna (generata ma non inserita nella lista),
     * il sistema non sollevi eccezioni e lasci la playlist inalterata.
     */
    @Test
    public void testExecuteWithTrackNotFound() {
        Track track4 = factory.createTrack("Track 4", "Artist 4", 2023, "Genre 4", 320, "Album 4", "path/to/track4.mp3", null);
        MoveUpTrack command = new MoveUpTrack(trackList, track4);

        assertDoesNotThrow(() -> command.execute(), "L'esecuzione con traccia non presente non deve sollevare eccezioni.");

        assertEquals(0, trackList.indexOf(track1));
        assertEquals(1, trackList.indexOf(track2));
        assertEquals(2, trackList.indexOf(track3));

        assertDoesNotThrow(() -> command.undo(), "L'undo con traccia non trovata non deve sollevare eccezioni.");
    }
    
}
