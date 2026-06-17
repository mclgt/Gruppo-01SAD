package com.Command;

import com.DataLayer.DAO.Playlist.PlaylistDAO;
import com.Model.Playlist;
import com.Model.PlaylistCatalog;

/**
 * @class ModifyPlaylist
 * @brief Comando per la modifica dei dettagli di una playlist.
 *        Implementa l'interfaccia ICommand e gestisce la logica per
 *        modificare il nome di una playlist esistente. L'operazione salva
 *        il nome originale al momento della creazione del comando,
 *        permettendo così di eseguire un annullamento.
 */
public class ModifyPlaylist implements ICommand {
    private final PlaylistCatalog receiver;
    private final Playlist playlistToModify;
    private final PlaylistDAO playlistDAO;

    private String oldName;
    private String newName;

    /**
     * @brief Costruisce il comando per modificare una playlist.
     *        Al momento dell'istanziazione, il comando salva automaticamente il
     *        nome attuale della playlist nella variabile per garantire
     *        un corretto ripristino in caso di undo.
     * @param receiver         Il catalogo che gestisce la collezione di playlist.
     * @param playlistToModify La playlist specifica da rinominare.
     * @param newName          Il nuovo nome da assegnare alla playlist.
     * @param playlistDAO      L'oggetto DAO responsabile dell'aggiornamento nel
     *                         database.
     */
    public ModifyPlaylist(PlaylistCatalog receiver, Playlist playlistToModify, String newName,
            PlaylistDAO playlistDAO) {
        this.playlistToModify = playlistToModify;
        this.receiver = receiver;
        this.playlistDAO = playlistDAO;
        this.oldName = playlistToModify.getName();
        this.newName = newName;
    }

    /**
     * @brief Esegue il comando di modifica.
     *        Applica il nuovo nome alla playlist all'interno del catalogo in
     *        memoria
     *        e successivamente esegue un'operazione di aggiornamento nel
     *        database. Eventuali errori di connessione o scrittura vengono
     *        intercettati
     *        e stampati per evitare interruzioni dell'applicazione.
     */
    @Override
    public void execute() {
        this.receiver.updatePlaylist(playlistToModify, newName);
        try {
            if (playlistDAO != null)
                playlistDAO.update(playlistToModify);
        } catch (Exception e) {
            System.err.println("DB Error in ModifyPlaylist(execute): " + e.getMessage());
        }
    }

    /**
     * @brief Annulla il comando di modifica precedentemente eseguito.
     *        Ripristina il nome originale della playlist nel catalogo
     *        in memoria e invia un nuovo aggiornamento al database per riflettere
     *        il ripristino. Gli errori del database vengono gestiti in modo sicuro.
     */
    @Override
    public void undo() {
        this.receiver.updatePlaylist(playlistToModify, oldName);
        try {
            if (playlistDAO != null)
                playlistDAO.update(playlistToModify);
        } catch (Exception e) {
            System.err.println("DB Error in ModifyPlaylist(undo): " + e.getMessage());
        }
    }
}
