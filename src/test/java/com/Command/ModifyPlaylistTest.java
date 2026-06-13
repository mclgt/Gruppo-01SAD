package com.Command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.Playlist;
import com.Model.PlaylistCatalog;
import com.Model.Track;
import com.Model.TrackFactory;

import com.Model.MockTrackFactoryTest;

/**
 * @class ModifyPlaylistTest
 * 
 * @brief Classe di test per verificare il funzionamento del comando di
 *        modifica. Si assicura che l'operazione di modifica del nome di una
 *        playlist sia eseguibile correttamente e reversibile tramite il
 *        meccanismo di Undo, rispettando il Command Pattern.
 */
public class ModifyPlaylistTest {
    PlaylistCatalog catalog;
    private Playlist playlistTest;
    private Track t;
    private TrackFactory factory;

    /**
     * @brief Configura il test prima di ogni esecuzione. Crea un nuovo catalogo,
     *        una playlist di test, la aggiunge al catalogo e prepara una traccia da
     *        associare alla playlist.
     */
    @BeforeEach
    public void setUp() {
        factory = new MockTrackFactoryTest();
        catalog = new PlaylistCatalog();
        playlistTest = new Playlist("playlist test");
        catalog.addPlaylist(playlistTest);
        t = factory.createTrack("Bohemian Rhapsody", "Queen", 1975, "Rock", 355, "A Night at the Opera",
                "dummy.mp3", null);
        playlistTest.addTrack(t);
    }

    /**
     * @brief Verifica l'esecuzione del comando di modifica e la sua reversibilità.
     *        Si modifica il noma della plylist, si verifica l'avvenuta modifica, si
     *        invoca 'undo()' e ci si assicura che il nome sia tornato allo stato
     *        originale.
     */
    @Test
    public void testUpdatePlaylist_executeAndUndo() {
        ICommand updateCommand = new ModifyPlaylist(catalog, playlistTest, "nuovo test");
        updateCommand.execute();
        assertEquals("nuovo test", playlistTest.getName(), "Nome non modificato");
        updateCommand.undo();
        assertEquals("playlist test", playlistTest.getName(), "Nome non ripristinato");
    }

}
