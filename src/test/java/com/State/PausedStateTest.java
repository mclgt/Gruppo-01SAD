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
 *        del PlayerContext.
 *
 *        PausedState è lo stato in cui il player si trova quando la
 *        riproduzione è sospesa.
 *        I test verificano che:
 *        - pause() sia un no-op quando già in pausa (non cambia stato)
 *        - play() esca dalla pausa, riporti il player in PlayingState e
 *        aggiorni la traccia corrente
 *        - stop() esca dalla pausa tornando a PlayingState
 *        - next() e previous() escano dalla pausa e navighino rispettando la
 *        strategy corrente
 *
 * @author Christian
 * @see PausedState
 * @see PlayerContext
 * @see IPlayerState
 */
public class PausedStateTest {

    /**
     * @brief DummyStrategy che restituisce valori predefiniti per nextTrack() e
     *        previousTrack().
     *        Uso uno stub manuale senza framework di mocking, per coerenza con gli
     *        altri test della suite.
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
     * @brief Crea un PlayerContext con DummyStrategy, avvia la riproduzione su
     *        currentTrack
     *        e poi lo porta in stato di pausa.
     *        In questo modo ogni test parte già con il context in PausedState,
     *        pronto da testare.
     */
    private PlayerContext pausedContextWith(Track currentTrack, Track nextTrack, Track prevTrack) {
        PlaybackContext pb = new PlaybackContext(new DummyStrategy(nextTrack, prevTrack));
        PlayerContext ctx = new PlayerContext(pb);
        // avvio la riproduzione così ho una traccia corrente impostata
        ctx.play(currentTrack);
        // metto in pausa: PlayingState.pause() chiama setState(pausedState)
        ctx.pause();
        return ctx;
    }

    // -----------------------------------------------------------------------
    // Test di pause() - no-op quando già in pausa
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che pause() chiamato quando già in pausa sia un no-op:
     *        il player deve rimanere in pausa senza cambiare stato.
     */
    @Test
    public void testPause_whenAlreadyPaused_remainsPaused() {
        System.out.println("[TEST PausedState] pause() quando già in pausa -> deve rimanere in pausa");

        PlayerContext ctx = pausedContextWith(track1, null, null);

        // chiamo pause() una seconda volta: deve essere un no-op, lo stato non deve
        // cambiare
        ctx.pause();

        assertTrue(ctx.isPaused());
    }

