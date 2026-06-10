package com.Strategy;

import com.Model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class LoopPlaylistStrategyTest
 * @brief Test class for LoopPlaylistStrategy.
 *        Verifies that looped playlist navigation wraps around correctly
 *        at both ends of the queue, and handles edge cases such as
 *        a null current track or a single-track queue.
 */
public class LoopPlaylistStrategyTest {

    private LoopPlaylistStrategy strategy;
    private Track track1;
    private Track track2;
    private Track track3;

    /**
     * @brief Initializes the strategy and three dummy tracks before each test.
     */
    @BeforeEach
    void setUp() {
        strategy = new LoopPlaylistStrategy();
        track1 = new Track("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "dummy1.mp3", null);
        track2 = new Track("Canzone B", "Artista B", 2001, "Rock", 180, "Album B", "dummy2.mp3", null);
        track3 = new Track("Canzone C", "Artista C", 2002, "Jazz", 210, "Album C", "dummy3.mp3", null);
    }

    // -----------------------------------------------------------------------
    // Tests for nextTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that nextTrack() returns the first track when current is null.
     */
    @Test
    void nextTrack_currentNull_returnsFirstTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track1, strategy.nextTrack(queue, null));
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
     * @brief Verifies that nextTrack() returns the second track when current is the first.
     */
    @Test
    void nextTrack_firstTrack_returnsSecondTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track2, strategy.nextTrack(queue, track1));
    }

    /**
     * @brief Verifies that nextTrack() wraps around and returns the first track when current is the last.
     */
    @Test
    void nextTrack_lastTrack_returnsFirstTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track1, strategy.nextTrack(queue, track3));
    }

    // -----------------------------------------------------------------------
    // Tests for previousTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that previousTrack() returns the first track when current is null.
     */
    @Test
    void previousTrack_currentNull_returnsFirstTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track1, strategy.previousTrack(queue, null));
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
     * @brief Verifies that previousTrack() returns the first track when current is the second.
     */
    @Test
    void previousTrack_secondTrack_returnsFirstTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track1, strategy.previousTrack(queue, track2));
    }

    /**
     * @brief Verifies that previousTrack() wraps around and returns the last track when current is the first.
     */
    @Test
    void previousTrack_firstTrack_returnsLastTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track3, strategy.previousTrack(queue, track1));
    }

}
