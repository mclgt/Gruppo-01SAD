package com.Command;

import com.Model.ITrackContainer;
import com.Model.Track;

/**
 * @brief Comando concreto per spostare verso l'alto una traccia in una playlist.
 * Implementa l'interfaccia ICommand. Questo comando incapsula l'azione di scambio
 * di posizione tra una traccia selezionata e quella immediatamente precedente all'interno di una 
 * ObservableList di oggetti Track. Supporta l'operazione di annullamento (undo) per ripristinare la posizione.
 */
public class MoveUpTrack implements ICommand{
    private final ITrackContainer receiver;
    private final Track track;
    private Boolean moved = false;

    /**
     * @brief Costruttore parametrizzato del comando MoveUpTrack.
     * @param receiver Il contenitore di tracce su cui agire.
     * @param track La traccia specifica da spostare.
     */
    public MoveUpTrack(ITrackContainer receiver, Track track){
        this.receiver=receiver;
        this.track=track;
    }

    /**
     * @brief Esegue lo spostamento verso l'alto delegando al Receiver.
     * Poiché la UI gestisce la disabilitazione del pulsante a inizio lista, 
     * il comando assume l'operazione come sicura, ma memorizza l'esito nel flag `moved`.
     */
    @Override
    public void execute(){
        this.moved = receiver.moveTrackUp(track);
    }

    /**
     * @brief Ripristina lo stato precedente annullando lo spostamento verso l'alto.
     * L'esatto contrario di un movimento verso l'alto ("Su") è un movimento verso il basso ("Giù").
     */
    @Override
    public void undo(){
        if (moved) {
            receiver.moveTrackDown(track);
            this.moved = false; 
        }
    }
    
}
