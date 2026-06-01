package com.Command;

/**
 * @brief Interfaccia base che definisce le operazioni di undo e execute per i comandi 
 * che modificano lo stato dell'applicazione, come ad esempio l'aggiunta o 
 * la rimozione di tracce dalla libreria.
 */
public interface ICommand {
    /**
     * @brief Annulla l'operazione eseguita dal comando, ripristinando lo stato precedente.
     */
    void undo();

    /**
     * @brief Esegue l'operazione definita dal comando.
     */
    void execute();
}
