package com.State;

import com.Model.Track;
import com.Strategy.IPlaybackStrategy;
import com.Strategy.PlaybackContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Test per PausedState: verifica il comportamento dello stato di pausa
 *        nel PlayerContext.
 *
 *        PausedState è lo stato in cui si trova il player quando la riproduzione è sospesa.
 *        I test verificano che:
 *        - pause() sia un no-op quando già in pausa (lo stato non cambia)
 *        - play() esca dalla pausa, riporti il player in PlayingState e
 *          aggiorni la traccia corrente
 *        - stop() esca dalla pausa riportando in PlayingState
 *        - next() e previous() escano dalla pausa e navighino secondo la
 *          strategia corrente
 *
 * @author Christian
 * @see PausedState
 * @see PlayerContext
 * @see IPlayerState
 */
public class PausedStateTest {

    /**
     * @brief DummyStrategy che restituisce valori predefiniti per nextTrack() e previousTrack().
     *        Stub manuale senza framework di mocking, coerente con il resto della suite di test.
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

    @BeforeEach
    public void setUp() {
        track1 = new Track("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "dummy1.mp3",null);
        track2 = new Track("Canzone B", "Artista B", 2001, "Rock", 210, "Album B", "dummy2.mp3",null);
        track3 = new Track("Canzone C", "Artista C", 2002, "Jazz", 220, "Album C", "dummy3.mp3",null);
        queue = Arrays.asList(track1, track2, track3);
    }

    /**
     * @brief Crea un PlayerContext con DummyStrategy, avvia la riproduzione su currentTrack,
     *        poi la mette in pausa.
     *        Ogni test parte con il contesto già in PausedState, pronto per essere testato.
     */
    private PlayerContext pausedContextWith(Track currentTrack, Track nextTrack, Track prevTrack) {
        PlaybackContext pb = new PlaybackContext(new DummyStrategy(nextTrack, prevTrack));
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

    /**
     * @brief Verifica che pause() chiamata quando già in pausa sia un no-op:
     *        il player deve rimanere in pausa senza cambiare stato.
     */
    @Test
    public void testPause_whenAlreadyPaused_remainsPaused() {
        System.out.println("[TEST PausedState] pause() quando già in pausa -> deve rimanere in pausa");

        PlayerContext ctx = pausedContextWith(track1, null, null);

        // chiamare pause() una seconda volta deve essere un no-op: lo stato non deve cambiare
        ctx.pause();

        assertTrue(ctx.isPaused());
    }

    /**
     * @brief Verifica che pause() chiamata quando già in pausa non avvii accidentalmente la riproduzione.
     */
    @Test
    public void testPause_whenAlreadyPaused_doesNotStartPlaying() {
        System.out.println("[TEST PausedState] pause() quando già in pausa -> non deve avviare la riproduzione");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.pause();

        // dopo una seconda pause() il player non deve essere in riproduzione
        assertFalse(ctx.isPlaying());
    }

    /**
     * @brief Verifica che pause() non lanci eccezioni.
     */
    @Test
    public void testPause_doesNotThrow() {
        System.out.println("[TEST PausedState] pause() -> non deve lanciare eccezioni");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        assertDoesNotThrow(() -> ctx.pause());
    }

    // -----------------------------------------------------------------------
    // Test per play() — uscita dalla pausa e ripresa della riproduzione
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che play() esca dallo stato di pausa: il player non deve più essere in pausa.
     */
    @Test
    public void testPlay_exitsPausedState() {
        System.out.println("[TEST PausedState] play() -> deve uscire dallo stato di pausa");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        // chiamare play su qualsiasi traccia: deve uscire da PausedState
        ctx.play(track2);

        assertFalse(ctx.isPaused());
    }

    /**
     * @brief Verifica che play() riporti il player nello stato di riproduzione attiva (PlayingState).
     */
    @Test
    public void testPlay_setsPlayingState() {
        System.out.println("[TEST PausedState] play() -> deve riportare il player nello stato di riproduzione");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.play(track2);

        assertTrue(ctx.isPlaying());
    }

    /**
     * @brief Verifica che play() aggiorni la traccia corrente nel contesto con quella passata come parametro.
     */
    @Test
    public void testPlay_updatesCurrentTrack() {
        System.out.println("[TEST PausedState] play() -> deve aggiornare la traccia corrente nel contesto");

        // era in pausa su track1, chiamando play(track2): ci si aspetta track2 come traccia corrente
        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.play(track2);

        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che play() sulla stessa traccia messa in pausa esca comunque dallo stato di pausa.
     *        Questo è il tipico caso di "ripresa" dopo aver premuto pausa sulla stessa canzone.
     */
    @Test
    public void testPlay_sameTrack_exitsPausedState() {
        System.out.println("[TEST PausedState] play() sulla stessa traccia in pausa -> deve uscire dallo stato di pausa");

        // simula il caso in cui l'utente preme play sulla stessa canzone messa in pausa
        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.play(track1);

        // deve uscire dalla pausa e la traccia corrente deve rimanere track1
        assertFalse(ctx.isPaused());
        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che play() su una traccia senza audioSource non lanci eccezioni.
     *        Le tracce nei test non hanno audioSource impostato; play() deve gestire questo senza crashare.
     */
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

    /**
     * @brief Verifica che stop() esca dallo stato di pausa.
     */
    @Test
    public void testStop_exitsPausedState() {
        System.out.println("[TEST PausedState] stop() -> deve uscire dallo stato di pausa");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.stop();

        // dopo stop il contesto non deve più essere in pausa
        assertFalse(ctx.isPaused());
    }

    /**
     * @brief Verifica che stop() non lanci eccezioni.
     */
    @Test
    public void testStop_doesNotThrow() {
        System.out.println("[TEST PausedState] stop() -> non deve lanciare eccezioni");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        assertDoesNotThrow(() -> ctx.stop());
    }

    // -----------------------------------------------------------------------
    // Test per next() — navigazione dallo stato di pausa
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che next() esca dallo stato di pausa.
     */
    @Test
    public void testNext_exitsPausedState() {
        System.out.println("[TEST PausedState] next() -> deve uscire dallo stato di pausa");

        // la strategia restituirà track2 come traccia successiva
        PlayerContext ctx = pausedContextWith(track1, track2, null);
        ctx.next(queue, track1);

        assertFalse(ctx.isPaused());
    }

    /**
     * @brief Verifica che next() riporti il player nello stato di riproduzione attiva.
     */
    @Test
    public void testNext_setsPlayingState() {
        System.out.println("[TEST PausedState] next() -> deve riportare il player nello stato di riproduzione");

        PlayerContext ctx = pausedContextWith(track1, track2, null);
        ctx.next(queue, track1);

        assertTrue(ctx.isPlaying());
    }

    /**
     * @brief Verifica che next() rispetti la strategia corrente e aggiorni la traccia corrente.
     *        DummyStrategy è impostata per restituire track2: dopo next() la traccia corrente deve essere track2.
     */
    @Test
    public void testNext_updatesCurrentTrackUsingStrategy() {
        System.out.println("[TEST PausedState] next() -> deve aggiornare la traccia corrente usando la strategia");

        // DummyStrategy restituisce track2: ci si aspetta che diventi la traccia corrente
        PlayerContext ctx = pausedContextWith(track1, track2, null);
        ctx.next(queue, track1);

        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che next() senza traccia successiva disponibile (null) esca comunque dallo stato di pausa.
     *        Anche senza traccia successiva il player non deve rimanere bloccato in pausa.
     */
    @Test
    public void testNext_withNoNextTrack_exitsPausedState() {
        System.out.println("[TEST PausedState] next() senza traccia successiva -> deve uscire dalla pausa comunque");

        // null come next: la strategia segnala che non è disponibile alcuna traccia successiva
        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.next(queue, track1);

        // deve uscire dalla pausa anche in questo caso
        assertFalse(ctx.isPaused());
    }

    /**
     * @brief Verifica che next() non lanci eccezioni.
     */
    @Test
    public void testNext_doesNotThrow() {
        System.out.println("[TEST PausedState] next() -> non deve lanciare eccezioni");

        PlayerContext ctx = pausedContextWith(track1, track2, null);
        assertDoesNotThrow(() -> ctx.next(queue, track1));
    }

    // -----------------------------------------------------------------------
    // Test per previous() — navigazione all'indietro dallo stato di pausa
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che previous() esca dallo stato di pausa.
     */
    @Test
    public void testPrevious_exitsPausedState() {
        System.out.println("[TEST PausedState] previous() -> deve uscire dallo stato di pausa");

        // la strategia restituirà track1 come traccia precedente
        PlayerContext ctx = pausedContextWith(track2, null, track1);
        ctx.previous(queue, track2);

        assertFalse(ctx.isPaused());
    }

    /**
     * @brief Verifica che previous() riporti il player nello stato di riproduzione attiva.
     */
    @Test
    public void testPrevious_setsPlayingState() {
        System.out.println("[TEST PausedState] previous() -> deve riportare il player nello stato di riproduzione");

        PlayerContext ctx = pausedContextWith(track2, null, track1);
        ctx.previous(queue, track2);

        assertTrue(ctx.isPlaying());
    }

    /**
     * @brief Verifica che previous() rispetti la strategia corrente e aggiorni la traccia corrente.
     *        DummyStrategy è impostata per restituire track1: dopo previous() la traccia corrente deve essere track1.
     */
    @Test
    public void testPrevious_updatesCurrentTrackUsingStrategy() {
        System.out.println("[TEST PausedState] previous() -> deve aggiornare la traccia corrente usando la strategia");

        // era in pausa su track2, DummyStrategy restituisce track1 come precedente
        PlayerContext ctx = pausedContextWith(track2, null, track1);
        ctx.previous(queue, track2);

        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che previous() senza traccia precedente disponibile (null) esca comunque dallo stato di pausa.
     *        Anche senza traccia precedente il player non deve rimanere bloccato in pausa.
     */
    @Test
    public void testPrevious_withNoPreviousTrack_exitsPausedState() {
        System.out.println("[TEST PausedState] previous() senza traccia precedente -> deve uscire dalla pausa comunque");

        // null come previous: la strategia segnala che non è disponibile alcuna traccia precedente
        PlayerContext ctx = pausedContextWith(track2, null, null);
        ctx.previous(queue, track2);

        // deve uscire dalla pausa anche senza una traccia precedente
        assertFalse(ctx.isPaused());
    }

    /**
     * @brief Verifica che previous() non lanci eccezioni.
     */
    @Test
    public void testPrevious_doesNotThrow() {
        System.out.println("[TEST PausedState] previous() -> non deve lanciare eccezioni");

        PlayerContext ctx = pausedContextWith(track2, null, track1);
        assertDoesNotThrow(() -> ctx.previous(queue, track2));
    }
}
