package com.Controller.track;

import java.util.Optional;

import com.Command.ICommand;
import com.Command.RemoveTrack;
import com.Controller.core.MainController;
import com.Model.Track;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * @class TrackTableController
 * @brief Controller per la gestione della tabella principale delle tracce
 *        musicali. La classe coordina la visualizzazione della libreria dei
 *        brani, la gestione delle selezioni, le modifiche e l'esecuzione del
 *        comando di rimozione.
 */
public class TrackTableController {
    private MainController mainController;

    private TableView<Track> trackTable;
    private VBox detailPanel;

    /**
     * @brief Inizializza il controller e configura il binding della tabella.
     *        Effettua un collegamento tra le proprietà delle Track e le colonne
     *        della tabella. Configura un listener per la selezione del brani e
     *        imposta lo stato iniziale del pannello dettagli.
     * @param mainController controller principale
     * @param trackTable     tableView contenente le tracce
     * @param titleCol       colonna del titolo
     * @param authorCol      colonna dell'autore
     * @param genreCol       colonna del genere
     * @param detailPanel    pannello dei dettagli da aggiornare alla selezione
     */
    public void init(MainController mainController, TableView<Track> trackTable, TableColumn<Track, String> titleCol,
            TableColumn<Track, String> authorCol, TableColumn<Track, String> genreCol, VBox detailPanel) {
        this.mainController = mainController;
        this.trackTable = trackTable;
        this.detailPanel = detailPanel;
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));

        trackTable.setItems(mainController.getLibrary().getTracks());
        detailPanel.setVisible(false);

        trackTable.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
            if (mainController != null && newVal != null) {
                mainController.updateDetailPanel(newVal);
                if (mainController.getPlaylistTableController() != null) {
                    mainController.getPlaylistTableController().clearSelection();
                }

            }
        });
    }

    /**
     * @brief Recupera la traccia selezionata in quell'istante nella tabella
     * @return la traccia selezionata, null se nessuna selezione è presente
     */
    public Track getSelectedTrack() {
        return trackTable.getSelectionModel().getSelectedItem();
    }

    /**
     * @brief Rimuove la selezione corrente dalla tabella
     */
    public void clearSelection() {
        trackTable.getSelectionModel().clearSelection();
    }

    /**
     * @brief Gestisce il click sullo sfondo della tabella per deselezionare il
     *        brano corrente
     * @param ev click del mouse
     */
    @FXML
    public void handleBckgroundClick(MouseEvent ev) {
        clearSelection();
    }

    /**
     * @brief Apre la finestra per l'aggiunta di una nuova traccia
     * @param ev pressione sul pulsante "Aggiungi"
     */
    public void openAddTrackWindow(ActionEvent ev) {
        mainController.getWindowManager().openWindow("/com/View/AddTrackView.fxml", "Aggiungi nuovo brano", null);
    }

    /**
     * @brief Apre la finestra per la modifica di una traccia selezionata
     * @param ev pressione sul opulsante "Modifica"
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
     * @brief Esegue la rimozione della traccia selezionata tramite il Command
     *        Pattern. Chiede conferma all'utente, esegue il comando RemoveTrack,
     *        gestisce l'Undo e aggiorna lo stato del Player nel caso in cui la
     *        traccia rimossa fosse in esecuzione.
     * @param ev pressione sul pulsante "Rimuovi"
     */
    public void handleRemoveTrack(ActionEvent ev) {
        Track selectedTrack = getSelectedTrack();
        if (selectedTrack == null) {
            mainController.getWindowManager().showWarning("Nessuna selezione",
                    "Seleziona una traccia dalla tabella da rimuovere.");
            return;
        }

        Optional<ButtonType> result = mainController.getWindowManager().showConfirmation("Conferma eliminazione", "Sei sicuro di voler eliminare il brano \"" + selectedTrack.getTitle() + "\"?", null);

        if (result.isPresent() && result.get() == ButtonType.OK) {
            ICommand removeCommand = new RemoveTrack(mainController.getLibrary(), selectedTrack);
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
}
