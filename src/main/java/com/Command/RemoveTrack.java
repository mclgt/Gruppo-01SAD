package com.Command;

import com.Model.Track;
import com.Model.Library;

/**
 * @brief Rappresenta un ConcretoCommand che incapsula l'operazione di rimozione
 * di una traccia dalla libreria. Implementa le operazioni di execute e undo, 
 * memorizza la traccia da rimuovere e la posizione originale nella libreria per poterla
 * reinserire in caso di undo.
 */
public class RemoveTrack implements ICommand {
    private final Library receiver;
    private final Track track;
    private int index;

    /**
     * @brief Costruttore che inizializza la rimozione di una traccia.
     * @param library lista osservabile che rappresenta la libreria da cui rimuovere la traccia.
     * @param track la traccia da rimuovere.
     */
    public RemoveTrack(Library receiver, Track track) {
        this.receiver = receiver;
        this.track = track;
    }

    /**
     * @brief Esegue l'operazione di rimozione della traccia dalla libreria, memorizzando
     * l'indice originale della posizione.
     */
    @Override
    public void execute() {
        this.index = this.receiver.getLibrary().indexOf(track);
        if(index != -1)
            this.receiver.removeTrack(track);
    }

    /**
     * @brief Ripristina la traccia rimossa all'interno della libreria, reinserendola nella
     * posizione originale.
     */
    @Override
    public void undo() {
        this.receiver.addTrack(index, track);
    }
    
}
