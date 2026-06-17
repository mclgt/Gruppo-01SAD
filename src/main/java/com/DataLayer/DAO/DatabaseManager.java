package com.DataLayer.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @class DatabaseManager
 * @brief Gestore centralizzato e sicuro della connessione al database SQLite.
 *
 * Questa classe implementa una gestione centralizzata della connessione JDBC secondo una logica 
 * vicina al pattern Singleton (Lazy Initialization). Fornisce i metodi per aprire, 
 * chiudere e deviare la connessione su un database temporaneo in RAM per i test JUnit.
 */
public class DatabaseManager {
    private static String DB_URL = "jdbc:sqlite:data/musicplayer.db";
    private static Connection connection = null;

    /**
     * @brief Costruttore privato per impedire l'istanziazione diretta della classe.
     */
    private DatabaseManager() {}

    /**
     * @brief Restituisce la connessione attiva al database, inizializzandola se necessario (Lazy Loading).
     * * Se la connessione non è ancora stata creata o è stata precedentemente chiusa, provvede 
     * a caricare il driver JDBC, stabilire il canale e abilitare il supporto alle Foreign Key.
     * * @return Connection L'oggetto di connessione JDBC attivo e pronto all'uso.
     * @throws SQLException Se il driver SQLite non è presente nel classpath o se fallisce l'apertura del file di DB.
     */
    public static Connection getConnection() throws SQLException {
        if(connection == null || connection.isClosed()){
            try{
                // Carica dinamicamente il driver SQLite tramite Reflection
                Class.forName("org.sqlite.JDBC");

                // Stabilisce la connessione fisica usando l'URL attualmente configurato
                connection = DriverManager.getConnection(DB_URL);

                // Di default SQLite disabilita i vincoli d'integrità referenziale (Foreign Keys). 
                // Questo comando PRAGMA forza l'attivazione del controllo sui vincoli ad ogni apertura.
                connection.createStatement().execute("PRAGMA foreign_keys = ON;");
            }catch(ClassNotFoundException ex){
                throw new SQLException("Driver JDBC non trovato", ex);
            }
        }
        return connection;
    }

    /**
     * @brief Chiude in modo sicuro la connessione attualmente aperta, se valida.
     * Intercetta internamente le eventuali eccezioni SQL per permettere una chiusura pulita 
     * senza interrompere il flusso di terminazione dell'applicazione.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.getMessage();
            }
        }
    }

    /**
     * @brief Metodo speciale progettato esclusivamente per i test di unità JUnit.
     * * Se attivato, reindirizza l'URL del database verso un'istanza volatile allocata direttamente 
     * nella RAM del computer. Questo isola i test evitando di sporcare o corrompere il DB reale.
     * * @param isTest Flag booleano: 'true' per attivare la modalità isolata in RAM, 'false' per ripristinare il file fisico.
     */
    public static void setTestMode(boolean isTest) {
        if (isTest) {
            DB_URL = "jdbc:sqlite::memory:"; // Database temporaneo in RAM
        }
        closeConnection(); // Forza la chiusura immediata della connessione corrente
    }
}