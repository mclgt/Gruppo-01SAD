package com.Controller.util;

import java.io.IOException;

import com.Controller.core.MainController;
import com.Controller.track.AddModTrackController;
import com.Controller.playlist.ModPlaylistController;
import com.Model.Track;
import com.Model.Playlist;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WindowManager {
    public final MainController mainController;

    public WindowManager(MainController mainController) {
        this.mainController = mainController;
    }

    // vale solo per i brani
    public void openWindow(String fxmlPath, String title, Track trackToModify) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent p = loader.load();

            AddModTrackController controller = loader.getController();
            controller.setMainController(mainController);

            if (trackToModify != null) {
                controller.setTrack(trackToModify);
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(p));
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della finestra " + fxmlPath + ": " + e.getMessage());
        }
    }

    public void openPlaylistWindow(String fxmlPath, String title, Playlist playlistToModify) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent p = loader.load();
            ModPlaylistController controller = loader.getController();
            controller.setMainController(mainController);
            if (playlistToModify != null) {
                controller.setPlaylist(playlistToModify);
            }
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(p));
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della finestra " + fxmlPath + ": " + e.getMessage());
        }
    }

    public void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}