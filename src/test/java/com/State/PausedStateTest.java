package com.State;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.MockTrackFactory;
import com.Model.Track;
import com.Model.TrackFactory;
import com.Strategy.IPlaybackStrategy;
import com.Strategy.PlaybackContext;

public class PausedStateTest {

    private static class DummyStrategy implements IPlaybackStrategy {
        private final Track nextResult;
        private final Track prevResult;

        DummyStrategy(Track nextResult, Track prevResult, List<Track> q) {
            this.nextResult = nextResult;
            this.prevResult = prevResult;
        }

        @Override
        public Track nextTrack(Track track) {
            return nextResult;
        }

        @Override
        public Track previousTrack(Track track) {
            return prevResult;
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
        track2 = factory.createTrack("Canzone B", "Artista B", 2001, "Rock", 210, "Album B", "dummy2.mp3",
                null);
        track3 = factory.createTrack("Canzone C", "Artista C", 2002, "Jazz", 220, "Album C", "dummy3.mp3",
                null);
        queue = Arrays.asList(track1, track2, track3);

    }

    private PlayerContext pausedContextWith(Track currentTrack, Track nextTrack, Track prevTrack) {
        PlaybackContext pb = new PlaybackContext(new DummyStrategy(nextTrack, prevTrack, queue));
        PlayerContext ctx = new PlayerContext(pb);
        // avvia la riproduzione per impostare una traccia corrente
        ctx.play(currentTrack);
        // pausa: PlayingState.pause() chiama setState(pausedState)
        ctx.pause();
        return ctx;
    }

    // -----------------------------------------------------------------------
    // Test per pause() — no-op quando già in pausa
    // -----------------------------------------------------------------------

    @Test
    public void testPause_whenAlreadyPaused_remainsPaused() {
        System.out.println("[TEST PausedState] pause() quando già in pausa -> deve rimanere in pausa");

        PlayerContext ctx = pausedContextWith(track1, null, null);

        // chiamare pause() una seconda volta deve essere un no-op: lo stato non deve cambiare
        ctx.pause();

        assertTrue(ctx.isPaused());
    }

    @Test
    public void testPause_whenAlreadyPaused_doesNotStartPlaying() {
        System.out.println("[TEST PausedState] pause() quando già in pausa -> non deve avviare la riproduzione");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.pause();

        // dopo una seconda pause() il player non deve essere in riproduzione
        assertFalse(ctx.isPlaying());
    }

    @Test
    public void testPause_doesNotThrow() {
        System.out.println("[TEST PausedState] pause() -> non deve lanciare eccezioni");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        assertDoesNotThrow(() -> ctx.pause());
    }

    // -----------------------------------------------------------------------
    // Test per play() — uscita dalla pausa e ripresa della riproduzione
    // -----------------------------------------------------------------------

    @Test
    public void testPlay_exitsPausedState() {
        System.out.println("[TEST PausedState] play() -> deve uscire dallo stato di pausa");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        // chiamare play su qualsiasi traccia: deve uscire da PausedState
        ctx.play(track2);

        assertFalse(ctx.isPaused());
    }

    @Test
    public void testPlay_setsPlayingState() {
        System.out.println("[TEST PausedState] play() -> deve riportare il player nello stato di riproduzione");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.play(track2);

        assertTrue(ctx.isPlaying());
    }

    @Test
    public void testPlay_updatesCurrentTrack() {
        System.out.println("[TEST PausedState] play() -> deve aggiornare la traccia corrente nel contesto");

        // era in pausa su track1, chiamando play(track2): ci si aspetta track2 come traccia corrente
        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.play(track2);

        assertEquals(track2, ctx.getCurrentTrack());
    }

    @Test
    public void testPlay_sameTrack_exitsPausedState() {
        System.out.println("[TEST PausedState] play() sulla stessa traccia in pausa -> deve uscire dallo stato di pausa");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.play(track1);

        // deve uscire dalla pausa e la traccia corrente deve rimanere track1
        assertFalse(ctx.isPaused());
        assertEquals(track1, ctx.getCurrentTrack());
    }

    @Test
    public void testPlay_trackWithoutAudioSource_doesNotThrow() {
        System.out.println("[TEST PausedState] play() su traccia senza audioSource -> non deve lanciare eccezioni");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        // track2 non ha audioSource (null): play() non deve crashare
        assertDoesNotThrow(() -> ctx.play(track2));
    }

