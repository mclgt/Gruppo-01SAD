package com.Strategy;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.Track;
import com.Model.TrackFactory;
import com.Model.MockTrackFactoryTest;

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
    private TrackFactory factory;

    /**
     * @brief Initializes the strategy, three dummy tracks, and the queue before
     *        each test.
     */
    @BeforeEach
    public void setUp() {
        factory = new MockTrackFactoryTest();
        strategy = new SequentialStrategy();
        track1 = factory.createTrack("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "dummy1.mp3",
                null);
        track2 = factory.createTrack("Canzone B", "Artista B", 2001, "Pop", 210, "Album B", "dummy2.mp3",
                null);
        track3 = factory.createTrack("Canzone C", "Artista C", 2002, "Pop", 220, "Album C", "dummy3.mp3",
                null);
        queue = Arrays.asList(track1, track2, track3);
    }

    // -----------------------------------------------------------------------
    // Test per nextTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that nextTrack() returns the second track when advancing from
     *        the first.
     */
    @Test
    public void testNextTrack_fromFirst_returnsSecond() {
        assertEquals(track2, strategy.nextTrack(queue, track1));
    }

    /**
     * @brief Verifies that nextTrack() returns the third track when advancing from
     *        the middle.
     */
    @Test
    public void testNextTrack_fromMiddle_returnsThird() {
        assertEquals(track3, strategy.nextTrack(queue, track2));
    }

    /**
     * @brief Verifies that nextTrack() returns null when advancing past the last
     *        track (no wrap-around).
     */
    @Test
    public void testNextTrack_fromLast_returnsNull() {
        assertNull(strategy.nextTrack(queue, track3));
    }

    /**
     * @brief Verifies that nextTrack() returns null when the current track is not
     *        present in the queue.
     */
    @Test
    public void testNextTrack_trackNotInQueue_returnsNull() {
        Track outsider = factory.createTrack("Ghost", "Nobody", 1999, "Jazz", 180, "None", "dummy.mp3",
                null);
        assertNull(strategy.nextTrack(queue, outsider));
    }

    // -----------------------------------------------------------------------
    // Test per previousTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that previousTrack() returns the middle track when going back
     *        from the last.
     */
    @Test
    public void testPreviousTrack_fromLast_returnsMiddle() {
        assertEquals(track2, strategy.previousTrack(queue, track3));
    }

    /**
     * @brief Verifies that previousTrack() returns the first track when going back
     *        from the middle.
     */
    @Test
    public void testPreviousTrack_fromMiddle_returnsFirst() {
        assertEquals(track1, strategy.previousTrack(queue, track2));
    }

    /**
     * @brief Verifies that previousTrack() returns null when going back past the
     *        first track (no wrap-around).
     */
    @Test
    public void testPreviousTrack_fromFirst_returnsNull() {
        assertNull(strategy.previousTrack(queue, track1));
    }

    /**
     * @brief Verifies that previousTrack() returns null when the current track is
     *        not present in the queue.
     */
    @Test
    public void testPreviousTrack_trackNotInQueue_returnsNull() {
        Track outsider = factory.createTrack("Ghost", "Nobody", 1999, "Jazz", 180, "None", "dummy.mp3",
                null);
        assertNull(strategy.previousTrack(queue, outsider));
    }
}
