package com.Strategy;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.MockTrackFactory;
import com.Model.Track;
import com.Model.TrackFactory;

public class PlaybackContextTest {

    private static class DummyStrategyA implements IPlaybackStrategy {
        private final Track fixedNext;
        private final Track fixedPrevious;

        DummyStrategyA(Track fixedNext, Track fixedPrevious) {
            this.fixedNext = fixedNext;
            this.fixedPrevious = fixedPrevious;
        }

        @Override
        public Track nextTrack(Track current) {
            return fixedNext;
        }

        @Override
        public Track previousTrack(Track current) {
            return fixedPrevious;
        }
            @Override
        public void setQueue(List<Track> queue, Track currentTrack) {}

        @Override
        public void updateQueue(List<Track> updatedQueue) {}
    }

    private static class DummyStrategyB implements IPlaybackStrategy {
        @Override
        public Track nextTrack( Track current) {
            return null;
        }

        @Override
        public Track previousTrack(Track current) {
            return null;
        }
            @Override
        public void setQueue(List<Track> queue, Track currentTrack) {}

        @Override
        public void updateQueue(List<Track> updatedQueue) {}

    }

    private Track track1;
    private Track track2;
    private Track track3;
    private List<Track> queue;
    private TrackFactory factory;

    @BeforeEach
    public void setUp() {
        factory = new MockTrackFactory();
        track1 = factory.createTrack("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "dummy1.mp3",
                null);
        track2 = factory.createTrack("Canzone B", "Artista B", 2001, "Pop", 210, "Album B", "dummy2.mp3",
                null);
        track3 = factory.createTrack("Canzone C", "Artista C", 2002, "Pop", 220, "Album C", "dummy3.mp3",
                null);
        queue = Arrays.asList(track1, track2, track3);
    }

    // -----------------------------------------------------------------------
    // Test per il costruttore e getStrategy()
    // -----------------------------------------------------------------------

    @Test
    public void testConstructor_strategyIsStored() {
        DummyStrategyA strategyA = new DummyStrategyA(track2, track1);
        PlaybackContext context = new PlaybackContext(strategyA);
        assertEquals(strategyA, context.getStrategy());
    }

    // -----------------------------------------------------------------------
    // Test per setStrategy()
    // -----------------------------------------------------------------------

    @Test
    public void testSetStrategy_replacesStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        DummyStrategyB strategyB = new DummyStrategyB();
        context.setStrategy(strategyB);
        assertEquals(strategyB, context.getStrategy());
    }

    // -----------------------------------------------------------------------
    // Test per nextTrack()
    // -----------------------------------------------------------------------

    @Test
    public void testNextTrack_delegatesToStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        assertEquals(track2, context.nextTrack(queue, track1));
    }

    @Test
    public void testNextTrack_afterStrategyChange_usesNewStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        context.setStrategy(new DummyStrategyB());
        assertNull(context.nextTrack(queue, track1));
    }

    // -----------------------------------------------------------------------
    // Test per previousTrack()
    // -----------------------------------------------------------------------

    @Test
    public void testPreviousTrack_delegatesToStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        assertEquals(track1, context.previousTrack(queue, track2));
    }

    @Test
    public void testPreviousTrack_afterStrategyChange_usesNewStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        context.setStrategy(new DummyStrategyB());
        assertNull(context.previousTrack(queue, track2));
    }
}
