package com.Strategy;

import com.Model.Track;
import com.Model.TrackFactory;
import com.Model.MockTrackFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class LoopPlaylistStrategyTest
 * @brief Classe di test per LoopPlaylistStrategy.
 *        Verifica che la navigazione con loop nella playlist effettui il wrap-around correttamente
 *        a entrambe le estremità della coda, e gestisca casi limite come
 *        la traccia corrente null o una coda con un solo elemento.
 */
public class LoopPlaylistStrategyTest {

    private LoopPlaylistStrategy strategy;
    private Track track1;
    private Track track2;
    private Track track3;
    private TrackFactory factory;

    /**
     * @brief Inizializza la strategia e tre tracce dummy prima di ogni test.
     */
    @BeforeEach
    void setUp() {
        factory = new MockTrackFactory();
        strategy = new LoopPlaylistStrategy();
        track1 = factory.createTrack("Canzone A", "Artista A", 2000, "Pop", 200, "Album A", "dummy1.mp3",
                null);
        track2 = factory.createTrack("Canzone B", "Artista B", 2001, "Rock", 180, "Album B", "dummy2.mp3",
                null);
        track3 = factory.createTrack("Canzone C", "Artista C", 2002, "Jazz", 210, "Album C", "dummy3.mp3",
                null);
    }

    // -----------------------------------------------------------------------
    // Test per nextTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that nextTrack() returns the first track when current is
     *        null.
     */
    @Test
    void nextTrack_currentNull_returnsFirstTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track1, strategy.nextTrack(queue, null));
    }

    /**
     * @brief Verifies that nextTrack() returns the same track when the queue
     *        contains only one element.
     */
    @Test
    void nextTrack_singleTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1);
        assertSame(track1, strategy.nextTrack(queue, track1));
    }

    /**
     * @brief Verifies that nextTrack() returns the second track when current is the
     *        first.
     */
    @Test
    void nextTrack_firstTrack_returnsSecondTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track2, strategy.nextTrack(queue, track1));
    }

    /**
     * @brief Verifies that nextTrack() wraps around and returns the first track
     *        when current is the last.
     */
    @Test
    void nextTrack_lastTrack_returnsFirstTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track1, strategy.nextTrack(queue, track3));
    }

    // -----------------------------------------------------------------------
    // Test per previousTrack()
    // -----------------------------------------------------------------------

    /**
     * @brief Verifies that previousTrack() returns the first track when current is
     *        null.
     */
    @Test
    void previousTrack_currentNull_returnsFirstTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track1, strategy.previousTrack(queue, null));
    }

    /**
     * @brief Verifies that previousTrack() returns the same track when the queue
     *        contains only one element.
     */
    @Test
    void previousTrack_singleTrack_returnsSameTrack() {
        List<Track> queue = List.of(track1);
        assertSame(track1, strategy.previousTrack(queue, track1));
    }

    /**
     * @brief Verifies that previousTrack() returns the first track when current is
     *        the second.
     */
    @Test
    void previousTrack_secondTrack_returnsFirstTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track1, strategy.previousTrack(queue, track2));
    }

    /**
     * @brief Verifies that previousTrack() wraps around and returns the last track
     *        when current is the first.
     */
    @Test
    void previousTrack_firstTrack_returnsLastTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        assertSame(track3, strategy.previousTrack(queue, track1));
    }

}
