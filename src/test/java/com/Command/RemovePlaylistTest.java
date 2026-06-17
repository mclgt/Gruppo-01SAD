package com.Command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.DataLayer.DAO.Playlist.PlaylistDAO;
import com.Model.Playlist;
import com.Model.PlaylistCatalog;

/**
 * @class RemovePlaylistTest
 * @brief Classe di test per verificare la rimozione di una playlist e
 *        l'integrazione con UndoManager.
 *        Il test sfrutta l'UndoManager per simulare un flusso operativo reale
 *        all'interno dell'applicazione.
 */
public class RemovePlaylistTest {
    private PlaylistCatalog playlistCatalog;
    private UndoManager undoManager;
    private PlaylistDAO playlistDAO;
    private Playlist playlist1;
    private Playlist playlist2;
    private Playlist playlist3;

    /**
     * @brief Metodo di configurazione iniziale (Setup dell'ambiente di test).
     *        Viene eseguito automaticamente prima del test.
     *        Prepara un ambiente controllato e noto: istanzia un catalogo vuoto,
     *        un gestore degli undo e popola il catalogo con tre playlist di base
     *        ("Rock", "Pop", "Jazz") per avere una lista su cui effettuare le
     *        rimozioni.
     */
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

    /**
     * @brief Testa l'esecuzione della rimozione e il successivo ripristino
     *        posizionale.
     * 
     *        Verifica pre-condizioni: Assicura che il setup abbia caricato
     *        correttamente le 3 playlist.
     *        Execute: Invia il comando di rimozione per la playlist centrale
     *        ("Pop", indice 1)
     *        tramite l'UndoManager. Verifica che la dimensione del catalogo scenda
     *        a 2 e che
     *        la playlist target non sia più presente.
     *        Undo: Invoca l'annullamento. Verifica che la dimensione torni a 3
     *        e, soprattutto, che la playlist ripristinata sia stata ricollocata
     *        esattamente
     *        nella sua posizione originaria (indice 1), garantendo la coerenza
     *        visiva della lista.
     */
    @Test
    public void testRemovePlaylistCommand() {
        assertEquals(3, playlistCatalog.getPlaylists().size(), "Il catalogo deve contenere 3 playlist");

        ICommand removeCommand = new RemovePlaylist(playlistCatalog, playlist2, playlistDAO);
        undoManager.executeCommand(removeCommand);

        assertEquals(2, playlistCatalog.getPlaylists().size(),
                "Il catalogo deve contenere 2 playlist dopo la rimozione");
        assertFalse(playlistCatalog.getPlaylists().contains(playlist2), "La playlist non è stata rimossa dal catalogo");

        undoManager.undo();
        assertEquals(3, playlistCatalog.getPlaylists().size(), "Il catalogo deve contenere 3 playlist dopo l'undo");
        assertEquals(1, playlistCatalog.getPlaylists().indexOf(playlist2),
                "La playlist ripristinata deve trovarsi al suo indice originario (1)");
    }
}
