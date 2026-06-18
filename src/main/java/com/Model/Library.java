package com.Model;

import java.util.Collections;

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
public class Library implements ITrackContainer {
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
    @Override
    public void addTrack(Track track) {
        this.library.add(track);
    }

    /**
     * @brief Inserisce un nuovo brano in una posizione specifica all'interno della
     *        libreria musicale.
     * 
     * @param index La posizione in cui inserire il brano.
     * @param track L'oggetto Track (brano musicale) da aggiungere alla collezione.
     */
    @Override
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
     * @return True se il brano è stato rimosso, false altrimenti
     */
    @Override
    public boolean removeTrack(Track track) {
        return this.library.remove(track);
    }

    @Override
    public int indexOf(Track track) {
        return this.library.indexOf(track);
    }

    /**
     * @brief Aggiorna i dati di un brano esistente nella libreria.
     *        Rimpiazza l'oggetto nella lista osservabile per forzare
     *        l'aggiornamento automatico della UI.
     *        * @param track Il brano originale da modificare.
     * @param title    Il nuovo titolo da assegnare.
     * @param author   Il nuovo autore da assegnare.
     * @param year     Il nuovo anno da assegnare.
     * @param genre    Il nuovo genere da assegnare.
     * @param duration La nuova durata da assegnare.
     * @param album    Il nuovo album da assegnare.
     * @param filePath Il nuovo percorso del file audio.
     * @param tag      Il nuovo tag da assegnare al brano.
     */
    public void updateTrack(Track track, String title, String author, int year, String genre, int duration,
            String album, String filePath, TrackTag tag) {
        int index = this.library.indexOf(track);

        track.setTitle(title);
        track.setAuthor(author);
        track.setYear(year);
        track.setGenre(genre);
        track.setDuration(duration);
        track.setAlbum(album);

        if (filePath != null && !filePath.equals(track.getFilePath())) {
            track.setFilePath(filePath);
            track.setAudioSource(new TrackProxy(filePath));
        }

        if (tag != null) {
            track.setTag(tag);
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
    public ObservableList<Track> getTracks() {
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

    /**
     * @brief Sposta una traccia specifica verso il basso all'interno della libreria.
     * Il metodo individua la posizione attuale del brano. Se il brano è presente
     * e non occupa già l'ultima posizione della lista, effettua
     * uno scambio con il brano immediatamente successivo. 
     * @param track La traccia musicale (Track) da spostare verso il basso.
     * @return true se lo spostamento è avvenuto con successo, false se la traccia 
     * non è stata trovata o si trovava già in fondo alla lista.
     */
    @Override
    public boolean moveTrackDown(Track track) {
        int currentIndex = library.indexOf(track);
        if (currentIndex >= 0 && currentIndex < library.size() - 1) {
            Collections.swap(library, currentIndex, currentIndex + 1);
            return true; 
        }
        return false;
    }

      /**
     * @brief Sposta una traccia specifica verso l'alto all'interno della libreria.
     * Il metodo individua la posizione attuale del brano. Se il brano è presente
     * e non occupa la prima posizione, effettua
     * uno scambio con il brano immediatamente precedente.
     * @param track La traccia musicale (Track) da spostare verso l'alto.
     * @return true se lo spostamento è avvenuto con successo, false se la traccia
     * non è stata trovata o si trovava già in cima alla lista.
     */
    @Override
    public boolean moveTrackUp(Track track) {
        int currentIndex = library.indexOf(track);
        if (currentIndex > 0) {
            Collections.swap(library, currentIndex, currentIndex - 1);
            return true; 
        }
        return false;
    }


}