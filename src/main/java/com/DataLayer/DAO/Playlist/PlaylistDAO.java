package com.DataLayer.DAO.Playlist;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.DataLayer.DAO.DatabaseManager;
import com.DataLayer.DAO.Track.TrackDAO;
import com.Model.Playlist;
import com.Model.Track;

/**
 * @class PlaylistDAO
 * @brief Implementazione concreta del Data Access Object per l'entità Playlist.
 *        Gestisce l'interazione fisica con il database SQLite per le playlist.
 *        Si occupa della persistenza dei dati della singola playlist e
 *        delle gestione della tabella ponte `playlist_tracks` per mantenere
 *        la relazione tra playlist e brani musicali. Sfrutta il TrackDAO
 *        per ricostruire la gerarchia completa degli oggetti durante le
 *        letture.
 */
public class PlaylistDAO implements IPlaylistDAO {

    private final TrackDAO trackDAO;

    /**
     * @brief Costruttore del DAO.
     *        Riceve un'istanza del TrackDAO, necessaria per caricare i brani fisici
     *        associati a ogni playlist
     *        estratta dal database.
     * @param trackDAO Il DAO responsabile per l'accesso ai dati delle singole
     *                 tracce.
     */
    public PlaylistDAO(TrackDAO trackDAO) {
        this.trackDAO = trackDAO;
    }

    /**
     * @brief Salva una nuova playlist e le sue associazioni nel database.
     *        Prima inserisce il record principale nella tabella `playlists`, poi
     *        itera sui brani contenuti e salva
     *        le relazioni nella tabella `playlist_tracks`.
     * @param playlist L'oggetto Playlist da persistere.
     * @throws Exception Se si verificano errori SQL durante l'inserimento.
     */
    @Override
    public void save(Playlist playlist) throws Exception {
        if (playlist == null) {
            return;
        }

        String sql = "INSERT INTO playlists (id, name, play_count) VALUES (?, ?, ?);";

        Connection c = DatabaseManager.getConnection();
        try (PreparedStatement st = c.prepareStatement(sql)) {
            setPlaylistParameters(st, playlist);
            st.executeUpdate();
        }

        String sqlRelation = "INSERT INTO playlist_tracks (playlist_id, track_id) VALUES (?, ?);";
        try (PreparedStatement st = c.prepareStatement(sqlRelation)) {
            for (Track t : playlist.getTracks()) {
                st.setString(1, playlist.getId());
                st.setString(2, t.getId());
                st.addBatch();
            }

            st.executeBatch();
        }
    }

    /**
     * @brief Aggiorna i dati di una playlist esistente.
     *        Modifica esclusivamente il record nella tabella `playlists`. Non
     *        intacca le relazioni con i brani.
     * @param playlist La playlist con i dati aggiornati.
     * @throws Exception Se si verificano errori SQL durante l'aggiornamento.
     */
    @Override
    public void update(Playlist playlist) throws Exception {
        if (playlist == null) {
            return;
        }

        String sql = "UPDATE playlists SET id = ?, name = ?, play_count = ? WHERE id = ?;";
        Connection c = DatabaseManager.getConnection();
        try (PreparedStatement st = c.prepareStatement(sql)) {
            setPlaylistParameters(st, playlist);

            st.setString(4, playlist.getId());
            st.executeUpdate();
        }
    }

