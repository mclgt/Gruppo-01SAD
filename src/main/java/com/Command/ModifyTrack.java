package com.Command;

import com.DataLayer.DAO.Track.TrackDAO;
import com.Model.Library;
import com.Model.Track;
import com.Model.TrackTag;

/**
 * @class ModifyTrack
 * @brief Comando per la modifica dei dati di una traccia musicale.
 *        Implementa l'interfaccia ICommand e incapsula tutta la logica
 *        necessaria per aggiornare i dettagli di un brano (titolo, autore,
 *        anno, ecc.).
 *        Lo stato originale della traccia viene salvato
 *        al momento dell'istanziazione, permettendo un ripristino corretto.
 *        Le modifiche vengono applicate sia al modello in memoria sia
 *        salvate in modo persistente nel database tramite il TrackDAO.
 */
public class ModifyTrack implements ICommand {
    private final Library receiver;
    private final Track trackToModify;
    private final TrackDAO trackDAO;

    private final String oldTitle, oldAuthor, oldGenre, oldAlbum, oldFilePath;
    private final int oldYear, oldDuration;
    private final TrackTag oldTag;
    private final String newTitle, newAuthor, newGenre, newAlbum, newFilePath;
    private final int newYear, newDuration;
    private final TrackTag newTag;

    /**
     * @brief Costruisce il comando di modifica, memorizzando lo stato preesistente.
     *        Al momento della creazione, il comando estrae e congela i valori
     *        attuali della traccia per consentirel'operazione di undo,
     *        e prepara i nuovi valori da applicare.
     * @param receiver      La libreria principale che contiene la traccia.
     * @param trackToModify La traccia specifica da aggiornare.
     * @param newTitle      Il nuovo titolo del brano.
     * @param newAuthor     Il nuovo autore o artista.
     * @param newYear       L'anno di pubblicazione aggiornato.
     * @param newGenre      Il nuovo genere musicale.
     * @param newDuration   La nuova durata della traccia in secondi.
     * @param newAlbum      Il nuovo album di appartenenza.
     * @param newFilePath   Il nuovo percorso fisico del file audio.
     * @param newTag        Il nuovo tag associato al brano.
     * @param trackDAO      L'oggetto DAO per la persistenza delle modifiche.
     */
    public ModifyTrack(Library receiver, Track trackToModify, String newTitle, String newAuthor, int newYear,
            String newGenre, int newDuration, String newAlbum, String newFilePath, TrackTag newTag, TrackDAO trackDAO) {
        this.receiver = receiver;
        this.trackToModify = trackToModify;
        this.trackDAO = trackDAO;

        // Vecchi valori
        this.oldTitle = trackToModify.getTitle();
        this.oldAuthor = trackToModify.getAuthor();
        this.oldYear = trackToModify.getYear();
        this.oldGenre = trackToModify.getGenre();
        this.oldDuration = trackToModify.getDuration();
        this.oldAlbum = trackToModify.getAlbum();
        this.oldFilePath = trackToModify.getFilePath();
        this.oldTag = trackToModify.getTag();

        // Nuovi valori
        this.newTitle = newTitle;
        this.newAuthor = newAuthor;
        this.newYear = newYear;
        this.newGenre = newGenre;
        this.newDuration = newDuration;
        this.newAlbum = newAlbum;
        this.newFilePath = newFilePath;
        this.newTag = newTag;
    }

    /**
     * @brief Annulla il comando di modifica ripristinando i dati originali.
     *        Riapplica alla traccia tutti i valori salvati al momento della
     *        creazione del comando e sincronizza il ripristino con il database.
     *        Eventuali eccezioni di persistenza vengono gestite per non
     *        interrompere il flusso.
     */
    @Override
    public void undo() {
        receiver.updateTrack(trackToModify, oldTitle, oldAuthor, oldYear, oldGenre, oldDuration, oldAlbum, oldFilePath,
                oldTag);

        try {
            if (trackDAO != null)
                trackDAO.update(trackToModify);
        } catch (Exception e) {
            System.err.println("Errore DB in ModifyTrack (execute): " + e.getMessage());
        }
    }

    /**
     * @brief Esegue il comando applicando i nuovi dati alla traccia.
     *        Invia l'aggiornamento con i nuovi parametri alla libreria e,
     *        successivamente, esegue l'aggiornamento della riga corrispondente
     *        nel database tramite il DAO.
     */
    @Override
    public void execute() {
        receiver.updateTrack(trackToModify, newTitle, newAuthor, newYear, newGenre, newDuration, newAlbum, newFilePath,
                newTag);

        try {
            if (trackDAO != null)
                trackDAO.update(trackToModify);
        } catch (Exception e) {
            System.err.println("Errore DB in ModifyTrack (undo): " + e.getMessage());
        }
    }
}