    // -----------------------------------------------------------------------
    // Test per stop()
    // -----------------------------------------------------------------------

    @Test
    public void testStop_exitsPausedState() {
        System.out.println("[TEST PausedState] stop() -> deve uscire dallo stato di pausa");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.stop();

        // dopo stop il contesto non deve più essere in pausa
        assertFalse(ctx.isPaused());
    }

    @Test
    public void testStop_doesNotThrow() {
        System.out.println("[TEST PausedState] stop() -> non deve lanciare eccezioni");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        assertDoesNotThrow(() -> ctx.stop());
    }

    // -----------------------------------------------------------------------
    // Test per next() — navigazione dallo stato di pausa
    // -----------------------------------------------------------------------

    @Test
    public void testNext_exitsPausedState() {
        System.out.println("[TEST PausedState] next() -> deve uscire dallo stato di pausa");

        // la strategia restituirà track2 come traccia successiva
        PlayerContext ctx = pausedContextWith(track1, track2, null);
        ctx.next();

        assertFalse(ctx.isPaused());
    }

    @Test
    public void testNext_setsPlayingState() {
        System.out.println("[TEST PausedState] next() -> deve riportare il player nello stato di riproduzione");

        PlayerContext ctx = pausedContextWith(track1, track2, null);
        ctx.next();

        assertTrue(ctx.isPlaying());
    }

    @Test
    public void testNext_updatesCurrentTrackUsingStrategy() {
        System.out.println("[TEST PausedState] next() -> deve aggiornare la traccia corrente usando la strategia");

        // DummyStrategy restituisce track2: ci si aspetta che diventi la traccia corrente
        PlayerContext ctx = pausedContextWith(track1, track2, null);

        ctx.next();
        assertEquals(track2, ctx.getCurrentTrack());
    }

    @Test
    public void testNext_withNoNextTrack_exitsPausedState() {
        System.out.println("[TEST PausedState] next() senza traccia successiva -> deve uscire dalla pausa comunque");

        // null come next: la strategia segnala che non è disponibile alcuna traccia successiva
        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.next();

        // deve uscire dalla pausa anche in questo caso
        assertFalse(ctx.isPaused());
    }

    @Test
    public void testNext_doesNotThrow() {
        System.out.println("[TEST PausedState] next() -> non deve lanciare eccezioni");

        PlayerContext ctx = pausedContextWith(track1, track2, null);
        assertDoesNotThrow(() -> ctx.next());
    }

    // -----------------------------------------------------------------------
    // Test per previous() — navigazione all'indietro dallo stato di pausa
    // -----------------------------------------------------------------------

    @Test
    public void testPrevious_exitsPausedState() {
        System.out.println("[TEST PausedState] previous() -> deve uscire dallo stato di pausa");

        // la strategia restituirà track1 come traccia precedente
        PlayerContext ctx = pausedContextWith(track2, null, track1);
        ctx.previous();

        assertFalse(ctx.isPaused());
    }

    @Test
    public void testPrevious_setsPlayingState() {
        System.out.println("[TEST PausedState] previous() -> deve riportare il player nello stato di riproduzione");

        PlayerContext ctx = pausedContextWith(track2, null, track1);
        ctx.previous();

        assertTrue(ctx.isPlaying());
    }

    @Test
    public void testPrevious_updatesCurrentTrackUsingStrategy() {
        System.out.println("[TEST PausedState] previous() -> deve aggiornare la traccia corrente usando la strategia");

        // era in pausa su track2, DummyStrategy restituisce track1 come precedente
        PlayerContext ctx = pausedContextWith(track2, null, track1);
        ctx.previous();

        assertEquals(track1, ctx.getCurrentTrack());
    }

    @Test
    public void testPrevious_withNoPreviousTrack_exitsPausedState() {
        System.out.println("[TEST PausedState] previous() senza traccia precedente -> deve uscire dalla pausa comunque");

        // null come previous: la strategia segnala che non è disponibile alcuna traccia precedente
        PlayerContext ctx = pausedContextWith(track2, null, null);
        ctx.previous();

        // deve uscire dalla pausa anche senza una traccia precedente
        assertFalse(ctx.isPaused());
    }

    @Test
    public void testPrevious_doesNotThrow() {
        System.out.println("[TEST PausedState] previous() -> non deve lanciare eccezioni");

        PlayerContext ctx = pausedContextWith(track2, null, track1);
        assertDoesNotThrow(() -> ctx.previous());
    }
}
