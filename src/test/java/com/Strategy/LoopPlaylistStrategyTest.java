package com.Strategy;

import com.Model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class LoopPlaylistStrategyTest
 * @brief Classe di test per LoopPlaylistStrategy.
 *        Verifica che la navigazione con loop nella playlist effettui il wrap-around correttamente
 *        a entrambe le estremità della coda, e gestisca casi limite come
 *        la traccia corrente null o una coda con un solo elemento.
 */
public class LoopPlaylistStrategyTest {

    private LoopPlaylistStrategy strategy;
    private Track track1;
    private Track track2;
    private Track track3;

    /**
     * @brief Inizializza la strategia e tre tracce dummy prima di ogni test.
     */
    @BeforeEach
    void setUp() {
        strategy = new LoopPlaylistStrategy();
        track1 = new Track("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "dummy1.mp3", null);
        track2 = new Track("Canzone B", "Artista B", 2001, "Rock", 180, "Album B", "dummy2.mp3", null);
        track3 = new Track("Canzone C", "Artista C", 2002, "Jazz", 210, "Album C", "dummy3.mp3", null);
    }

    // -----------------------------------------------------------------------
    // Test per nextTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che nextTrack() restituisca la prima traccia quando current è null.
     */
    @Test
    void nextTrack_currentNull_returnsFirstTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track1, strategy.nextTrack(queue, null));
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
     * @brief Verifica che nextTrack() restituisca la seconda traccia quando current è la prima.
     */
    @Test
    void nextTrack_firstTrack_returnsSecondTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track2, strategy.nextTrack(queue, track1));
    }

    /**
     * @brief Verifica che nextTrack() effettui il wrap-around e restituisca la prima traccia quando current è l'ultima.
     */
    @Test
    void nextTrack_lastTrack_returnsFirstTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track1, strategy.nextTrack(queue, track3));
    }

    // -----------------------------------------------------------------------
    // Test per previousTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che previousTrack() restituisca la prima traccia quando current è null.
     */
    @Test
    void previousTrack_currentNull_returnsFirstTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track1, strategy.previousTrack(queue, null));
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
     * @brief Verifica che previousTrack() restituisca la prima traccia quando current è la seconda.
     */
    @Test
    void previousTrack_secondTrack_returnsFirstTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track1, strategy.previousTrack(queue, track2));
    }

    /**
     * @brief Verifica che previousTrack() effettui il wrap-around e restituisca l'ultima traccia quando current è la prima.
     */
    @Test
    void previousTrack_firstTrack_returnsLastTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track3, strategy.previousTrack(queue, track1));
    }

}
