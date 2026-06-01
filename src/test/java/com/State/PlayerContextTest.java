package com.State;

import com.Model.Track;
import com.Strategy.IPlaybackStrategy;
import com.Strategy.PlaybackContext;
import com.Strategy.SequentialStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Test per US-6: riproduzione singola traccia tramite PlayingState.
 *        Verifica il comportamento di play, next e previous nel solo stato Playing.
 *        Usa stub manuali per PlaybackContext, senza framework di mocking.
 * @author Christian
 */
public class PlayerContextTest {

    // Stub strategy: restituisce next e previous fissi (possono essere null)
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
        track1 = new Track("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "");
        track2 = new Track("Canzone B", "Artista B", 2001, "Pop", 210, "Album B", "");
        track3 = new Track("Canzone C", "Artista C", 2002, "Pop", 220, "Album C", "");
        queue = Arrays.asList(track1, track2, track3);
    }

    private PlayerContext contextWith(Track nextTrack, Track prevTrack) {
        PlaybackContext playbackContext = new PlaybackContext(new DummyStrategy(nextTrack, prevTrack));
        return new PlayerContext(playbackContext);
    }

    //TEST DI PLAY o PlayingState 

    @Test
    public void testPlay_startsPlaying() {
        System.out.println("[TEST] play() → il player deve essere in stato Playing");
        PlayerContext ctx = contextWith(null, null);
        ctx.play(track1);
        assertTrue(ctx.isPlaying());
    }

    @Test
    public void testPlay_setsCurrentTrack() {
        System.out.println("[TEST] play() → la traccia corrente deve essere quella passata");
        PlayerContext ctx = contextWith(null, null);
        ctx.play(track1);
        assertEquals(track1, ctx.getCurrentTrack());
    }

    @Test
    public void testPlay_differentTrack_updatesCurrentTrack() {
        System.out.println("[TEST] play() su traccia diversa → deve aggiornare la traccia corrente e restare in Playing");
        PlayerContext ctx = contextWith(null, null);
        ctx.play(track1);
        ctx.play(track2);
        assertEquals(track2, ctx.getCurrentTrack());
        assertTrue(ctx.isPlaying());
    }

    //TEST PER NEXT TRACK

    @Test
    public void testNext_withValidNext_updatesCurrentTrack() {
        System.out.println("[TEST] next() con traccia successiva disponibile → deve aggiornare la traccia corrente");
        PlayerContext ctx = contextWith(track2, null);
        ctx.play(track1);
        ctx.next(queue, track1);
        assertEquals(track2, ctx.getCurrentTrack());
    }

    @Test
    public void testNext_withValidNext_remainsPlaying() {
        System.out.println("[TEST] next() con traccia successiva disponibile → il player deve restare in Playing");
        PlayerContext ctx = contextWith(track2, null);
        ctx.play(track1);
        ctx.next(queue, track1);
        assertTrue(ctx.isPlaying());
    }

    @Test
    public void testNext_withNoNext_currentTrackUnchanged() {
        System.out.println("[TEST] next() senza traccia successiva (null) → la traccia corrente non deve cambiare");
        PlayerContext ctx = contextWith(null, null);
        ctx.play(track1);
        ctx.next(queue, track1);
        assertEquals(track1, ctx.getCurrentTrack());
    }

    //TEST PER PREVIOUS TRACK

    @Test
    public void testPrevious_withValidPrevious_updatesCurrentTrack() {
        System.out.println("[TEST] previous() con traccia precedente disponibile → deve aggiornare la traccia corrente");
        PlayerContext ctx = contextWith(null, track1);
        ctx.play(track2);
        ctx.previous(queue, track2);
        assertEquals(track1, ctx.getCurrentTrack());
    }

    @Test
    public void testPrevious_withValidPrevious_remainsPlaying() {
        System.out.println("[TEST] previous() con traccia precedente disponibile → il player deve restare in Playing");
        PlayerContext ctx = contextWith(null, track1);
        ctx.play(track2);
        ctx.previous(queue, track2);
        assertTrue(ctx.isPlaying());
    }

    @Test
    public void testPrevious_withNoPrevious_currentTrackUnchanged() {
        System.out.println("[TEST] previous() senza traccia precedente (null) → la traccia corrente non deve cambiare");
        PlayerContext ctx = contextWith(null, null);
        ctx.play(track2);
        ctx.previous(queue, track2);
        assertEquals(track2, ctx.getCurrentTrack());
    }

    //TEST per riproduzione sequenziale US-8 

    private PlayerContext sequentialContext() {
        System.out.println("\n [TEST US-8] sequential Context");
        return new PlayerContext(new PlaybackContext(new SequentialStrategy()));
    }

    @Test
    public void testSequential_next_advancesFromFirstToSecond() {
        System.out.println("[TEST US-8] next() sequenziale → avanza da track1 a track2");
        PlayerContext ctx = sequentialContext();
        ctx.play(track1);
        ctx.next(queue, track1);
        assertEquals(track2, ctx.getCurrentTrack());
    }

    @Test
    public void testSequential_next_advancesFromSecondToThird() {
        System.out.println("[TEST US-8] next() sequenziale → avanza da track2 a track3");
        PlayerContext ctx = sequentialContext();
        ctx.play(track2);
        ctx.next(queue, track2);
        assertEquals(track3, ctx.getCurrentTrack());
    }

    @Test
    public void testSequential_next_atLastTrack_currentTrackUnchanged() {
        System.out.println("[TEST US-8] next() sull'ultima traccia → la traccia corrente non deve cambiare");
        PlayerContext ctx = sequentialContext();
        ctx.play(track3);
        ctx.next(queue, track3);
        assertEquals(track3, ctx.getCurrentTrack());
    }

    @Test
    public void testSequential_previous_goesBackFromThirdToSecond() {
        System.out.println("[TEST US-8] previous() sequenziale → torna da track3 a track2");
        PlayerContext ctx = sequentialContext();
        ctx.play(track3);
        ctx.previous(queue, track3);
        assertEquals(track2, ctx.getCurrentTrack());
    }

    @Test
    public void testSequential_previous_atFirstTrack_currentTrackUnchanged() {
        System.out.println("[TEST US-8] previous() sulla prima traccia → la traccia corrente non deve cambiare");
        PlayerContext ctx = sequentialContext();
        ctx.play(track1);
        ctx.previous(queue, track1);
        assertEquals(track1, ctx.getCurrentTrack());
    }

    @Test
    public void testSequential_fullSequence_traversesAllTracks() {
        System.out.println("[TEST US-8] sequenza completa → attraversa tutte le tracce in ordine");
        PlayerContext ctx = sequentialContext();
        ctx.play(track1);
        assertEquals(track1, ctx.getCurrentTrack());

        ctx.next(queue, ctx.getCurrentTrack());
        assertEquals(track2, ctx.getCurrentTrack());

        ctx.next(queue, ctx.getCurrentTrack());
        assertEquals(track3, ctx.getCurrentTrack());

        ctx.next(queue, ctx.getCurrentTrack());
        assertEquals(track3, ctx.getCurrentTrack()); // fine coda, rimane sull'ultima
    }
}
