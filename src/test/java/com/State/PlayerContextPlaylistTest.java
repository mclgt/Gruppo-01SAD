package com.State;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.Playlist;
import com.Model.Track;
import com.Strategy.PlaybackContext;
import com.Strategy.SequentialStrategy;

public class PlayerContextPlaylistTest {
    private PlayerContext context;
    private Playlist testPlaylist;
    private Track track1;
    private Track track2;

    @BeforeEach
    void setUp() {
        PlaybackContext playbackContext = new PlaybackContext(new SequentialStrategy());
        context = new PlayerContext(playbackContext);
        testPlaylist = new Playlist("Playlist Test");
        track1 = new Track("A", "B", 2010, "C", 100, "D", "E");
        track2 = new Track("F", "G", 2010, "H", 100, "I", "J");
        testPlaylist.addTrack(track1);
        testPlaylist.addTrack(track2);

    }

    @Test
    void testInitialPlayUpdateState() {
        context.setCurrentTrack(null);
        context.next(testPlaylist.getTracks(), null);
        Track firstTrack = context.getCurrentTrack();
        if (firstTrack != null) {
            context.play(firstTrack);
        }
        assertEquals(track1, context.getCurrentTrack(), "La prima traccia della playlist dovrebbe essere A");
        assertTrue(context.isPlaying(), "Il player dovrebbe trovarsi nello stato 'playingState'");

    }

    @Test
    void testSequentialNextUpdateTrack() {
        context.setCurrentTrack(track1);
        context.play(track1);
        assertTrue(context.isPlaying());
        context.next(testPlaylist.getTracks(), track1);
        Track nextTrack = context.getCurrentTrack();
        assertEquals(track2, nextTrack, "La  traccia successiva della playlist dovrebbe essere F");

    }

    @Test
    void testEndOfPlaylistSequential() {
        context.setCurrentTrack(track2);
        context.play(track2);
        assertTrue(context.isPlaying());
        context.next(testPlaylist.getTracks(), track2);
        Track afterLast = context.getCurrentTrack();
        assertTrue(afterLast == track2,
                "Alla fine della playlist modalità sequenziale, il next non deve tornare alla prima traccia");
    }

}
