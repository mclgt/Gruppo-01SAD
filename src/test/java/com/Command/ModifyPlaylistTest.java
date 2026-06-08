package com.Command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.Playlist;
import com.Model.PlaylistCatalog;
import com.Model.Track;

public class ModifyPlaylistTest {
    PlaylistCatalog catalog;
    private Playlist playlistTest;
    private Track t;

    @BeforeEach
    public void setUp() {
        catalog = new PlaylistCatalog();
        playlistTest = new Playlist("playlist test");
        catalog.addPlaylist(playlistTest);
        t = new Track("Bohemian Rhapsody", "Queen", 1975, "Rock", 355, "A Night at the Opera", "C:/audio.wav");
        playlistTest.addTrack(t);
    }

    @Test
    public void testUpdatePlaylist_executeAndUndo() {
        ICommand updateCommand = new ModifyPlaylist(catalog, playlistTest, "nuovo test");
        updateCommand.execute();
        assertEquals("nuovo test", playlistTest.getName(), "Nome non modificato");
        updateCommand.undo();
        assertEquals("playlist test", playlistTest.getName(), "Nome non ripristinato");
    }

}
