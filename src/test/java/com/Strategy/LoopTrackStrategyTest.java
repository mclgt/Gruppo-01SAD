package com.Strategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.MockTrackFactory;
import com.Model.Track;
import com.Model.TrackFactory;

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
    private TrackFactory factory;

    /**
     * @brief Inizializza la strategia e tre tracce dummy prima di ogni test.
     */
    @BeforeEach
    void setUp() {
        factory = new MockTrackFactory();
        strategy = new LoopTrackStrategy();
        track1 = factory.createTrack("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "dummy1.mp3",
                null);
        track2 = factory.createTrack("Canzone B", "Artista B", 2001, "Rock", 180, "Album B", "dummy2.mp3",
                null);
        track3 = factory.createTrack("Canzone C", "Artista C", 2002, "Jazz", 210, "Album C", "dummy3.mp3",
                null);
    }

    // -----------------------------------------------------------------------
    // Test per nextTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that nextTrack() returns null when current is null — no track
     *        to loop on.
     */
    @Test
    void nextTrack_currentNull_returnsNull() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, null);
        assertNull(strategy.nextTrack(null));
    }

    /**
     * @brief Verifies that nextTrack() returns the same track when the queue
     *        contains only one element.
     */
    @Test
    void nextTrack_singleTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1);
        strategy.setQueue(queue, track1);
        assertSame(track1, strategy.nextTrack(track1));
    }

    /**
     * @brief Verifies that nextTrack() always returns the current track regardless
     *        of queue size.
     */
    @Test
    void nextTrack_multipleTracks_returnsSameTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, track2);
        assertSame(track2, strategy.nextTrack(track2));
    }

    /**
     * @brief Verifies that nextTrack() returns the same last track when looping on
     *        it.
     */
    @Test
    void nextTrack_lastTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, track3);
        assertSame(track3, strategy.nextTrack(track3));
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
        strategy.setQueue(queue, null);

        assertNull(strategy.previousTrack( null));
    }

    /**
     * @brief Verifies that previousTrack() returns the same track when the queue
     *        contains only one element.
     */
    @Test
    void previousTrack_singleTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1);
        strategy.setQueue(queue, track1);

        assertSame(track1, strategy.previousTrack(track1));
    }

    /**
     * @brief Verifies that previousTrack() always returns the current track
     *        regardless of queue size.
     */
    @Test
    void previousTrack_multipleTracks_returnsSameTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, track2);
        assertSame(track2, strategy.previousTrack(track2));
    }

    /**
     * @brief Verifies that previousTrack() returns the same first track when
     *        looping on it.
     */
    @Test
    void previousTrack_firstTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, track1);

        assertSame(track1, strategy.previousTrack(track1));
    }
}
