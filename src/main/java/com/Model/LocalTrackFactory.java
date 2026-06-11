package com.Model;

import java.io.File;

import com.DataLayer.TrackProxy;

/**
 * @brief Implementazione concreta della Factory per i brani. Estende la classe
 *        TrackFactort applicando il Factory Method. Questa classe si occupa di
 *        istanziare i brani che risiedono nel file system dell'utente,
 *        occupandosi di validare l'esistenza del file e di istanziare il proxy
 *        per la gestione dell'audio.
 * 
 */
public class LocalTrackFactory extends TrackFactory {

    /**
     * @brief Crea una nuova istanza di Track assicurandosi che ogni campo sia
     *        validato
     *        Implementa gli Acceptance Criteria della [US-1], verificando che il
     *        file specificato esistea sul sistema, se la validazione ha successo,
     *        l'entità viene creata e le viene associato un proxy per la lazy
     *        initialization.
     * 
     * @param title    Titolo del brano musicale
     * @param author   Autore del brano
     * @param year     Anno di pubblicazione del brano
     * @param album    Album a cui appartiene la traccia
     * @param genre    Genere musivale
     * @param duration Durata del brano espressa in secondi
     * @param filePath Indica il percorso del file da importare
     * @param tag      Tag associato al brano, se presente. Se null, viene impostato
     *                 a TrackTag.NONE
     * @return Track: Una nuova istanza validata dalla classe
     * @throws IllegalArgumentException se il file audio non esiste sul disco,
     *                                  oppure se i controlli interni relativi ai
     *                                  singoli attributi falliscono.
     */
    @Override
    public Track instantiateTrack(String title, String author, int year, String genre, int duration, String album,
            String filePath, TrackTag tag) {
        File audioFile = new File(filePath);
        if (!audioFile.exists() || !audioFile.isFile()) {
            throw new IllegalArgumentException("Errore: file audio mancante o non valido: " + filePath);
        }
        Track track = new Track(title.trim(), author.trim(), year, genre.trim(), duration, album.trim(),
                filePath.trim(), tag);

        TrackProxy proxy = new TrackProxy(filePath);
        track.setAudioSource(proxy);

        return track;
    }
}
