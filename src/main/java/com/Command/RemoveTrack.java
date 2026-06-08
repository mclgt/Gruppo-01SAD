package com.Command;

import com.Model.Track;

import java.util.HashMap;
import java.util.Map;

import com.Model.ITrackContainer;
import com.Model.Playlist;
import com.Model.PlaylistCatalog;

/**
 * @brief Rappresenta un ConcretoCommand che incapsula l'operazione di rimozione
 * di una traccia dalla libreria. Implementa le operazioni di execute e undo, 
 * memorizza la traccia da rimuovere e la posizione originale nella libreria per poterla
 * reinserire in caso di undo.
 */
public class RemoveTrack implements ICommand {
    private final ITrackContainer receiver;
    private final Track track;
    private final int savedIndex;
    private final Map<Playlist, Integer> savedPlaylistIndicies = new HashMap<>();

    /**
     * @brief Costruttore che inizializza la rimozione di una traccia.
     * @param library lista osservabile che rappresenta la libreria da cui rimuovere la traccia.
     * @param track la traccia da rimuovere.
     */
    public RemoveTrack(ITrackContainer receiver, Track track) {
        this.receiver = receiver;
        this.track = track;
        this.savedIndex = receiver.indexOf(track);
    }

    public RemoveTrack(ITrackContainer receiver, Track track, PlaylistCatalog playlistCatalog){
        this(receiver, track);

        if(playlistCatalog != null){
            for(Playlist p : playlistCatalog.getPlaylists()){
                int index = p.indexOf(track);
                if(index >= 0){
                    this.savedPlaylistIndicies.put(p, index);
                }
            }
        }
    }

    /**
     * @brief Esegue l'operazione di rimozione della traccia dalla libreria, memorizzando
     * l'indice originale della posizione.
     */
    @Override
    public void execute() {
        this.receiver.removeTrack(track);

        for(Playlist p : this.savedPlaylistIndicies.keySet()){
            p.removeTrack(this.track);
        }
    }

    /**
     * @brief Ripristina la traccia rimossa all'interno della libreria, reinserendola nella
     * posizione originale.
     */
    @Override
    public void undo() {
        if(this.savedIndex >= 0){
            this.receiver.addTrack(this.savedIndex, this.track);
        }else{
            this.receiver.addTrack(this.track);
        }

        for(Map.Entry<Playlist, Integer> entry : this.savedPlaylistIndicies.entrySet()){
            Playlist p = entry.getKey();
            int originalIndex = entry.getValue();
            p.addTrack(originalIndex, this.track);
        }
    }
}