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
 * @brief Test class for verifying playback logic with Playlists.
 *        Verifies that PlayerContext interacts correctly with a Playlist,
 *        using the right playback strategy and ensuring correct track advancement.
 */
public class PlayerContextPlaylistTest {
    private Playlist testPlaylist;
    private Track track1;
    private Track track2;

    /**
     * @brief Sets up the test fixtures: a playback context and a populated playlist.
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
     * @brief Verifies that starting playback from an empty/initial state loads the first available track.
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
        assertEquals(track1, context.getCurrentTrack(), "The first track of the playlist should be A");
        assertTrue(context.isPlaying(), "The player should be in the 'playingState' state");
    }

    /**
     * @brief Verifies that given a playing track, the 'next' command correctly
     *        advances the current track to the next one in the playlist.
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
        assertEquals(track2, nextTrack, "The next track in the playlist should be F");
    }

    /**
     * @brief Verifies the behavior at the end of the playlist in sequential mode.
     *        The player must not automatically loop to the first track.
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
                "At the end of the playlist in sequential mode, next must not wrap to the first track");
    }

    /**
     * @brief Verifies the behavior at the end of the playlist in loop mode.
     *        The player must automatically loop back to the first track.
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
                "At the end of the playlist in loop mode, next must wrap back to the first track");
    }

    /** Verifies that the shuffle strategy picks a valid track from the playlist and keeps the player playing. */
    @Test
    void testShuffleStrategy() {
        PlaybackContext playbackContext = new PlaybackContext(new ShuffleStrategy());
        PlayerContext context = new PlayerContext(playbackContext);
        Track track3 = new Track("K", "L", 2010, "M", 100, "N", "O", null);
        testPlaylist.addTrack(track3);
        context.setCurrentTrack(track1);
        context.play(track1);
        assertTrue(context.isPlaying(), "The player should be in the playing state");
        context.next(testPlaylist.getTracks(), track1);
        Track nextTrack = context.getCurrentTrack();
        assertNotNull(nextTrack, "The context must have updated the current track");
        assertTrue(testPlaylist.getTracks().contains(nextTrack),
                "The context must have selected a track from the playlist");
        assertTrue(context.isPlaying(), "The player must remain in the playing state after advancing");
    }

}
