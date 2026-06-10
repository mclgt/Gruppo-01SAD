package com.State;

import com.Model.Track;
import com.Strategy.IPlaybackStrategy;
import com.Strategy.PlaybackContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Tests for PausedState: verifies the behavior of the pause state
 *        in PlayerContext.
 *
 *        PausedState is the state the player is in when playback is suspended.
 *        Tests verify that:
 *        - pause() is a no-op when already paused (state does not change)
 *        - play() exits the pause, returns the player to PlayingState, and
 *          updates the current track
 *        - stop() exits the pause returning to PlayingState
 *        - next() and previous() exit the pause and navigate according to the
 *          current strategy
 *
 * @author Christian
 * @see PausedState
 * @see PlayerContext
 * @see IPlayerState
 */
public class PausedStateTest {

    /**
     * @brief DummyStrategy that returns predefined values for nextTrack() and previousTrack().
     *        Manual stub without any mocking framework, consistent with the rest of the test suite.
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

    @BeforeEach
    public void setUp() {
        track1 = new Track("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "dummy1.mp3",null);
        track2 = new Track("Canzone B", "Artista B", 2001, "Rock", 210, "Album B", "dummy2.mp3",null);
        track3 = new Track("Canzone C", "Artista C", 2002, "Jazz", 220, "Album C", "dummy3.mp3",null);
        queue = Arrays.asList(track1, track2, track3);
    }

    /**
     * @brief Creates a PlayerContext with DummyStrategy, starts playback on currentTrack,
     *        then pauses it.
     *        Every test starts with the context already in PausedState, ready to be tested.
     */
    private PlayerContext pausedContextWith(Track currentTrack, Track nextTrack, Track prevTrack) {
        PlaybackContext pb = new PlaybackContext(new DummyStrategy(nextTrack, prevTrack));
        PlayerContext ctx = new PlayerContext(pb);
        // start playback to set a current track
        ctx.play(currentTrack);
        // pause: PlayingState.pause() calls setState(pausedState)
        ctx.pause();
        return ctx;
    }

    // -----------------------------------------------------------------------
    // Tests for pause() — no-op when already paused
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that pause() called when already paused is a no-op:
     *        the player must stay paused without changing state.
     */
    @Test
    public void testPause_whenAlreadyPaused_remainsPaused() {
        System.out.println("[TEST PausedState] pause() when already paused -> must remain paused");

        PlayerContext ctx = pausedContextWith(track1, null, null);

        // calling pause() a second time must be a no-op: state must not change
        ctx.pause();

        assertTrue(ctx.isPaused());
    }

    /**
     * @brief Verifies that pause() called when already paused does not accidentally start playback.
     */
    @Test
    public void testPause_whenAlreadyPaused_doesNotStartPlaying() {
        System.out.println("[TEST PausedState] pause() when already paused -> must not start playback");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.pause();

        // after a second pause() the player must not be playing
        assertFalse(ctx.isPlaying());
    }

    /**
     * @brief Verifies that pause() does not throw any exception.
     */
    @Test
    public void testPause_doesNotThrow() {
        System.out.println("[TEST PausedState] pause() -> must not throw any exception");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        assertDoesNotThrow(() -> ctx.pause());
    }

    // -----------------------------------------------------------------------
    // Tests for play() — exit pause and resume playback
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that play() exits the paused state: the player must no longer be paused.
     */
    @Test
    public void testPlay_exitsPausedState() {
        System.out.println("[TEST PausedState] play() -> must exit the paused state");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        // call play on any track: must exit PausedState
        ctx.play(track2);

        assertFalse(ctx.isPaused());
    }

    /**
     * @brief Verifies that play() returns the player to the active playing state (PlayingState).
     */
    @Test
    public void testPlay_setsPlayingState() {
        System.out.println("[TEST PausedState] play() -> must return the player to the playing state");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.play(track2);

        assertTrue(ctx.isPlaying());
    }

