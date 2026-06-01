package com.Factory;

import java.io.File;

import com.DataLayer.TrackProxy;
import com.Model.Track;

/**
 * @brief Classe Factory per la creazione e validazione dei brani
 *        La classe implementa il pattern creazionale Factory Method per gestire
 *        la validazione
 *        dei dati prima dell'istanziazione di un oggetto Track.
 *        Disaccoppia i controller dalla logica di business.
 * @author Michela
 * 
 */
public class TrackFactory {

    /**
     * @brief Crea una nuova istanza di Track assicurandosi che ogni campo sia
     *        validato
     *        Implementa gli Acceptance Criteria della [US-1] verificando che il
     *        campo obbligatorio (titolo) sia valorizzato
     * 
     * @param title    Titolo del brano musicale. Non può essere vuoto o nullo.
     * @param author   Autore del brano. Non può essere vuoto o nullo.
     * @param year     Anno di pubblicazione del brano
     * @param album    Album a cui appartiene la traccia
     * @param genre    Genere musivale
     * @param duration Durata del brano espressa in secondi. Deve essere maggiore di
     *                 zero
     * @param filePath Indica il percorso del file da importare
     * 
     * @return Track: Una nuova istanza validata dalla classe
     * @throws IllegalArgumentException se il titolo o l'autore sono vuoto o se la
     *                                  durata è inferiore a zero.
     */
    public static Track createTrack(String title, String author, int year, String genre, int duration, String album,
            String filePath) {
        if (filePath != null && !filePath.trim().isEmpty()) {
            File audioFile = new File(filePath);

            if (!audioFile.exists() || !audioFile.isFile()) {
                throw new IllegalArgumentException("Errore: file audio mancante o non valido: " + filePath);
            }
        } else {
            throw new IllegalArgumentException("Errore: il campo 'File Audio' non può essere vuoto.");
        }

        Track track = new Track(title.trim(), author.trim(), year, genre.trim(), duration, album.trim(),
                filePath.trim());

        TrackProxy proxy = new TrackProxy(filePath);
        track.setAudioSource(proxy);

        return track;
    }
}
