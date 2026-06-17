package com.State;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.MockTrackFactory;
import com.Model.Track;
import com.Model.TrackFactory;
import com.Strategy.IPlaybackStrategy;
import com.Strategy.PlaybackContext;
import com.Strategy.SequentialStrategy;

import javafx.collections.FXCollections;

public class PlayerContextTest {

    // Stub strategy: restituisce valori fissi di next e previous (possono essere null).
    // Isola il Context sotto test dalla logica reale della strategia.
    private static class DummyStrategy implements IPlaybackStrategy {
        private final Track nextResult;
        private final Track prevResult;

        DummyStrategy(Track nextResult, Track prevResult) {
            this.nextResult = nextResult;
            this.prevResult = prevResult;
        }

        @Override
        public Track nextTrack(Track current) {
            return nextResult;
        }

        @Override
        public Track previousTrack(Track current) {
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
        track2 = factory.createTrack("Canzone B", "Artista B", 2001, "Pop", 210, "Album B", "dummy2.mp3",
                null);
        track3 = factory.createTrack("Canzone C", "Artista C", 2002, "Pop", 220, "Album C", "dummy3.mp3",
                null);
        queue = FXCollections.observableArrayList(track1, track2, track3);

    }

    private PlayerContext contextWith(Track nextTrack, Track prevTrack) {
        PlaybackContext playbackContext = new PlaybackContext(new DummyStrategy(nextTrack, prevTrack));
        return new PlayerContext(playbackContext);
    }

    // -----------------------------------------------------------------------
    // Test per play()
    // -----------------------------------------------------------------------

    @Test
    public void testPlay_startsPlaying() {
        System.out.println("[TEST] play() -> il player deve essere nello stato Playing");
        PlayerContext ctx = contextWith(null, null);
        ctx.play(track1);
        assertTrue(ctx.isPlaying());
    }

    @Test
    public void testPlay_setsCurrentTrack() {
        System.out.println("[TEST] play() -> la traccia corrente deve essere quella passata");
        PlayerContext ctx = contextWith(null, null);
        ctx.play(track1);
        assertEquals(track1, ctx.getCurrentTrack());
    }

    @Test
    public void testPlay_differentTrack_updatesCurrentTrack() {
        System.out.println("[TEST] play() su una traccia diversa -> deve aggiornare la traccia corrente e rimanere in Playing");
        PlayerContext ctx = contextWith(null, null);
        ctx.play(track1);
        ctx.play(track2);
        assertEquals(track2, ctx.getCurrentTrack());
        assertTrue(ctx.isPlaying());
    }

    // -----------------------------------------------------------------------
    // Test per next()
    // -----------------------------------------------------------------------

    @Test
    public void testNext_withValidNext_updatesCurrentTrack() {
        System.out.println("[TEST] next() con una traccia successiva valida -> deve aggiornare la traccia corrente");
        PlayerContext ctx = contextWith(track2, null);
        ctx.play(track1);
        ctx.next();
        assertEquals(track2, ctx.getCurrentTrack());
    }

    @Test
    public void testNext_withValidNext_remainsPlaying() {
        System.out.println("[TEST] next() con una traccia successiva valida -> il player deve rimanere in Playing");
        PlayerContext ctx = contextWith(track2, null);
        ctx.play(track1);
        ctx.next();
        assertTrue(ctx.isPlaying());
    }

    @Test
    public void testNext_withNoNext_currentTrackUnchanged() {
        System.out.println("[TEST] next() senza traccia successiva (null) -> la traccia corrente non deve cambiare");
        PlayerContext ctx = contextWith(null, null);
        ctx.play(track1);
        ctx.next();
        assertEquals(track1, ctx.getCurrentTrack());
    }

    // -----------------------------------------------------------------------
    // Test per previous()
    // -----------------------------------------------------------------------

    @Test
    public void testPrevious_withValidPrevious_updatesCurrentTrack() {
        System.out.println("[TEST] previous() con una traccia precedente valida -> deve aggiornare la traccia corrente");
        PlayerContext ctx = contextWith(null, track1);
        ctx.play(track2);
        ctx.previous();
        assertEquals(track1, ctx.getCurrentTrack());
    }

    @Test
    public void testPrevious_withValidPrevious_remainsPlaying() {
        System.out.println("[TEST] previous() con una traccia precedente valida -> il player deve rimanere in Playing");
        PlayerContext ctx = contextWith(null, track1);
        ctx.play(track2);
        ctx.previous();
        assertTrue(ctx.isPlaying());
    }

    @Test
    public void testPrevious_withNoPrevious_currentTrackUnchanged() {
        System.out.println("[TEST] previous() senza traccia precedente (null) -> la traccia corrente non deve cambiare");
        PlayerContext ctx = contextWith(null, null);
        ctx.play(track2);
        ctx.previous();
        assertEquals(track2, ctx.getCurrentTrack());
    }

    // -----------------------------------------------------------------------
    // Test per la riproduzione sequenziale (US-8)
    // -----------------------------------------------------------------------

    private PlayerContext sequentialContext() {
        System.out.println("\n [TEST US-8] sequential Context");
        SequentialStrategy strategy = new SequentialStrategy();
        strategy.setQueue(queue, null);
        PlaybackContext playbackContext = new PlaybackContext(strategy);
        return new PlayerContext(playbackContext);
    }

    @Test
    public void testSequential_next_advancesFromFirstToSecond() {
        System.out.println("[TEST US-8] next() sequenziale -> avanza da track1 a track2");
        PlayerContext ctx = sequentialContext();
        ctx.play(track1);
        ctx.next();
        assertEquals(track2, ctx.getCurrentTrack());
    }

    @Test
    public void testSequential_next_advancesFromSecondToThird() {
        System.out.println("[TEST US-8] next() sequenziale -> avanza da track2 a track3");
        PlayerContext ctx = sequentialContext();
        ctx.play(track2);
        ctx.next();
        assertEquals(track3, ctx.getCurrentTrack());
    }

    @Test
    public void testSequential_next_atLastTrack_currentTrackUnchanged() {
        System.out.println("[TEST US-8] next() sull'ultima traccia -> la traccia corrente non deve cambiare");
        PlayerContext ctx = sequentialContext();
        ctx.play(track3);
        ctx.next();
        assertEquals(track3, ctx.getCurrentTrack());
    }

    @Test
    public void testSequential_previous_goesBackFromThirdToSecond() {
        System.out.println("[TEST US-8] previous() sequenziale -> torna da track3 a track2");
        PlayerContext ctx = sequentialContext();
        ctx.play(track3);
        ctx.previous();
        assertEquals(track2, ctx.getCurrentTrack());
    }

    @Test
    public void testSequential_previous_atFirstTrack_currentTrackUnchanged() {
        System.out.println("[TEST US-8] previous() sulla prima traccia -> la traccia corrente non deve cambiare");
        PlayerContext ctx = sequentialContext();
        ctx.play(track1);
        ctx.previous();
        assertEquals(track1, ctx.getCurrentTrack());
    }

    @Test
    public void testSequential_fullSequence_traversesAllTracks() {
        System.out.println("[TEST US-8] sequenza completa -> attraversa tutte le tracce in ordine");
        PlayerContext ctx = sequentialContext();
        ctx.play(track1);
        assertEquals(track1, ctx.getCurrentTrack());

        ctx.next();
        assertEquals(track2, ctx.getCurrentTrack());

        ctx.next();
        assertEquals(track3, ctx.getCurrentTrack());

        ctx.next();
        assertEquals(track3, ctx.getCurrentTrack()); // fine della coda: rimane sull'ultima traccia
    }
}
