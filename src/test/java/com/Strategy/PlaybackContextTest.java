package com.Strategy;

import com.Model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/***
 * @brief Classe di test per PlaybackContext.
 *        Verifica che il Context deleghi correttamente alle strategie
 *        e che il cambio di strategia a runtime funzioni come atteso.
 *        Le strategie usate sono stub dummy scritti a mano, senza framework di mocking.
 */
public class PlaybackContextTest {

    // Uso delle strategie dummy, nmon dipendenti da implementazioni reali
    //ma solo per testare il comportamento del Context.

    
    // Strategia A: restituisce sempre track1 come next e track2 come previous
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


    // Strategia B: restituisce sempre null (simula fine/inizio coda)
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



    //Setup prima di ogni test

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





    //Test sul meotodo  getStrategy e costruttore 

    @Test
    public void testConstructor_strategyIsStored() {
        DummyStrategyA strategyA = new DummyStrategyA(track2, track1);
        PlaybackContext context = new PlaybackContext(strategyA);
        assertEquals(strategyA, context.getStrategy());
    }

    //Test sul metodo setStrategy per verificare che la strategia venga effettivamente cambiata a runtime

    @Test
    public void testSetStrategy_replacesStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        DummyStrategyB strategyB = new DummyStrategyB();
        context.setStrategy(strategyB);
        assertEquals(strategyB, context.getStrategy());
    }




    //Tests per controllare che il Context deleghi nextTrack alla strategia corrente
    //e che venga fatto tutto il cambio di strategia a runtime.

    @Test
    public void testNextTrack_delegatesToStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        assertEquals(track2, context.nextTrack(queue, track1));
    }

    @Test
    public void testNextTrack_afterStrategyChange_usesNewStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        context.setStrategy(new DummyStrategyB());
        assertNull(context.nextTrack(queue, track1));
    }




    //Tests per controllare previousTrack, simili a quelli di nextTrack

    @Test
    public void testPreviousTrack_delegatesToStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        assertEquals(track1, context.previousTrack(queue, track2));
    }

    @Test
    public void testPreviousTrack_afterStrategyChange_usesNewStrategy() {
        PlaybackContext context = new PlaybackContext(new DummyStrategyA(track2, track1));
        context.setStrategy(new DummyStrategyB());
        assertNull(context.previousTrack(queue, track2));
    }
}
