package com.State;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.Playlist;
import com.Model.Track;
import com.Strategy.LoopPlaylistStrategy;
import com.Strategy.PlaybackContext;
import com.Strategy.SequentialStrategy;
import com.Strategy.ShuffleStrategy;

/**
 * @class PlayerContextPlaylistTest
 * @brief Classe di test per verificare la logica di riproduzione per le
 *        Playlist. Verifica che il PlayerContext interagisca correttamente con
 *        la Playlist utilizzando la giusta strategia di riproduzione e
 *        assicurando il corretto avanzamento tra i brani.
 */

public class PlayerContextPlaylistTest {
    private Playlist testPlaylist;
    private Track track1;
    private Track track2;

    /**
     * @brief Configura gli elementi utili per i test: contesto di riproduzione e
     *        una playlist popolata com delle tracce.
     */
    @BeforeEach
    void setUp() {
        testPlaylist = new Playlist("Playlist Test");
        track1 = new Track("A", "B", 2010, "C", 100, "D", "E", null);
        track2 = new Track("F", "G", 2010, "H", 100, "I", "J", null);
        testPlaylist.addTrack(track1);
        testPlaylist.addTrack(track2);

    }

    /**
     * @brief Verifica che l'avvio della riproduzione da una playlist vuota (o dallo
     *        stato iniziale) carichi correttamente il primo brano disponibile
     */
    @Test
    void testInitialPlayUpdateState() {
        PlaybackContext playbackContext = new PlaybackContext(new SequentialStrategy());
        PlayerContext context = new PlayerContext(playbackContext);
        context.setCurrentTrack(null);
        context.next(testPlaylist.getTracks(), null);
        Track firstTrack = context.getCurrentTrack();
        if (firstTrack != null) {
            context.play(firstTrack);
        }
        assertEquals(track1, context.getCurrentTrack(), "La prima traccia della playlist dovrebbe essere A");
        assertTrue(context.isPlaying(), "Il player dovrebbe trovarsi nello stato 'playingState'");

    }

    /**
     * @brief Verifica che, data una traccia in riproduzione, il comando 'next'
     *        aggiorni correttamente la traccia corrente alla successiva nella
     *        playlist.
     */
    @Test
    void testSequentialNextUpdateTrack() {
        PlaybackContext playbackContext = new PlaybackContext(new SequentialStrategy());
        PlayerContext context = new PlayerContext(playbackContext);
        context.setCurrentTrack(track1);
        context.play(track1);
        assertTrue(context.isPlaying());
        context.next(testPlaylist.getTracks(), track1);
        Track nextTrack = context.getCurrentTrack();
        assertEquals(track2, nextTrack, "La  traccia successiva della playlist dovrebbe essere F");

    }

    /**
     * @brief Verifica il comportamento alla fine della playlist in modalità
     *        sequenziale. Assicura che il player non esegua il loop automatico alla
     *        prima traccia, rimanendo bloccato sull'ultima.
     */
    @Test
    void testEndOfPlaylistSequential() {
        PlaybackContext playbackContext = new PlaybackContext(new SequentialStrategy());
        PlayerContext context = new PlayerContext(playbackContext);
        context.setCurrentTrack(track2);
        context.play(track2);
        assertTrue(context.isPlaying());
        context.next(testPlaylist.getTracks(), track2);
        Track afterLast = context.getCurrentTrack();
        assertTrue(afterLast == track2,
                "Alla fine della playlist modalità sequenziale, il next non deve tornare alla prima traccia");
    }

    /**
     * @brief Verifica il comportamento alla fine della playlist in modalità
     *        loop. Assicura che il player esegua il loop automatico alla
     *        prima traccia..
     */
    @Test
    void testEndOfPlaylistLoop() {
        PlaybackContext playbackContext = new PlaybackContext(new LoopPlaylistStrategy());
        PlayerContext context = new PlayerContext(playbackContext);
        context.setCurrentTrack(track2);
        context.play(track2);
        assertTrue(context.isPlaying());
        context.next(testPlaylist.getTracks(), track2);
        Track afterLast = context.getCurrentTrack();
        assertTrue(afterLast == track1,
                "Alla fine della playlist modalità loop, il next deve tornare alla prima traccia");
    }

    @Test
    void testShuffleStrategy() {
        PlaybackContext playbackContext = new PlaybackContext(new ShuffleStrategy());
        PlayerContext context = new PlayerContext(playbackContext);
        Track track3 = new Track("K", "L", 2010, "M", 100, "N", "O", null);
        testPlaylist.addTrack(track3);
        context.setCurrentTrack(track1);
        context.play(track1);
        assertTrue(context.isPlaying(), "Il player dovrebbe essere in riproduzione");
        context.next(testPlaylist.getTracks(), track1);
        Track nextTrack = context.getCurrentTrack();
        assertNotNull(nextTrack, "Il context deve aver aggiornato la traccia corrente");
        assertTrue(testPlaylist.getTracks().contains(nextTrack),
                "Il context deve aver selezionato una traccia della playlist");
        assertTrue(context.isPlaying(), "Il player deve mantenere lo stato di riproduzione dopo il passaggio");

    }

}
