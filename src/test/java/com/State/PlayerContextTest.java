package com.State;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.Track;
import com.Strategy.IPlaybackStrategy;
import com.Strategy.PlaybackContext;
import com.Strategy.SequentialStrategy;

/**
 * @class PlayerContextTest
 * @brief Tests for PlayerContext via PlayingState (single track playback).
 *        Verifies the behavior of play(), next(), and previous() in the Playing state.
 *        Uses manual stubs for PlaybackContext, without any mocking framework.
 * @author Christian
 */
public class PlayerContextTest {

    /**
     * @brief Stub strategy: returns fixed next and previous values (can be null).
     *        Isolates the Context under test from real strategy logic.
     */
    private static class DummyStrategy implements IPlaybackStrategy {
        private final Track nextResult;
        private final Track prevResult;

        DummyStrategy(Track nextResult, Track prevResult) {
            this.nextResult = nextResult;
            this.prevResult = prevResult;
        }

        @Override
        public Track nextTrack(List<Track> queue, Track current) {
            return nextResult;
        }

        @Override
        public Track previousTrack(List<Track> queue, Track current) {
            return prevResult;
        }
    }

    private Track track1;
    private Track track2;
    private Track track3;
    private List<Track> queue;

    /**
     * @brief Initializes three dummy tracks and the queue before each test.
     */
    @BeforeEach
    public void setUp() {
        track1 = new Track("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "dummy1.mp3", null);
        track2 = new Track("Canzone B", "Artista B", 2001, "Pop", 210, "Album B", "dummy2.mp3", null);
        track3 = new Track("Canzone C", "Artista C", 2002, "Pop", 220, "Album C", "dummy3.mp3", null);
        queue = Arrays.asList(track1, track2, track3);
    }

    /**
     * @brief Creates a PlayerContext backed by a DummyStrategy with fixed next/previous results.
     */
    private PlayerContext contextWith(Track nextTrack, Track prevTrack) {
        PlaybackContext playbackContext = new PlaybackContext(new DummyStrategy(nextTrack, prevTrack));
        return new PlayerContext(playbackContext);
    }

    // -----------------------------------------------------------------------
    // Tests for play()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that play() puts the player in the Playing state.
     */
    @Test
    public void testPlay_startsPlaying() {
        System.out.println("[TEST] play() -> the player must be in Playing state");
        PlayerContext ctx = contextWith(null, null);
        ctx.play(track1);
        assertTrue(ctx.isPlaying());
    }

    /**
     * @brief Verifies that play() sets the current track to the one passed as parameter.
     */
    @Test
    public void testPlay_setsCurrentTrack() {
        System.out.println("[TEST] play() -> the current track must be the one passed");
        PlayerContext ctx = contextWith(null, null);
        ctx.play(track1);
        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that play() on a different track updates the current track
     *        and keeps the player in Playing state.
     */
    @Test
    public void testPlay_differentTrack_updatesCurrentTrack() {
        System.out.println("[TEST] play() on a different track -> must update current track and remain in Playing");
        PlayerContext ctx = contextWith(null, null);
        ctx.play(track1);
        ctx.play(track2);
        assertEquals(track2, ctx.getCurrentTrack());
        assertTrue(ctx.isPlaying());
    }

    // -----------------------------------------------------------------------
    // Tests for next()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that next() with a valid next track updates the current track.
     */
    @Test
    public void testNext_withValidNext_updatesCurrentTrack() {
        System.out.println("[TEST] next() with a valid next track -> must update the current track");
        PlayerContext ctx = contextWith(track2, null);
        ctx.play(track1);
        ctx.next(queue, track1);
        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that next() with a valid next track keeps the player in Playing state.
     */
    @Test
    public void testNext_withValidNext_remainsPlaying() {
        System.out.println("[TEST] next() with a valid next track -> the player must remain in Playing");
        PlayerContext ctx = contextWith(track2, null);
        ctx.play(track1);
        ctx.next(queue, track1);
        assertTrue(ctx.isPlaying());
    }

    /**
     * @brief Verifies that next() with no next track (null) does not change the current track.
     */
    @Test
    public void testNext_withNoNext_currentTrackUnchanged() {
        System.out.println("[TEST] next() with no next track (null) -> current track must not change");
        PlayerContext ctx = contextWith(null, null);
        ctx.play(track1);
        ctx.next(queue, track1);
        assertEquals(track1, ctx.getCurrentTrack());
    }

    // -----------------------------------------------------------------------
    // Tests for previous()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that previous() with a valid previous track updates the current track.
     */
    @Test
    public void testPrevious_withValidPrevious_updatesCurrentTrack() {
        System.out.println("[TEST] previous() with a valid previous track -> must update the current track");
        PlayerContext ctx = contextWith(null, track1);
        ctx.play(track2);
        ctx.previous(queue, track2);
        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that previous() with a valid previous track keeps the player in Playing state.
     */
    @Test
    public void testPrevious_withValidPrevious_remainsPlaying() {
        System.out.println("[TEST] previous() with a valid previous track -> the player must remain in Playing");
        PlayerContext ctx = contextWith(null, track1);
        ctx.play(track2);
        ctx.previous(queue, track2);
        assertTrue(ctx.isPlaying());
    }

    /**
     * @brief Verifies that previous() with no previous track (null) does not change the current track.
     */
    @Test
    public void testPrevious_withNoPrevious_currentTrackUnchanged() {
        System.out.println("[TEST] previous() with no previous track (null) -> current track must not change");
        PlayerContext ctx = contextWith(null, null);
        ctx.play(track2);
        ctx.previous(queue, track2);
        assertEquals(track2, ctx.getCurrentTrack());
    }

    // -----------------------------------------------------------------------
    // Tests for sequential playback (US-8)
    // -----------------------------------------------------------------------

    /**
     * @brief Creates a PlayerContext backed by a real SequentialStrategy.
     */
    private PlayerContext sequentialContext() {
        System.out.println("\n [TEST US-8] sequential Context");
        return new PlayerContext(new PlaybackContext(new SequentialStrategy()));
    }

    /**
     * @brief Verifies that sequential next() advances from track1 to track2.
     */
    @Test
    public void testSequential_next_advancesFromFirstToSecond() {
        System.out.println("[TEST US-8] sequential next() -> advances from track1 to track2");
        PlayerContext ctx = sequentialContext();
        ctx.play(track1);
        ctx.next(queue, track1);
        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that sequential next() advances from track2 to track3.
     */
    @Test
    public void testSequential_next_advancesFromSecondToThird() {
        System.out.println("[TEST US-8] sequential next() -> advances from track2 to track3");
        PlayerContext ctx = sequentialContext();
        ctx.play(track2);
        ctx.next(queue, track2);
        assertEquals(track3, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that sequential next() on the last track does not change the current track.
     */
    @Test
    public void testSequential_next_atLastTrack_currentTrackUnchanged() {
        System.out.println("[TEST US-8] next() on the last track -> current track must not change");
        PlayerContext ctx = sequentialContext();
        ctx.play(track3);
        ctx.next(queue, track3);
        assertEquals(track3, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that sequential previous() goes back from track3 to track2.
     */
    @Test
    public void testSequential_previous_goesBackFromThirdToSecond() {
        System.out.println("[TEST US-8] sequential previous() -> goes back from track3 to track2");
        PlayerContext ctx = sequentialContext();
        ctx.play(track3);
        ctx.previous(queue, track3);
        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that sequential previous() on the first track does not change the current track.
     */
    @Test
    public void testSequential_previous_atFirstTrack_currentTrackUnchanged() {
        System.out.println("[TEST US-8] previous() on the first track -> current track must not change");
        PlayerContext ctx = sequentialContext();
        ctx.play(track1);
        ctx.previous(queue, track1);
        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies a full sequential traversal of all tracks in order,
     *        including the no-op behavior when the end of the queue is reached.
     */
    @Test
    public void testSequential_fullSequence_traversesAllTracks() {
        System.out.println("[TEST US-8] full sequence -> traverses all tracks in order");
        PlayerContext ctx = sequentialContext();
        ctx.play(track1);
        assertEquals(track1, ctx.getCurrentTrack());

        ctx.next(queue, ctx.getCurrentTrack());
        assertEquals(track2, ctx.getCurrentTrack());

        ctx.next(queue, ctx.getCurrentTrack());
        assertEquals(track3, ctx.getCurrentTrack());

        ctx.next(queue, ctx.getCurrentTrack());
        assertEquals(track3, ctx.getCurrentTrack()); // end of queue: stays on last track
    }
}
