package com.Command;

import com.DataLayer.DAO.Playlist.PlaylistDAO;
import com.Model.Playlist;
import com.Model.PlaylistCatalog;

/**
 * @class AddPlaylist
 * @brief Comando per l'aggiunta di una nuova playlist.
 *        Implementa l'interfaccia ICommand e incapsula tutta la logica
 *        necessaria per aggiungere una playlist all'interno dell'applicazione.
 *        Fornisce il meccanismo per annullare l'operazione in modo sicuro.
 */
public class AddPlaylist implements ICommand {
    private final PlaylistCatalog playlistCatalog;
    private final Playlist playlist;
    private final PlaylistDAO playlistDAO;

    /**
     * @brief Costruisce il comando per l'aggiunta di una playlist.
     * @param playlistCatalog Il catalogo in memoria che gestisce le
     *                        playlist.
     * @param playlist        L'oggetto playlist che deve essere aggiunto.
     * @param playlistDAO     L'oggetto per l'accesso ai dati responsabile del
     *                        salvataggio nel database.
     */
    public AddPlaylist(PlaylistCatalog playlistCatalog, Playlist playlist, PlaylistDAO playlistDAO) {
        this.playlistCatalog = playlistCatalog;
        this.playlist = playlist;
        this.playlistDAO = playlistDAO;
    }

    /**
     * @brief Esegue il comando di aggiunta.
     *        Inserisce la playlist nel catalogo in e invoca il
     *        salvataggio fisico nel database. Se il salvataggio fallisce,
     *        l'eccezione viene intercettata e stampata senza far crashare
     *        l'applicazione.
     */
    @Override
    public void execute() {
        playlistCatalog.addPlaylist(playlist);
        try {
            if (playlistDAO != null)
                playlistDAO.save(playlist);
        } catch (Exception e) {
            System.err.println("DB Error in AddPlaylist(execute): " + e.getMessage());
        }
    }

    /**
     * @brief Annulla il comando di aggiunta precedentemente eseguito.
     *        Rimuove la playlist dal catalogo e la elimina definitivamente
     *        dal database utilizzando il suo identificativo. Eventuali errori di
     *        accesso ai dati vengono intercettati in modo
     *        sicuro.
     */
    @Override
    public void undo() {
        playlistCatalog.removePlaylist(playlist);
        try {
            if (playlistDAO != null)
                playlistDAO.delete(playlist.getId());
        } catch (Exception e) {
            System.err.println("DB Error in AddPlaylist(undo): " + e.getMessage());
        }
    }
}