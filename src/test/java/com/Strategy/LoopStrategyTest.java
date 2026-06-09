package com.Strategy;

import com.Model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LoopStrategyTest {

    private LoopStrategy strategy;
    private Track track1;
    private Track track2;
    private Track track3;

    @BeforeEach
    void setUp() {
        strategy = new LoopStrategy();
        track1 = new Track("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "dummy1.mp3", null);
        track2 = new Track("Canzone B", "Artista B", 2001, "Rock", 180, "Album B", "dummy2.mp3", null);
        track3 = new Track("Canzone C", "Artista C", 2002, "Jazz", 210, "Album C", "dummy3.mp3", null);
    }


    @Test
    void nextTrack_currentNull_restituisceNull() {
        List<Track> queue = List.of(track1, track2, track3);
        assertNull(strategy.nextTrack(queue, null));
    }

    @Test
    void nextTrack_unBrano_restituisceStessoBrano() {
        List<Track> queue = List.of(track1);
        assertSame(track1, strategy.nextTrack(queue, track1));
    }

    @Test
    void nextTrack_piuBrani_restituisceStessoBrano() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track2, strategy.nextTrack(queue, track2));
    }

    @Test
    void nextTrack_ultimoBrano_restituisceStessoBrano() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track3, strategy.nextTrack(queue, track3));
    }



    @Test
    void previousTrack_currentNull_restituisceNull() {
        List<Track> queue = List.of(track1, track2, track3);
        assertNull(strategy.previousTrack(queue, null));
    }

    @Test
    void previousTrack_unBrano_restituisceStessoBrano() {
        List<Track> queue = List.of(track1);
        assertSame(track1, strategy.previousTrack(queue, track1));
    }

    @Test
    void previousTrack_piuBrani_restituisceStessoBrano() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track2, strategy.previousTrack(queue, track2));
    }

    @Test
    void previousTrack_primoBrano_restituisceStessoBrano() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track1, strategy.previousTrack(queue, track1));
    }
}
