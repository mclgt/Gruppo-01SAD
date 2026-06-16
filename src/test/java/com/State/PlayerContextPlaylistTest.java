package com.State;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.MockTrackFactory;
import com.Model.Playlist;
import com.Model.Track;
import com.Model.TrackFactory;
import com.Strategy.LoopPlaylistStrategy;
import com.Strategy.PlaybackContext;
import com.Strategy.SequentialStrategy;
import com.Strategy.ShuffleStrategy;

/**
 * @class PlayerContextPlaylistTest
 * @brief Test class for verifying playback logic with Playlists.
 *        Verifies that PlayerContext interacts correctly with a Playlist,
 *        using the right playback strategy and ensuring correct track
 *        advancement.
 */
public class PlayerContextPlaylistTest {
    private Playlist testPlaylist;
    private Track track1;
    private Track track2;
    private TrackFactory factory;

    /**
     * @brief Sets up the test fixtures: a playback context and a populated
     *        playlist.
     */
    @BeforeEach
    void setUp() {
        factory = new MockTrackFactory();
        testPlaylist = new Playlist("Playlist Test");
        track1 = factory.createTrack("A", "B", 2010, "C", 100, "D", "dummy1.mp3", null);
        track2 = factory.createTrack("F", "G", 2010, "H", 100, "I", "dummy2.mp3", null);
        testPlaylist.addTrack(track1);
        testPlaylist.addTrack(track2);
    }

    /**
     * @brief Verifies that starting playback from an empty/initial state loads the
     *        first available track.
     */
    @Test
    void testInitialPlayUpdateState() {
        PlaybackContext playbackContext = new PlaybackContext(new SequentialStrategy());
        playbackContext.setCurrentQueue(testPlaylist.getTracks(), track1);
        PlayerContext context = new PlayerContext(playbackContext);
        context.setCurrentTrack(testPlaylist.getTracks().get(0));
        //context.next();
        /*Track firstTrack = context.getCurrentTrack();
        if (firstTrack != null) {
            context.play(firstTrack);
        }*/
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
        playbackContext.setCurrentQueue(testPlaylist.getTracks(), track1);
        PlayerContext context = new PlayerContext(playbackContext);
        context.setCurrentTrack(track1);
        context.play(track1);
        assertTrue(context.isPlaying());
        context.next();
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
        playbackContext.setCurrentQueue(testPlaylist.getTracks(), track2);
        PlayerContext context = new PlayerContext(playbackContext);
        context.setCurrentTrack(track2);
        context.play(track2);
        assertTrue(context.isPlaying());
        context.next();
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
        playbackContext.setCurrentQueue(testPlaylist.getTracks(), track2);
        PlayerContext context = new PlayerContext(playbackContext);
        context.setCurrentTrack(track2);
        context.play(track2);
        assertTrue(context.isPlaying());
        context.next();
        Track afterLast = context.getCurrentTrack();
        assertTrue(afterLast == track1,
                "Alla fine della playlist in modalità loop, next deve tornare alla prima traccia");
    }

    /**
     * Verifies that the shuffle strategy picks a valid track from the playlist and
     * keeps the player playing.
     */
    @Test
    void testShuffleStrategy() {
        PlaybackContext playbackContext = new PlaybackContext(new ShuffleStrategy());
        PlayerContext context = new PlayerContext(playbackContext);
        Track track3 = factory.createTrack("K", "L", 2010, "M", 100, "N", "dummy3.mp3", null);
        testPlaylist.addTrack(track3);
        playbackContext.setCurrentQueue(testPlaylist.getTracks(), track3);
        context.setCurrentTrack(track1);
        context.play(track1);
        assertTrue(context.isPlaying(), "Il player dovrebbe essere nello stato di riproduzione");
        context.next();
        Track nextTrack = context.getCurrentTrack();
        assertNotNull(nextTrack, "Il contesto deve aver aggiornato la traccia corrente");
        assertTrue(testPlaylist.getTracks().contains(nextTrack),
                "Il contesto deve aver selezionato una traccia dalla playlist");
        assertTrue(context.isPlaying(), "Il player deve rimanere nello stato di riproduzione dopo l'avanzamento");
    }

}
