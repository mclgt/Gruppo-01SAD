package com.Model;

import com.DataLayer.TrackProxy;

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
 *      
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
     */
    public void addTrack(Track track) {
        this.library.add(track);
    }

    /**
     * @brief Inserisce un nuovo brano in una posizione specifica all'interno della libreria musicale.
     * 
     * @param index La posizione in cui inserire il brano.
     * @param track L'oggetto Track (brano musicale) da aggiungere alla collezione.
     */
    public void addTrack(int index, Track track) {
        this.library.add(index, track);
    }
    /**
     * @brief Rimuove un brano specifico dalla libreria musicale.
     *
     *        Il brano viene cercato e rimosso sfruttando l'implementazione del
     *        metodo equals della classe Track.
     *        Trattandosi di una lista osservabile, la rimozione scatena un evento
     *        di aggiornamento automatico sulla UI.
     *
     * @param track L'oggetto Track da eliminare dalla collezione
     * @return L'oggetto Track che è stato rimosso.
     */
    public Track removeTrack(Track track) {
        this.library.remove(track);
        return track;
    }

    /**
     * @brief Aggiorna i dati di un brano esistente nella libreria.
     * Rimpiazza l'oggetto nella lista osservabile per forzare l'aggiornamento automatico della UI.
     * * @param track Il brano originale da modificare.
     * @param title Il nuovo titolo da assegnare.
     * @param author Il nuovo autore da assegnare.
     * @param year Il nuovo anno da assegnare.
     * @param genre Il nuovo genere da assegnare.
     * @param duration La nuova durata da assegnare.
     * @param album Il nuovo album da assegnare.
     * @param filePath Il nuovo percorso del file audio.
     */
    public void updateTrack(Track track, String title, String author, int year, String genre, int duration, String album, String filePath) {
        int index = this.library.indexOf(track);

        track.setTitle(title);
        track.setAuthor(author);
        track.setYear(year);
        track.setGenre(genre);
        track.setDuration(duration);
        track.setAlbum(album);

        if(filePath != null && !filePath.equals(track.getFilePath())) {
            track.setFilePath(filePath);
            track.setAudioSource(new TrackProxy(filePath));
        }

        this.library.set(index, track);
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
