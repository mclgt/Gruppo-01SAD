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
 * @brief Test per PlayingState: verifica il comportamento dello stato di riproduzione attiva (ovvero una canzone è in riproduzione) del PlayerContext.
 *
 *        PlayingState è lo stato in cui il player si trova durante la riproduzione.
 *        I test verificano che play() aggiorni correttamente la traccia corrente nel
 *        PlayerContext, e che next()/previous() navighino tra le diverse tracce; facendola partire a sua volta,
 *        oppure invochino stop() quando non esiste una traccia disponibile.
 *
 *
 * @author Christian
 * @see PlayingState
 * @see PlayerContext
 * @see IPlayerState
 */
public class PlayingStateTest {

    /**
     * @brief Per semplificare i test, definiamo una DummyStrategy che restituisce 
     *        valori predefiniti per nextTrack() e previousTrack().
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
        track1 = new Track("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "dummy1.mp3");
        track2 = new Track("Canzone B", "Artista B", 2001, "Rock", 210, "Album B", "dummy2.mp3");
        track3 = new Track("Canzone C", "Artista C", 2002, "Jazz", 220, "Album C", "dummy3.mp3");
        queue = Arrays.asList(track1, track2, track3);
    }

    /** @brief Crea un PlayerContext con una DummyStrategy che restituisce i valori forniti. */
    private PlayerContext contextWith(Track nextTrack, Track prevTrack) {
        PlaybackContext pb = new PlaybackContext(new DummyStrategy(nextTrack, prevTrack));
        return new PlayerContext(pb);
    }




    // Test per il metodo play()

