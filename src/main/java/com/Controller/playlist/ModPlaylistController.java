package com.Controller.playlist;

import com.Model.Playlist;

import com.Controller.core.MainController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModPlaylistController {
    @FXML
    private TextField txtPlaylistName;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnSave;
    private MainController mainController;
    private Playlist currentPlaylist;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setPlaylist(Playlist playlist) {
        this.currentPlaylist = playlist;
        if (playlist != null) {
            txtPlaylistName.setText(playlist.getName());
        }
    }

    @FXML
    public void handleSave(ActionEvent ev) {
        String name = txtPlaylistName.getText().trim();
        try {
            currentPlaylist.setName(name);
            closeWindow();
        } catch (IllegalArgumentException e) {
            mainController.getWindowManager().showWarning("Attenzione", e.getMessage());
        }
    }

    @FXML
    public void handleDelete(ActionEvent ev) {
        closeWindow();
    }

    public void closeWindow() {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

}
