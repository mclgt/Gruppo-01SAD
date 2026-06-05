package com.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PlaylistTest {

    @Test
    public void testPlaylistInitialization() {
        Playlist playlist = new Playlist("Rock Anni 90");
        
        assertEquals("Rock Anni 90", playlist.getName());
        assertTrue(playlist.getTracks().isEmpty(), "La playlist appena creata deve essere vuota");
        assertEquals(0, playlist.getTotalDuration(), "La durata iniziale deve essere 0");
    }

    @Test
    public void testAddTrackAndDuration() {
        Playlist playlist = new Playlist("Preferite");
        Track t1 = new Track("Brano 1", "Autore", 2020, "Pop", 100, "Album 1", "path1.wav");
        Track t2 = new Track("Brano 2", "Autore", 2021, "Rock", 50, "Album 2", "path2.wav");

        playlist.addTrack(t1);
        playlist.addTrack(t2);

        assertEquals(2, playlist.getTracks().size(), "La playlist deve contenere 2 brani");
        assertEquals(150, playlist.getTotalDuration(), "La durata totale deve essere la somma esatta (150s)");
        assertEquals("02:30", playlist.getFormattedTotalDuration(), "La formattazione del tempo deve essere 02:30");
    }

    @Test
    public void testRemoveTrack() {
        Playlist playlist = new Playlist("Test Remove");
        Track t1 = new Track("Brano 1", "Autore", 2020, "Pop", 100, "Album", "path1.wav");
        
        playlist.addTrack(t1);
        assertEquals(1, playlist.getTracks().size());

        Track rimosso = playlist.removeTrack(t1);
        
        assertNotNull(rimosso, "Il metodo deve restituire il brano rimosso");
        assertTrue(playlist.getTracks().isEmpty(), "La playlist deve essere di nuovo vuota");
        assertEquals(0, playlist.getTotalDuration());
    }
}