    /**
     * @brief Verifies that play() updates the current track in the context with the one passed as parameter.
     */
    @Test
    public void testPlay_updatesCurrentTrack() {
        System.out.println("[TEST PausedState] play() -> must update the current track in the context");

        // was paused on track1, calling play(track2): expecting track2 as current track
        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.play(track2);

        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that play() on the same track that was paused still exits the paused state.
     *        This is the typical "resume" case after pressing pause on the same song.
     */
    @Test
    public void testPlay_sameTrack_exitsPausedState() {
        System.out.println("[TEST PausedState] play() on the same paused track -> must exit the paused state");

        // simulates the case where the user presses play on the same song that was paused
        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.play(track1);

        // must exit pause and current track must remain track1
        assertFalse(ctx.isPaused());
        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that play() on a track without an audioSource does not throw.
     *        Tracks in tests have no audioSource set; play() must handle this without crashing.
     */
    @Test
    public void testPlay_trackWithoutAudioSource_doesNotThrow() {
        System.out.println("[TEST PausedState] play() on track without audioSource -> must not throw any exception");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        // track2 has no audioSource (null): play() must not crash
        assertDoesNotThrow(() -> ctx.play(track2));
    }

    // -----------------------------------------------------------------------
    // Tests for stop()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that stop() exits the paused state.
     */
    @Test
    public void testStop_exitsPausedState() {
        System.out.println("[TEST PausedState] stop() -> must exit the paused state");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.stop();

        // after stop the context must no longer be paused
        assertFalse(ctx.isPaused());
    }

    /**
     * @brief Verifies that stop() does not throw any exception.
     */
    @Test
    public void testStop_doesNotThrow() {
        System.out.println("[TEST PausedState] stop() -> must not throw any exception");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        assertDoesNotThrow(() -> ctx.stop());
    }

    // -----------------------------------------------------------------------
    // Tests for next() — navigation from paused state
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that next() exits the paused state.
     */
    @Test
    public void testNext_exitsPausedState() {
        System.out.println("[TEST PausedState] next() -> must exit the paused state");

        // the strategy will return track2 as the next track
        PlayerContext ctx = pausedContextWith(track1, track2, null);
        ctx.next(queue, track1);

        assertFalse(ctx.isPaused());
    }

    /**
     * @brief Verifies that next() returns the player to the active playing state.
     */
    @Test
    public void testNext_setsPlayingState() {
        System.out.println("[TEST PausedState] next() -> must return the player to the playing state");

        PlayerContext ctx = pausedContextWith(track1, track2, null);
        ctx.next(queue, track1);

        assertTrue(ctx.isPlaying());
    }

    /**
     * @brief Verifies that next() respects the current strategy and updates the current track.
     *        DummyStrategy is set to return track2: after next() the current track must be track2.
     */
    @Test
    public void testNext_updatesCurrentTrackUsingStrategy() {
        System.out.println("[TEST PausedState] next() -> must update the current track using the strategy");

        // DummyStrategy returns track2: expecting it to become the current track
        PlayerContext ctx = pausedContextWith(track1, track2, null);
        ctx.next(queue, track1);

        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that next() with no next track available (null) still exits the paused state.
     *        Even with no next track the player must not remain stuck in pause.
     */
    @Test
    public void testNext_withNoNextTrack_exitsPausedState() {
        System.out.println("[TEST PausedState] next() with no next track -> must exit pause anyway");

        // null as next: the strategy signals no next track available
        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.next(queue, track1);

        // must exit pause even in this case
        assertFalse(ctx.isPaused());
    }

    /**
     * @brief Verifies that next() does not throw any exception.
     */
    @Test
    public void testNext_doesNotThrow() {
        System.out.println("[TEST PausedState] next() -> must not throw any exception");

        PlayerContext ctx = pausedContextWith(track1, track2, null);
        assertDoesNotThrow(() -> ctx.next(queue, track1));
    }

    // -----------------------------------------------------------------------
    // Tests for previous() — backward navigation from paused state
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that previous() exits the paused state.
     */
    @Test
    public void testPrevious_exitsPausedState() {
        System.out.println("[TEST PausedState] previous() -> must exit the paused state");

        // the strategy will return track1 as the previous track
        PlayerContext ctx = pausedContextWith(track2, null, track1);
        ctx.previous(queue, track2);

        assertFalse(ctx.isPaused());
    }

    /**
     * @brief Verifies that previous() returns the player to the active playing state.
     */
    @Test
    public void testPrevious_setsPlayingState() {
        System.out.println("[TEST PausedState] previous() -> must return the player to the playing state");

        PlayerContext ctx = pausedContextWith(track2, null, track1);
        ctx.previous(queue, track2);

        assertTrue(ctx.isPlaying());
    }

    /**
     * @brief Verifies that previous() respects the current strategy and updates the current track.
     *        DummyStrategy is set to return track1: after previous() the current track must be track1.
     */
    @Test
    public void testPrevious_updatesCurrentTrackUsingStrategy() {
        System.out.println("[TEST PausedState] previous() -> must update the current track using the strategy");

        // was paused on track2, DummyStrategy returns track1 as previous
        PlayerContext ctx = pausedContextWith(track2, null, track1);
        ctx.previous(queue, track2);

        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that previous() with no previous track available (null) still exits the paused state.
     *        Even with no previous track the player must not remain stuck in pause.
     */
    @Test
    public void testPrevious_withNoPreviousTrack_exitsPausedState() {
        System.out.println("[TEST PausedState] previous() with no previous track -> must exit pause anyway");

        // null as previous: the strategy signals no previous track available
        PlayerContext ctx = pausedContextWith(track2, null, null);
        ctx.previous(queue, track2);

        // must exit pause even without a previous track
        assertFalse(ctx.isPaused());
    }

    /**
     * @brief Verifies that previous() does not throw any exception.
     */
    @Test
    public void testPrevious_doesNotThrow() {
        System.out.println("[TEST PausedState] previous() -> must not throw any exception");

        PlayerContext ctx = pausedContextWith(track2, null, track1);
        assertDoesNotThrow(() -> ctx.previous(queue, track2));
    }
}
