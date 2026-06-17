package com.DataLayer.DAO.Track;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.DataLayer.DAO.DatabaseManager;
import com.Model.Track;
import com.Model.TrackFactory;
import com.Model.TrackTag;

/**
 * @class TrackDAO
 * @brief Implementazione concreta dell'interfaccia ITrackDAO per la persistenza delle tracce musicali.
 *
 * Questa classe si occupa di interfacciarsi con il database relazionale SQLite tramite JDBC
 * per eseguire tutte le operazioni di persistenza, aggiornamento, rimozione e recupero dei brani musicali.
 * Integra l'uso del pattern Abstract Factory per l'istanza controllata delle entità Track.
 */
public class TrackDAO implements ITrackDAO {
    private final TrackFactory factory;

    /**
     * @brief Costruttore che inizializza il DAO delle tracce.
     * @param factory La factory concreta da utilizzare per ricreare gli oggetti Track a partire dai record del DB.
     */
    public TrackDAO(TrackFactory factory) {
        this.factory = factory;
    }

    /**
     * @brief Salva una nuova traccia musicale nel database.
     * @param track L'oggetto Track contenente le informazioni da inserire.
     * @throws Exception Se si verifica un errore durante l'esecuzione della query SQL o se la connessione fallisce.
     */
    @Override
    public void save(Track track) throws Exception {
        if (track == null) {
            return; // Clausola di guardia per prevenire riferimenti nulli
        }

        String sql = "INSERT INTO tracks (id, title, author, year, genre, duration, album, file_path, play_count, tag) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Connection c = DatabaseManager.getConnection();
        try (PreparedStatement st = c.prepareStatement(sql)) {
            // Delega la configurazione dei parametri all'helper metodologico privato
            setTrackParameters(st, track);
            st.executeUpdate();
        }
    }

    /**
     * @brief Aggiorna i metadati e lo stato di una traccia esistente nel database.
     * @param track L'istanza di Track aggiornata localmente.
     * @throws Exception Se si verifica un errore di persistenza SQL.
     */
    @Override
    public void update(Track track) throws Exception {
        if (track == null) {
            return;
        }

        String sql = "UPDATE tracks SET id = ?, title = ?, author = ?, year = ?, genre = ?, "
                + "duration = ?, album = ?, file_path = ?, play_count = ?, tag = ? "
                + "WHERE id = ?;";

        Connection c = DatabaseManager.getConnection();
        try (PreparedStatement st = c.prepareStatement(sql)) {
            setTrackParameters(st, track);

            // Imposta l'id di destinazione nella clausola WHERE (undicesimo parametro)
            st.setString(11, track.getId());
            st.executeUpdate();
        }
    }

