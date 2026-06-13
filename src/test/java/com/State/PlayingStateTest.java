package com.State;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.Track;
import com.Model.TrackFactory;
import com.Model.MockTrackFactory;
import com.Strategy.IPlaybackStrategy;
import com.Strategy.PlaybackContext;

/**
 * @brief Tests for PlayingState: verifies the behavior of the active playback
 *        state
 *        (i.e., a song is currently playing) in PlayerContext.
 *
 *        PlayingState is the state the player is in during playback.
 *        Tests verify that play() correctly updates the current track in
 *        PlayerContext,
 *        that next()/previous() navigate between tracks and start the new one,
 *        or call stop() when no track is available.
 *
 * @author Christian
 * @see PlayingState
 * @see PlayerContext
 * @see IPlayerState
 */
public class PlayingStateTest {

    /**
     * @brief DummyStrategy that returns predefined values for nextTrack() and
     *        previousTrack(),
     *        used to simplify tests without a mocking framework.
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

    /** @brief Crea un PlayerContext con una DummyStrategy che restituisce i valori forniti. */
    private PlayerContext contextWith(Track nextTrack, Track prevTrack) {
        PlaybackContext pb = new PlaybackContext(new DummyStrategy(nextTrack, prevTrack));
        return new PlayerContext(pb);
    }

