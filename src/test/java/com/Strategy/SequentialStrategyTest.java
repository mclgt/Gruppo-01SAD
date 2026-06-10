package com.Strategy;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.Track;

/**
 * @class SequentialStrategyTest
 * @brief Classe di test per SequentialStrategy.
 *        Verifica che la navigazione sequenziale in avanti e indietro
 *        nella coda si comporti correttamente nei casi limite:
 *        fine della coda, inizio della coda e traccia non presente nella coda.
 */
public class SequentialStrategyTest {

    private SequentialStrategy strategy;
    private Track track1;
    private Track track2;
    private Track track3;
    private List<Track> queue;

    /**
     * @brief Inizializza la strategia, tre tracce dummy e la coda prima di ogni test.
     */
    @BeforeEach
    public void setUp() {
        strategy = new SequentialStrategy();
        track1 = new Track("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "dummy1.mp3",null);
        track2 = new Track("Canzone B", "Artista B", 2001, "Pop", 210, "Album B", "dummy2.mp3",null);
        track3 = new Track("Canzone C", "Artista C", 2002, "Pop", 220, "Album C", "dummy3.mp3",null);
        queue = Arrays.asList(track1, track2, track3);
    }

    // -----------------------------------------------------------------------
    // Test per nextTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che nextTrack() restituisca la seconda traccia avanzando dalla prima.
     */
    @Test
    public void testNextTrack_fromFirst_returnsSecond() {
        assertEquals(track2, strategy.nextTrack(queue, track1));
    }

    /**
     * @brief Verifica che nextTrack() restituisca la terza traccia avanzando dalla posizione centrale.
     */
    @Test
    public void testNextTrack_fromMiddle_returnsThird() {
        assertEquals(track3, strategy.nextTrack(queue, track2));
    }

    /**
     * @brief Verifica che nextTrack() restituisca null avanzando oltre l'ultima traccia (nessun wrap-around).
     */
    @Test
    public void testNextTrack_fromLast_returnsNull() {
        assertNull(strategy.nextTrack(queue, track3));
    }

    /**
     * @brief Verifica che nextTrack() restituisca null quando la traccia corrente non è presente nella coda.
     */
    @Test
    public void testNextTrack_trackNotInQueue_returnsNull() {
        Track outsider = new Track("Ghost", "Nobody", 1999, "Jazz", 180, "None", "dummy4.mp3",null);
        assertNull(strategy.nextTrack(queue, outsider));
    }

    // -----------------------------------------------------------------------
    // Test per previousTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che previousTrack() restituisca la traccia centrale tornando dall'ultima.
     */
    @Test
    public void testPreviousTrack_fromLast_returnsMiddle() {
        assertEquals(track2, strategy.previousTrack(queue, track3));
    }

    /**
     * @brief Verifica che previousTrack() restituisca la prima traccia tornando dalla posizione centrale.
     */
    @Test
    public void testPreviousTrack_fromMiddle_returnsFirst() {
        assertEquals(track1, strategy.previousTrack(queue, track2));
    }

    /**
     * @brief Verifica che previousTrack() restituisca null tornando oltre la prima traccia (nessun wrap-around).
     */
    @Test
    public void testPreviousTrack_fromFirst_returnsNull() {
        assertNull(strategy.previousTrack(queue, track1));
    }

    /**
     * @brief Verifica che previousTrack() restituisca null quando la traccia corrente non è presente nella coda.
     */
    @Test
    public void testPreviousTrack_trackNotInQueue_returnsNull() {
        Track outsider = new Track("Ghost", "Nobody", 1999, "Jazz", 180, "None", "dummy4.mp3",null);
        assertNull(strategy.previousTrack(queue, outsider));
    }
}
