package com.Strategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.MockTrackFactory;
import com.Model.Track;
import com.Model.TrackFactory;

class ShuffleStrategyTest {

    private ShuffleStrategy strategy;
    private Track track1;
    private Track track2;
    private Track track3;
    private TrackFactory factory;

    @BeforeEach
    void setUp() {
        factory = new MockTrackFactory();
        strategy = new ShuffleStrategy();
        track1 = factory.createTrack("Track 1", "Artista", 2020, "Pop", 180, "Album", "dummy1.mp3", null);
        track2 = factory.createTrack("Track 2", "Artista", 2021, "Pop", 200, "Album", "dummy2.mp3", null);
        track3 = factory.createTrack("Track 3", "Artista", 2022, "Pop", 210, "Album", "dummy3.mp3", null);
    }

    // -----------------------------------------------------------------------
    // Test per nextTrack()
    // -----------------------------------------------------------------------

    @Test
    void nextTrack_emptyQueue_returnsNull() {
        List<Track> queue = List.of();
        strategy.setQueue(queue, null);
        assertNull(strategy.nextTrack( null));
    }

    @Test
    void nextTrack_singleTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1);
        strategy.setQueue(queue, track1);
        assertEquals(track1, strategy.nextTrack(track1));
    }

    @Test
    void nextTrack_multipleTracks_almostNeverReturnsCurrentTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, track1);

        for (int i = 0; i < 5; i++) {
            assertNotEquals(track1, strategy.nextTrack(track1));
        }
    }

    @Test
    void nextTrack_multipleTracks_returnsValidTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, track1);
        assertNotEquals(track1, strategy.nextTrack(track1));
    }

    // -----------------------------------------------------------------------
    // Test per previousTrack()
    // -----------------------------------------------------------------------

    @Test
    void previousTrack_behaviorSameAsNextTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, track1);
        for (int i = 0; i < 20; i++) {
            assertNotEquals(track1, strategy.previousTrack(track1));
        }
    }
}
