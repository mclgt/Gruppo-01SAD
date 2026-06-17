package com.Controller.core;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;
import com.Controller.navigation.ViewNavigator;
import com.Controller.playlist.PlaylistController;
import com.Controller.track.SearchController;
import com.DataLayer.DAO.DatabaseManager;
import com.Model.Playlist;
import com.Model.Track;
import com.Model.TrackTag;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

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
    private Button btnPlay, btnUndo, btnAddToPlaylist, btnAddTrack, btnEditTrack, btnRemoveTrack, btnNext;
    @FXML
    private Label lblTagTitle;
    @FXML
    private Label lblTag;
    @FXML
    private TableView<Playlist> playlistList;
    @FXML
    private TableColumn<Playlist, String> nameCol;
    @FXML
    private Button btnUpTrack;
    @FXML
    private Button btnDownTrack;
    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> playbackModeCombo;

    @FXML
    private Label lblEmptyHistoryMessage;
    @FXML
    private HBox frequentlyPlayedContainer;

    @FXML
    private TableView<Track> frequentlyPlayedTable;
    @FXML
    private TableColumn<Track, String> topTitleCol, topAuthorCol;
    @FXML
    private TableColumn<Track, Integer> topCountCol;

    @FXML
    private TableView<Playlist> frequentlyPlayedPlaylistTable;
    @FXML
    private TableColumn<Playlist, String> topPlaylistNameCol;
    @FXML
    private TableColumn<Playlist, Integer> topPlaylistCountCol;

    private AppState appState;
    private ViewNavigator navigator;
    private javafx.scene.Node mainContentView;

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
    public void init(AppState appState) {
        this.appState = appState;
        this.navigator = new ViewNavigator(centerContentArea, this);
        appState.getWindowManager().setMainController(this);
        btnUndo.disableProperty().bind(appState.getUndoManager().undoDisabledProperty());
        if (playbackModeCombo != null) {
            playbackModeCombo.setItems(FXCollections.observableArrayList(
                    "Singola", "Sequenziale", "Loop brano", "Shuffle"));

            playbackModeCombo.getSelectionModel().select("Singola");

            playbackModeCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    appState.getPlayerController().setPlaybackMode(newValue);
                }
            });
        }
        appState.getTrackTableController().init(this, trackTable, titleCol, authorCol, genreCol, detailPanel, lblTitle,
                lblAuthor, lblAlbum, lblGenre, lblDuration, lblYear, lblTagTitle, lblTag);

        appState.getSearchController().init(appState.getLibrary().getTracks(), trackTable);
        appState.getSearchController().bindSearchField(searchField);
        appState.getPlayerController().init(this, lblNowPlaying, lblCurrentTime, lblTotalTime, progressSlider);
        appState.getPlaylistTableController().init(this, playlistList, nameCol);
        appState.getPlayerContext().subscribe(appState.getPlaylistTableController());
        appState.getPlayerContext().subscribe(appState.getTrackTableController());
        appState.getPlaylistTableController().init(this, playlistList, nameCol);
        if (!centerContentArea.getChildren().isEmpty()) {
            mainContentView = centerContentArea.getChildren().get(0);
        }
        topTitleCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("title"));
        topAuthorCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("author"));
        topCountCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("playCount"));

        topPlaylistNameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));
        topPlaylistCountCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("playCount"));
        updateTop();
    }

    /**
     * @brief Aggiorna lo stato visivo dei tab Frequently Played (US-21)
     *        Compatibile con versioni Java 11+ tramite
     *        .collect(Collectors.toList())
     */

    public void updateTop() {
        javafx.application.Platform.runLater(() -> {
            try {
                List<Track> topTracks = appState.getTrackDAO().getFrequentlyPlayed(5);
                List<Playlist> topPlaylists = appState.getPlaylistDAO().getFrequentlyPlayed(5);

                boolean hasHistory = topTracks.stream().anyMatch(t -> t.getPlayCount() > 0)
                        ||
                        topPlaylists.stream().anyMatch(p -> p.getPlayCount() > 0);

                if (hasHistory) {
                    if (lblEmptyHistoryMessage != null) {
                        lblEmptyHistoryMessage.setVisible(false);
                        lblEmptyHistoryMessage.setManaged(false);
                    }
                    if (frequentlyPlayedContainer != null) {
                        frequentlyPlayedContainer.setVisible(true);
                        frequentlyPlayedContainer.setManaged(true);
                    }

                    List<Track> filteredTracks = topTracks.stream()
                            .filter(t -> t.getPlayCount() > 0)
                            .collect(Collectors.toList());
                    frequentlyPlayedTable.setItems(FXCollections.observableArrayList(
                            filteredTracks));

                    List<Playlist> filteredPlaylists = topPlaylists.stream()
                            .filter(p -> p.getPlayCount() > 0)
                            .collect(Collectors.toList());
                    frequentlyPlayedPlaylistTable.setItems(FXCollections.observableArrayList(
                            filteredPlaylists));
                } else {
                    if (frequentlyPlayedContainer != null) {
                        frequentlyPlayedContainer.setVisible(false);
                        frequentlyPlayedContainer.setManaged(false);
                    }
                    if (lblEmptyHistoryMessage != null) {
                        lblEmptyHistoryMessage.setVisible(true);
                        lblEmptyHistoryMessage.setManaged(true);
                    }
                }
            } catch (Exception e) {
                System.err.println("Errore nell'aggiornamento grafico del Frequently Played: " +
                        e.getMessage());
            }
        });
    }

    /**
     * @brief Pialla preventivamente il DB ed esegue il salvataggio atomico massivo
     *        tramite Transazione (Commit).
     *        Invocato esclusivamente dal metodo stop() della classe Main.
     */

    public void saveDB() {
        System.out.println("Avvio fallback di chiusura ...");
        Connection c = null;
        try {
            c = DatabaseManager.getConnection();
            c.setAutoCommit(false); // Avviamo la transazione sicura

            try (Statement st = c.createStatement()) {
                st.executeUpdate("DELETE FROM playlist_tracks;");
                st.executeUpdate("DELETE FROM tracks;");
                st.executeUpdate("DELETE FROM playlists;");
            }

            for (Track track : appState.getLibrary().getTracks()) {
                appState.getTrackDAO().save(track);
            }

            for (Playlist playlist : appState.getPlaylistCatalog().getPlaylists()) {
                appState.getPlaylistDAO().save(playlist);
            }

            c.commit(); // Scrittura fisica bloccata sul file db
            System.out.println("Sincronizzazione finale completata. File SQLite aggiornato.");
        } catch (Exception e) {
            System.err.println("Errore durante la sincronizzazione di sicurezza: ");
            e.printStackTrace();
            if (c != null) {
                try {
                    c.rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (c != null)
                    c.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public Button getBtnAddToPlaylist() {
        return btnAddToPlaylist;
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
        appState.getLibrary().addTrack(track);
    }

    public void notifyTrackModified(Track track) {
        appState.getPlayerController().handleTrackModified(track);
        updateDetailPanel(track);
    }

    /**
     * @brief Gestisce l'evento di pressione sul pulsante di undo.
     *        Richiama il metodo undo() dell'UndoManager, che si occupa di
     *        annullare l'ultima operazione.
     * @param event
     */
    @FXML
    public void handleUndo(ActionEvent event) {
        appState.getUndoManager().undo();
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
        TableView<Track> activeTable = trackTable;
        PlaylistController activePlaylist = appState.getPlaylistController();
        if (activePlaylist != null) {
            activeTable = activePlaylist.getPlaylistTrackList();
        }

        appState.getPlayerController().togglePlayPause(appState.getPlayerContext(), activeTable);
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

    public void updateNextButton() {
        if (btnNext != null) {
            btnNext.setDisable(!appState.getPlayerController().isNextAvailable());
        }
    }

    @FXML
    public void handleNext(ActionEvent ev) {
        appState.getPlayerController().handleNext(ev);
    }

    @FXML
    public void handlePrev(ActionEvent ev) {
        appState.getPlayerController().handlePrev(ev);
    }

    @FXML
    public void openAddTrackWindow(ActionEvent ev) {
        appState.getTrackTableController().openAddTrackWindow(ev);
    }

    @FXML
    public void openModifyTrackView(ActionEvent ev) {
        appState.getTrackTableController().openModifyTrackView(ev);
    }

    @FXML
    public void openAddPlaylistView(ActionEvent ev) {
        appState.getWindowManager().openPlaylistWindow("/com/View/AddPlaylistView.fxml", "Nuova Playlist", null, this);
    }

    @FXML
    public void openAutoPlaylistWindow(ActionEvent ev) {
        appState.getWindowManager().openAutoPlaylistWindow(this);
    }

    public void openPlaylistView(Playlist selectedPlaylist) {
        navigator.loadPlaylistView(selectedPlaylist);
    }

    @FXML
    public void openAddTrackToPlaylistView() {
        Playlist selectedPlaylist = appState.getPlaylistTableController().getSelectedPlaylist();
        openAddTrackToPlaylistView(selectedPlaylist);
    }

    public void openAddTrackToPlaylistView(Playlist selectedPlaylist) {
        /*
         * if (selectedPlaylist != null) {
         * try {
         * FXMLLoader loader = new
         * FXMLLoader(getClass().getResource("/com/View/AddTrackToPlaylistView.fxml"));
         * Parent root = loader.load();
         * 
         * AddTrackToPlaylistController controller = loader.getController();
         * controller.initData(this, selectedPlaylist);
         * 
         * Stage stage = new Stage();
         * stage.setTitle("Aggiungi brani a " + selectedPlaylist.getName());
         * stage.setScene(new Scene(root));
         * stage.show();
         * } catch (Exception ex) {
         * ex.printStackTrace();
         * }
         * } else {
         * System.out.println("Nessuna playlist selezionata!");
         * }
         */
        appState.getWindowManager().openAddTrackToPlaylistWindow(selectedPlaylist);
    }

    /**
     * @brief Ripristina la visualizzazione della libreria globale nell'area
     *        centrale,
     *        chiudendo di fatto la vista della playlist.
     */
    /*
     * public PlaylistController getPlaylistController() {
     * return appState.playlistController;
     * }
     */

    public void restoreMainLibraryView() {
        navigator.restoreMainLibraryView(mainContentView);
    }

    @FXML
    public void handleRemoveTrack(ActionEvent ev) {
        appState.getTrackTableController().handleRemoveTrack(ev);
    }

    @FXML
    public void handleBackgroundClick(MouseEvent ev) {
        javafx.scene.Node node = (javafx.scene.Node) ev.getTarget();
        while (node != null) {
            if (node instanceof javafx.scene.control.Control)
                return;
            node = node.getParent();
        }
        appState.getTrackTableController().clearSelection();
        appState.getPlaylistTableController().clearSelection();
    }

    @FXML
    public void openModPlaylistView(ActionEvent ev) {
        appState.getPlaylistTableController().openModPlaylistView(ev);
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
        appState.getPlaylistTableController().handleDeletePlaylist(ev);
    }

    public void setMoveButtonDisable(boolean disableUp, boolean disableDown) {
        if (btnUpTrack != null) {
            btnUpTrack.setDisable(disableUp);
        }
        if (btnDownTrack != null) {
            btnDownTrack.setDisable(disableDown);
        }
    }

    @FXML
    private void handleMoveUp(ActionEvent event) {
        appState.getTrackTableController().handleMoveUp(event);
    }

    @FXML
    private void handleMoveDown(ActionEvent event) {
        appState.getTrackTableController().handleMoveDown(event);
    }

    public AppState getAppState() {
        return appState;
    }

}