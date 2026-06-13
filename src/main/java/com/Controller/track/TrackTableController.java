package com.Controller.track;

import java.util.Optional;

import com.Command.ICommand;
import com.Command.RemoveTrack;
import com.Controller.core.MainController;
import com.Model.Track;
import com.Observer.IPlayerSubscriber;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

/**
 * @class TrackTableController
 * @brief COntroller per la gestione della tabella della libreria musicale.
 *        Gestisce la visualizzazione, selezione e le operazioni di
 *        aggiunta/modifica/rimozione sui brani presenti nella libreria
 *        principale. Implementa l'interfaccia IPlayerSubscriber per
 *        sincronizzazre la visualizzazione della tabella con lo stato del
 *        player.
 */
public class TrackTableController implements IPlayerSubscriber {
    private MainController mainController;
    @FXML
    private Label lblTagTitle;
    private TableView<Track> trackTable;

    @FXML
    private Label lblTag;

    /**
     * @brief Inizializza il controller, configurando le colonne della tabella, i
     *        listener di selezione e la costruzione delle righe.
     * @param mainController controller principale
     * @param trackTable     tabella da inizializzare
     * @param titleCol       colonna del titolo
     * @param authorCol      colonna dell'autore
     * @param genreCol       colonna del genere
     * @param detailPanel    pannello di dettaglio del brano da aggiornare alla
     *                       selezione
     * @param lblTitle       label per il titolo nel pannello dettagli
     * @param lblAuthor      label per l'autore nel pannello dettagli
     * @param lblAlbum       label per l'album nel pannello dettagli
     * @param lblGenre       label per il genere nel pannello dettagli
     * @param lblDuration    label per la durata nel pannello dettagli
     * @param lblYear        label per l'anno nel pannello dettaglio
     * @param lblTagTitle    label per il titolo del tag nel pannello dettagli
     * @param lblTag         label per il contenuto del tag nel pannello dettagli
     */
    public void init(MainController mainController, TableView<Track> trackTable, TableColumn<Track, String> titleCol,
            TableColumn<Track, String> authorCol, TableColumn<Track, String> genreCol, VBox detailPanel,
            Label lblTitle, Label lblAuthor, Label lblAlbum, Label lblGenre, Label lblDuration, Label lblYear,
            Label lblTagTitle, Label lblTag) {
        this.mainController = mainController;
        mainController.getPlayerContext().subscribe(this);
        this.trackTable = trackTable;
        this.lblTagTitle = lblTagTitle;
        this.lblTag = lblTag;
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));

        trackTable.setItems(mainController.getLibrary().getTracks());
        detailPanel.setVisible(false);

        trackTable.setRowFactory(tv -> {
            TableRow<Track> row = new TableRow<>();
            row.itemProperty().addListener((observed, oldVal, newVal) -> {
                if (newVal != null && newVal.equals(mainController.getPlayerContext().getCurrentTrack())) {
                    row.setStyle("-fx-background-color: #f5a747;"); // cambia colore
                } else {
                    row.setStyle("");
                }

            });
            return row;
        });

        trackTable.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
            mainController.updateDetailPanel(newVal);
        });
    }

    /**
     * @brief Recupera la traccia selezionata nella tabella della libreria
     * @return L'oggetto Track selezionato, o null se non è selezionato nulla
     */
    public Track getSelectedTrack() {
        return trackTable.getSelectionModel().getSelectedItem();
    }

    /**
     * @brief Rimuove qualsiasi selezione corrente dalla tabella della libreria.
     */
    public void clearSelection() {
        trackTable.getSelectionModel().clearSelection();
    }

    /**
     * @brief Seleziona una traccia all'interno della tabella. Utilizzato dal player
     *        per mantenere la selezione sincronizzata con il brano in esecuzione.
     * @param track traccia da selezionare
     */
    public void selectTrack(Track track) {
        trackTable.getSelectionModel().select(track);
    }

    /**
     * @brief Apre la finestra per l'inserimento di un nuovo brano nella libreria.
     * @param ev pressione sul pulsante di aggiunta
     */
    public void openAddTrackWindow(ActionEvent ev) {
        mainController.getWindowManager().openWindow("/com/View/AddTrackView.fxml", "Aggiungi nuovo brano", null);
    }

    /**
     * @brief Apre la finestra per la modifica di un brano selezioanto nella
     *        libreria
     * @param ev pressione sul pulsante di modifica
     */
    public void openModifyTrackView(ActionEvent ev) {
        Track selectedTrack = getSelectedTrack();
        if (selectedTrack == null) {
            mainController.getWindowManager().showWarning("Nessuna selezione",
                    "Seleziona una traccia dalla tabella da modificare.");
            return;
        }

        mainController.getWindowManager().openWindow("/com/View/ModifyTrackView.fxml",
                "Modifica Brano - " + selectedTrack.getTitle(), selectedTrack);
    }

    /**
     * @brief Gestisce la rimozione di un brano selezionato dalla libreria: mostra
     *        una finestra di conferma, esegue il comando di rimozione tramite
     *        pattern Command e aggiorna il player se la traccia rimossa era in
     *        esecuzione.
     * @param ev evento di pressione del tasto
     */
    public void handleRemoveTrack(ActionEvent ev) {
        Track selectedTrack = getSelectedTrack();
        if (selectedTrack == null) {
            mainController.getWindowManager().showWarning("Nessuna selezione",
                    "Seleziona una traccia dalla tabella da rimuovere.");
            return;
        }
        Optional<ButtonType> result = mainController.getWindowManager().showConfirmation("Conferma rimozione",
                "Rimozione dalla libreria e da tutte le playlist",
                "Sei sicuro di voler rimuovere \"" + selectedTrack.getTitle() + "\"?", null);

        if (result.isPresent() && result.get() == ButtonType.OK) {
            ICommand removeCommand = new RemoveTrack(mainController.getLibrary(), selectedTrack,
                    mainController.getPlaylistCatalog());

            boolean wasPlaying = mainController.getPlayerContext().isPlaying()
                    && selectedTrack == mainController.getPlayerContext().getCurrentTrack();

            mainController.getDeletedPlayingStack().push(wasPlaying);
            if (wasPlaying) {
                mainController.getTimerManager().stop();
            }

            int idx = mainController.getLibrary().getTracks().indexOf(selectedTrack);
            mainController.getUndoManager().executeCommand(removeCommand);
            clearSelection();

            if (wasPlaying) {
                mainController.getPlayerController().handleTrackRemoval(idx);
            }
        }
    }

    /**
     * @brief Callback invocata dal PlayerContext ad ogni cambio traccia. Forza
     *        l'aggiornamento grafico per modificare il brano evidenziato, in modo
     *        che corrisponda con quello in esecuzione.
     * @param newTrack nuova traccia in riproduzione
     */
    @Override
    public void onPlaybackChanged(Track newTrack) {
        Platform.runLater(() -> {
            if(mainController != null){
                mainController.updateTop();
            }
            trackTable.refresh();
        });
    }

    /**
     * @brief Rilascia le risorse e annulla l'iscrizione al player per evitare
     *        problemi di memoria.
     */
    public void dispose() {
        mainController.getPlayerContext().unsubscribe(this);
    }
}