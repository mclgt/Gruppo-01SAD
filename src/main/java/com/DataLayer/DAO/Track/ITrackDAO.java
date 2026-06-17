package com.DataLayer.DAO.Track;

import java.util.List;

import com.Model.Track;
/**
 * @interface ITrackDAO
 * @brief Interfaccia per il Data Access Object (DAO) delle Track.
 *
 * Stabilisce il contratto architetturale per le operazioni CRUD (Create, Read, Update, Delete)
 * e di interrogazione statistica per gli oggetti di tipo Track.
 */
public interface ITrackDAO {
    /**
     * @brief Salva una nuova traccia musicale nel database.
     * * @param track L'oggetto Track da salvare nel database.
     * @throws Exception Se si verifica un errore di connessione o di esecuzione della query di inserimento SQL.
     */
    void save(Track track) throws Exception;
    
    /**
     * @brief Aggiorna i metadati di una traccia già esistente nel database.
     * * Viene utilizzata per aggiornare informazioni mutabili del brano come il titolo o l'artista.
     * * @param track L'oggetto Track contenente i dati modificati da sovrascrivere.
     * @throws Exception Se la traccia non esiste o se si riscontrano problemi durante l'esecuzione dell'UPDATE SQL.
     */
    void update(Track track) throws Exception;
    
    /**
     * @brief Elimina una traccia dal database utilizzando il suo identificativo univoco.
     * * @param trackId L'identificativo della traccia da rimuovere.
     * @throws Exception Se si verifica un errore durante la rimozione fisica del record.
     */
    void delete(String trackId) throws Exception;
    
    /**
     * @brief Recupera l'elenco completo di tutte le tracce memorizzate nel database.
     * * @return List<Track> Una lista contenente tutte le istanze di Track estratte dal database.
     * @throws Exception Se si verifica un errore di lettura o di parsing dei dati SQL.
     */
    List<Track> getAll() throws Exception;
    
    /**
     * @brief Estrae la classifica delle tracce riprodotte più frequentemente.
     * * Ordina i brani in base al loro contatore di riproduzioni in ordine decrescente e ne limita il risultato.
     * * @param limit Il numero massimo di tracce da restituire nella lista.
     * @return List<Track> La lista delle tracce più ascoltate, fino al tetto massimo specificato dal parametro limit.
     * @throws Exception Se si verifica un errore nell'interrogazione o nell'ordinamento dei record statistici.
     */
    List<Track> getFrequentlyPlayed(int limit) throws Exception;
}