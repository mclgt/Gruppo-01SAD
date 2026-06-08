package com.Controller.track;

import java.util.Optional;

import com.Command.ICommand;
import com.Command.RemoveTrack;
import com.Controller.core.MainController;
import com.Model.Track;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class TrackTableController {
    private MainController mainController;

    private TableView<Track> trackTable;
    private VBox detailPanel;

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

    public Track getSelectedTrack() {
        return trackTable.getSelectionModel().getSelectedItem();
    }

    public void clearSelection() {
        trackTable.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleBckgroundClick(MouseEvent ev) {
        clearSelection();
    }

    public void openAddTrackWindow(ActionEvent ev) {
        mainController.getWindowManager().openWindow("/com/View/AddTrackView.fxml", "Aggiungi nuovo brano", null);
    }

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

    public void handleRemoveTrack(ActionEvent ev) {
        Track selectedTrack = getSelectedTrack();
        if (selectedTrack == null) {
            mainController.getWindowManager().showWarning("Nessuna selezione",
                    "Seleziona una traccia dalla tabella da rimuovere.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma rimozione");
        alert.setContentText("Sei sicuro di voler rimuovere la traccia selezionata?");
        alert.setContentText(selectedTrack.getTitle());
        Optional<ButtonType> result = alert.showAndWait();

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
