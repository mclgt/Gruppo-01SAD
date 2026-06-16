package com.Command;

import com.Model.Track;

import javafx.collections.ObservableList;

/**
 * @brief Comando concreto per spostare verso l'alto una traccia in una playlist.
 * Implementa l'interfaccia ICommand. Questo comando incapsula l'azione di scambio
 * di posizione tra una traccia selezionata e quella immediatamente precedente all'interno di una 
 * ObservableList di oggetti Track. Supporta l'operazione di annullamento (undo) per ripristinare la posizione.
 */
public class MoveUpTrack implements ICommand{
    private final ObservableList <Track> list;
    private final Track track;
    private int index=0;

    /**
     * @brief Costruttore parametrizzato del comando MoveUpTrack.
     * Inizializza il comando legandolo alla lista di riferimento e alla traccia specifica da manipolare.
     * @param list L'ObservableList contenente le tracce musicali.
     * @param track La traccia specifica da spostare all'interno della lista.
     */
    public MoveUpTrack(ObservableList <Track> list, Track track){
        this.list=list;
        this.track=track;
    }

    /**
     * @brief Esegue lo spostamento verso l'alto della traccia.
     * Recupera l'indice corrente della traccia nella lista. Se la traccia è presente
     * e non si trova già nella prima posizione, ne memorizza 
     * l'indice nell'attributo `index` ed effettua uno scambio (swap) con l'elemento precedente.
     */
    @Override
    public void execute(){
        int currentIndex=list.indexOf(track);
        if(currentIndex>0){
            this.index=currentIndex;
            java.util.Collections.swap(list, currentIndex, currentIndex - 1);
        }
    }

    /**
     * @brief Ripristina lo stato precedente della lista annullando lo spostamento verso l'alto.
     * Se l'indice registrato durante l'esecuzione è maggiore di 0,
     * effettua nuovamente uno swap invertito tra la posizione dell'elemento sceso e la sua posizione originaria,
     * riportando la lista allo stato esatto in cui si trovava prima del comando.
     */
    @Override
    public void undo(){
        if(index>0){
            java.util.Collections.swap(list,index-1,index);
        }
    }
    
}