    /**
     * @brief Rimuove definitivamente una playlist dal database.
     * @param playlistId L'ID della playlist da eliminare.
     * @throws Exception Se si verificano errori SQL durante la cancellazione.
     */
    @Override
    public void delete(String playlistId) throws Exception {
        String sql = "DELETE FROM playlists WHERE id = ?;";
        Connection c = DatabaseManager.getConnection();
        try (PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, playlistId);
            st.executeUpdate();
        }
    }

    /**
     * @brief Recupera dal database l'elenco completo di tutte le playlist.
     * @return Una lista di oggetti Playlist interamente "idratati", comprensivi
     *         delle rispettive liste di brani.
     * @throws Exception Se si verificano errori SQL durante l'interrogazione o la
     *                   mappatura.
     */
    @Override
    public List<Playlist> getAll() throws Exception {
        List<Playlist> playlists = new ArrayList<>();
        String query = "SELECT * FROM playlists;";

        Connection c = DatabaseManager.getConnection();
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                playlists.add(mapRowToPlaylist(rs));
            }
        }
        return playlists;
    }

    /**
     * @brief Recupera le playlist più ascoltate (Top Playlists).
     * @param limit Il numero massimo di risultati desiderati.
     * @return Lista delle playlist ordinate per numero decrescente di riproduzioni.
     * @throws Exception Se si verificano errori SQL.
     */
    @Override
    public List<Playlist> getFrequentlyPlayed(int limit) throws Exception {
        List<Playlist> topPlaylists = new ArrayList<>();
        String query = "SELECT * FROM playlists ORDER BY play_count DESC LIMIT ?;";

        Connection c = DatabaseManager.getConnection();
        try (PreparedStatement st = c.prepareStatement(query)) {
            st.setInt(1, limit);
            try (ResultSet rs = st.executeQuery()) {

                while (rs.next()) {
                    topPlaylists.add(mapRowToPlaylist(rs));
                }
            }
        }
        return topPlaylists;
    }

    /**
     * @brief Aggiunge la relazione tra un brano e una playlist nel database.
     * @param playlistId L'ID della playlist di destinazione.
     * @param trackId    L'ID della traccia da associare.
     * @throws Exception Se si verificano errori SQL durante l'inserimento.
     */
    public void addTrackToPlaylist(String playlistId, String trackId) throws Exception {
        String sql = "INSERT INTO playlist_tracks (playlist_id, track_id) VALUES (?, ?);";

        Connection c = DatabaseManager.getConnection();
        try (PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, playlistId);
            st.setString(2, trackId);

            st.executeUpdate();
        }
    }

    /**
     * @brief Rimuove la relazione tra un brano e una playlist dal database.
     * @param playlistId L'ID della playlist da cui rimuovere il brano.
     * @param trackId    L'ID della traccia da scollegare.
     * @throws Exception Se si verificano errori SQL.
     */
    public void removeTrackFromPlaylist(String playlistId, String trackId) throws Exception {
        String sql = "DELETE FROM playlist_tracks WHERE playlist_id = ? AND track_id = ?;";
        Connection c = DatabaseManager.getConnection();
        try (PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, playlistId);
            st.setString(2, trackId);

            st.executeUpdate();
        }
    }

    /**
     * @brief Carica e associa fisicamente i brani a un oggetto Playlist.
     *        Sfrutta il TrackDAO per interrogare la tabella di giunzione
     *        e
     *        recuperare tutte le tracce appartenenti all'ID della playlist passata
     *        in input.
     * @param p L'oggetto Playlist da popolare.
     * @throws Exception Se si verificano errori SQL.
     */
    private void loadTracks(Playlist p) throws Exception {
        List<Track> playlistTracks = trackDAO.getTracksByPlaylist(p.getId());
        p.getTracks().addAll(playlistTracks);
    }

    /**
     * @brief Funzione di utilità per il mapping dei parametri base della playlist.
     * @param st       Il PreparedStatement da configurare.
     * @param playlist L'oggetto Playlist da cui estrarre i dati.
     * @throws Exception Se si verificano errori durante il set dei parametri.
     */
    private void setPlaylistParameters(PreparedStatement st, Playlist playlist) throws Exception {
        st.setString(1, playlist.getId());
        st.setString(2, playlist.getName());
        st.setInt(3, playlist.getPlayCount());
    }

    /**
     * @brief Mappa una singola riga del ResultSet in un oggetto di dominio
     *        Playlist.
     * @param rs Il ResultSet puntato sulla riga corrente.
     * @return L'oggetto Playlist interamente ricostruito.
     * @throws Exception Se si verificano errori di SQL
     */
    private Playlist mapRowToPlaylist(ResultSet rs) throws Exception {
        Playlist p = new Playlist(rs.getString("name"));

        Field idField = Playlist.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(p, rs.getString("id"));

        p.setPlayCount(rs.getInt("play_count"));

        loadTracks(p);

        return p;
    }
}
