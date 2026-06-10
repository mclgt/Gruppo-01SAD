package com.Strategy;

import com.Model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class LoopTrackStrategyTest
 * @brief Test class for LoopTrackStrategy.
 *        Verifies that single-track loop always returns the same current track,
 *        regardless of queue size, and returns null when current is null.
 */
public class LoopTrackStrategyTest {

    private LoopTrackStrategy strategy;
    private Track track1;
    private Track track2;
    private Track track3;

    /**
     * @brief Initializes the strategy and three dummy tracks before each test.
     */
    @BeforeEach
    void setUp() {
        strategy = new LoopTrackStrategy();
        track1 = new Track("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "dummy1.mp3", null);
        track2 = new Track("Canzone B", "Artista B", 2001, "Rock", 180, "Album B", "dummy2.mp3", null);
        track3 = new Track("Canzone C", "Artista C", 2002, "Jazz", 210, "Album C", "dummy3.mp3", null);
    }

    // -----------------------------------------------------------------------
    // Tests for nextTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that nextTrack() returns null when current is null — no track to loop on.
     */
    @Test
    void nextTrack_currentNull_returnsNull() {
        List<Track> queue = List.of(track1, track2, track3);
        assertNull(strategy.nextTrack(queue, null));
    }

    /**
     * @brief Verifies that nextTrack() returns the same track when the queue contains only one element.
     */
    @Test
    void nextTrack_singleTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1);
        assertSame(track1, strategy.nextTrack(queue, track1));
    }

    /**
     * @brief Verifies that nextTrack() always returns the current track regardless of queue size.
     */
    @Test
    void nextTrack_multipleTracks_returnsSameTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track2, strategy.nextTrack(queue, track2));
    }

    /**
     * @brief Verifies that nextTrack() returns the same last track when looping on it.
     */
    @Test
    void nextTrack_lastTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track3, strategy.nextTrack(queue, track3));
    }

    // -----------------------------------------------------------------------
    // Tests for previousTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that previousTrack() returns null when current is null.
     */
    @Test
    void previousTrack_currentNull_returnsNull() {
        List<Track> queue = List.of(track1, track2, track3);
        assertNull(strategy.previousTrack(queue, null));
    }

    /**
     * @brief Verifies that previousTrack() returns the same track when the queue contains only one element.
     */
    @Test
    void previousTrack_singleTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1);
        assertSame(track1, strategy.previousTrack(queue, track1));
    }

    /**
     * @brief Verifies that previousTrack() always returns the current track regardless of queue size.
     */
    @Test
    void previousTrack_multipleTracks_returnsSameTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track2, strategy.previousTrack(queue, track2));
    }

    /**
     * @brief Verifies that previousTrack() returns the same first track when looping on it.
     */
    @Test
    void previousTrack_firstTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track1, strategy.previousTrack(queue, track1));
    }
}
