package com.Model;

/**
 * @brief Implementazione di una factoring per il testing
 * 
 *        Factory concreta utilizzata solo per i test. A differenza della
 *        LocalTrackFactory non controlla se il filePath fornito esiste
 *        effettivamente sul disco, consentendo di istanziare oggetti tarccia
 *        velocemente.
 */
public class MockTrackFactoryTest extends TrackFactory {
    /**
     * @brief Istanzia una traccia di test senza ulteriori validazioni.
     * @param title    titolo del brano
     * @param author   autore del brano
     * @param year     anno di pubblicazione
     * @param genre    genere del brano
     * @param duration durata del brano in secondi
     * @param album    album di appartenenza
     * @param filePath percorso del file (non necessariamente valido)
     * @param tag      tag associato al brano
     * @return Track L'istanza creata (i controlli di validazione interni
     *         rimangono attivi all'interno dell'entità Track).
     */
    @Override
    public Track instantiateTrack(String title, String author, int year, String genre, int duration, String album,
            String filePath, TrackTag tag) {
        return new Track(title.trim(), author.trim(), year, genre.trim(), duration, album.trim(), filePath.trim(), tag);
    }

}
