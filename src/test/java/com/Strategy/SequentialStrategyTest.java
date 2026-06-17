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

public class SequentialStrategyTest {

    private SequentialStrategy strategy;
    private Track track1;
    private Track track2;
    private Track track3;
    private List<Track> queue;
    private TrackFactory factory;

    @BeforeEach
    public void setUp() {
        factory = new MockTrackFactory();
        strategy = new SequentialStrategy();
        track1 = factory.createTrack("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "dummy1.mp3",
                null);
        track2 = factory.createTrack("Canzone B", "Artista B", 2001, "Pop", 210, "Album B", "dummy2.mp3",
                null);
        track3 = factory.createTrack("Canzone C", "Artista C", 2002, "Pop", 220, "Album C", "dummy3.mp3",
                null);
        queue = Arrays.asList(track1, track2, track3);
        strategy.setQueue(queue, track1);
    }

    // -----------------------------------------------------------------------
    // Test per nextTrack()
    // -----------------------------------------------------------------------

    @Test
    public void testNextTrack_fromFirst_returnsSecond() {
        strategy.setQueue(queue, track1);
        assertEquals(track2, strategy.nextTrack(track1));
    }

    @Test
    public void testNextTrack_fromMiddle_returnsThird() {
        strategy.setQueue(queue, track2);
        assertEquals(track3, strategy.nextTrack(track2));
    }

    @Test
    public void testNextTrack_fromLast_returnsNull() {
        strategy.setQueue(queue, track3);
        assertNull(strategy.nextTrack(track3));
    }

    @Test
    public void testNextTrack_trackNotInQueue_returnsNull() {
        Track outsider = factory.createTrack("Ghost", "Nobody", 1999, "Jazz", 180, "None", "dummy.mp3",
                null);
        assertNull(strategy.nextTrack(outsider));
    }

    // -----------------------------------------------------------------------
    // Test per previousTrack()
    // -----------------------------------------------------------------------

    @Test
    public void testPreviousTrack_fromLast_returnsMiddle() {
        strategy.setQueue(queue, track3);
        assertEquals(track2, strategy.previousTrack(track3));
    }

    @Test
    public void testPreviousTrack_fromMiddle_returnsFirst() {
        strategy.setQueue(queue, track2);
        assertEquals(track1, strategy.previousTrack(track2));
    }

    @Test
    public void testPreviousTrack_fromFirst_returnsNull() {
        strategy.setQueue(queue, track1);
        assertNull(strategy.previousTrack(track1));
    }

    @Test
    public void testPreviousTrack_trackNotInQueue_returnsNull() {
        Track outsider = factory.createTrack("Ghost", "Nobody", 1999, "Jazz", 180, "None", "dummy.mp3",
                null);
        assertNull(strategy.previousTrack(outsider));
    }
}