    /**
     * @brief Elimina una traccia dal database in base al suo identificativo.
     * @param trackId L'ID della traccia da rimuovere.
     * @throws Exception Se si verifica un errore SQL durante la cancellazione.
     */
    @Override
    public void delete(String trackId) throws Exception {
        String sql = "DELETE FROM tracks WHERE id = ?;";
        Connection c = DatabaseManager.getConnection();

        try (PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, trackId);
            st.executeUpdate();
        }
    }

    /**
     * @brief Recupera la lista completa di tutte le tracce presenti nel database.
     * @return List<Track> Lista di tutti i brani musicali trovati.
     * @throws Exception Se si verifica un errore di lettura o mapping dal ResultSet.
     */
    @Override
    public List<Track> getAll() throws Exception {
        List<Track> tracks = new ArrayList<>();
        String query = "SELECT * FROM tracks;";

        Connection c = DatabaseManager.getConnection();
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(query)) {

            // Itera su tutti i record restituiti dalla tabella dei brani
            while (rs.next()) {
                tracks.add(mapRowToTrack(rs));
            }
        }
        return tracks;
    }

    /**
     * @brief Estrae le tracce più ascoltate in ordine decrescente in base al contatore delle riproduzioni.
     * @param limit Numero massimo di risultati da includere nella lista.
     * @return List<Track> Elenco delle tracce più riprodotte.
     * @throws Exception Se fallisce l'interrogazione SQL parametrizzata.
     */
    @Override
    public List<Track> getFrequentlyPlayed(int limit) throws Exception {
        List<Track> topTracks = new ArrayList<>();
        String query = "SELECT * FROM tracks ORDER BY play_count DESC LIMIT ?;";

        Connection c = DatabaseManager.getConnection();
        try (PreparedStatement st = c.prepareStatement(query)) {
            st.setInt(1, limit);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    topTracks.add(mapRowToTrack(rs));
                }
            }
        }
        return topTracks;
    }

    /**
     * @brief Popola i parametri di un PreparedStatement con i dati di un oggetto Track.
     * @note Gestisce esplicitamente i tipi opzionali e i campi di testo che possono essere NULL nel database.
     * @param st Il PreparedStatement da configurare.
     * @param track La traccia da cui estrarre le informazioni.
     * @throws Exception Se l'indicizzazione dei campi non è coerente con la query chiamante.
     */
    private void setTrackParameters(PreparedStatement st, Track track) throws Exception {
        st.setString(1, track.getId());
        st.setString(2, track.getTitle());
        st.setString(3, track.getAuthor());
        st.setInt(6, track.getDuration());
        st.setString(8, track.getFilePath());
        st.setInt(9, track.getPlayCount());
        st.setString(10, track.getTag().name()); // Salva il nome dell'Enum come stringa SQL

        // Gestione campo facoltativo 'year'
        if (track.getYear() > 0) {
            st.setInt(4, track.getYear());
        } else {
            st.setNull(4, Types.INTEGER);
        }

        // Gestione campo facoltativo 'genre'
        if (track.getGenre() != null && !track.getGenre().trim().isEmpty()) {
            st.setString(5, track.getGenre());
        } else {
            st.setNull(5, Types.VARCHAR);
        }

        // Gestione campo facoltativo 'album'
        if (track.getAlbum() != null && !track.getAlbum().trim().isEmpty()) {
            st.setString(7, track.getAlbum());
        } else {
            st.setNull(7, Types.VARCHAR);
        }
    }

    /**
     * @brief Recupera tutte le tracce associate a una specifica playlist mediante un'interrogazione molti-a-molti.
     * @param playlistId L'id della playlist di cui si vogliono ottenere i brani.
     * @return List<Track> Lista delle tracce facenti parte della playlist selezionata.
     * @throws Exception Se si riscontrano errori nell'unione (INNER JOIN) delle tabelle relazionali.
     */
    public List<Track> getTracksByPlaylist(String playlistId) throws Exception {
        List<Track> tracks = new ArrayList<>();

        // Query con JOIN per navigare la tabella di legame molti-a-molti
        String query = "SELECT t.* FROM tracks t INNER JOIN playlist_tracks pt ON t.id = pt.track_id WHERE pt.playlist_id = ?;";

        Connection c = DatabaseManager.getConnection();
        try(PreparedStatement st = c.prepareStatement(query)){
            st.setString(1, playlistId);
            try(ResultSet rs = st.executeQuery()){

                while(rs.next()){
                    tracks.add(mapRowToTrack(rs));
                }
            }
        }
        return tracks;
    }

    /**
     * @brief Trasforma una riga estratta dal database in un oggetto strutturato di tipo Track.
     * @note Questo metodo implementa sia il pattern Template Method/Factory (demandando la creazione fisica 
     * dell'istanza a 'factory.createTrack') sia la Reflection Java per ripristinare l'id originario immutabile.
     * @param rs Il ResultSet posizionato sul record corrente.
     * @return Track L'oggetto Track completo di tutti i suoi metadati nativi.
     * @throws Exception Se fallisce l'estrazione dei tipi o l'aggiramento dei modificatori di visibilità del campo id.
     */
    private Track mapRowToTrack(ResultSet rs) throws Exception {
        // Normalizzazione del genere musicale se nullo nel database
        String genre = rs.getString("genre");
        if (genre == null) {
            genre = "";
        }

        // Normalizzazione del titolo dell'album se nullo nel database
        String album = rs.getString("album");
        if (album == null) {
            album = "";
        }

        // Conversione sicura della stringa memorizzata nell'equivalente valore Enum
        String tagStr = rs.getString("tag");
        TrackTag currentTag = TrackTag.NONE;
        if (tagStr != null && !tagStr.trim().isEmpty()) {
            currentTag = TrackTag.valueOf(tagStr);
        }

        // Gestione del tipo int: controlla se il valore estratto fosse effettivamente un NULL SQL
        int year = rs.getInt("year");
        if (rs.wasNull()) {
            year = 0; // Ripristina il valore di default per l'applicazione
        }

        // Delega la creazione dell'oggetto alla Factory concreta
        Track track = factory.createTrack(
                rs.getString("title"),
                rs.getString("author"),
                year,
                genre,
                rs.getInt("duration"),
                album,
                rs.getString("file_path"),
                currentTag);
        
        // Uso della reflection: scrittura forzata sull'attributo 'id' (che è immutabile e privo di setter pubblici)
        Field idField = Track.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(track, rs.getString("id"));

        // Configura il contatore degli ascolti del brano
        track.setPlayCount(rs.getInt("play_count"));

        return track;
    }
}