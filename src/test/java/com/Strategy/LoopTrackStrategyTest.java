package com.Strategy;

import com.Model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class LoopTrackStrategyTest
 * @brief Classe di test per LoopTrackStrategy.
 *        Verifica che il loop su singola traccia restituisca sempre la stessa traccia corrente,
 *        indipendentemente dalla dimensione della coda, e restituisca null quando current è null.
 */
public class LoopTrackStrategyTest {

    private LoopTrackStrategy strategy;
    private Track track1;
    private Track track2;
    private Track track3;

    /**
     * @brief Inizializza la strategia e tre tracce dummy prima di ogni test.
     */
    @BeforeEach
    void setUp() {
        strategy = new LoopTrackStrategy();
        track1 = new Track("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "dummy1.mp3", null);
        track2 = new Track("Canzone B", "Artista B", 2001, "Rock", 180, "Album B", "dummy2.mp3", null);
        track3 = new Track("Canzone C", "Artista C", 2002, "Jazz", 210, "Album C", "dummy3.mp3", null);
    }

    // -----------------------------------------------------------------------
    // Test per nextTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che nextTrack() restituisca null quando current è null — nessuna traccia su cui fare loop.
     */
    @Test
    void nextTrack_currentNull_returnsNull() {
        List<Track> queue = List.of(track1, track2, track3);
        assertNull(strategy.nextTrack(queue, null));
    }

    /**
     * @brief Verifica che nextTrack() restituisca la stessa traccia quando la coda contiene un solo elemento.
     */
    @Test
    void nextTrack_singleTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1);
        assertSame(track1, strategy.nextTrack(queue, track1));
    }

    /**
     * @brief Verifica che nextTrack() restituisca sempre la traccia corrente indipendentemente dalla dimensione della coda.
     */
    @Test
    void nextTrack_multipleTracks_returnsSameTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track2, strategy.nextTrack(queue, track2));
    }

    /**
     * @brief Verifica che nextTrack() restituisca la stessa ultima traccia quando si effettua il loop su di essa.
     */
    @Test
    void nextTrack_lastTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track3, strategy.nextTrack(queue, track3));
    }

    // -----------------------------------------------------------------------
    // Test per previousTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che previousTrack() restituisca null quando current è null.
     */
    @Test
    void previousTrack_currentNull_returnsNull() {
        List<Track> queue = List.of(track1, track2, track3);
        assertNull(strategy.previousTrack(queue, null));
    }

    /**
     * @brief Verifica che previousTrack() restituisca la stessa traccia quando la coda contiene un solo elemento.
     */
    @Test
    void previousTrack_singleTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1);
        assertSame(track1, strategy.previousTrack(queue, track1));
    }

    /**
     * @brief Verifica che previousTrack() restituisca sempre la traccia corrente indipendentemente dalla dimensione della coda.
     */
    @Test
    void previousTrack_multipleTracks_returnsSameTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track2, strategy.previousTrack(queue, track2));
    }

    /**
     * @brief Verifica che previousTrack() restituisca la stessa prima traccia quando si effettua il loop su di essa.
     */
    @Test
    void previousTrack_firstTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track1, strategy.previousTrack(queue, track1));
    }
}
