package com.Controller.core;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import com.Command.UndoManager;
import com.Controller.playback.PlaybackTimerManager;
import com.Controller.playback.PlayerController;
import com.Controller.playlist.AddTrackToPlaylistController;
import com.Controller.playlist.PlaylistController;
import com.Controller.playlist.PlaylistTableController;
import com.Controller.track.SearchController;
import com.Controller.track.TrackTableController;
import com.Controller.util.WindowManager;
import com.Model.Library;
import com.Model.Playlist;
import com.Model.PlaylistCatalog;
import com.Model.Track;
import com.Model.TrackFactory;
import com.Model.TrackTag;
import com.State.PlayerContext;
import com.Strategy.PlaybackContext;
import com.Strategy.SequentialStrategy;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @brief Controller principale dell'applicazione, gestisce la schermata
 *        principale e si occupa della visualizzazione della tabella dei brani
 *        musicali e dell'interazione con l'utente.
 */

public class MainController {
    @FXML
    private TableView<Track> trackTable;
    @FXML
    private TableColumn<Track, String> titleCol, authorCol, genreCol;
    @FXML
    private VBox detailPanel;
    @FXML
    private StackPane centerContentArea;
    @FXML
    private Label lblTitle, lblAuthor, lblAlbum, lblGenre, lblDuration, lblYear;
    @FXML
    private Label lblNowPlaying, lblCurrentTime, lblTotalTime;
    @FXML
    private Slider progressSlider;
    @FXML
    private Button btnPlay;
    @FXML
    private Button btnUndo;
    @FXML
    private Button btnAddToPlaylist;
    @FXML
    private Button btnAddTrack, btnEditTrack, btnRemoveTrack;
    @FXML
    private Label lblTagTitle;
    @FXML
    private Label lblTag;
    @FXML
    private TableView<Playlist> playlistList;
    @FXML
    private TableColumn<Playlist, String> nameCol;

    @FXML
    private TextField searchField;

    private PlaylistController playlistController;

    private final TrackTableController trackTableController = new TrackTableController();
    private final PlayerController playerController = new PlayerController();

    private PlayerContext playerContext;
    private final UndoManager undoManager = new UndoManager();
    private final PlaylistCatalog playlistCatalog = new PlaylistCatalog();
    private final PlaylistTableController playlistTableController = new PlaylistTableController();
    private final Deque<Boolean> deletedPlayingStack = new ArrayDeque<>();
    private final PlaybackTimerManager timerManager = new PlaybackTimerManager();
    private WindowManager windowManager;

    private SearchController searchController = new SearchController();

    private Library trackList = new Library();
    private TrackFactory factory;

    public MainController(TrackFactory factory) {
        this.factory = factory;
        windowManager = new WindowManager(this, factory);
    }