    /**
     * @brief Verifica che play() imposti la traccia corrente nel context.
     */
    @Test
    public void testPlay_setsCurrentTrack() {
        //stampa per identificare il test in output
        System.out.println("[TEST PlayingState] play() -> deve impostare la traccia corrente nel context");

        // do come parametri null, null perchè voglio solo testare che play() aggiorni la traccia attualmente in riproduzione
        PlayerContext ctx = contextWith(null, null);

        // creo direttamente lo stato PlayingState passandogli il context
        PlayingState state = new PlayingState(ctx);

        // eseguo play su track1
        state.play(track1);

        // verifico che il context abbia registrato track1 come traccia corrente
        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che una seconda chiamata a play() aggiorni la traccia corrente.
     */
    @Test
    public void testPlay_subsequentCall_updatesCurrentTrack() {
        //stampa per identificare il test in output
        System.out.println("[TEST PlayingState] play() due volte -> la traccia corrente deve essere quella dell'ultima chiamata");

        // null, null: non mi serve navigazione, verifico solo la sovrascrittura della traccia corrente
        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);

        // prima riproduzione: imposta track1 come corrente
        state.play(track1);
        // seconda riproduzione: deve sovrascrivere la traccia con track2 altrimenti è un errore
        state.play(track2);

        // la traccia corrente deve essere l'ultima riprodotta, ovvero track2 e nessun'altra
        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che play() su tracce diverse aggiorni sempre la traccia corrente.
     */
    @Test
    public void testPlay_thirdTrack_updatesCurrentTrack() {
        //stampa per identificare il test in output
        System.out.println("[TEST PlayingState] play() su traccia diversa -> deve aggiornare la traccia corrente");

        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);

        // riproduco track1 prima, poi salto a track3 (saltando track2)
        state.play(track1);
        state.play(track3);

        // la corrente deve essere track3 e non la 2, quindi devo poter passare da una traccia ad un'altra senza problemi, anche se ne salto alcune
        assertEquals(track3, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che play() con una traccia priva di audioSource non lanci eccezioni.
     */
    @Test
    public void testPlay_trackWithoutAudioSource_doesNotThrow() {
        //stampa per identificare il test in output
        System.out.println("[TEST PlayingState] play() su traccia senza audioSource -> non deve lanciare eccezioni");

        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);

        // track1 non ha un audioSource impostato (null): play() non deve crashare
        assertDoesNotThrow(() -> state.play(track1));
    }

    // -----------------------------------------------------------------------
    // Test di next()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che next() con una traccia successiva 
     *        disponibile la imposti come corrente.
     */
    @Test
    public void testNext_withValidNext_setsNextTrackAsCurrent() {
        //stampa per identificare il test in output
        System.out.println("[TEST PlayingState] next() con traccia disponibile -> deve impostare la traccia successiva come corrente");

        // la strategia restituirà track2 come prossima traccia; null per la precedente (non serve)
        PlayerContext ctx = contextWith(track2, null);
        PlayingState state = new PlayingState(ctx);
        // imposto manualmente track1 come traccia corrente di partenza
        ctx.setCurrentTrack(track1);

        // chiedo di avanzare alla prossima traccia
        state.next(queue, track1);

        // la strategia ha restituito track2, quindi il context deve averla impostata come corrente
        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che next() con una traccia successiva disponibile avanzi di due passi se
     *        chiamato consecutivamente (la strategia stub restituisce sempre lo stesso valore).
     */
    @Test
    public void testNext_calledTwice_usesStrategyResultEachTime() {
        //stampa per identificare il test in output
        System.out.println("[TEST PlayingState] next() chiamato due volte -> ogni chiamata usa il risultato della strategia");

        // la DummyStrategy restituisce sempre track2 come prossima, indipendentemente dalla corrente
        PlayerContext ctx = contextWith(track2, null);
        PlayingState state = new PlayingState(ctx);
        ctx.setCurrentTrack(track1);

        // primo next: da track1 -> track2
        state.next(queue, track1);
        assertEquals(track2, ctx.getCurrentTrack());

        // secondo next: la stub restituisce ancora track2, quindi rimane su track2
        state.next(queue, track2);
        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che next() senza traccia successiva (null) non modifichi la traccia corrente.
     */
    @Test
    public void testNext_withNoNext_currentTrackUnchanged() {
        //stampa per identificare il test in output
        System.out.println("[TEST PlayingState] next() senza traccia successiva -> la traccia corrente non deve cambiare");

        // null, null: la strategia dice che non c'è nessuna traccia successiva disponibile
        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);
        ctx.setCurrentTrack(track1);

        // chiamo next, ma la strategia restituisce null -> PlayingState chiama stop()
        state.next(queue, track1);

        // track1 deve rimanere la traccia corrente perché non c'era una prossima
        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che next() con traccia successiva non lanci eccezioni.
     */
    @Test
    public void testNext_withValidNext_doesNotThrow() {
        //stampa per identificare il test in output
        System.out.println("[TEST PlayingState] next() con traccia disponibile -> non deve lanciare eccezioni");

        // la strategia restituisce track2 come prossima: non deve esplodere nulla
        PlayerContext ctx = contextWith(track2, null);
        PlayingState state = new PlayingState(ctx);

        assertDoesNotThrow(() -> state.next(queue, track1));
    }

    // -----------------------------------------------------------------------
    // Test di previous()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che previous() con una traccia precedente disponibile la imposti come corrente.
     */
    @Test
    public void testPrevious_withValidPrevious_setsPreviousTrackAsCurrent() {
        //stampa per identificare il test in output
        System.out.println("[TEST PlayingState] previous() con traccia disponibile -> deve impostare la traccia precedente come corrente");

        // null per next (non serve), track1 come traccia precedente restituita dalla strategia
        PlayerContext ctx = contextWith(null, track1);
        PlayingState state = new PlayingState(ctx);
        // parto da track2 come traccia corrente
        ctx.setCurrentTrack(track2);

        // chiedo di tornare alla traccia precedente
        state.previous(queue, track2);

        // la strategia ha restituito track1, quindi deve diventare la traccia corrente
        assertEquals(track1, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che previous() senza traccia precedente (null) non modifichi la traccia corrente.
     */
    @Test
    public void testPrevious_withNoPrevious_currentTrackUnchanged() {
        //stampa per identificare il test in output
        System.out.println("[TEST PlayingState] previous() senza traccia precedente -> la traccia corrente non deve cambiare");

        // null, null: la strategia dice che non c'è nessuna traccia precedente disponibile
        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);
        ctx.setCurrentTrack(track2);

        // chiamo previous, ma la strategia restituisce null -> PlayingState chiama stop()
        state.previous(queue, track2);

        // track2 deve rimanere la traccia corrente perché non c'era una precedente
        assertEquals(track2, ctx.getCurrentTrack());
    }

    /**
     * @brief Verifica che previous() con traccia precedente non lanci eccezioni.
     */
    @Test
    public void testPrevious_withValidPrevious_doesNotThrow() {
        //stampa per identificare il test in output
        System.out.println("[TEST PlayingState] previous() con traccia disponibile -> non deve lanciare eccezioni");

        // null per next, track1 come precedente: non deve esplodere nulla
        PlayerContext ctx = contextWith(null, track1);
        PlayingState state = new PlayingState(ctx);

        assertDoesNotThrow(() -> state.previous(queue, track2));
    }

    /**
     * @brief Verifica che previous() senza traccia precedente non lanci eccezioni.
     */
    @Test
    public void testPrevious_withNoPrevious_doesNotThrow() {
        //stampa per identificare il test in output
        System.out.println("[TEST PlayingState] previous() senza traccia precedente -> non deve lanciare eccezioni");

        // null, null: nessuna traccia precedente, ma non deve esplodere
        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);

        assertDoesNotThrow(() -> state.previous(queue, track1));
    }

    // -----------------------------------------------------------------------
    // Test di pause() e stop()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che pause() non lanci eccezioni (comportamento attualmente no-op).
     */
    @Test
    public void testPause_doesNotThrow() {
        //stampa per identificare il test in output
        System.out.println("[TEST PlayingState] pause() -> non deve lanciare eccezioni");

        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);

        // pause() è attualmente no-op in PlayingState: verifichiamo che non lanci eccezioni
        assertDoesNotThrow(() -> state.pause());
    }

    /**
     * @brief Verifica che stop() non lanci eccezioni (comportamento attualmente no-op).
     */
    @Test
    public void testStop_doesNotThrow() {
        //stampa per identificare il test in output
        System.out.println("[TEST PlayingState] stop() -> non deve lanciare eccezioni");

        PlayerContext ctx = contextWith(null, null);
        PlayingState state = new PlayingState(ctx);

        // stop() è attualmente no-op in PlayingState: verifichiamo che non lanci eccezioni
        assertDoesNotThrow(() -> state.stop());
    }
}
