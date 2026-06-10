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
 * @brief Test class for SequentialStrategy.
 *        Verifies that sequential forward and backward navigation
 *        in the queue behaves correctly in edge cases:
 *        end of queue, beginning of queue, and track not present in queue.
 */
public class SequentialStrategyTest {

    private SequentialStrategy strategy;
    private Track track1;
    private Track track2;
    private Track track3;
    private List<Track> queue;

    /**
     * @brief Initializes the strategy, three dummy tracks, and the queue before each test.
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
    // Tests for nextTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that nextTrack() returns the second track when advancing from the first.
     */
    @Test
    public void testNextTrack_fromFirst_returnsSecond() {
        assertEquals(track2, strategy.nextTrack(queue, track1));
    }

    /**
     * @brief Verifies that nextTrack() returns the third track when advancing from the middle.
     */
    @Test
    public void testNextTrack_fromMiddle_returnsThird() {
        assertEquals(track3, strategy.nextTrack(queue, track2));
    }

    /**
     * @brief Verifies that nextTrack() returns null when advancing past the last track (no wrap-around).
     */
    @Test
    public void testNextTrack_fromLast_returnsNull() {
        assertNull(strategy.nextTrack(queue, track3));
    }

    /**
     * @brief Verifies that nextTrack() returns null when the current track is not present in the queue.
     */
    @Test
    public void testNextTrack_trackNotInQueue_returnsNull() {
        Track outsider = new Track("Ghost", "Nobody", 1999, "Jazz", 180, "None", "dummy4.mp3",null);
        assertNull(strategy.nextTrack(queue, outsider));
    }

    // -----------------------------------------------------------------------
    // Tests for previousTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that previousTrack() returns the middle track when going back from the last.
     */
    @Test
    public void testPreviousTrack_fromLast_returnsMiddle() {
        assertEquals(track2, strategy.previousTrack(queue, track3));
    }

    /**
     * @brief Verifies that previousTrack() returns the first track when going back from the middle.
     */
    @Test
    public void testPreviousTrack_fromMiddle_returnsFirst() {
        assertEquals(track1, strategy.previousTrack(queue, track2));
    }

    /**
     * @brief Verifies that previousTrack() returns null when going back past the first track (no wrap-around).
     */
    @Test
    public void testPreviousTrack_fromFirst_returnsNull() {
        assertNull(strategy.previousTrack(queue, track1));
    }

    /**
     * @brief Verifies that previousTrack() returns null when the current track is not present in the queue.
     */
    @Test
    public void testPreviousTrack_trackNotInQueue_returnsNull() {
        Track outsider = new Track("Ghost", "Nobody", 1999, "Jazz", 180, "None", "dummy4.mp3",null);
        assertNull(strategy.previousTrack(queue, outsider));
    }
}
