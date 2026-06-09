package com.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlaylistCatalogTest {
    private PlaylistCatalog catalog;
    private Playlist p1;
    private Playlist p2;
    
    @BeforeEach
    public void setUp(){
        catalog = new PlaylistCatalog();
        p1 = new Playlist("Rock Anni 80");
        p2 = new Playlist("Pop Relax");
    }

    @Test
    public void testAddAndGetPlaylists(){
        assertTrue(catalog.getPlaylists().isEmpty());

        catalog.addPlaylist(p1);
        assertEquals(1, catalog.getPlaylists().size(), "Il playlistCatalog deve contenere un unico elemento");

        catalog.addPlaylist(p1);
        assertEquals(1, catalog.getPlaylists().size(), "Il playlistCatalog non deve permettere duplicati");
    }

    @Test
    public void testAddPlaylistAtIndex(){
        catalog.addPlaylist(p1);
        catalog.addPlaylist(0, p2);

        assertEquals(2, catalog.getPlaylists().size(), "Il playlistCatalog deve contenere due elementi");
        assertEquals(p2, catalog.getPlaylists().get(0), "p2 dovrebbe essere in posizione 0");
        assertEquals(p1, catalog.getPlaylists().get(1), "p1 dovrebbe essere slitatta in posizione 1");
    }

    @Test
    public void testRemovePlaylist(){
        catalog.addPlaylist(p1);

        boolean isRemoved = catalog.removePlaylist(p1);
        assertTrue(isRemoved, "La playlist è stata rimossa con successo");

        boolean isRemovedAgain = catalog.removePlaylist(p1);
        assertFalse(isRemovedAgain, "La playlist non può essere rimossa se non è presente nel playlistCatalog");
    }

    @Test
    public void testUpdatePlaylist(){
        catalog.addPlaylist(p1);

        catalog.updatePlaylist(p1, "Hard Rock");
        
        assertEquals("Hard Rock", p1.getName(), "La playlist è stata rinominata con successo");
        catalog.addPlaylist(p2);
        
        assertEquals(p1, catalog.getPlaylists().get(0), "La playlist aggioranta dovrebbe trovarsi ancora nel playlistCatalog in posizione 0");
    }
}
