package com.Command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.DataLayer.DAO.Playlist.PlaylistDAO;
import com.Model.Playlist;
import com.Model.PlaylistCatalog;

public class AddPlaylistTest {
    private PlaylistCatalog catalog;
    private Playlist playlist;
    private PlaylistDAO playlistDAO;

    @BeforeEach
    public void setUp(){
        catalog = new PlaylistCatalog();
        playlist = new Playlist("Playlist test");
    }
    
    @Test
    public void testAddPlaylist_executeAndUndo(){
        ICommand addCommand = new AddPlaylist(catalog, playlist, playlistDAO);

        addCommand.execute();
        assertEquals(1, catalog.getPlaylists().size(), "Il playlistCatalog deve contenere un unico elemento");
        assertTrue(catalog.getPlaylists().contains(playlist), "Il playlistCatalog deve contenere la playlist 'Test Playlist'");

        addCommand.undo();
        assertEquals(0, catalog.getPlaylists().size(), "Il playlistCatalog dovrebbe essere vuoto dopo la rimozione");
        assertFalse(catalog.getPlaylists().contains(playlist), "Il playlistController non deve più contenere la playlist 'Test Playlist'");
    }
}
