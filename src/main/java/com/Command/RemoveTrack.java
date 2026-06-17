package com.Command;

import java.util.HashMap;
import java.util.Map;

import com.DataLayer.DAO.Playlist.PlaylistDAO;
import com.DataLayer.DAO.Track.TrackDAO;
import com.Model.ITrackContainer;
import com.Model.Playlist;
import com.Model.PlaylistCatalog;
import com.Model.Track;

/**
 * @brief Rappresenta un ConcretoCommand che incapsula l'operazione di rimozione
 *        di una traccia dalla libreria. Implementa le operazioni di execute e
 *        undo,
 *        memorizza la traccia da rimuovere e la posizione originale nella
 *        libreria per poterla
 *        reinserire in caso di undo.
 */
public class RemoveTrack implements ICommand {
    private final ITrackContainer receiver;
    private final Track track;
    private int savedIndex;
    private final Map<Playlist, Integer> savedPlaylistIndicies = new HashMap<>();

    private final TrackDAO trackDAO;
    private PlaylistDAO playlistDAO;
    private PlaylistCatalog playlistCatalog;

    /**
     * @brief Costruttore che inizializza la rimozione di una traccia.
     * @param library lista osservabile che rappresenta la libreria da cui rimuovere
     *                la traccia.
     * @param track   la traccia da rimuovere.
     */
    public RemoveTrack(ITrackContainer receiver, Track track, TrackDAO trackDAO) {
        this.receiver = receiver;
        this.track = track;
        this.trackDAO = trackDAO;
        this.playlistDAO = null;
        this.playlistCatalog = null;
    }

    public RemoveTrack(ITrackContainer receiver, Track track, PlaylistCatalog playlistCatalog, TrackDAO trackDAO,
            PlaylistDAO playlistDAO) {

        this(receiver, track, trackDAO);
        this.playlistDAO = playlistDAO;
        this.playlistCatalog = playlistCatalog;
    }

    /**
     * @brief Esegue l'operazione di rimozione della traccia dalla libreria,
     *        memorizzando
     *        l'indice originale della posizione.
     */
    @Override
    public void execute() {
        this.savedIndex = receiver.indexOf(track);

        if (playlistCatalog != null) {
            for (Playlist p : playlistCatalog.getPlaylists()) {
                int index = p.indexOf(track);
                if (index >= 0) {
                    this.savedPlaylistIndicies.put(p, index);
                }
            }
        }
        
        this.receiver.removeTrack(track);

        for (Playlist p : this.savedPlaylistIndicies.keySet()) {
            p.removeTrack(this.track);
        }

        try {
            if (trackDAO != null)
                trackDAO.delete(track.getId());
        } catch (Exception e) {
            System.err.println("Errore DB in RemoveTrack (execute): " + e.getMessage());
        }
    }

    /**
     * @brief Ripristina la traccia rimossa all'interno della libreria,
     *        reinserendola nella
     *        posizione originale.
     */
    @Override
    public void undo() {
        if (this.savedIndex >= 0) {
            this.receiver.addTrack(this.savedIndex, this.track);
        } else {
            this.receiver.addTrack(this.track);
        }

        for (Map.Entry<Playlist, Integer> entry : this.savedPlaylistIndicies.entrySet()) {
            Playlist p = entry.getKey();
            int originalIndex = entry.getValue();
            p.addTrack(originalIndex, this.track);
        }

        try {
            if (trackDAO != null)
                trackDAO.save(track);

            if (playlistDAO != null) {
                for (Playlist p : this.savedPlaylistIndicies.keySet()) {
                    playlistDAO.addTrackToPlaylist(p.getId(), track.getId());
                }
            }
        } catch (Exception e) {
            System.err.println("Errore DB in RemoveTrack (undo): " + e.getMessage());
        }
    }
}