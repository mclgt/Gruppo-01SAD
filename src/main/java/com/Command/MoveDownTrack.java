package com.Command;

import com.Model.ITrackContainer;
import com.Model.Track;
/**
 * @brief Comando concreto per spostare verso il basso una traccia in una collezione di brani.
 * * Implementa l'interfaccia ICommand. Questo comando incapsula l'azione di scambio
 * di posizione tra una traccia selezionata e quella immediatamente successiva all'interno di una 
 * ObservableList di oggetti Track. Supporta nativamente l'operazione di annullamento (undo).
 */
public class MoveDownTrack implements ICommand{
    private ITrackContainer receiver;
    private Track track;
    private Boolean moved = false;

    /**
     * @brief Costruttore parametrizzato del comando MoveDownTrack.
     * @param receiver Il contenitore concreto (implementazione di ITrackContainer) su cui agire.
     * @param track La traccia specifica da spostare all'interno del contenitore.
     */
    public MoveDownTrack(ITrackContainer receiver, Track track){
        this.receiver=receiver;
        this.track=track;
    }

    /**
     * @brief Esegue lo spostamento verso il basso della traccia.
     * Delega l'azione di business interamente al Receiver tramite il metodo `moveTrackDown`.
     * Registra l'esito dello spostamento nella variabile booleana di istanza `moved` per 
     * garantire la consistenza di un eventuale successivo undo.
     */
    @Override
    public void execute(){
        this.moved=receiver.moveTrackDown(track);
    }
    /**
     * @brief Ripristina lo stato precedente del contenitore annullando lo spostamento.
     * * Sfrutta il principio di simmetria: l'annullamento di uno spostamento verso il basso ("Giù") 
     * equivale a uno spostamento verso l'alto ("Su") della medesima traccia.
     * L'operazione viene invocata solo se l'esecuzione (`execute`) era andata a buon fine.
     */
    @Override
    public void undo(){
        if (moved) {
            receiver.moveTrackUp(track);
            this.moved = false;
        }
    }
    
}
