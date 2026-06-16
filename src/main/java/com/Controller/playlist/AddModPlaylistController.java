package com.Controller.playlist;

import com.Command.AddPlaylist;
import com.Command.ICommand;
import com.Command.ModifyPlaylist;
import com.Controller.core.MainController;
import com.Model.Playlist;

import javafx.collections.ObservableList;
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
    private boolean isEditMode = false;

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
     *        esistente, il campo viene aggiornato con il suo nome attuale.
     * @param playlist oggetto da modificare
     */
    public void setPlaylist(Playlist playlist) {
        this.currentPlaylist = playlist;
        if (playlist != null) {
            this.isEditMode = true;
            txtPlaylistName.setText(playlist.getName());
        } else {
            this.isEditMode = false;
        }
    }

    /**
     * @brief Gestisce il salvataggio. Se il modello accetta il nome inserito viene
     *        modificata la playlist, altrimenti viene mostrata un'eccezione.
     * @param ev evento di pressione del pulsante.
     */
    @FXML
    public void handleSave(ActionEvent e) {
        try {
            String name = txtPlaylistName.getText().trim();

            ObservableList<Playlist> existingPlaylists = mainController.getAppState().getPlaylistCatalog()
                    .getPlaylists();

            for (Playlist p : existingPlaylists) {
                boolean isSamePlaylist = isEditMode && p.equals(currentPlaylist);
                if (!isSamePlaylist && p.getName().equalsIgnoreCase(name)) {
                    throw new IllegalArgumentException(
                            "Esiste già una playlist chiamata '" + name + "'. Scegli un altro nome.");
                }
            }

            if (isEditMode) {
                ICommand modifyCommand = new ModifyPlaylist(mainController.getAppState().getPlaylistCatalog(),
                        currentPlaylist, name);

                if (mainController != null) {
                    mainController.getAppState().getUndoManager().executeCommand(modifyCommand);
                }

                mainController.openPlaylistView(currentPlaylist);
            } else {
                Playlist newPlaylist = new Playlist(name);

                ICommand addCommand = new AddPlaylist(mainController.getAppState().getPlaylistCatalog(), newPlaylist);
                mainController.getAppState().getUndoManager().executeCommand(addCommand);

                mainController.openPlaylistView(newPlaylist);
            }

            closeWindow();
        } catch (IllegalArgumentException ex) {
            mainController.getAppState().getWindowManager().showWarning("Dati non validi", ex.getMessage());
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