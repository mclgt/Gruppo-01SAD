package com.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @brief Gestisce la collezione globale (libreria) di tutti i brani musicali
 *        disponibili nel sistema.
 *
 *        Questa classe rappresenta il contenitore principale all'interno del
 *        Modello per la memorizzazione
 *        e la manipolazione delle tracce audio. Sfrutta le proprietà reattive
 *        di una ObservableList di JavaFX,
 *        consentendo alle componenti grafiche della View (come le TableView) di
 *        sincronizzarsi e aggiornarsi
 *        in tempo reale in seguito ad aggiunte o rimozioni, riducendo
 *        l'accoppiamento con il Controller.
 *        * @author Rebecca
 */
public class Library {
    /**
     * @brief Lista osservabile interna contenente gli oggetti di tipo Track
     *        presenti nella libreria.
     */
    private ObservableList<Track> library;

    /**
     * @brief Costruttore di default della classe Library.
     *
     *        Inizializza una nuova istanza di libreria musicale vuota creando una
     *        collezione
     *        osservabile mediante l'ausilio della factory
     *        FXCollections.observableArrayList().
     */
    public Library() {
        this.library = FXCollections.observableArrayList();
    }

    /**
     * @brief Inserisce un nuovo brano all'interno della libreria musicale.
     *
     * @param track L'oggetto Track (brano musicale) da aggiungere alla collezione.
     * @return Il riferimento alla ObservableList aggiornata contenente tutte le
     *         tracce della libreria.
     */
    public ObservableList<Track> addTrack(Track track) {
        this.library.add(track);
        return this.library;
    }

    /**
     * @brief Rimuove un brano specifico dalla libreria musicale.
     *
     *        Il brano viene cercato e rimosso sfruttando l'implementazione del
     *        metodo equals della classe Track.
     *        Trattandosi di una lista osservabile, la rimozione scatena un evento
     *        di aggiornamento automatico sulla UI.
     *
     * @param track L'oggetto Track da eliminare dalla collezione.
     */
    public void removeTrack(Track track) {
        this.library.remove(track);
    }

    /**
     * @brief Restituisce il riferimento alla lista osservabile completa dei brani.
     *
     *        Questo metodo permette alle classi esterne (come i Controller) di
     *        accedere alla collezione
     *        per effettuarne il binding con elementi grafici o per scorrere i brani
     *        in essa contenuti.
     *
     * @return Un oggetto ObservableList di tipo Track contenente tutti i brani in
     *         libreria.
     */
    public ObservableList<Track> getLibrary() {
        return library;
    }

    /**
     * @brief Calcola il numero totale di brani attualmente presenti nella libreria.
     *
     * @return Un valore intero che rappresenta la dimensione corrente della
     *         collezione (dimensione della lista).
     */
    public int getTracksCount() {
        return this.library.size();
    }
}
