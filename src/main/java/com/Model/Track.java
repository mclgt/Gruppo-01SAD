package com.Model;

import com.DataLayer.IAudioTrack;
import javafx.beans.property.*;

/**
 * @brief Rappresenta l'entità del singolo brano musicale.
 *        Modella i dati relativi ad una traccia così come definiti dalla user
 *        story [US-1]. Utilizza le classi Property di JavaFX per consentire il
 *        binding con l'interfaccia grafica.
 * 
 *        Implementa dei controlli di validazione interni per evitare che si
 *        violino le regole relative agli agttributi dell'oggetto.
 * 
 */
public class Track {
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty author = new SimpleStringProperty();
    private final IntegerProperty year = new SimpleIntegerProperty();
    private final StringProperty genre = new SimpleStringProperty();
    private final IntegerProperty duration = new SimpleIntegerProperty();
    private final StringProperty album = new SimpleStringProperty();
    private final StringProperty filePath = new SimpleStringProperty();

    private IAudioTrack audioSource;

    /**
     * @brief Inizializza una nuova traccia musicale. Sebbene il costruttore sia
     *        pubblico,
     *        si utilizzerà l'apposito Factory Method, in quanto quest'ultimo
     *        implementa anche la verifica
     *        della validità del singolo brano musicale.
     *
     * @param title    Titolo del brano musicale
     * @param author   Autore
     * @param year     Anno di pubblicazione
     * @param genre    genere musicale
     * @param duration durata fisica del brano espressa in secondi
     * @param album    Album di appartenenza del brano
     * @param filePath Percorso del file audio da inserire
     */
    public Track(String title, String author, int year, String genre, int duration, String album, String filePath) {
        setTitle(title);
        setAuthor(author);
        setGenre(genre);
        setYear(year);
        setDuration(duration);
        setAlbum(album);
        setFilePath(filePath);
    }

    // Getter e setter classici
    public String getTitle() {
        return title.get();
    }

    public String getAuthor() {
        return author.get();
    }

    public int getYear() {
        return year.get();
    }

    public String getGenre() {
        return genre.get();
    }

    public int getDuration() {
        return duration.get();
    }

    public String getAlbum() {
        return album.get();
    }

    public String getFilePath() {
        return filePath.get();
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Il campo 'Titolo' non può essere vuoto");
        }
        this.title.set(title);
    }

    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Il campo 'Autore' non può essere vuoto");
        }
        this.author.set(author);
    }

    public void setYear(int year) {
        this.year.set(year);
    }

    public void setGenre(String genre) {
        this.genre.set(genre);
    }

    public void setDuration(int duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("Il campo 'Durata' non può avere valore negativo");
        }
        this.duration.set(duration);
    }

    public void setAlbum(String album) {
        this.album.set(album);
    }

    public void setFilePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Il percorso 'File Audio' non può essere vuoto!");
        }
        this.filePath.set(filePath);
    }

    // getter per il binding grafico

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty authorProperty() {
        return author;
    }

    public IntegerProperty yearProperty() {
        return year;
    }

    public StringProperty genreProperty() {
        return genre;
    }

    public IntegerProperty durationProperty() {
        return duration;
    }

    public StringProperty albumProperty() {
        return album;
    }

    public StringProperty filePathProperty() {
        return filePath;
    }

    /**
     * @brief Imposta la sorgente audio (Proxy) associata a questo brano.
     * @param audioSource L'oggetto che implementa la gestione audio.
     */
    public void setAudioSource(IAudioTrack audioSource) {
        this.audioSource = audioSource;
    }

    /**
     * @brief Restituisce la sorgente audio per avviare o fermare la riproduzione.
     * @return IAudioTrack L'interfaccia per controllare l'audio.
     */
    public IAudioTrack getAudioSource() {
        return this.audioSource;
    }

    /**
     * @brief Restituisce la durata del brano formattata come "MM:SS".
     */
    public String getFormattedDuration() {
        int totalSeconds = getDuration();
        int min = totalSeconds / 60;
        int sec = totalSeconds % 60;
        return String.format("%02d:%02d", min, sec);
    }

    /**
     * @brief Restituisce una rappresentazione formattata del brano
     * @return String: la rappresentazione del brano
     */
    @Override
    public String toString() {
        return title + " - " + author + "( " + year + ")" + "-" + genre + "(" + duration + "s ), " + album;
    }
}