    // -----------------------------------------------------------------------
    // Test per play()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che play() imposti la traccia corrente nel contesto.
     */
    @Test
    public void testPlay_setsCurrentTrack() {
        System.out.println("[TEST PlayingState] play() -> deve impostare la traccia corrente nel contesto");

        // null, null: si testa solo che play() aggiorni la traccia in riproduzione
        PlayerContext ctx = contextWith(null, null);

        // crea PlayingState direttamente, passando il contesto
        PlayingState state = new PlayingState(ctx);

        state.play(track1);

        // verifica che il contesto abbia registrato track1 come traccia corrente
        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che una seconda chiamata a play() aggiorni la traccia corrente.
     */
    @Test
    public void testPlay_subsequentCall_updatesCurrentTrack() {
        System.out.println("[TEST PlayingState] play() chiamata due volte -> la traccia corrente deve essere l'ultima riprodotta");

        // null, null: nessuna navigazione necessaria, si testa solo la sovrascrittura della traccia
        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);

        // prima chiamata: imposta track1 come corrente
        state.play(track1);
        // seconda chiamata: deve sovrascrivere con track2
        state.play(track2);

        // la traccia corrente deve essere l'ultima riprodotta, ovvero track2
        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that play() on different tracks always updates the current
     *        track.
     */
    @Test
    public void testPlay_thirdTrack_updatesCurrentTrack() {
        System.out.println("[TEST PlayingState] play() su una traccia diversa -> deve aggiornare la traccia corrente");

        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);

        // riproduce prima track1, poi salta a track3 (saltando track2)
        state.play(track1);
        state.play(track3);

        // la corrente deve essere track3; saltare in modo non sequenziale deve funzionare correttamente
        assertEquals(track3, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che play() su una traccia senza audioSource non lanci eccezioni.
     */
    @Test
    public void testPlay_trackWithoutAudioSource_doesNotThrow() {
        System.out.println("[TEST PlayingState] play() su traccia senza audioSource -> non deve lanciare eccezioni");

        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);

        // track1 non ha audioSource (null): play() non deve crashare
        assertDoesNotThrow(() -> state.play(track1));
    }

    // -----------------------------------------------------------------------
    // Test per next()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that next() with a valid next track sets it as the current
     *        track.
     */
    @Test
    public void testNext_withValidNext_setsNextTrackAsCurrent() {
        System.out.println("[TEST PlayingState] next() con traccia valida -> deve impostare la traccia successiva come corrente");

        // la strategia restituisce track2 come traccia successiva; null per previous (non necessario)
        PlayerContext ctx = contextWith(track2, null);
        PlayingState state = new PlayingState(ctx);
        // imposta manualmente track1 come traccia corrente di partenza
        ctx.setCurrentTrack(track1);

        state.next(queue, track1);

        // la strategia ha restituito track2, quindi il contesto deve averla impostata come corrente
        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that calling next() twice uses the strategy result each time.
     *        The stub always returns the same value, so the current track
     *        stabilizes on it.
     */
    @Test
    public void testNext_calledTwice_usesStrategyResultEachTime() {
        System.out.println("[TEST PlayingState] next() chiamata due volte -> ogni chiamata usa il risultato della strategia");

        // DummyStrategy restituisce sempre track2 come next, indipendentemente dal corrente
        PlayerContext ctx = contextWith(track2, null);
        PlayingState state = new PlayingState(ctx);
        ctx.setCurrentTrack(track1);

        // primo next: track1 -> track2
        state.next(queue, track1);
        assertEquals(track2, ctx.getCurrentTrack());

        // secondo next: lo stub restituisce di nuovo track2, quindi rimane su track2
        state.next(queue, track2);
        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that next() with no next track (null) does not change the
     *        current track.
     */
    @Test
    public void testNext_withNoNext_currentTrackUnchanged() {
        System.out.println("[TEST PlayingState] next() senza traccia successiva -> la traccia corrente non deve cambiare");

        // null, null: la strategia segnala che non è disponibile alcuna traccia successiva
        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);
        ctx.setCurrentTrack(track1);

        // next() chiamato ma la strategia restituisce null -> PlayingState non aggiorna la traccia
        state.next(queue, track1);

        // track1 deve rimanere corrente perché non c'era traccia successiva
        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that next() with a valid next track does not throw any
     *        exception.
     */
    @Test
    public void testNext_withValidNext_doesNotThrow() {
        System.out.println("[TEST PlayingState] next() con traccia valida -> non deve lanciare eccezioni");

        // la strategia restituisce track2 come next: non deve lanciare eccezioni
        PlayerContext ctx = contextWith(track2, null);
        PlayingState state = new PlayingState(ctx);

        assertDoesNotThrow(() -> state.next(queue, track1));
    }

    // -----------------------------------------------------------------------
    // Test per previous()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that previous() with a valid previous track sets it as the
     *        current track.
     */
    @Test
    public void testPrevious_withValidPrevious_setsPreviousTrackAsCurrent() {
        System.out.println("[TEST PlayingState] previous() con traccia valida -> deve impostare la traccia precedente come corrente");

        // null per next (non necessario), track1 come previous restituito dalla strategia
        PlayerContext ctx = contextWith(null, track1);
        PlayingState state = new PlayingState(ctx);
        // parte da track2 come corrente
        ctx.setCurrentTrack(track2);

        state.previous(queue, track2);

        // la strategia ha restituito track1, quindi deve diventare la traccia corrente
        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that previous() with no previous track (null) does not change
     *        the current track.
     */
    @Test
    public void testPrevious_withNoPrevious_currentTrackUnchanged() {
        System.out.println("[TEST PlayingState] previous() senza traccia precedente -> la traccia corrente non deve cambiare");

        // null, null: la strategia segnala che non è disponibile alcuna traccia precedente
        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);
        ctx.setCurrentTrack(track2);

        // previous() chiamato ma la strategia restituisce null -> PlayingState non aggiorna la traccia
        state.previous(queue, track2);

        // track2 deve rimanere corrente perché non c'era traccia precedente
        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifies that previous() with a valid previous track does not throw
     *        any exception.
     */
    @Test
    public void testPrevious_withValidPrevious_doesNotThrow() {
        System.out.println("[TEST PlayingState] previous() con traccia valida -> non deve lanciare eccezioni");

        // null per next, track1 come previous: non deve lanciare eccezioni
        PlayerContext ctx = contextWith(null, track1);
        PlayingState state = new PlayingState(ctx);

        assertDoesNotThrow(() -> state.previous(queue, track2));
    }

    /**
     * @brief Verifies that previous() with no previous track does not throw any
     *        exception.
     */
    @Test
    public void testPrevious_withNoPrevious_doesNotThrow() {
        System.out.println("[TEST PlayingState] previous() senza traccia precedente -> non deve lanciare eccezioni");

        // null, null: nessuna traccia precedente, ma non deve lanciare eccezioni
        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);

        assertDoesNotThrow(() -> state.previous(queue, track1));
    }

    // -----------------------------------------------------------------------
    // Test per pause() e stop()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that pause() does not throw any exception (currently a no-op
     *        in PlayingState).
     */
    @Test
    public void testPause_doesNotThrow() {
        System.out.println("[TEST PlayingState] pause() -> non deve lanciare eccezioni");

        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);

        // pause() è attualmente un no-op in PlayingState: verifica che non lanci eccezioni
        assertDoesNotThrow(() -> state.pause());
    }

    /**
     * @brief Verifies that stop() does not throw any exception (currently a no-op
     *        in PlayingState).
     */
    @Test
    public void testStop_doesNotThrow() {
        System.out.println("[TEST PlayingState] stop() -> non deve lanciare eccezioni");

        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);

        // stop() è attualmente un no-op in PlayingState: verifica che non lanci eccezioni
        assertDoesNotThrow(() -> state.stop());
    }
}
