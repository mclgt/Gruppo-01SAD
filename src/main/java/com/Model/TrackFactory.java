package com.Model;

/**
 * @brief classe astratta per la creazione dei brani musicali.
 *        Definisce la base dell'algoritmo di creazione di una traccia,
 *        centralizzando la validazione dei dati in comune (come la presenza di
 *        un path non nullo). L'effettiva instanziazione viene delegata alla
 *        sottoclasse concreta.
 */
public abstract class TrackFactory {

    /**
     * @brief Il metodo è final per impedire alle sottoclassi di alterare la
     *        sequenza di creazione. Esegue prima la validazione del path e delega
     *        la creazione fisica all'implementazione concreta. Sfrutta il Template
     *        Method Pattern e rappresenta il template di base.
     * @param title    titolo del brano
     * @param author   autore del brano
     * @param year     anno di pubblicazione
     * @param genre    genere musicale
     * @param duration durata del brano espressa in secondi
     * @param album    album di appartenenza
     * @param filePath percorso del file audio
     * @param tag      tag associato al brano
     * @return Track istanza del brano opportunamente validata
     * @throws IllegalArgumentException se la validazione del percorso fallisce
     */
    public final Track createTrack(String title, String author, int year, String genre, int duration, String album,
            String filePath, TrackTag tag) {
        validatePath(filePath);
        return instantiateTrack(title, author, year, genre, duration, album, filePath, tag);
    }

    /**
     * @brief Esegue la validazione del percorso del file, assicurandosi che la
     *        stringa fornita non sia nulla o vuota. La validazione viene sempre
     *        fatta, a prescindere dal tipo di traccia che si crea (traccia mock,
     *        traccia reale etc..)
     * @param filePath stringa che rappresenta il percorso
     * @throws IllegalArgumentException Se il parametro è nullo o composto da soli
     *                                  spazi
     */
    protected void validatePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Il campo 'File Audio' non può essere vuoto.");
        }
    }

    /**
     * @brief Factory Method astratto per istanziare una specifica traccia. Le
     *        sottoclassi implementano questo metodo per eseguire ulteriori
     *        validazioni e per restituire un oggetto Track coerente.
     * @param title    titolo del brano
     * @param author   autore del brano
     * @param year     anno di pubblicazione
     * @param genre    genere del brano
     * @param duration durata del brano espressa in secondi
     * @param album    album di appartenenza
     * @param filePath percorso del file
     * @param tag      tag associato al branp
     * @return Track la nuova istanza specifica restituita dalla sottoclasse.
     */
    public abstract Track instantiateTrack(String title, String author, int year, String genre, int duration,
            String album,
            String filePath, TrackTag tag);
}
