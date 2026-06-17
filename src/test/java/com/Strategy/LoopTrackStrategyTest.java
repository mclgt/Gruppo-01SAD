package com.Strategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.MockTrackFactory;
import com.Model.Track;
import com.Model.TrackFactory;

public class LoopTrackStrategyTest {

    private LoopTrackStrategy strategy;
    private Track track1;
    private Track track2;
    private Track track3;
    private TrackFactory factory;

    @BeforeEach
    void setUp() {
        factory = new MockTrackFactory();
        strategy = new LoopTrackStrategy();
        track1 = factory.createTrack("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "dummy1.mp3",
                null);
        track2 = factory.createTrack("Canzone B", "Artista B", 2001, "Rock", 180, "Album B", "dummy2.mp3",
                null);
        track3 = factory.createTrack("Canzone C", "Artista C", 2002, "Jazz", 210, "Album C", "dummy3.mp3",
                null);
    }

    // -----------------------------------------------------------------------
    // Test per nextTrack()
    // -----------------------------------------------------------------------

    @Test
    void nextTrack_currentNull_returnsNull() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, null);
        assertNull(strategy.nextTrack(null));
    }

    @Test
    void nextTrack_singleTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1);
        strategy.setQueue(queue, track1);
        assertSame(track1, strategy.nextTrack(track1));
    }

    @Test
    void nextTrack_multipleTracks_returnsSameTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, track2);
        assertSame(track2, strategy.nextTrack(track2));
    }

    @Test
    void nextTrack_lastTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, track3);
        assertSame(track3, strategy.nextTrack(track3));
    }

    // -----------------------------------------------------------------------
    // Test per previousTrack()
    // -----------------------------------------------------------------------

    @Test
    void previousTrack_currentNull_returnsNull() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, null);

        assertNull(strategy.previousTrack( null));
    }

    @Test
    void previousTrack_singleTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1);
        strategy.setQueue(queue, track1);

        assertSame(track1, strategy.previousTrack(track1));
    }

    @Test
    void previousTrack_multipleTracks_returnsSameTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, track2);
        assertSame(track2, strategy.previousTrack(track2));
    }

    @Test
    void previousTrack_firstTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, track1);

        assertSame(track1, strategy.previousTrack(track1));
    }
}
