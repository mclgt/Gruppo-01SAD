package com.Controller.core;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import com.Controller.playlist.PlaylistController;

import com.Command.UndoManager;
import com.Controller.playback.PlaybackTimerManager;
import com.Controller.playback.PlayerController;
import com.Controller.playlist.AddTrackToPlaylistController;
import com.Controller.playlist.PlaylistController;
import com.Controller.playlist.PlaylistTableController;
import com.Controller.track.TrackTableController;
import com.Controller.util.WindowManager;
import com.Model.Library;
import com.Model.Track;
import com.Model.Playlist;
import com.Model.PlaylistCatalog;
import com.State.PlayerContext;
import com.Strategy.PlaybackContext;
import com.Strategy.SequentialStrategy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private Button btnUndo;
    @FXML
    private Button btnAddToPlaylist;

    @FXML
    private TableView<Playlist> playlistList;
    @FXML
    private TableColumn<Playlist, String> nameCol;
    private PlaylistController activePlaylistController;

    private final TrackTableController trackTableController = new TrackTableController();
    private final PlayerController playerController = new PlayerController();

    private PlayerContext playerContext;
    private final UndoManager undoManager = new UndoManager();
    private final PlaylistCatalog playlistCatalog = new PlaylistCatalog();
    private final PlaylistTableController playlistTableController = new PlaylistTableController();
    private final Deque<Boolean> deletedPlayingStack = new ArrayDeque<>();
    private final PlaybackTimerManager timerManager = new PlaybackTimerManager();
    private WindowManager windowManager = new WindowManager(this);

    private Library trackList = new Library();

    /***
     * @brief Inizializza i componenti dell'interfaccia grafica. Effettua il binding
     *        tra le colonne della tabella e le StringProperty del modello Track,
     *        sfruttando il pattern Observer per consentire l'aggiornamento in tempo
     *        reale.
     */
    @FXML
    public void initialize() {
        playerContext = new PlayerContext(new PlaybackContext(new SequentialStrategy()));
        btnUndo.disableProperty().bind(undoManager.undoDisabledProperty());

        // Inizialzzazione dei sotto-controller
        trackTableController.init(this, trackTable, titleCol, authorCol, genreCol, detailPanel);
        playerController.init(this, lblNowPlaying, lblCurrentTime, lblTotalTime, progressSlider);
        playlistTableController.init(this, playlistList, nameCol);
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

    public PlaylistController getPlaylistController() {
        return activePlaylistController;
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

    @FXML
    public void playSong() {
        playerController.playSong();
    }

    @FXML
    public void sequentialRip(ActionEvent ev) {
        playerController.sequentialRip(ev);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/View/PlaylistView.fxml"));
            VBox playlistViewNode = loader.load();

            PlaylistController playlistController = loader.getController();
            playlistController.setMainController(this);
            playlistController.setPlaylistData(selectedPlaylist);
            this.activePlaylistController = playlistController;

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
    public void restoreMainLibraryView() {
        if (centerContentArea != null && trackTable != null) {
            centerContentArea.getChildren().clear();
            centerContentArea.getChildren().add(trackTable);
            this.activePlaylistController = null;
        }
    }

    @FXML
    public void handleRemoveTrack(ActionEvent ev) {
        trackTableController.handleRemoveTrack(ev);
    }

    @FXML
    public void handleBackgroundClick(MouseEvent ev) {
        if (trackTableController != null) {
            trackTableController.clearSelection();
        }
        if (playlistTableController != null) {
            playlistTableController.clearSelection();
        }
        if (getPlaylistController() != null) {
            getPlaylistController().clearSelection();
        }
        updateDetailPanel(null);
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

            detailPanel.setVisible(true);
        }
    }

}