    /**
     * @brief Inizializza i componenti dell'interfaccia grafica e i
     *        sotto-controller.
     *        Crea il @ref PlayerContext con strategia sequenziale di default,
     *        inizializza
     * @ref TrackTableController, @ref PlayerController e @ref
     *      PlaylistTableController
     *      passando i riferimenti ai componenti FXML. Carica inoltre un set di
     *      brani
     *      demo nella libreria per facilitare i test sull'interfaccia.
     */
    @FXML
    public void initialize() {
        playerContext = new PlayerContext(new PlaybackContext(new SequentialStrategy()));
        btnUndo.disableProperty().bind(undoManager.undoDisabledProperty());

        // Inizialzzazione dei sotto-controller
        trackTableController.init(this, trackTable, titleCol, authorCol, genreCol, detailPanel, lblTitle, lblAuthor,
                lblAlbum, lblGenre, lblDuration, lblYear, lblTagTitle, lblTag);
        playerController.init(this, lblNowPlaying, lblCurrentTime, lblTotalTime, progressSlider);
        playlistTableController.init(this, playlistList, nameCol);
        searchController.init(trackList.getTracks(), trackTable);
        searchController.bindSearchField(searchField);

        trackList.addTrack(factory.instantiateTrack("Bohemian Rhapsody",    "Queen",             1975, "Rock",            354, "A Night at the Opera",   "dummy.mp3", TrackTag.NONE));
        trackList.addTrack(factory.instantiateTrack("Hotel California",      "Eagles",            1976, "Rock",            391, "Hotel California",        "dummy.mp3", TrackTag.NONE));
        trackList.addTrack(factory.instantiateTrack("Stairway to Heaven",    "Led Zeppelin",      1971, "Rock",            482, "Led Zeppelin IV",         "dummy.mp3", TrackTag.NONE));
        trackList.addTrack(factory.instantiateTrack("Smells Like Teen Spirit","Nirvana",          1991, "Grunge",          301, "Nevermind",               "dummy.mp3", TrackTag.NONE));
        trackList.addTrack(factory.instantiateTrack("Billie Jean",           "Michael Jackson",   1982, "Pop",             294, "Thriller",                "dummy.mp3", TrackTag.NONE));
        trackList.addTrack(factory.instantiateTrack("Like a Rolling Stone",  "Bob Dylan",         1965, "Folk Rock",       369, "Highway 61 Revisited",    "dummy.mp3", TrackTag.NONE));
        trackList.addTrack(factory.instantiateTrack("Purple Haze",           "Jimi Hendrix",      1967, "Psychedelic Rock",170, "Are You Experienced",     "dummy.mp3", TrackTag.NONE));
        trackList.addTrack(factory.instantiateTrack("Johnny B. Goode",       "Chuck Berry",       1958, "Rock and Roll",   162, "Chuck Berry Is on Top",   "dummy.mp3", TrackTag.NONE));
        trackList.addTrack(factory.instantiateTrack("What's Going On",       "Marvin Gaye",       1971, "Soul",            235, "What's Going On",         "dummy.mp3", TrackTag.NONE));
        trackList.addTrack(factory.instantiateTrack("Superstition",          "Stevie Wonder",     1972, "Funk",            245, "Talking Book",            "dummy.mp3", TrackTag.NONE));
        trackList.addTrack(factory.instantiateTrack("Good Vibrations",       "The Beach Boys",    1966, "Pop",             215, "Smiley Smile",            "dummy.mp3", TrackTag.NONE));
        trackList.addTrack(factory.instantiateTrack("Imagine",               "John Lennon",       1971, "Pop",             187, "Imagine",                 "dummy.mp3", TrackTag.NONE));
        trackList.addTrack(factory.instantiateTrack("Respect",               "Aretha Franklin",   1967, "Soul",            147, "I Never Loved a Man",     "dummy.mp3", TrackTag.NONE));
        trackList.addTrack(factory.instantiateTrack("Johnny Guitar",         "Peggy Lee",         1954, "Jazz",            181, "Black Coffee",            "dummy.mp3", TrackTag.NONE));
        trackList.addTrack(factory.instantiateTrack("Blue Suede Shoes",      "Elvis Presley",     1956, "Rock and Roll",   140, "Elvis Presley",           "dummy.mp3", TrackTag.NONE));
        trackList.addTrack(factory.instantiateTrack("Born to Run",           "Bruce Springsteen", 1975, "Rock",            270, "Born to Run",             "dummy.mp3", TrackTag.NONE));
        trackList.addTrack(factory.instantiateTrack("Yesterday",             "The Beatles",       1965, "Pop",             125, "Help!",                   "dummy.mp3", TrackTag.NONE));
        trackList.addTrack(factory.instantiateTrack("Lose Yourself",         "Eminem",            2002, "Hip-Hop",         326, "8 Mile Soundtrack",       "dummy.mp3", TrackTag.NONE));
        trackList.addTrack(factory.instantiateTrack("Blinding Lights",       "The Weeknd",        2019, "Synth-Pop",       200, "After Hours",             "dummy.mp3", TrackTag.NONE));
        trackList.addTrack(factory.instantiateTrack("Shape of You",          "Ed Sheeran",        2017, "Pop",             234, "Divide",                  "dummy.mp3", TrackTag.NONE));
    }

