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
 * @brief Test class for ShuffleStrategy.
 *        Verifies that shuffle navigation returns valid tracks from the queue
 *        and almost never returns the current track when multiple tracks are available.
 *        Statistical checks are performed over 20 iterations to rule out always
 *        returning the same track by chance.
 */
class ShuffleStrategyTest {

    private ShuffleStrategy strategy;
    private Track track1;
    private Track track2;
    private Track track3;

    /**
     * @brief Initializes the strategy and three dummy tracks before each test.
     */
    @BeforeEach
    void setUp() {
        strategy = new ShuffleStrategy();
        track1 = new Track("Track 1", "Artista", 2020, "Pop", 180, "Album", "/path/1.mp3", null);
        track2 = new Track("Track 2", "Artista", 2021, "Pop", 200, "Album", "/path/2.mp3", null);
        track3 = new Track("Track 3", "Artista", 2022, "Pop", 210, "Album", "/path/3.mp3", null);
    }

    // -----------------------------------------------------------------------
    // Tests for nextTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that nextTrack() returns null when the queue is empty.
     */
    @Test
    void nextTrack_emptyQueue_returnsNull() {
        List<Track> queue = List.of();
        assertNull(strategy.nextTrack(queue, null));
    }

    /**
     * @brief Verifies that nextTrack() returns the only available track when the queue has one element.
     */
    @Test
    void nextTrack_singleTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1);
        assertEquals(track1, strategy.nextTrack(queue, track1));
    }

    /**
     * @brief Verifies that nextTrack() almost never returns the current track when multiple tracks are
     *        available, checked over 20 iterations to rule out accidental repetition.
     */
    @Test
    void nextTrack_multipleTracks_almostNeverReturnsCurrentTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        for (int i = 0; i < 20; i++) {
            assertNotEquals(track1, strategy.nextTrack(queue, track1));
        }
    }

    /**
     * @brief Verifies that nextTrack() returns a track different from the current one
     *        when multiple tracks are available.
     */
    @Test
    void nextTrack_multipleTracks_returnsValidTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertNotEquals(track1, strategy.nextTrack(queue, track1));
    }

    // -----------------------------------------------------------------------
    // Tests for previousTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that previousTrack() behaves identically to nextTrack() to preserve
     *        shuffle semantics: almost never returns the current track over 20 iterations.
     */
    @Test
    void previousTrack_behaviorSameAsNextTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        for (int i = 0; i < 20; i++) {
            assertNotEquals(track1, strategy.previousTrack(queue, track1));
        }
    }
}
