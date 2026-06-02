package com.Command;

import java.util.ArrayDeque;
import java.util.Deque;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * @brief Classe che gestisce la cronologia dei comandi eseguiti. Rappresenta l'Invoker del
 * pattern Command. Mantiene uno storico LIFO dei comandi eseguiti. Fornisce inoltre
 * una BooleanProperty per consentire il binding con l'interfaccia grafica, disabilitando 
 * il pulsante di undo quando non ci sono comandi da annullare.
 *  */
public class UndoManager {
    private final Deque<ICommand> undoStack = new ArrayDeque<>();
    private BooleanProperty undoDisabled = new SimpleBooleanProperty(true);

    /** 
     * @brief Esegue il comando, aggiunge un comando allo storico, abilitando il pulsante di undo se necessario.
     * @param command Il comando da aggiungere allo storico.
    */
    public void executeCommand(ICommand command) {
        command.execute();
        undoStack.push(command);
        undoDisabled.set(false);
    }

    /**
     * @brief Annulla l'ultimo comando eseguito, se presente, e aggiorna 
     * lo stato del pulsante di undo.
     */
    public void undo(){
        if (!undoStack.isEmpty()) {
            ICommand command = undoStack.pop();
            command.undo();
        }
        undoDisabled.set(undoStack.isEmpty());
    }

    /**
     * @brief Restituisce la proprietà booleana associata allo stato dello storico.
     * @return BooleanProperty che vale true se lo storico è vuoto, false altrimenti.
     */
    public BooleanProperty undoDisabledProperty() {
        return undoDisabled;
    }

    /**
     * @brief Svuota completamente lo storico dei comandi e disabilita il pulsante di undo.
     */
    public void clear() {
        undoStack.clear();
        undoDisabled.set(true);
    }

}
