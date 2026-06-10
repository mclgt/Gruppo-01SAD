package com.Strategy;

import com.Model.Track;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @class ShuffleStrategyTest
 * @brief Classe di test per ShuffleStrategy.
 *        Verifica che la navigazione shuffle restituisca tracce valide dalla coda
 *        e quasi mai restituisca la traccia corrente quando sono disponibili più tracce.
 *        I controlli statistici vengono eseguiti su 20 iterazioni per escludere
 *        che la stessa traccia venga restituita per caso.
 */
class ShuffleStrategyTest {

    private ShuffleStrategy strategy;
    private Track track1;
    private Track track2;
    private Track track3;

    /**
     * @brief Inizializza la strategia e tre tracce dummy prima di ogni test.
     */
    @BeforeEach
    void setUp() {
        strategy = new ShuffleStrategy();
        track1 = new Track("Track 1", "Artista", 2020, "Pop", 180, "Album", "/path/1.mp3", null);
        track2 = new Track("Track 2", "Artista", 2021, "Pop", 200, "Album", "/path/2.mp3", null);
        track3 = new Track("Track 3", "Artista", 2022, "Pop", 210, "Album", "/path/3.mp3", null);
    }

    // -----------------------------------------------------------------------
    // Test per nextTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che nextTrack() restituisca null quando la coda è vuota.
     */
    @Test
    void nextTrack_emptyQueue_returnsNull() {
        List<Track> queue = List.of();
        assertNull(strategy.nextTrack(queue, null));
    }

    /**
     * @brief Verifica che nextTrack() restituisca l'unica traccia disponibile quando la coda ha un solo elemento.
     */
    @Test
    void nextTrack_singleTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1);
        assertEquals(track1, strategy.nextTrack(queue, track1));
    }

    /**
     * @brief Verifica che nextTrack() quasi mai restituisca la traccia corrente quando sono disponibili più tracce,
     *        controllato su 20 iterazioni per escludere ripetizioni accidentali.
     */
    @Test
    void nextTrack_multipleTracks_almostNeverReturnsCurrentTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        for (int i = 0; i < 20; i++) {
            assertNotEquals(track1, strategy.nextTrack(queue, track1));
        }
    }

    /**
     * @brief Verifica che nextTrack() restituisca una traccia diversa da quella corrente
     *        quando sono disponibili più tracce.
     */
    @Test
    void nextTrack_multipleTracks_returnsValidTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertNotEquals(track1, strategy.nextTrack(queue, track1));
    }

    // -----------------------------------------------------------------------
    // Test per previousTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che previousTrack() si comporti in modo identico a nextTrack() per mantenere
     *        la semantica shuffle: quasi mai restituisce la traccia corrente su 20 iterazioni.
     */
    @Test
    void previousTrack_behaviorSameAsNextTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        for (int i = 0; i < 20; i++) {
            assertNotEquals(track1, strategy.previousTrack(queue, track1));
        }
    }
}