    /**
     * @brief Fornisce il riferimento alla libreria musicale (che funge da Receiver
     *        globale)
     * @return L'oggetto Library corrente.
     */
    public Library getLibrary() {
        return trackList;
    }

    public Button getBtnAddToPlaylist() {
        return btnAddToPlaylist;
    }

    /**
     * @brief Fornisce il riferimento all'Invoker del sistema per la gestione
     *        dell'Undo list.
     * @return L'oggetto UndoManager corrente.
     */
    public UndoManager getUndoManager() {
        return undoManager;
    }

    public PlaylistCatalog getPlaylistCatalog() {
        return playlistCatalog;
    }

    public PlaylistTableController getPlaylistTableController() {
        return playlistTableController;
    }

    public PlayerContext getPlayerContext() {
        return playerContext;
    }

    public PlaybackTimerManager getTimerManager() {
        return timerManager;
    }

    public Deque<Boolean> getDeletedPlayingStack() {
        return deletedPlayingStack;
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public TrackTableController getTrackTableController() {
        return trackTableController;
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

    public void setTrackManagementButtonVisible(boolean visible) {
        btnAddTrack.setVisible(visible);
        btnAddTrack.setManaged(visible);

        btnEditTrack.setVisible(visible);
        btnEditTrack.setManaged(visible);

        btnRemoveTrack.setVisible(visible);
        btnRemoveTrack.setManaged(visible);
    }

    public void addTrackMainTable(Track track) {
        trackList.addTrack(track);
    }

    /**
     * @brief Gestisce l'evento di pressione sul pulsante di undo.
     *        Richiama il metodo undo() dell'UndoManager, che si occupa di
     *        annullare l'ultima operazione.
     * @param event
     */
    @FXML
    public void handleUndo(ActionEvent event) {
        undoManager.undo();
        boolean wasPlayingDelete = !deletedPlayingStack.isEmpty() && deletedPlayingStack.pop();
        if (wasPlayingDelete) {
            timerManager.stop();
            playerController.resetUI();
            playerController.setTrackFinished(false);
            playerContext.setCurrentTrack(null);
        }
    }

    /**
     * @brief Pulsante unico play/pausa.
     *        Se non c'è un brano attivo (o è terminato) avvia la riproduzione;
     *        se il brano è in riproduzione lo mette in pausa;
     *        se è in pausa lo riprende dal punto in cui era stato fermato.
     *        Il testo del pulsante viene aggiornato da @ref updatePlayPauseButton.
     */
    @FXML
    public void togglePlayPause() {
        Track current = playerContext.getCurrentTrack();
        if (current != null && !playerController.isTrackFinished()) {
            playerController.pauseSong();
        } else {
            playerController.playSong();
        }
    }

    /**
     * @brief Aggiorna il testo del pulsante play/pausa in base allo stato.
     *        Chiamato da @ref PlayerController ogni volta che lo stato cambia.
     * @param playing {@code true} mostra "⏸ Pause", {@code false} mostra "▶ Play".
     */
    public void updatePlayPauseButton(boolean playing) {
        if (btnPlay != null) {
            btnPlay.setText(playing ? "⏸ Pause" : "▶ Play");
        }
    }

    @FXML
    public void sequentialRip(ActionEvent ev) {
        playerController.sequentialRip(ev);
    }

    @FXML
    public void loopRip(ActionEvent ev) {
        playerController.loopRip(ev);
    }

    @FXML
    public void shuffleRip(ActionEvent ev) {
        playerController.shuffleRip(ev);
    }

    @FXML
    public void handleNext(ActionEvent ev) {
        playerController.handleNext(ev);
    }

    @FXML
    public void handlePrev(ActionEvent ev) {
        playerController.handlePrev(ev);
    }

    @FXML
    public void openAddTrackWindow(ActionEvent ev) {
        trackTableController.openAddTrackWindow(ev);
    }

    @FXML
    public void openModifyTrackView(ActionEvent ev) {
        trackTableController.openModifyTrackView(ev);
    }

    @FXML
    public void openAddPlaylistView(ActionEvent ev) {
        windowManager.openPlaylistWindow("/com/View/AddPlaylistView.fxml", "Nuova Playlist", null, this);
    }

    public void openPlaylistView(Playlist selectedPlaylist) {
        try {
            setTrackManagementButtonVisible(false);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/View/PlaylistView.fxml"));
            VBox playlistViewNode = loader.load();

            playlistController = loader.getController();
            playlistController.setMainController(this);
            playlistController.setPlaylistData(selectedPlaylist);
            searchController.resetContext(selectedPlaylist.getTracks(), playlistController.getPlaylistTrackList());

            centerContentArea.getChildren().clear();
            centerContentArea.getChildren().add(playlistViewNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openAddTrackToPlaylistView() {
        Playlist selectedPlaylist = playlistTableController.getSelectedPlaylist();
        openAddTrackToPlaylistView(selectedPlaylist);
    }

    public void openAddTrackToPlaylistView(Playlist selectedPlaylist) {
        if (selectedPlaylist != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/View/AddTrackToPlaylistView.fxml"));
                Parent root = loader.load();

                AddTrackToPlaylistController controller = loader.getController();
                controller.initData(this, selectedPlaylist);

                Stage stage = new Stage();
                stage.setTitle("Aggiungi brani a " + selectedPlaylist.getName());
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Nessuna playlist selezionata!");
        }
    }

    /**
     * @brief Ripristina la visualizzazione della libreria globale nell'area
     *        centrale,
     *        chiudendo di fatto la vista della playlist.
     */
    public PlaylistController getPlaylistController() {
        return playlistController;
    }

    public void restoreMainLibraryView() {
        playlistController = null;
        setTrackManagementButtonVisible(true);
        searchController.resetContext(trackList.getTracks(), trackTable);
        if (centerContentArea != null && trackTable != null) {
            centerContentArea.getChildren().clear();
            centerContentArea.getChildren().add(trackTable);
        }
    }

    @FXML
    public void handleRemoveTrack(ActionEvent ev) {
        trackTableController.handleRemoveTrack(ev);
    }

    @FXML
    public void handleBackgroundClick(MouseEvent ev) {
        trackTableController.clearSelection();
        playlistTableController.clearSelection();
    }

    @FXML
    public void openModPlaylistView(ActionEvent ev) {
        playlistTableController.openModPlaylistView(ev);
    }

    public void updateDetailPanel(Track track) {
        if (track == null) {
            detailPanel.setVisible(false);
        } else {
            lblTitle.setText(track.getTitle());
            lblAuthor.setText(track.getAuthor());
            String album = track.getAlbum();
            lblAlbum.setText((album == null || album.trim().isEmpty()) ? "-" : track.getAlbum());
            String genre = track.getGenre();
            lblGenre.setText((genre == null || genre.trim().isEmpty()) ? "-" : track.getGenre());
            lblYear.setText(track.getYear() == 0 ? "-" : String.valueOf(track.getYear()));
            lblDuration.setText(track.getFormattedDuration());
            if (track.getTag() == null || track.getTag() == TrackTag.NONE) {
                lblTagTitle.setVisible(false);
                lblTag.setVisible(false);
            } else {
                lblTagTitle.setVisible(true);
                lblTag.setVisible(true);
                lblTag.setText(track.getTag().toString());
            }

            detailPanel.setVisible(true);
        }
    }

    /**
     * @brief Gestisce l'evento di eliminazione di una playlist.
     * @param ev
     */
    @FXML
    public void handleDeletePlaylist(ActionEvent ev) {
        playlistTableController.handleDeletePlaylist(ev);
    }
}