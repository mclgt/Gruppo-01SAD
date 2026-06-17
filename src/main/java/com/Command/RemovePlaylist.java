package com.Command;

import com.DataLayer.DAO.Playlist.PlaylistDAO;
import com.Model.Playlist;
import com.Model.PlaylistCatalog;

/**
 * @brief Comando per la rimozione di una playlist.
 *        Implementa l'interfaccia ICommand e incapsula l'operazione di
 *        rimozione di
 *        una playlist dal catalogo. Memorizza la playlist da rimuovere e la sua
 *        posizione
 *        originale. In caso di undo, la playlist viene reinserita nella
 *        posizione originale.
 */

public class RemovePlaylist implements ICommand {
    private PlaylistCatalog playlistList;
    private Playlist playlist;
    private PlaylistDAO playlistDAO;
    private int index = -1;

    /**
     * @brief Costruttore che inizializza il comando di rimozione di una playlist.
     * @param playlistList il catalogo da cui rimuovere la playlist.
     * @param playlist     la playlist da rimuovere. Viene memorizzata insieme alla
     *                     sua
     *                     posizione originale per poterla reinserire in caso di
     *                     undo.
     */
    public RemovePlaylist(PlaylistCatalog playlistList, Playlist playlist, PlaylistDAO playlistDAO) {
        this.playlistList = playlistList;
        this.playlist = playlist;
        this.playlistDAO = playlistDAO;
    }

    /**
     * @brief Esegue l'operazione di rimozione della playlist dal catalogo.
     *        Se la playlist esiste nel catalogo, viene rimossa e memorizzata la sua
     *        posizione originale.
     */
    @Override
    public void execute() {
        this.index = playlistList.getPlaylists().indexOf(playlist);
        
        if (index != -1) {
            playlistList.removePlaylist(playlist);
            try {
                if (playlistDAO != null)
                    playlistDAO.delete(playlist.getId());
            } catch (Exception e) {
                System.err.println("DB Error in RemovePlaylist(execute): " + e.getMessage());
            }
        }
    }

    /**
     * @brief Annulla l'operazione di rimozione della playlist.
     *        Se la playlist è stata rimossa, viene reinserita nella posizione
     *        originale.
     */
    @Override
    public void undo() {
        if (index != -1) {
            playlistList.addPlaylist(index, playlist);
            try {
                if (playlistDAO != null)
                    playlistDAO.save(playlist);
            } catch (Exception e) {
                System.err.println("DB Error in RemovePlaylist(undo): " + e.getMessage());
            }
        }
    }

}
