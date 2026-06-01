package com.Controller;

import java.io.IOException;
import java.util.Optional;

import com.Command.ICommand;
import com.Command.RemoveTrack;
import com.Command.UndoManager;
import com.Model.Library;
import com.Model.Track;
import com.State.PlayerContext;
import com.Strategy.PlaybackContext;
import com.Strategy.SequentialStrategy;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @brief Controller principale dell'applicazione, gestisce la schermata
 *        principale e si occupa della visualizzazione della tabella dei brani
 *        musicali e dell'interazione con l'utente.
 */

public class MainController {
    @FXML
    private Button btnAddTrack;
    @FXML
    private Button btnPlay;
    @FXML
    private Button btnPause;
    @FXML
    private Button btnPrev;
    @FXML
    private Button btnNext;
    @FXML
    private Label lblNowPlaying;
    @FXML
    private TableView<Track> trackTable;
    @FXML
    private TableColumn<Track, String> titleCol;
    @FXML
    private TableColumn<Track, String> authorCol;
    @FXML
    private TableColumn<Track, String> genreCol;
    @FXML
    private VBox detailPanel;
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblAuthor;
    @FXML
    private Label lblAlbum;
    @FXML
    private Label lblGenre;
    @FXML
    private Label lblDuration;
    @FXML
    private Label lblYear;
    @FXML
    private Button btnUndo;

    private Library trackList = new Library();
    private PlayerContext playerContext;
    private Track currentTrack;
    private PauseTransition playbackTimer;
    private boolean sequentialMode = false;
    private UndoManager undoManager = new UndoManager();
    /***
     * @brief Inizializza i componenti dell'interfaccia grafica. Effettua il binding
     *        tra le colonne della tabella e le StringProperty del modello Track,
     *        sfruttando il pattern Observer per consentire l'aggiornamento in tempo
     *        reale.
     */
    @FXML
    public void initialize() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        trackTable.setItems(trackList.getLibrary());
        playerContext = new PlayerContext(new PlaybackContext(new SequentialStrategy()));
        detailPanel.setVisible(false);
        trackTable.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
            updateDetailPanel(newVal);
        });

        btnUndo.disableProperty().bind(undoManager.undoDisabledProperty());
    }

    /**
     * @brief Inserisce un nuovo brano nella tabella principale
     * @param track oggetto appena creato da aggiungere alla collezione
     */
    public void addTrackMainTable(Track track) {
        trackList.addTrack(track);
    }

    /**
     * @brief Apre la finestra di dialogo per l'aggiunta del nuovo brano, l'evento
     *        è generato a partire dalla pressione sul pulsante "Aggiungi Brano"
     * @param ev evento generato dalla pressione del pulsante
     */
    @FXML
    public void openAddTrackWindow(ActionEvent ev) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/View/AddTrackView.fxml"));
            Parent p = fxmlLoader.load();
            AddModTrackController controller = fxmlLoader.getController();
            controller.setMainController(this);
            Stage stage = new Stage();
            stage.setTitle("Aggiungi Nuovo Brano");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(p));
            stage.showAndWait();

        } catch (IOException ex) {
            System.err.print("Errore nel caricamento della finestra:" + ex.getMessage());
        }
    }

    /**
     * @brief Apre la finestra di dialogo per la modifica di un brano esistente.
     *        Recupera il brano selezionato e lo passa al controller. Se nessun
     *        brano è selezionato, mostra un avviso all'utente.
     * @param ev evento generato dalla pressione del pulsante
     */
    @FXML
    public void openModifyTrackView(ActionEvent ev) {
        Track selectedTrack = trackTable.getSelectionModel().getSelectedItem();
        if (selectedTrack == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nessuna selezione");
            alert.setContentText("Seleziona una traccia dalla tabella da modificare.");
            alert.showAndWait();
            return;
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/View/ModifyTrackView.fxml"));
            Parent p = fxmlLoader.load();
            AddModTrackController controller = fxmlLoader.getController();
            controller.setMainController(this);
            controller.setTrack(selectedTrack);
            Stage stage = new Stage();
            stage.setTitle("Modifica Brano -" + selectedTrack.getTitle());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(p));
            stage.showAndWait();
            trackTable.getSelectionModel().clearSelection();

        } catch (IOException ex) {
            System.err.print("Errore nel caricamento della finestra:" + ex.getMessage());
        }
    }

    /**
     * @brief Consente di deselezionare la riga della tabella se l'utente clicca su
     *        un'area vuota.
     * @param ev evento di pressione su una qualsiasi parte dello sfondo
     *           dell'interfaccia
     */
    @FXML
    public void handleBackgroundClick(MouseEvent ev) {
        trackTable.getSelectionModel().clearSelection();
    }

    /**
     * @brief Rimuove il brano selezionato dalla tabella principale, l'evento è
     *        generato a partire dalla pressione sul pulsante "Rimuovi Brano", viene
     *        mostrato a schermo un messaggio di conferma. Nel caso in cui non sia 
     *        selezionato alcun brano, viene mostrato un messaggio di avviso. Se il 
     *        brano rimosso è attualmente in riproduzione, la riproduzione viene 
     *        interrotta e viene avviata la traccia successiva,se presente.
     * @param event evento generato dalla pressione del pulsante
     */

    @FXML
    private void handleRemoveTrack(ActionEvent event) {
        Track selectedTrack = this.trackTable.getSelectionModel().getSelectedItem();
        if (selectedTrack != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Conferma rimozione");
            alert.setHeaderText("Sei sicuro di voler rimuovere la traccia selezionata?");
            alert.setContentText(selectedTrack.getTitle());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                ICommand removeCommand = new RemoveTrack(trackList.getLibrary(), selectedTrack);
                if (playerContext.isPlaying() && selectedTrack == playerContext.getCurrentTrack()) {
                    playbackTimer.stop();
                    Track before = playerContext.getCurrentTrack();
                    playerContext.next(trackList.getLibrary(),before);
                    this.currentTrack = playerContext.getCurrentTrack();
                    updateNowPlayingLabel();
                    startPlaybackTimer(this.currentTrack);  
                }          
            removeCommand.execute();
            undoManager.push(removeCommand);
            this.trackTable.getSelectionModel().clearSelection();
            }
        } else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nessuna selezione");
            alert.setHeaderText("Nessuna traccia selezionata");
            alert.setContentText("Seleziona una traccia da rimuovere.");
            alert.showAndWait();
        }
    }
