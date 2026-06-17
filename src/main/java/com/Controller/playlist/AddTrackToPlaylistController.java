package com.Controller.playlist;

import com.Command.AddTrack;
import com.Command.ICommand;
import com.Controller.core.MainController;
import com.Model.Playlist;
import com.Model.Track;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;

public class AddTrackToPlaylistController {
    @FXML
    private ListView<Track> trackListView;

    private MainController mainController;
    private Playlist targetPlaylist;

    @FXML
    public void initialize() {
        trackListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        trackListView.setCellFactory(param -> new ListCell<Track>() {
            @Override
            protected void updateItem(Track track, boolean empty) {
                super.updateItem(track, empty);
                if (empty || track == null) {
                    setText(null);
                } else {
                    setText(track.getTitle() + " - " + track.getAuthor());
                }
            }
        });
    }

    public void initData(MainController mainController, Playlist targetPlaylist) {
        this.mainController = mainController;
        this.targetPlaylist = targetPlaylist;
        trackListView.setItems(mainController.getAppState().getLibrary().getTracks());
    }

    @FXML
    public void handleCancel(ActionEvent ev) {
        closeWindow();
    }

    @FXML
    public void handleAdd(ActionEvent ev) {
        var selectedTracks = trackListView.getSelectionModel().getSelectedItems();

        if (selectedTracks.isEmpty()) {
            mainController.getAppState().getWindowManager().showWarning("Attenzione",
                    "Seleziona almeno un brano da aggiungere.");
            return;
        }

        int addedCount = 0;
        for (Track track : selectedTracks) {
            if (targetPlaylist.getTracks().contains(track)) {
                mainController.getAppState().getWindowManager().showWarning("Brano Duplicato",
                        "Il brano '" + track.getTitle() + "' è già presente.");
            } else {
                ICommand addCmd = new AddTrack(targetPlaylist, track, mainController.getAppState().getTrackDAO());
                mainController.getAppState().getUndoManager().executeCommand(addCmd);
                addedCount++;
            }
        }

        if (addedCount > 0) {
            closeWindow();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) trackListView.getScene().getWindow();
        stage.close();
    }
}