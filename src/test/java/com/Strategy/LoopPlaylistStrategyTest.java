package com.Strategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.MockTrackFactory;
import com.Model.Track;
import com.Model.TrackFactory;

public class LoopPlaylistStrategyTest {

    private LoopPlaylistStrategy strategy;
    private Track track1;
    private Track track2;
    private Track track3;
    private TrackFactory factory;

    @BeforeEach
    void setUp() {
        factory = new MockTrackFactory();
        strategy = new LoopPlaylistStrategy();
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
    void nextTrack_currentNull_returnsFirstTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, null);
        assertSame(track1, strategy.nextTrack(null));
    }

    @Test
    void nextTrack_singleTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1);
        strategy.setQueue(queue, track1);

        assertSame(track1, strategy.nextTrack( track1));
    }

    @Test
    void nextTrack_firstTrack_returnsSecondTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, track1);

        assertSame(track2, strategy.nextTrack(track1));
    }

    @Test
    void nextTrack_lastTrack_returnsFirstTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, track3);
        assertSame(track1, strategy.nextTrack(track3));
    }

    // -----------------------------------------------------------------------
    // Test per previousTrack()
    // -----------------------------------------------------------------------

    @Test
    void previousTrack_currentNull_returnsFirstTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, null);
        assertSame(track1, strategy.previousTrack(null));
    }

    @Test
    void previousTrack_singleTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1);
        strategy.setQueue(queue, track1);

        assertSame(track1, strategy.previousTrack( track1));
    }

    @Test
    void previousTrack_secondTrack_returnsFirstTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, track2);

        assertSame(track1, strategy.previousTrack( track2));
    }

    @Test
    void previousTrack_firstTrack_returnsLastTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        strategy.setQueue(queue, track1);

        assertSame(track3, strategy.previousTrack( track1));
    }

}
