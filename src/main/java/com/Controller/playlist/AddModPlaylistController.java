package com.Controller.playlist;

import com.Model.Playlist;

import com.Controller.core.MainController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * @brief Controller dedicato alla finestra di dialogo per la modifica di una
 *        playlist. Gestisce l'input utente per il nome della playlist,
 *        notificando eventuali errori di validazione (nome vuoto).
 */
public class AddModPlaylistController {
    @FXML
    private TextField txtPlaylistName;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnSave;
    private MainController mainController;
    private Playlist currentPlaylist;

    /**
     * @brief Imposta il riferimento al controller principale necessario per
     *        comunicare con il windowmanager.
     * @param mainController controller passato
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * @brief Imposta la playlist da modificara. Se viene passata una playlist
     *        esistente, il campo biene aggiornato con il suo nome attuale.
     * @param playlist oggetto da modificare
     */
    public void setPlaylist(Playlist playlist) {
        this.currentPlaylist = playlist;
        if (playlist != null) {
            txtPlaylistName.setText(playlist.getName());
        }
    }

    /**
     * @brief Gestisce il salvataggio. Se il modello accetta il nome inserito viene
     *        modificata la playlist, altrimenti viene mostrata un'eccezione.
     * @param ev evento di pressione del pulsante.
     */
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

    /**
     * @brief Gestisce l'azione di annullamento, interrompendo l'operazione corrente
     *        senza salvare alcuna modifica chiudendo il form.
     * @param ev evento di pressione del pulsante.
     */
    @FXML
    public void handleDelete(ActionEvent ev) {
        closeWindow();
    }

    /**
     * @brief Metodo per chiudere la finestra corrente.
     */
    public void closeWindow() {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

}
