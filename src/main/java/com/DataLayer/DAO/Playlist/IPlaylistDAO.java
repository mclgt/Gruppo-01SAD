package com.DataLayer.DAO.Playlist;

import java.util.List;

import com.Model.Playlist;

/**
 * @interface IPlaylistDAO
 * @brief Interfaccia per il pattern Data Access Object (DAO) relativo alle
 *        Playlist.
 *        Definisce le operazioni CRUD (Create, Read, Update, Delete)
 *        e le query di dominio specifiche per le entità Playlist.
 */
public interface IPlaylistDAO {
    /**
     * @brief Salva una nuova playlist nel database.
     * @param playlist L'oggetto Playlist da persistere fisicamente.
     * @throws Exception Se si verifica un errore di connessione o scrittura sul
     *                   supporto di memorizzazione.
     */
    void save(Playlist playlist) throws Exception;

    /**
     * @brief Aggiorna i dati di una playlist preesistente nel database.
     *        Sincronizza lo stato attuale dell'oggetto con il record corrispondente
     *        nel database.
     * @param playlist L'oggetto Playlist contenente i dati aggiornati.
     * @throws Exception Se si verifica un errore durante l'operazione di
     *                   aggiornamento .
     */
    void update(Playlist playlist) throws Exception;

    /**
     * @brief Elimina in modo definitivo una playlist dal database.
     * @param playlistId L'identificativo univoco della playlist da rimuovere.
     * @throws Exception Se si verifica un errore durante l'esecuzione
     *                   dell'eliminazione.
     */
    void delete(String playlistId) throws Exception;

    /**
     * @brief Recupera l'intero catalogo delle playlist salvate.
     * @return Una lista di oggetti Playlist ricostruiti a partire dai record nel
     *         database.
     * @throws Exception Se si verifica un errore durante l'estrazione o la
     *                   mappatura dei dati.
     */
    List<Playlist> getAll() throws Exception;

    /**
     * @brief Recupera le playlist più ascoltate in base allo storico.
     *        Esegue una query interrogando il campo contatore delle riproduzioni,
     *        ordinando i risultati in senso decrescente e troncando la lista al
     *        limite richiesto.
     * @param limit Il numero massimo di playlist da restituire (es. le prime 5).
     * @return Una lista ordinata delle playlist con il maggior numero di
     *         riproduzioni.
     * @throws Exception Se si verifica un errore durante l'interrogazione del
     *                   database.
     */
    List<Playlist> getFrequentlyPlayed(int limit) throws Exception;
}