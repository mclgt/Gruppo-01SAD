package com.Strategy;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.Track;

/**
 * @class PlaybackContextTest
 * @brief Classe di test per PlaybackContext.
 *        Verifica che il Context deleghi correttamente alle strategie
 *        e che il cambio di strategia a runtime funzioni come previsto.
 *        Le strategie utilizzate sono stub dummy scritti a mano, senza framework di mocking.
 */
public class PlaybackContextTest {

    /**
     * @brief Stub strategy A: restituisce sempre una traccia successiva e una precedente fisse.
     *        Usata per verificare che il Context deleghi effettivamente le chiamate alla strategia.
     */
    private static class DummyStrategyA implements IPlaybackStrategy {
        private final Track fixedNext;
        private final Track fixedPrevious;

        DummyStrategyA(Track fixedNext, Track fixedPrevious) {
            this.fixedNext = fixedNext;
            this.fixedPrevious = fixedPrevious;
        }

        @Override
        public Track nextTrack(List<Track> queue, Track current) {
            return fixedNext;
        }

        @Override
        public Track previousTrack(List<Track> queue, Track current) {
            return fixedPrevious;
        }
    }

    /**
     * @brief Stub strategy B: restituisce sempre null, simulando la fine/inizio della coda.
     *        Usata per verificare il comportamento dopo un cambio di strategia a runtime.
     */
    private static class DummyStrategyB implements IPlaybackStrategy {
        @Override
        public Track nextTrack(List<Track> queue, Track current) {
            return null;
        }

        @Override
        public Track previousTrack(List<Track> queue, Track current) {
            return null;
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
        track1 = new Track("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "dummy1.mp3",null);
        track2 = new Track("Canzone B", "Artista B", 2001, "Pop", 210, "Album B", "dummy2.mp3",null);
        track3 = new Track("Canzone C", "Artista C", 2002, "Pop", 220, "Album C", "dummy3.mp3",null);
        queue = Arrays.asList(track1, track2, track3);
    }

    // -----------------------------------------------------------------------
    // Test per il costruttore e getStrategy()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che la strategia passata al costruttore venga memorizzata
     *        e restituita da getStrategy().
     */
    @Test
    public void testConstructor_strategyIsStored() {
        DummyStrategyA strategyA = new DummyStrategyA(track2, track1);
        PlaybackContext context = new PlaybackContext(strategyA);
        assertEquals(strategyA, context.getStrategy());
    }

    // -----------------------------------------------------------------------
    // Test per setStrategy()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che setStrategy() sostituisca la strategia corrente a runtime.
     */
    @Test
    public void testSetStrategy_replacesStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        DummyStrategyB strategyB = new DummyStrategyB();
        context.setStrategy(strategyB);
        assertEquals(strategyB, context.getStrategy());
    }

    // -----------------------------------------------------------------------
    // Test per nextTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che nextTrack() deleghi la chiamata alla strategia corrente.
     */
    @Test
    public void testNextTrack_delegatesToStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        assertEquals(track2, context.nextTrack(queue, track1));
    }

    /**
     * @brief Verifica che nextTrack() utilizzi la nuova strategia dopo un cambio a runtime.
     */
    @Test
    public void testNextTrack_afterStrategyChange_usesNewStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        context.setStrategy(new DummyStrategyB());
        assertNull(context.nextTrack(queue, track1));
    }

    // -----------------------------------------------------------------------
    // Test per previousTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifica che previousTrack() deleghi la chiamata alla strategia corrente.
     */
    @Test
    public void testPreviousTrack_delegatesToStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        assertEquals(track1, context.previousTrack(queue, track2));
    }

    /**
     * @brief Verifica che previousTrack() utilizzi la nuova strategia dopo un cambio a runtime.
     */
    @Test
    public void testPreviousTrack_afterStrategyChange_usesNewStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        context.setStrategy(new DummyStrategyB());
        assertNull(context.previousTrack(queue, track2));
    }
}
