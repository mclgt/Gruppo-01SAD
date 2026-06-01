package com.Strategy;

import com.Model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/***
 * @brief Classe di test per SequentialStrategy.
 *        Verifica che la navigazione sequenziale avanti e indietro
 *        nella coda si comporti correttamente nei casi limite:
 *        fine coda, inizio coda e brano non presente in coda.
 */
public class SequentialStrategyTest {

    private SequentialStrategy strategy;
    private Track track1;
    private Track track2;
    private Track track3;
    private List<Track> queue;

    @BeforeEach
    public void setUp() {
        strategy = new SequentialStrategy();

        track1 = new Track("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "");
        track2 = new Track("Canzone B", "Artista B", 2001, "Pop", 210, "Album B", "");
        track3 = new Track("Canzone C", "Artista C", 2002, "Pop", 220, "Album C", "");

        queue = Arrays.asList(track1, track2, track3);
    }


    // Questi test servono per verificare se SequentialStrategy
    // gestisce correttamente i casi in cui si punta ad un brano successivo.
    @Test
    public void testNextTrack_fromFirst_returnsSecond() {
        assertEquals(track2, strategy.nextTrack(queue, track1));
    }

    @Test
    public void testNextTrack_fromMiddle_returnsThird() {
        assertEquals(track3, strategy.nextTrack(queue, track2));
    }

    @Test
    public void testNextTrack_fromLast_returnsNull() {
        assertNull(strategy.nextTrack(queue, track3));
    }

    @Test
    public void testNextTrack_trackNotInQueue_returnsNull() {
        Track outsider = new Track("Ghost", "Nobody", 1999, "Jazz", 180, "None", "");
        assertNull(strategy.nextTrack(queue, outsider));
    }

    // Questi test servono per verificare se SequentialStrategy 
    //gestisce correttamente i casi in cui si punta ad un brano precedente.

    @Test
    public void testPreviousTrack_fromLast_returnsMiddle() {
        assertEquals(track2, strategy.previousTrack(queue, track3));
    }

    @Test
    public void testPreviousTrack_fromMiddle_returnsFirst() {
        assertEquals(track1, strategy.previousTrack(queue, track2));
    }

    @Test
    public void testPreviousTrack_fromFirst_returnsNull() {
        assertNull(strategy.previousTrack(queue, track1));
    }

    @Test
    public void testPreviousTrack_trackNotInQueue_returnsNull() {
        Track outsider = new Track("Ghost", "Nobody", 1999, "Jazz", 180, "None", "");
        assertNull(strategy.previousTrack(queue, outsider));
    }
}
