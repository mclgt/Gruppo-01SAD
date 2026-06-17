package com.Controller.core;

import java.util.ArrayDeque;
import java.util.Deque;

import com.Command.UndoManager;
import com.Controller.navigation.WindowManager;
import com.Controller.playback.PlaybackTimerManager;
import com.Controller.playback.PlayerController;
import com.Controller.playlist.PlaylistController;
import com.Controller.playlist.PlaylistTableController;
import com.Controller.track.SearchController;
import com.Controller.track.TrackTableController;
import com.DataLayer.DAO.Playlist.PlaylistDAO;
import com.DataLayer.DAO.Track.TrackDAO;
import com.Model.Library;
import com.Model.PlaylistCatalog;
import com.Model.TrackFactory;
import com.State.PlayerContext;
import com.Strategy.PlaybackContext;
import com.Strategy.SequentialStrategy;

/**
 * @brief Contenitore globale dello stato dell'applicazione.
 *        Funge da Facade e da contenitore per gestire delle Dipendenze.
 *        Mantiene i riferimenti a tutti i controller principali, ai manager di
 *        sistema,
 *        ai modelli dei dati (Libreria e Cataloghi) e ai DAO per l'accesso al
 *        database.
 *        Garantisce che l'intero sistema condivida un'unico stato corretto.
 */
public class AppState {
    private final PlayerController playerController;
    private final TrackTableController trackTableController;
    private final PlaylistTableController playlistTableController;
    private final UndoManager undoManager;
    private final PlaybackTimerManager timerManager;
    private final Library library;
    private PlaylistController playlistController;
    private final PlaylistCatalog playlistCatalog;
    private final PlayerContext playerContext;
    private final Deque<Boolean> deletedPlayingStack;
    private final TrackDAO trackDAO;
    private final PlaylistDAO playlistDAO;
    private final SearchController searchController;
    private WindowManager windowManager;

    /**
     * @brief Costruttore della classe AppState.
     *        * Inizializza tutte le dipendenze principali, inclusi i controller,
     *        i gestori di stato, i contesti di riproduzione e i DAO.
     *        * @param factory La factory utilizzata dal sistema per la creazione
     *        dei brani.
     */
    public AppState(TrackFactory factory) {
        this.playerController = new PlayerController();
        this.trackTableController = new TrackTableController();
        this.playlistTableController = new PlaylistTableController();
        this.undoManager = new UndoManager();
        this.timerManager = new PlaybackTimerManager();
        this.windowManager = new WindowManager(factory);
        this.library = new Library();
        this.playlistCatalog = new PlaylistCatalog();
        this.playerContext = new PlayerContext(new PlaybackContext(new SequentialStrategy()));
        this.deletedPlayingStack = new ArrayDeque<>();
        this.trackDAO = new TrackDAO(factory);
        this.playlistDAO = new PlaylistDAO(trackDAO);
        this.searchController = new SearchController();
    }

    /**
     * @brief Carica i dati iniziali dal database.
     *        Popola la libreria musicale e il catalogo delle playlist richiamando
     *        i rispettivi metodi di lettura dai Data Access Object (DAO).
     * @throws Exception Se si verifica un errore di connessione o lettura
     *                   dal database.
     */
    public void loadData() throws Exception {
        this.library.getTracks().addAll(trackDAO.getAll());
        this.playlistCatalog.getPlaylists().addAll(playlistDAO.getAll());
    }

    /**
     * @brief Imposta il gestore delle finestre e dei popup.
     * @param windowManager L'istanza di WindowManager da associare allo
     *                      stato.
     */
    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    /**
     * @brief Restituisce il gestore delle finestre corrente.
     * @return L'oggetto WindowManager in uso.
     */
    public WindowManager getWindowManager() {
        return windowManager;
    }

    /**
     * @brief Restituisce il controller responsabile della riproduzione audio.
     * @return L'istanza di PlayerController.
     */
    public PlayerController getPlayerController() {
        return playerController;
    }

    /**
     * @brief Restituisce il controller responsabile della gestione della libreria
     *        brani.
     * @return L'istanza di TrackTableController.
     */
    public TrackTableController getTrackTableController() {
        return trackTableController;
    }

    /**
     * @brief Restituisce il controller responsabile della gestione della tabella
     *        playlist.
     * @return L'istanza di PlaylistTableController.
     */
    public PlaylistTableController getPlaylistTableController() {
        return playlistTableController;
    }

    /**
     * @brief Restituisce il controller della singola playlist attualmente in
     *        visualizzazione.
     * @return L'istanza di PlaylistController, oppure null se nessuna playlist è
     *         aperta.
     */
    public PlaylistController getPlaylistController() {
        return playlistController;
    }

    /**
     * @brief Restituisce il gestore responsabile delle operazioni di Undo e Redo.
     * @return L'istanza di UndoManager.
     */
    public UndoManager getUndoManager() {
        return undoManager;
    }

    /**
     * @brief Restituisce il gestore del timer di riproduzione visiva.
     * @return L'istanza di PlaybackTimerManager.
     */
    public PlaybackTimerManager getTimerManager() {
        return timerManager;
    }

    /**
     * @brief Restituisce la libreria principale contenente tutti i brani.
     * @return L'istanza di Library.
     */
    public Library getLibrary() {
        return library;
    }

    /**
     * @brief Restituisce il catalogo contenente tutte le playlist create
     *        dall'utente.
     * @return L'istanza di PlaylistCatalog.
     */
    public PlaylistCatalog getPlaylistCatalog() {
        return playlistCatalog;
    }

    /**
     * @brief Restituisce il contesto di stato corrente del player audio.
     * @return L'istanza di PlayerContext.
     */
    public PlayerContext getPlayerContext() {
        return playerContext;
    }

    /**
     * @brief Restituisce lo stack che memorizza se un brano era in esecuzione al
     *        momento dell'eliminazione.
     * @return Lo stack di valori booleani.
     */
    public Deque<Boolean> getDeletedPlayingStack() {
        return deletedPlayingStack;
    }

    /**
     * @brief Restituisce il Data Access Object per i brani.
     * @return L'istanza di TrackDAO.
     */
    public TrackDAO getTrackDAO() {
        return trackDAO;
    }

    /**
     * @brief Restituisce il Data Access Object per le playlist.
     * @return L'istanza di PlaylistDAO.
     */
    public PlaylistDAO getPlaylistDAO() {
        return playlistDAO;
    }

    /**
     * @brief Restituisce il controller che si occupa della ricerca
     * @return controller che gestisce la ricerca
     */
    public SearchController getSearchController() {
        return searchController;
    }

    /**
     * @brief Associa dinamicamente il controller di una playlist quando viene
     *        aperta la relativa vista.
     * @param playlistController L'istanza del controller della playlist da salvare
     *                           nello stato globale.
     */
    public void setPlaylistController(PlaylistController playlistController) {
        this.playlistController = playlistController;
    }

}
