package com.Command;

import com.DataLayer.DAO.Track.TrackDAO;
import com.Model.ITrackContainer;
import com.Model.Track;

/**
 * @class AddTrack
 * @brief Comando per l'aggiunta di una traccia a un contenitore.
 *        Implementa l'interfaccia ICommand e incapsula la logica
 *        per aggiungere un brano musicale (Track) a un contenitore
 *        (ITrackContainer),
 *        come ad esempio la libreria principale o una playlist.
 */
public class AddTrack implements ICommand {
    private final ITrackContainer receiver;
    private Track track;
    private TrackDAO trackDAO;

    /**
     * @brief Costruisce il comando per l'aggiunta di una traccia.
     * @param receiver Il contenitore (es. libreria o playlist) a cui aggiungere la
     *                 traccia.
     * @param track    La traccia musicale da aggiungere.
     * @param trackDAO L'oggetto per l'accesso ai dati responsabile del salvataggio
     *                 nel database.
     */
    public AddTrack(ITrackContainer receiver, Track track, TrackDAO trackDAO) {
        this.receiver = receiver;
        this.track = track;
        this.trackDAO = trackDAO;
    }

    /**
     * @brief Esegue il comando di aggiunta della traccia.
     *        Inserisce la traccia nel contenitore e
     *        tenta il salvataggio fisico nel database. Eventuali eccezioni
     *        generate dal database vengono intercettate e stampate
     *        per prevenire il blocco dell'applicazione.
     */
    @Override
    public void execute() {
        this.receiver.addTrack(this.track);
        try {
            if (trackDAO != null) {
                trackDAO.save(track);
            }
        } catch (Exception e) {
            System.err.println("Errore DB in AddTrack (execute): " + e.getMessage());
        }
    }

    /**
     * @brief Annulla il comando di aggiunta precedentemente eseguito.
     *        Rimuove la traccia dal contenitore e la elimina
     *        definitivamente dal database utilizzando il suo identificativo (ID).
     *        Eventuali errori durante la rimozione dal database vengono gestiti
     *        e stampati in modo sicuro.
     */
    @Override
    public void undo() {
        this.receiver.removeTrack(this.track);
        try {
            if (trackDAO != null) {
                trackDAO.delete(track.getId());
            }
        } catch (Exception e) {
            System.err.println("Errore DB in AddTrack (undo): " + e.getMessage());
        }
    }
}