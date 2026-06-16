package com.Command;

import com.Model.Track;

import javafx.collections.ObservableList;
/**
 * @brief Comando concreto per spostare verso il basso una traccia in una collezione di brani.
 * * Implementa l'interfaccia ICommand. Questo comando incapsula l'azione di scambio
 * di posizione tra una traccia selezionata e quella immediatamente successiva all'interno di una 
 * ObservableList di oggetti Track. Supporta nativamente l'operazione di annullamento (undo).
 */
public class MoveDownTrack implements ICommand{
    private ObservableList<Track> list;
    private Track track;
    private int index;

    /**
     * @brief Costruttore parametrizzato del comando MoveDownTrack.
     * Inizializza il comando legandolo alla lista di riferimento e alla traccia specifica da manipolare.
     * @param list L'ObservableList contenente le tracce musicali.
     * @param track La traccia specifica da spostare all'interno della lista.
     */
    public MoveDownTrack(ObservableList <Track> list, Track track){
        this.list=list;
        this.track=track;
    }

    /**
     * @brief Esegue lo spostamento verso il basso della traccia.
     * Cerca la traccia nella lista. Se la traccia è presente e non si trova già nell'ultima 
     * posizione (limite della playlist), ne memorizza l'indice corrente nell'attributo di istanza `index`
     * ed effettua uno scambio (swap) con l'elemento successivo (indice + 1).
     */
    @Override
    public void execute(){
        int currentIndex=list.indexOf(track);
        if(currentIndex >=0 && currentIndex < list.size()-1){
            this.index=currentIndex;
            java.util.Collections.swap(list, currentIndex, currentIndex + 1);
        }
    }
    /**
     * @brief Ripristina lo stato precedente della lista annullando lo spostamento.
     * Se l'indice memorizzato durante l'esecuzione è valido e rispetta i limiti della lista,
     * effettua nuovamente uno swap invertito tra la posizione `index + 1` e `index`, 
     * riportando la traccia esattamente alla sua posizione originaria.
     */
    @Override
    public void undo(){
        if(index >= 0 && index < list.size()-1){
            java.util.Collections.swap(list,index + 1,index);
        }
    }
    
}
