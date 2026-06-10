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
 * @brief Classe di test per verificare la logica di riproduzione con le Playlist.
 *        Verifica che PlayerContext interagisca correttamente con una Playlist,
 *        utilizzando la giusta strategia di riproduzione e assicurando il corretto avanzamento delle tracce.
 */
public class PlayerContextPlaylistTest {
    private Playlist testPlaylist;
    private Track track1;
    private Track track2;

    /**
     * @brief Inizializza le fixture di test: un contesto di riproduzione e una playlist popolata.
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
     * @brief Verifica che avviare la riproduzione da uno stato vuoto/iniziale carichi la prima traccia disponibile.
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
        assertTrue(context.isPlaying(), "Il player dovrebbe essere nello stato 'playingState'");
    }

    /**
     * @brief Verifica che, data una traccia in riproduzione, il comando 'next' avanzi correttamente
     *        la traccia corrente alla successiva nella playlist.
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
        assertEquals(track2, nextTrack, "La traccia successiva nella playlist dovrebbe essere F");
    }

    /**
     * @brief Verifica il comportamento alla fine della playlist in modalità sequenziale.
     *        Il player non deve ciclare automaticamente alla prima traccia.
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
                "Alla fine della playlist in modalità sequenziale, next non deve tornare alla prima traccia");
    }

    /**
     * @brief Verifica il comportamento alla fine della playlist in modalità loop.
     *        Il player deve tornare automaticamente alla prima traccia.
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
                "Alla fine della playlist in modalità loop, next deve tornare alla prima traccia");
    }

    /** @brief Verifica che la strategia shuffle selezioni una traccia valida dalla playlist e mantenga il player in riproduzione. */
    @Test
    void testShuffleStrategy() {
        PlaybackContext playbackContext = new PlaybackContext(new ShuffleStrategy());
        PlayerContext context = new PlayerContext(playbackContext);
        Track track3 = new Track("K", "L", 2010, "M", 100, "N", "O", null);
        testPlaylist.addTrack(track3);
        context.setCurrentTrack(track1);
        context.play(track1);
        assertTrue(context.isPlaying(), "Il player dovrebbe essere nello stato di riproduzione");
        context.next(testPlaylist.getTracks(), track1);
        Track nextTrack = context.getCurrentTrack();
        assertNotNull(nextTrack, "Il contesto deve aver aggiornato la traccia corrente");
        assertTrue(testPlaylist.getTracks().contains(nextTrack),
                "Il contesto deve aver selezionato una traccia dalla playlist");
        assertTrue(context.isPlaying(), "Il player deve rimanere nello stato di riproduzione dopo l'avanzamento");
    }

}