    /**
     * @brief Verifica che pause() chiamato quando già in pausa non avvii la
     *        riproduzione per errore.
     */
    @Test
    public void testPause_whenAlreadyPaused_doesNotStartPlaying() {
        System.out.println("[TEST PausedState] pause() quando già in pausa -> non deve avviare la riproduzione");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.pause();

        // dopo un secondo pause() il player non deve risultare in riproduzione
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
    // Test di play() - uscita dalla pausa e ripristino della riproduzione
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che play() esca dallo stato di pausa: dopo la chiamata il
     *        player non deve essere più in pausa.
     */
    @Test
    public void testPlay_exitsPausedState() {
        System.out.println("[TEST PausedState] play() -> deve uscire dallo stato di pausa");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        // chiamo play su una traccia qualsiasi: deve uscire da PausedState
        ctx.play(track2);

        assertFalse(ctx.isPaused());
    }

    /**
     * @brief Verifica che play() riporti il player in stato di riproduzione attiva
     *        (PlayingState).
     */
    @Test
    public void testPlay_setsPlayingState() {
        System.out.println("[TEST PausedState] play() -> deve riportare il player in stato di riproduzione");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.play(track2);

        assertTrue(ctx.isPlaying());
    }

    /**
     * @brief Verifica che play() aggiorni la traccia corrente nel context con
     *        quella passata come parametro.
     */
    @Test
    public void testPlay_updatesCurrentTrack() {
        System.out.println("[TEST PausedState] play() -> deve aggiornare la traccia corrente nel context");

        // ero in pausa su track1, chiamo play(track2): mi aspetto track2 come traccia
        // corrente
        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.play(track2);

        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che play() sulla stessa traccia che era in pausa esca
     *        ugualmente dalla pausa.
     *        Questo è il caso tipico del "riprendi" dopo aver premuto pausa sulla
     *        stessa canzone.
     */
    @Test
    public void testPlay_sameTrack_exitsPausedState() {
        System.out.println("[TEST PausedState] play() sulla stessa traccia in pausa -> deve uscire dalla pausa");

        // questo simula il caso in cui l'utente preme play sulla stessa canzone che era
        // in pausa
        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.play(track1);

        // deve uscire dalla pausa e la traccia corrente deve rimanere track1
        assertFalse(ctx.isPaused());
        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che play() su una traccia senza audioSource non lanci
     *        eccezioni.
     *        Le tracce nei test non hanno un audioSource impostato, play() deve
     *        gestirlo senza crash.
     */
    @Test
    public void testPlay_trackWithoutAudioSource_doesNotThrow() {
        System.out.println("[TEST PausedState] play() su traccia senza audioSource -> non deve lanciare eccezioni");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        // track2 non ha un audioSource impostato (null): play() non deve crashare
        assertDoesNotThrow(() -> ctx.play(track2));
    }

    // -----------------------------------------------------------------------
    // Test di stop()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che stop() esca dallo stato di pausa riportando il context a
     *        PlayingState.
     */
    @Test
    public void testStop_exitsPausedState() {
        System.out.println("[TEST PausedState] stop() -> deve uscire dallo stato di pausa");

        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.stop();

        // dopo stop il context non deve essere più in pausa
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
    // Test di next() - navigazione dalla pausa
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
     * @brief Verifica che next() riporti il player in stato di riproduzione attiva.
     */
    @Test
    public void testNext_setsPlayingState() {
        System.out.println("[TEST PausedState] next() -> deve riportare il player in stato di riproduzione");

        PlayerContext ctx = pausedContextWith(track1, track2, null);
        ctx.next(queue, track1);

        assertTrue(ctx.isPlaying());
    }

    /**
     * @brief Verifica che next() rispetti la strategy corrente e aggiorni la
     *        traccia corrente.
     *        La DummyStrategy è impostata per restituire track2: dopo next() la
     *        corrente deve essere track2.
     */
    @Test
    public void testNext_updatesCurrentTrackUsingStrategy() {
        System.out.println("[TEST PausedState] next() -> deve aggiornare la traccia corrente usando la strategy");

        // la DummyStrategy restituisce track2: mi aspetto che diventi la traccia
        // corrente
        PlayerContext ctx = pausedContextWith(track1, track2, null);
        ctx.next(queue, track1);

        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che next() senza traccia successiva disponibile (null) esca
     *        comunque dalla pausa.
     *        Anche se non c'è una traccia successiva il player non deve restare
     *        bloccato in pausa.
     */
    @Test
    public void testNext_withNoNextTrack_exitsPausedState() {
        System.out.println("[TEST PausedState] next() senza traccia successiva -> deve uscire dalla pausa comunque");

        // null come prossima: la strategia dice che non c'è nessuna traccia successiva
        PlayerContext ctx = pausedContextWith(track1, null, null);
        ctx.next(queue, track1);

        // anche in questo caso deve uscire dalla pausa
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
    // Test di previous() - navigazione a ritroso dalla pausa
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
     * @brief Verifica che previous() riporti il player in stato di riproduzione
     *        attiva.
     */
    @Test
    public void testPrevious_setsPlayingState() {
        System.out.println("[TEST PausedState] previous() -> deve riportare il player in stato di riproduzione");

        PlayerContext ctx = pausedContextWith(track2, null, track1);
        ctx.previous(queue, track2);

        assertTrue(ctx.isPlaying());
    }

    /**
     * @brief Verifica che previous() rispetti la strategy corrente e aggiorni la
     *        traccia corrente.
     *        La DummyStrategy è impostata per restituire track1: dopo previous() la
     *        corrente deve essere track1.
     */
    @Test
    public void testPrevious_updatesCurrentTrackUsingStrategy() {
        System.out.println("[TEST PausedState] previous() -> deve aggiornare la traccia corrente usando la strategy");

        // ero in pausa su track2, la DummyStrategy restituisce track1 come precedente
        PlayerContext ctx = pausedContextWith(track2, null, track1);
        ctx.previous(queue, track2);

        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che previous() senza traccia precedente disponibile (null)
     *        esca comunque dalla pausa.
     *        Anche se non c'è una traccia precedente il player non deve restare
     *        bloccato in pausa.
     */
    @Test
    public void testPrevious_withNoPreviousTrack_exitsPausedState() {
        System.out
                .println("[TEST PausedState] previous() senza traccia precedente -> deve uscire dalla pausa comunque");

        // null come precedente: la strategia dice che non c'è nessuna traccia
        // precedente
        PlayerContext ctx = pausedContextWith(track2, null, null);
        ctx.previous(queue, track2);

        // anche senza traccia precedente deve uscire dalla pausa
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
