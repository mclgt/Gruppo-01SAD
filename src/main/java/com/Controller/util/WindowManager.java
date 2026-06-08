package com.Controller.util;

import java.io.IOException;
import java.util.Optional;

import com.Controller.core.MainController;
import com.Controller.playlist.AddModPlaylistController;
import com.Controller.track.AddModTrackController;
import com.Model.Playlist;
import com.Model.Track;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @brief Classe per la gestione delle finestre e dei popup di sistema.
 *        Gestisce il caricamento dei file FXML, l'inizializzazione dei
 *        controller e la visualizzazione degli alert o delle finestre.
 */
public class WindowManager {
    public final MainController mainController;

    /**
     * @brief Costruttore del WindowManager
     * @param mainController controller principale dell'applicazione,
     */
    public WindowManager(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * @brief Apre la finestra di dialogo dedicata alla creazione o modifica di un
     *        Brano. Si occupa di caricare il file FXML, passare il controller
     *        principale e l'eventuale traccia, per poi mostrare la finestra.
     * @param fxmlPath      percorso del file FXML da caricare
     * @param title         titolo da assegnare alla finestra
     * @param trackToModify eventuale traccia da modificare, null se si deve creare
     */
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

    /**
     * @brief Apre la finestra di dialogo per modificare una playlist. Carica il
     *        file FXML specificato, imposta il maincontroller e la playlist da
     *        modificare.
     * @param fxmlPath         percorso del file FXML da caricare
     * @param title            titolo da assegnare alla finestra
     * @param playlistToModify playlist da modificare
     */
    public void openPlaylistWindow(String fxmlPath, String title, Playlist playlistToModify, MainController mc) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent p = loader.load();
            AddModPlaylistController controller = loader.getController();
            controller.setMainController(mc);

            controller.setPlaylist(playlistToModify);

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(p));
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della finestra " + fxmlPath + ": " + e.getMessage());
        }
    }

    /**
     * @brief Mostra un warning all'utente. Interrompe il flusso dell'applicazione
     *        finché l'utente non chiude l'alert.
     * @param title   titolo della finestra
     * @param content messaggio da mostrare nel popup
     */
    public void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * @brief Mostra un popup informativo all'utente. Interrompe il flusso finché
     *        l'utente non chiude l'alert.
     * @param title   titolo della finestra di informazioni
     * @param content messaggio da mostrare nel popup
     */
    public void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * @brief Mostra un alert di errore all'utente: è una finestra di dialogo che
     *        notifica all'utente l'errore avvenuto. Blocca l'interfaccia finché non
     *        si chiude la finestra.
     * @param title titlo da visualizzare nella barra in alto
     * @param content   messaggio di errore
     */
    public void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public Optional<ButtonType> showConfirmation(String title, String content, Runnable onConfirm) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        return alert.showAndWait();
    }
}