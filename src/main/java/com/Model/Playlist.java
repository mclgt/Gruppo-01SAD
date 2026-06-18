package com.Model;

import java.util.Collections;
import java.util.UUID;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @brief Rappresenta la playlist musicale all'interno del sistema. La classe
 *        funge da contenitore per un insieme di brani (Track). Sfrutta le
 *        StringProperty e le ObsrvabeList per consentire all'interfaccia di
 *        "osservare" i cambiameti e aggiornarsi in tempo reale automaticamente
 *        quando i dettagli cambiano.
 */
public class Playlist implements ITrackContainer {
    private StringProperty name = new SimpleStringProperty();
    private ObservableList<Track> tracks;
    private final String id;
    private int playCount = 0;

    /**
     * @brief Costruttore della classe Playlist. Inizializza la playlist con un nome
     *        e crea una lista osservabile vuota per contenre i brani.
     * @param name nome da assegnare alla nuova playlist
     * @throws IllegalArgumentException se il nome è nullo o vuoto
     */
    public Playlist(String name) {
        setName(name);
        this.tracks = FXCollections.observableArrayList();
        this.id = UUID.randomUUID().toString();
    }

    /**
     * @brief Imposta o modifica il nome della playlist che non può essere vuoto.
     * @param name nuovo nome della playlist
     * @throws IllegalArgumentException se il nome è nullo o vuoto
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della playlist non può essere vuoto!");
        }
        this.name.set(name);
    }

    /**
     * @brief Restituisce il nome attuale della playlist in formato testuale
     * @return nome della playlist come stringa
     */
    public String getName() {
        return name.get();
    }

    public void setPlayCount(int playCount){
        if(playCount < 0){
            throw new IllegalArgumentException("Il contatore delle riproduzioni non può essere negativo!");
        }
        this.playCount = playCount;
    }

    public int getPlayCount(){
        return this.playCount;
    }

    /**
     * @brief Restituisce l'identificativo univoco della playlist
     * @return Stringa rappresentando l'ID della playlist
     */
    public String getId() {
        return id;
    }

    /**
     * @brief Restituisce la proprietà osservabile del nome.
     * @return oggetto StringProperty associato al nome
     */
    public StringProperty getNameProperty() {
        return this.name;
    }

    /**
     * @brief Aggiunge un brano alla playlist. Il brano viene aggiunto solo se non
     *        nullo e se non è presente nella lista.
     * @param track oggetto track da aggiungere.
     */
    @Override
    public void addTrack(Track track) {
        if (track != null && !tracks.contains(track)) {
            tracks.add(track);
        }
    }

    /**
     * @brief Aggiunge un brano alla playlist nel posto desiderato. Il brano viene
     *        aggiunto solo se non
     *        nullo e se non è presente nella lista.
     * @param track oggetto track da aggiungere.
     * @param index la posizione nella quale inserire il brano all'interno della
     *              playlist.
     */
    @Override
    public void addTrack(int index, Track track) {
        if (track != null && !tracks.contains(track)) {
            tracks.add(index, track);
        }
    }

    /**
     * @brief Rimuove un brano specifico dalla playlist tramite il riferimento
     *        all'oggetto.
     * @param track traccia da rimuovere
     * @return true se il brano è stato rimosso, false altrimenti
     */
    @Override
    public boolean removeTrack(Track track) {
        return this.tracks.remove(track);
    }

    @Override
    public int indexOf(Track track) {
        return this.tracks.indexOf(track);
    }

    /**
     * @brief Restituisce la lista di tutti i brani contenuti nella playlist.
     * @return ObservableList contenente i brani della playlist
     */
    public ObservableList<Track> getTracks() {
        return tracks;
    }

    /**
     * @brief Restituisce il numero di brani contenuti nella playlist.
     * @return il numero di brani effettivamente contenuti nella playlist.
     */
    public int getTracksCount() {
        return tracks.size();
    }

    /**
     * @brief Calcola la durata totale della playlist sommando la durata dei singoli
     *        brani
     * @return durata totale in secondi
     */
    public int getTotalDuration() {
        int total = 0;
        for (Track track : tracks) {
            total += track.getDuration();
        }

        return total;
    }

    /**
     * @brief Restituisce la durata totale della playlist formattata in modo
     *        leggibile. Se la durata supera l'ora usa il formato 'HH:MM:SS',
     *        altrimenti 'MM:SS'
     * @return la stringa formattata rappresentante la durata totale
     */
    public String getFormattedTotalDuration() {
        int total = this.getTotalDuration();
        int hours = total / 3600;
        int minutes = (total % 3600) / 60;
        int seconds = total % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    /**
     * @brief Incrementa di 1 unità il numero di riproduzioni della playlist.
     */
    public void incrementPlayCount() {
        this.playCount++;
    }

    /**
     * @brief rappresentazione testuale dell'oggetto
     * @return il nome della playlist
     */
    @Override
    public String toString() {
        return this.name.get();
    }

    /**
     * @brief Verifica l'uguaglianza tra due oggetti PLaylist. Il confronto si basa
     *        solo sull'ID univoco. Due istanze di playlist vrranno considerate
     *        uguali solo se hanno lo stesso ID.
     * 
     * @param o oggetto da confrontare
     * @return true se gli oggetti hanno lo stesso ID, false altrimenti
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Playlist))
            return false;
        Playlist playlist = (Playlist) o;
        return id.equals(playlist.id);
    }

    /**
     * @brief Calcola il codice hash per l'oggetto Playlist basandosi sul suo ID
     * @return Codice hash calcolato
     */
    @Override
    public int hashCode() {
        return id.hashCode();
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
        int currentIndex = tracks.indexOf(track);
        if (currentIndex >= 0 && currentIndex < tracks.size() - 1) {
            Collections.swap(tracks, currentIndex, currentIndex + 1);
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
        int currentIndex = tracks.indexOf(track);
        if (currentIndex > 0) {
            Collections.swap(tracks, currentIndex, currentIndex - 1);
            return true; 
        }
        return false;
    }
}