/**
     * @brief Aggiorna il pannello di dettaglio in base alla traccia selezionato. Se
     *        non viene selezionata alcuna traccia, il pannello scompare.
     * @param t traccia selezionata dalla tabella
     */
    private void updateDetailPanel(Track t) {
        if (t == null) {
            detailPanel.setVisible(false);
        } else {
            detailPanel.setVisible(true);
            lblTitle.setText(t.getTitle());
            lblAuthor.setText(t.getAuthor());
            lblAlbum.setText(t.getAlbum());
            lblGenre.setText(t.getGenre());
            lblYear.setText(t.getYear() == 0 ? "-" : String.valueOf(t.getYear()));
            lblDuration.setText(t.getFormattedDuration());
        }
    }

    @FXML
    public void playSong() {
        Track selected = trackTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nessuna selezione");
            alert.setHeaderText("Nessuna traccia selezionata");
            alert.setContentText("Seleziona una traccia dalla lista per riprodurla.");
            alert.showAndWait();
            return;
        }
        if (playerContext.isPlaying() && selected == playerContext.getCurrentTrack()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Già in riproduzione");
            alert.setHeaderText(null);
            alert.setContentText("Sto già eseguendo questo brano.");
            alert.showAndWait();
            return;
        }
        sequentialMode = false;
        currentTrack = selected;
        playerContext.play(currentTrack);
        updateNowPlayingLabel();
        startPlaybackTimer(currentTrack);
    }

    @FXML
    public void SequentialRip(ActionEvent event) {
        Track selected = trackTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nessuna selezione");
            alert.setHeaderText("Nessuna traccia selezionata");
            alert.setContentText("Seleziona una traccia dalla lista per avviare la riproduzione sequenziale.");
            alert.showAndWait();
            return;
        }
        sequentialMode = true;
        currentTrack = selected;
        playerContext.play(currentTrack);
        updateNowPlayingLabel();
        startPlaybackTimer(currentTrack);
    }

    private void startPlaybackTimer(Track track) {
        if (playbackTimer != null)
            playbackTimer.stop();
        playbackTimer = new PauseTransition(Duration.seconds(track.getDuration()));
        playbackTimer.setOnFinished(e -> {
            if (!sequentialMode) {
                lblNowPlaying.setText("Canzone terminata");
                System.out.println(
                        "[PLAYER] Riproduzione singola terminata: " + playerContext.getCurrentTrack().getTitle());
                return;
            }
            Track before = playerContext.getCurrentTrack();
            playerContext.next(trackList.getLibrary(),before);
            Track after = playerContext.getCurrentTrack();
            if (after != null && after != before) {
                currentTrack = after;
                updateNowPlayingLabel();
                startPlaybackTimer(currentTrack);
            } else {
                lblNowPlaying.setText("Canzone terminata");
            }
        });
        playbackTimer.play();
    }

    @FXML
    public void handleNext(ActionEvent event) {
        if (playbackTimer != null)
            playbackTimer.stop();
        Track before = playerContext.getCurrentTrack();
        playerContext.next(trackList.getLibrary(),before);
        Track after = playerContext.getCurrentTrack();
        if (after != null && after != before) {
            currentTrack = after;
            updateNowPlayingLabel();
            startPlaybackTimer(currentTrack);
        }
    }

    @FXML
    public void handlePrev(ActionEvent event) {
        if (playbackTimer != null)
            playbackTimer.stop();
        Track before = playerContext.getCurrentTrack();
        playerContext.previous(trackList.getLibrary(),before);
        Track after = playerContext.getCurrentTrack();
        if (after != null && after != before) {
            currentTrack = after;
            updateNowPlayingLabel();
            startPlaybackTimer(currentTrack);
        }
    }

    private void updateNowPlayingLabel() {
        Track track = playerContext.getCurrentTrack();
        if (playerContext.isPlaying() && track != null) {
            lblNowPlaying.setText("▶  " + track.getTitle() + "  —  " + track.getAuthor());
        }
        else if (track == null){
            lblNowPlaying.setText("Nessuna traccia in riproduzione");
        }
    }
 
    /**
     * @brief Gestisce l'evento di pressione sul pulsante di undo.
     * Richiama il metodo undo() dell'UndoManager, che si occupa di
     * annullare l'ultima operazione.
     * @param event
     */
    @FXML
    public void handleUndo(ActionEvent event) {
        undoManager.undo();
    }
}