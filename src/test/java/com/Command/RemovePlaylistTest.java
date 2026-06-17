package com.Command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.DataLayer.DAO.Playlist.PlaylistDAO;
import com.Model.Playlist;
import com.Model.PlaylistCatalog;

public class RemovePlaylistTest {
    private PlaylistCatalog playlistCatalog;
    private UndoManager undoManager;
    private PlaylistDAO playlistDAO;
    private Playlist playlist1;
    private Playlist playlist2;
    private Playlist playlist3;

    @BeforeEach
    public void setUp() {
        playlistCatalog = new PlaylistCatalog();
        undoManager = new UndoManager();
        playlist1 = new Playlist("Rock");
        playlist2 = new Playlist("Pop");
        playlist3 = new Playlist("Jazz");

        playlistCatalog.addPlaylist(playlist1);
        playlistCatalog.addPlaylist(playlist2); 
        playlistCatalog.addPlaylist(playlist3);
    }

    @Test
    public void testRemovePlaylistCommand() {
        assertEquals(3, playlistCatalog.getPlaylists().size(), "Il catalogo deve contenere 3 playlist");

        ICommand removeCommand = new RemovePlaylist(playlistCatalog, playlist2, playlistDAO);
        undoManager.executeCommand(removeCommand);

        assertEquals(2, playlistCatalog.getPlaylists().size(), "Il catalogo deve contenere 2 playlist dopo la rimozione");
        assertFalse(playlistCatalog.getPlaylists().contains(playlist2), "La playlist non è stata rimossa dal catalogo");

        undoManager.undo();
        assertEquals(3, playlistCatalog.getPlaylists().size(), "Il catalogo deve contenere 3 playlist dopo l'undo");
        assertEquals(1, playlistCatalog.getPlaylists().indexOf(playlist2), "La playlist ripristinata deve trovarsi al suo indice originario (1)");
    }
}
