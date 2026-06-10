package com.State;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.Track;
import com.Strategy.IPlaybackStrategy;
import com.Strategy.PlaybackContext;
import com.Strategy.SequentialStrategy;

/**
 * @class PlayerContextTest
 * @brief Test per PlayerContext tramite PlayingState (riproduzione di singola traccia).
 *        Verifica il comportamento di play(), next() e previous() nello stato Playing.
 *        Usa stub manuali per PlaybackContext, senza framework di mocking.
 * @author Christian
 */
public class PlayerContextTest {

    /**
     * @brief Stub strategy: restituisce valori fissi di next e previous (possono essere null).
     *        Isola il Context sotto test dalla logica reale della strategia.
     */
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

    /**
     * @brief Inizializza tre tracce dummy e la coda prima di ogni test.
     */
    @BeforeEach
    public void setUp() {
        track1 = new Track("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "dummy1.mp3", null);
        track2 = new Track("Canzone B", "Artista B", 2001, "Pop", 210, "Album B", "dummy2.mp3", null);
        track3 = new Track("Canzone C", "Artista C", 2002, "Pop", 220, "Album C", "dummy3.mp3", null);
        queue = Arrays.asList(track1, track2, track3);
    }

    /**
     * @brief Crea un PlayerContext supportato da una DummyStrategy con risultati fissi di next/previous.
     */
    private PlayerContext contextWith(Track nextTrack, Track prevTrack) {
        PlaybackContext playbackContext = new PlaybackContext(new DummyStrategy(nextTrack, prevTrack));
        return new PlayerContext(playbackContext);
    }

    // -----------------------------------------------------------------------
    // Test per play()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che play() porti il player nello stato Playing.
     */
    @Test
    public void testPlay_startsPlaying() {
        System.out.println("[TEST] play() -> il player deve essere nello stato Playing");
        PlayerContext ctx = contextWith(null, null);
        ctx.play(track1);
        assertTrue(ctx.isPlaying());
    }

    /**
     * @brief Verifica che play() imposti la traccia corrente su quella passata come parametro.
     */
    @Test
    public void testPlay_setsCurrentTrack() {
        System.out.println("[TEST] play() -> la traccia corrente deve essere quella passata");
        PlayerContext ctx = contextWith(null, null);
        ctx.play(track1);
        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che play() su una traccia diversa aggiorni la traccia corrente
     *        e mantenga il player nello stato Playing.
     */
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

    /**
     * @brief Verifica che next() con una traccia successiva valida aggiorni la traccia corrente.
     */
    @Test
    public void testNext_withValidNext_updatesCurrentTrack() {
        System.out.println("[TEST] next() con una traccia successiva valida -> deve aggiornare la traccia corrente");
        PlayerContext ctx = contextWith(track2, null);
        ctx.play(track1);
        ctx.next(queue, track1);
        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che next() con una traccia successiva valida mantenga il player nello stato Playing.
     */
    @Test
    public void testNext_withValidNext_remainsPlaying() {
        System.out.println("[TEST] next() con una traccia successiva valida -> il player deve rimanere in Playing");
        PlayerContext ctx = contextWith(track2, null);
        ctx.play(track1);
        ctx.next(queue, track1);
        assertTrue(ctx.isPlaying());
    }

    /**
     * @brief Verifica che next() senza traccia successiva (null) non cambi la traccia corrente.
     */
    @Test
    public void testNext_withNoNext_currentTrackUnchanged() {
        System.out.println("[TEST] next() senza traccia successiva (null) -> la traccia corrente non deve cambiare");
        PlayerContext ctx = contextWith(null, null);
        ctx.play(track1);
        ctx.next(queue, track1);
        assertEquals(track1, ctx.getCurrentTrack());
    }

    // -----------------------------------------------------------------------
    // Test per previous()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che previous() con una traccia precedente valida aggiorni la traccia corrente.
     */
    @Test
    public void testPrevious_withValidPrevious_updatesCurrentTrack() {
        System.out.println("[TEST] previous() con una traccia precedente valida -> deve aggiornare la traccia corrente");
        PlayerContext ctx = contextWith(null, track1);
        ctx.play(track2);
        ctx.previous(queue, track2);
        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che previous() con una traccia precedente valida mantenga il player nello stato Playing.
     */
    @Test
    public void testPrevious_withValidPrevious_remainsPlaying() {
        System.out.println("[TEST] previous() con una traccia precedente valida -> il player deve rimanere in Playing");
        PlayerContext ctx = contextWith(null, track1);
        ctx.play(track2);
        ctx.previous(queue, track2);
        assertTrue(ctx.isPlaying());
    }

    /**
     * @brief Verifica che previous() senza traccia precedente (null) non cambi la traccia corrente.
     */
    @Test
    public void testPrevious_withNoPrevious_currentTrackUnchanged() {
        System.out.println("[TEST] previous() senza traccia precedente (null) -> la traccia corrente non deve cambiare");
        PlayerContext ctx = contextWith(null, null);
        ctx.play(track2);
        ctx.previous(queue, track2);
        assertEquals(track2, ctx.getCurrentTrack());
    }

    // -----------------------------------------------------------------------
    // Test per la riproduzione sequenziale (US-8)
    // -----------------------------------------------------------------------

    /**
     * @brief Crea un PlayerContext supportato da una SequentialStrategy reale.
     */
    private PlayerContext sequentialContext() {
        System.out.println("\n [TEST US-8] sequential Context");
        return new PlayerContext(new PlaybackContext(new SequentialStrategy()));
    }

    /**
     * @brief Verifica che next() sequenziale avanzi da track1 a track2.
     */
    @Test
    public void testSequential_next_advancesFromFirstToSecond() {
        System.out.println("[TEST US-8] next() sequenziale -> avanza da track1 a track2");
        PlayerContext ctx = sequentialContext();
        ctx.play(track1);
        ctx.next(queue, track1);
        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che next() sequenziale avanzi da track2 a track3.
     */
    @Test
    public void testSequential_next_advancesFromSecondToThird() {
        System.out.println("[TEST US-8] next() sequenziale -> avanza da track2 a track3");
        PlayerContext ctx = sequentialContext();
        ctx.play(track2);
        ctx.next(queue, track2);
        assertEquals(track3, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che next() sequenziale sull'ultima traccia non cambi la traccia corrente.
     */
    @Test
    public void testSequential_next_atLastTrack_currentTrackUnchanged() {
        System.out.println("[TEST US-8] next() sull'ultima traccia -> la traccia corrente non deve cambiare");
        PlayerContext ctx = sequentialContext();
        ctx.play(track3);
        ctx.next(queue, track3);
        assertEquals(track3, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che previous() sequenziale torni da track3 a track2.
     */
    @Test
    public void testSequential_previous_goesBackFromThirdToSecond() {
        System.out.println("[TEST US-8] previous() sequenziale -> torna da track3 a track2");
        PlayerContext ctx = sequentialContext();
        ctx.play(track3);
        ctx.previous(queue, track3);
        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che previous() sequenziale sulla prima traccia non cambi la traccia corrente.
     */
    @Test
    public void testSequential_previous_atFirstTrack_currentTrackUnchanged() {
        System.out.println("[TEST US-8] previous() sulla prima traccia -> la traccia corrente non deve cambiare");
        PlayerContext ctx = sequentialContext();
        ctx.play(track1);
        ctx.previous(queue, track1);
        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica una traversata sequenziale completa di tutte le tracce in ordine,
     *        incluso il comportamento no-op quando si raggiunge la fine della coda.
     */
    @Test
    public void testSequential_fullSequence_traversesAllTracks() {
        System.out.println("[TEST US-8] sequenza completa -> attraversa tutte le tracce in ordine");
        PlayerContext ctx = sequentialContext();
        ctx.play(track1);
        assertEquals(track1, ctx.getCurrentTrack());

        ctx.next(queue, ctx.getCurrentTrack());
        assertEquals(track2, ctx.getCurrentTrack());

        ctx.next(queue, ctx.getCurrentTrack());
        assertEquals(track3, ctx.getCurrentTrack());

        ctx.next(queue, ctx.getCurrentTrack());
        assertEquals(track3, ctx.getCurrentTrack()); // fine della coda: rimane sull'ultima traccia
    }
}
