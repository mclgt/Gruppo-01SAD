package com.Command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.DataLayer.DAO.Playlist.PlaylistDAO;
import com.Model.Playlist;
import com.Model.PlaylistCatalog;

/**
 * @class AddPlaylistTest
 * @brief Classe di test per verificare il corretto funzionamento del comando
 *        AddPlaylist.
 */
public class AddPlaylistTest {
    private PlaylistCatalog catalog;
    private Playlist playlist;
    private PlaylistDAO playlistDAO;

    /**
     * @brief Metodo di configurazione iniziale
     *        Viene eseguito automaticamente prima di ogni singolo test
     *        Inizializza un catalogo vuoto e una playlist di test per garantire
     *        che i test girino sempre in un ambiente pulito, isolato e predicibile.
     */
    @BeforeEach
    public void setUp() {
        catalog = new PlaylistCatalog();
        playlist = new Playlist("Playlist test");
    }

    /**
     * @brief Testa il ciclo di vita completo (aggiunta e rimozione) del comando.
     * 
     *        Execute: invoca l'aggiunta della playlist e verifica, tramite
     *        asserzioni,
     *        che il catalogo contenga esattamente un elemento e che tale elemento
     *        sia
     *        proprio la playlist istanziata nel setup.
     *        Undo: invoca il ripristino dell'azione e verifica che la playlist
     *        sia stata rimossa, lasciando il catalogo nuovamente vuoto.
     */
    @Test
    public void testAddPlaylist_executeAndUndo() {
        ICommand addCommand = new AddPlaylist(catalog, playlist, playlistDAO);

        addCommand.execute();
        assertEquals(1, catalog.getPlaylists().size(), "Il playlistCatalog deve contenere un unico elemento");
        assertTrue(catalog.getPlaylists().contains(playlist),
                "Il playlistCatalog deve contenere la playlist 'Test Playlist'");

        addCommand.undo();
        assertEquals(0, catalog.getPlaylists().size(), "Il playlistCatalog dovrebbe essere vuoto dopo la rimozione");
        assertFalse(catalog.getPlaylists().contains(playlist),
                "Il playlistController non deve più contenere la playlist 'Test Playlist'");
    }
}
