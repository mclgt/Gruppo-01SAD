package com.Command;

import com.Model.Track;

import javafx.collections.ObservableList;

/**
 * @brief Rappresenta un ConcretoCommand che incapsula l'operazione di rimozione
 * di una traccia dalla libreria. Implementa le operazioni di execute e undo, 
 * memorizza la traccia da rimuovere e la posizione originale nella libreria per poterla
 * reinserire in caso di undo.
 */
public class RemoveTrack implements ICommand {
    private final ObservableList<Track> library;
    private final Track track;
    private int index;

    /**
     * @brief Costruttore che inizializza la rimozione di una traccia.
     * @param library lista osservabile che rappresenta la libreria da cui rimuovere la traccia.
     * @param track la traccia da rimuovere.
     */
    public RemoveTrack(ObservableList<Track> library, Track track) {
        this.library = library;
        this.track = track;
    }

    /**
     * @brief Esegue l'operazione di rimozione della traccia dalla libreria, memorizzando
     * l'indice originale della posizione.
     */
    @Override
    public void execute() {
        this.index = library.indexOf(track);
        if(index != -1)
            library.remove(track);
    }

    /**
     * @brief Ripristina la traccia rimossa all'interno della libreria, reinserendola nella
     * posizione originale.
     */
    @Override
    public void undo() {
        library.add(index, track);
    }
    
}
