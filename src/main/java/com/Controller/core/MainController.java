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
 * @class MainController
 * @brief Controller principale dell'applicazione.
 *
 *        Gestisce la schermata principale: tabella dei brani, pannello di
 *        dettaglio, controlli di riproduzione, navigazione tra playlist e
 *        sezione "Ascoltati di frequente". Coordina tutti i sotto-controller
 *        (TrackTableController, PlayerController, PlaylistTableController,
 *        SearchController) tramite l'oggetto {@link AppState}.
 *
 * @see AppState
 * @see com.Controller.playback.PlayerController
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
    private Button btnPlay, btnUndo, btnAddToPlaylist, btnAddTrack, btnEditTrack, btnRemoveTrack, btnNext, btnPrev;
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
     * @brief Inizializza i componenti dell'interfaccia grafica e i sotto-controller.
     *        Crea il {@link com.State.PlayerContext} con strategia di default,
     *        inizializza TrackTableController, PlayerController e
     *        PlaylistTableController passando i riferimenti ai componenti FXML.
     *        Registra i subscriber dell'Observer e carica la sezione Frequently Played.
     * @param appState Il contenitore centralizzato dello stato dell'applicazione.
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
     * @brief Aggiorna la sezione "Ascoltati di frequente" nella UI.
     *        Recupera dal DAO i 5 brani e le 5 playlist più riprodotti;
     *        se nessuno ha playCount &gt; 0 mostra un messaggio vuoto,
     *        altrimenti popola le tabelle e nasconde il messaggio.
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
     * @brief Salva in modo atomico l'intero stato dell'applicazione nel database SQLite.
     *        Svuota le tabelle esistenti e reinserisce tutti i brani e le playlist
     *        all'interno di una singola transazione. In caso di errore esegue il rollback.
     *        Invocato esclusivamente da {@link com.Main#stop()}.
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

    /**
     * @brief Restituisce il pulsante "Aggiungi a Playlist" della toolbar.
     *        Usato da sotto-controller per abilitare/disabilitare il pulsante
     *        in base alla selezione corrente.
     * @return Il Button FXML btnAddToPlaylist.
     */
    public Button getBtnAddToPlaylist() {
        return btnAddToPlaylist;
    }

    /**
     * @brief Mostra o nasconde i pulsanti di gestione brani (Aggiungi, Modifica, Rimuovi).
     *        Viene invocato quando si passa dalla vista libreria alla vista playlist
     *        e viceversa, per adattare la toolbar al contesto corrente.
     * @param visible {@code true} per rendere i pulsanti visibili, {@code false} per nasconderli.
     */
    public void setTrackManagementButtonVisible(boolean visible) {
        btnAddTrack.setVisible(visible);
        btnAddTrack.setManaged(visible);

        btnEditTrack.setVisible(visible);
        btnEditTrack.setManaged(visible);

        btnRemoveTrack.setVisible(visible);
        btnRemoveTrack.setManaged(visible);
    }

    /**
     * @brief Aggiunge un brano alla libreria principale tramite l'AppState.
     *        Chiamato dai sotto-controller dopo aver creato un nuovo Track.
     * @param track Il brano da aggiungere alla libreria.
     */
    public void addTrackMainTable(Track track) {
        appState.getLibrary().addTrack(track);
    }

    /**
     * @brief Notifica al sistema che un brano esistente è stato modificato.
     *        Aggiorna il PlayerController (per sincronizzare il brano in riproduzione)
     *        e il pannello di dettaglio laterale.
     * @param track Il brano che è stato modificato.
     */
    public void notifyTrackModified(Track track) {
        appState.getPlayerController().handleTrackModified(track);
        updateDetailPanel(track);
    }

    /**
     * @brief Gestisce l'evento di pressione sul pulsante di undo.
     *        Richiama il metodo undo() dell'UndoManager, che si occupa di
     *        annullare l'ultima operazione.
     * @param event L'evento generato dalla pressione del pulsante.
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
     *        Il testo del pulsante viene aggiornato da {@link #updatePlayPauseButton}.
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
     *        Chiamato da PlayerController ogni volta che lo stato cambia.
     * @param playing {@code true} mostra "⏸ Pause", {@code false} mostra "▶ Play".
     */
    public void updatePlayPauseButton(boolean playing) {
        if (btnPlay != null) {
            btnPlay.setText(playing ? "⏸ Pause" : "▶ Play");
        }
    }

    /**
     * @brief Aggiorna lo stato abilitato/disabilitato del pulsante Next.
     *        Delega la verifica della disponibilità al PlayerController.
     */
    public void updateNextButton() {
        if (btnNext != null) {
            btnNext.setDisable(!appState.getPlayerController().isNextAvailable());
        }
    }

    public void updatePrevButton() {
        if (btnPrev != null) {
            btnPrev.setDisable(!appState.getPlayerController().isPrevAvailable());
        }
    }

    /**
     * @brief Gestisce il click sul pulsante "Successivo": avanza al brano seguente
     *        secondo la modalità di riproduzione attiva.
     * @param ev L'evento generato dalla pressione del pulsante.
     */
    @FXML
    public void handleNext(ActionEvent ev) {
        appState.getPlayerController().handleNext(ev);
    }

    /**
     * @brief Gestisce il click sul pulsante "Precedente": torna al brano precedente
     *        secondo la modalità di riproduzione attiva.
     * @param ev L'evento generato dalla pressione del pulsante.
     */
    @FXML
    public void handlePrev(ActionEvent ev) {
        appState.getPlayerController().handlePrev(ev);
    }

    /**
     * @brief Apre la finestra di dialogo per l'aggiunta di un nuovo brano alla libreria.
     * @param ev L'evento generato dalla pressione del pulsante.
     */
    @FXML
    public void openAddTrackWindow(ActionEvent ev) {
        appState.getTrackTableController().openAddTrackWindow(ev);
    }

    /**
     * @brief Apre la finestra di dialogo per la modifica del brano selezionato.
     * @param ev L'evento generato dalla pressione del pulsante.
     */
    @FXML
    public void openModifyTrackView(ActionEvent ev) {
        appState.getTrackTableController().openModifyTrackView(ev);
    }

    /**
     * @brief Apre la finestra di dialogo per la creazione di una nuova playlist.
     * @param ev L'evento generato dalla pressione del pulsante.
     */
    @FXML
    public void openAddPlaylistView(ActionEvent ev) {
        appState.getWindowManager().openPlaylistWindow("/com/View/AddPlaylistView.fxml", "Nuova Playlist", null, this);
    }

    /**
     * @brief Apre la finestra per la creazione automatica di una playlist
     *        basata su criteri di filtraggio (tag, genere, anno, ecc.).
     * @param ev L'evento generato dalla pressione del pulsante.
     */
    @FXML
    public void openAutoPlaylistWindow(ActionEvent ev) {
        appState.getWindowManager().openAutoPlaylistWindow(this);
    }

    /**
     * @brief Sostituisce la vista centrale con la vista dettaglio della playlist selezionata.
     * @param selectedPlaylist La playlist da visualizzare.
     */
    public void openPlaylistView(Playlist selectedPlaylist) {
        navigator.loadPlaylistView(selectedPlaylist);
    }

    /**
     * @brief Apre la finestra "Aggiungi a Playlist" usando la playlist attualmente
     *        selezionata nella tabella delle playlist.
     */
    @FXML
    public void openAddTrackToPlaylistView() {
        Playlist selectedPlaylist = appState.getPlaylistTableController().getSelectedPlaylist();
        openAddTrackToPlaylistView(selectedPlaylist);
    }

    /**
     * @brief Apre la finestra "Aggiungi a Playlist" per la playlist specificata.
     *        Delegato al WindowManager per il caricamento dell'FXML.
     * @param selectedPlaylist La playlist a cui aggiungere i brani.
     */
    public void openAddTrackToPlaylistView(Playlist selectedPlaylist) {
        appState.getWindowManager().openAddTrackToPlaylistWindow(selectedPlaylist);
    }

    /**
     * @brief Ripristina la visualizzazione della libreria globale nell'area centrale,
     *        chiudendo di fatto la vista della playlist corrente.
     */
    public void restoreMainLibraryView() {
        navigator.restoreMainLibraryView(mainContentView);
    }

    /**
     * @brief Gestisce la rimozione del brano selezionato dalla libreria.
     *        Delega l'operazione al TrackTableController che usa il pattern Command.
     * @param ev L'evento generato dalla pressione del pulsante.
     */
    @FXML
    public void handleRemoveTrack(ActionEvent ev) {
        appState.getTrackTableController().handleRemoveTrack(ev);
    }

    /**
     * @brief Deseleziona brani e playlist quando l'utente clicca su un'area vuota
     *        della schermata. Percorre l'albero dei nodi per verificare che il click
     *        non sia avvenuto su un controllo JavaFX.
     * @param ev L'evento di click del mouse.
     */
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

    /**
     * @brief Apre la finestra di modifica per la playlist selezionata.
     * @param ev L'evento generato dalla pressione del pulsante.
     */
    @FXML
    public void openModPlaylistView(ActionEvent ev) {
        appState.getPlaylistTableController().openModPlaylistView(ev);
    }

    /**
     * @brief Aggiorna il pannello laterale di dettaglio con le informazioni del brano.
     *        Se il brano passato è null, nasconde il pannello. Gestisce anche la
     *        visibilità dell'etichetta del tag (non mostrata se il tag è NONE o null).
     * @param track Il brano da visualizzare, oppure null per nascondere il pannello.
     */
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
     * @brief Gestisce l'evento di eliminazione di una playlist selezionata.
     *        Delega l'operazione al PlaylistTableController che usa il pattern Command.
     * @param ev L'evento generato dalla pressione del pulsante.
     */
    @FXML
    public void handleDeletePlaylist(ActionEvent ev) {
        appState.getPlaylistTableController().handleDeletePlaylist(ev);
    }

    /**
     * @brief Abilita o disabilita i pulsanti di riordinamento (Su / Giù) in base
     *        alla posizione del brano selezionato nella lista.
     * @param disableUp   {@code true} per disabilitare il pulsante "Sposta su".
     * @param disableDown {@code true} per disabilitare il pulsante "Sposta giù".
     */
    public void setMoveButtonDisable(boolean disableUp, boolean disableDown) {
        if (btnUpTrack != null) {
            btnUpTrack.setDisable(disableUp);
        }
        if (btnDownTrack != null) {
            btnDownTrack.setDisable(disableDown);
        }
    }

    /**
     * @brief Sposta il brano selezionato di una posizione verso l'alto nella lista.
     * @param event L'evento generato dalla pressione del pulsante.
     */
    @FXML
    private void handleMoveUp(ActionEvent event) {
        appState.getTrackTableController().handleMoveUp(event);
    }

    /**
     * @brief Sposta il brano selezionato di una posizione verso il basso nella lista.
     * @param event L'evento generato dalla pressione del pulsante.
     */
    @FXML
    private void handleMoveDown(ActionEvent event) {
        appState.getTrackTableController().handleMoveDown(event);
    }

    /**
     * @brief Restituisce il riferimento all'AppState dell'applicazione.
     * @return L'istanza di {@link AppState} che contiene tutti i sotto-sistemi.
     */
    public AppState getAppState() {
        return appState;
    }

}
