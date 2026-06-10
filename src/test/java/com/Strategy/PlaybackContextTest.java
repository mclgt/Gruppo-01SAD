package com.Strategy;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.Track;

/**
 * @class PlaybackContextTest
 * @brief Test class for PlaybackContext.
 *        Verifies that the Context correctly delegates to strategies
 *        and that runtime strategy switching works as expected.
 *        Strategies used are hand-written dummy stubs, without any mocking framework.
 */
public class PlaybackContextTest {

    /**
     * @brief Stub strategy A: always returns a fixed next track and a fixed previous track.
     *        Used to verify that the Context actually delegates calls to the strategy.
     */
    private static class DummyStrategyA implements IPlaybackStrategy {
        private final Track fixedNext;
        private final Track fixedPrevious;

        DummyStrategyA(Track fixedNext, Track fixedPrevious) {
            this.fixedNext = fixedNext;
            this.fixedPrevious = fixedPrevious;
        }

        @Override
        public Track nextTrack(List<Track> queue, Track current) {
            return fixedNext;
        }

        @Override
        public Track previousTrack(List<Track> queue, Track current) {
            return fixedPrevious;
        }
    }

    /**
     * @brief Stub strategy B: always returns null, simulating end/beginning of queue.
     *        Used to verify behavior after a runtime strategy switch.
     */
    private static class DummyStrategyB implements IPlaybackStrategy {
        @Override
        public Track nextTrack(List<Track> queue, Track current) {
            return null;
        }

        @Override
        public Track previousTrack(List<Track> queue, Track current) {
            return null;
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
        track1 = new Track("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "dummy1.mp3",null);
        track2 = new Track("Canzone B", "Artista B", 2001, "Pop", 210, "Album B", "dummy2.mp3",null);
        track3 = new Track("Canzone C", "Artista C", 2002, "Pop", 220, "Album C", "dummy3.mp3",null);
        queue = Arrays.asList(track1, track2, track3);
    }

    // -----------------------------------------------------------------------
    // Tests for constructor and getStrategy()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that the strategy passed to the constructor is stored
     *        and returned by getStrategy().
     */
    @Test
    public void testConstructor_strategyIsStored() {
        DummyStrategyA strategyA = new DummyStrategyA(track2, track1);
        PlaybackContext context = new PlaybackContext(strategyA);
        assertEquals(strategyA, context.getStrategy());
    }

    // -----------------------------------------------------------------------
    // Tests for setStrategy()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that setStrategy() replaces the current strategy at runtime.
     */
    @Test
    public void testSetStrategy_replacesStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        DummyStrategyB strategyB = new DummyStrategyB();
        context.setStrategy(strategyB);
        assertEquals(strategyB, context.getStrategy());
    }

    // -----------------------------------------------------------------------
    // Tests for nextTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that nextTrack() delegates the call to the current strategy.
     */
    @Test
    public void testNextTrack_delegatesToStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        assertEquals(track2, context.nextTrack(queue, track1));
    }

    /**
     * @brief Verifies that nextTrack() uses the new strategy after a runtime switch.
     */
    @Test
    public void testNextTrack_afterStrategyChange_usesNewStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        context.setStrategy(new DummyStrategyB());
        assertNull(context.nextTrack(queue, track1));
    }

    // -----------------------------------------------------------------------
    // Tests for previousTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that previousTrack() delegates the call to the current strategy.
     */
    @Test
    public void testPreviousTrack_delegatesToStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        assertEquals(track1, context.previousTrack(queue, track2));
    }

    /**
     * @brief Verifies that previousTrack() uses the new strategy after a runtime switch.
     */
    @Test
    public void testPreviousTrack_afterStrategyChange_usesNewStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        context.setStrategy(new DummyStrategyB());
        assertNull(context.previousTrack(queue, track2));
    }
}
