package com.State;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.Track;
import com.Strategy.IPlaybackStrategy;
import com.Strategy.PlaybackContext;

/**
 * @brief Tests for PlayingState: verifies the behavior of the active playback state
 *        (i.e., a song is currently playing) in PlayerContext.
 *
 *        PlayingState is the state the player is in during playback.
 *        Tests verify that play() correctly updates the current track in PlayerContext,
 *        that next()/previous() navigate between tracks and start the new one,
 *        or call stop() when no track is available.
 *
 * @author Christian
 * @see PlayingState
 * @see PlayerContext
 * @see IPlayerState
 */
public class PlayingStateTest {

    /**
     * @brief DummyStrategy that returns predefined values for nextTrack() and previousTrack(),
     *        used to simplify tests without a mocking framework.
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

    /** Creates a PlayerContext with a DummyStrategy returning the given values. */
    private PlayerContext contextWith(Track nextTrack, Track prevTrack) {
        PlaybackContext pb = new PlaybackContext(new DummyStrategy(nextTrack, prevTrack));
        return new PlayerContext(pb);
    }

    // -----------------------------------------------------------------------
    // Tests for play()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that play() sets the current track in the context.
     */
    @Test
    public void testPlay_setsCurrentTrack() {
        System.out.println("[TEST PlayingState] play() -> must set the current track in the context");

        // null, null: only testing that play() updates the currently playing track
        PlayerContext ctx = contextWith(null, null);

        // create PlayingState directly, passing the context
        PlayingState state = new PlayingState(ctx);

        state.play(track1);

        // verify the context has registered track1 as the current track
        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that a second call to play() updates the current track.
     */
    @Test
    public void testPlay_subsequentCall_updatesCurrentTrack() {
        System.out.println("[TEST PlayingState] play() called twice -> current track must be the last one played");

        // null, null: no navigation needed, only testing track overwrite
        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);

        // first call: sets track1 as current
        state.play(track1);
        // second call: must overwrite with track2
        state.play(track2);

        // current track must be the last one played, i.e., track2
        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that play() on different tracks always updates the current track.
     */
    @Test
    public void testPlay_thirdTrack_updatesCurrentTrack() {
        System.out.println("[TEST PlayingState] play() on a different track -> must update the current track");

        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);

        // play track1 first, then jump to track3 (skipping track2)
        state.play(track1);
        state.play(track3);

        // current must be track3; jumping non-sequentially must work correctly
        assertEquals(track3, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that play() on a track with no audioSource does not throw.
     */
    @Test
    public void testPlay_trackWithoutAudioSource_doesNotThrow() {
        System.out.println("[TEST PlayingState] play() on track without audioSource -> must not throw any exception");

        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);

        // track1 has no audioSource (null): play() must not crash
        assertDoesNotThrow(() -> state.play(track1));
    }

    // -----------------------------------------------------------------------
    // Tests for next()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that next() with a valid next track sets it as the current track.
     */
    @Test
    public void testNext_withValidNext_setsNextTrackAsCurrent() {
        System.out.println("[TEST PlayingState] next() with valid track -> must set the next track as current");

        // strategy returns track2 as the next track; null for previous (not needed)
        PlayerContext ctx = contextWith(track2, null);
        PlayingState state = new PlayingState(ctx);
        // manually set track1 as the starting current track
        ctx.setCurrentTrack(track1);

        state.next(queue, track1);

        // strategy returned track2, so the context must have set it as current
        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that calling next() twice uses the strategy result each time.
     *        The stub always returns the same value, so the current track stabilizes on it.
     */
    @Test
    public void testNext_calledTwice_usesStrategyResultEachTime() {
        System.out.println("[TEST PlayingState] next() called twice -> each call uses the strategy result");

        // DummyStrategy always returns track2 as next, regardless of current
        PlayerContext ctx = contextWith(track2, null);
        PlayingState state = new PlayingState(ctx);
        ctx.setCurrentTrack(track1);

        // first next: track1 -> track2
        state.next(queue, track1);
        assertEquals(track2, ctx.getCurrentTrack());

        // second next: stub returns track2 again, so stays on track2
        state.next(queue, track2);
        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that next() with no next track (null) does not change the current track.
     */
    @Test
    public void testNext_withNoNext_currentTrackUnchanged() {
        System.out.println("[TEST PlayingState] next() with no next track -> current track must not change");

        // null, null: strategy signals no next track available
        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);
        ctx.setCurrentTrack(track1);

        // next() called but strategy returns null -> PlayingState calls stop()
        state.next(queue, track1);

        // track1 must remain current because there was no next track
        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that next() with a valid next track does not throw any exception.
     */
    @Test
    public void testNext_withValidNext_doesNotThrow() {
        System.out.println("[TEST PlayingState] next() with valid track -> must not throw any exception");

        // strategy returns track2 as next: must not throw
        PlayerContext ctx = contextWith(track2, null);
        PlayingState state = new PlayingState(ctx);

        assertDoesNotThrow(() -> state.next(queue, track1));
    }

    // -----------------------------------------------------------------------
    // Tests for previous()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that previous() with a valid previous track sets it as the current track.
     */
    @Test
    public void testPrevious_withValidPrevious_setsPreviousTrackAsCurrent() {
        System.out.println("[TEST PlayingState] previous() with valid track -> must set the previous track as current");

        // null for next (not needed), track1 as previous returned by strategy
        PlayerContext ctx = contextWith(null, track1);
        PlayingState state = new PlayingState(ctx);
        // start from track2 as current
        ctx.setCurrentTrack(track2);

        state.previous(queue, track2);

        // strategy returned track1, so it must become the current track
        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that previous() with no previous track (null) does not change the current track.
     */
    @Test
    public void testPrevious_withNoPrevious_currentTrackUnchanged() {
        System.out.println("[TEST PlayingState] previous() with no previous track -> current track must not change");

        // null, null: strategy signals no previous track available
        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);
        ctx.setCurrentTrack(track2);

        // previous() called but strategy returns null -> PlayingState calls stop()
        state.previous(queue, track2);

        // track2 must remain current because there was no previous track
        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that previous() with a valid previous track does not throw any exception.
     */
    @Test
    public void testPrevious_withValidPrevious_doesNotThrow() {
        System.out.println("[TEST PlayingState] previous() with valid track -> must not throw any exception");

        // null for next, track1 as previous: must not throw
        PlayerContext ctx = contextWith(null, track1);
        PlayingState state = new PlayingState(ctx);

        assertDoesNotThrow(() -> state.previous(queue, track2));
    }

    /**
     * @brief Verifies that previous() with no previous track does not throw any exception.
     */
    @Test
    public void testPrevious_withNoPrevious_doesNotThrow() {
        System.out.println("[TEST PlayingState] previous() with no previous track -> must not throw any exception");

        // null, null: no previous track, but must not throw
        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);

        assertDoesNotThrow(() -> state.previous(queue, track1));
    }

    // -----------------------------------------------------------------------
    // Tests for pause() and stop()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that pause() does not throw any exception (currently a no-op in PlayingState).
     */
    @Test
    public void testPause_doesNotThrow() {
        System.out.println("[TEST PlayingState] pause() -> must not throw any exception");

        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);

        // pause() is currently a no-op in PlayingState: verify it does not throw
        assertDoesNotThrow(() -> state.pause());
    }

    /**
     * @brief Verifies that stop() does not throw any exception (currently a no-op in PlayingState).
     */
    @Test
    public void testStop_doesNotThrow() {
        System.out.println("[TEST PlayingState] stop() -> must not throw any exception");

        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);

        // stop() is currently a no-op in PlayingState: verify it does not throw
        assertDoesNotThrow(() -> state.stop());
    }
}
