package com.Controller.playlist;

import com.Controller.core.MainController;
import com.Model.Playlist;
import com.Command.ICommand;
import com.Command.RemovePlaylist;
import javafx.scene.control.ButtonType;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

/**
 * @class PlaylistTableController
 * @brief Controller dedicato alla gestione della tabella delle playlist.
 *        Gestisce l'interazione con l'elenco delle playlist visualizzato nel
 *        pannello laterale, incluse le operazioni di selezione, apertura
 *        tramite doppio click e sincronizzazione con gli altri componenti
 *        tramite il MainController.
 */

public class PlaylistTableController {
    private MainController mainController;

    private TableView<Playlist> playlistList;

    /**
     * @brief Inizializza il controller, impostando i riferimenti necessari e
     *        configurando l'interfaccia utente. Effettua i binding tra la colonna
     *        visualizzata a schermo e la StringProperty contenuta in Playlist
     *        (nome).
     * @param controller    rifeirmento al maincontroller per accedere alle
     *                      funzionalità del WindowManager
     * @param playlistTable riferimento al componente TableView definito
     * @param nameCol       riferimento alla colonna contenente i nomi delle
     *                      playlist
     */
    public void init(MainController controller, TableView<Playlist> playlistList,
            TableColumn<Playlist, String> nameCol) {
        this.mainController = controller;
        this.playlistList = playlistList;

        nameCol.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        playlistList.setItems(mainController.getPlaylistCatalog().getPlaylists());

        // Apertura con doppio click
        playlistList.setOnMouseClicked((MouseEvent ev) -> {
            if (ev.getClickCount() == 2) {
                Playlist selectedPlaylist = playlistList.getSelectionModel().getSelectedItem();
                if (selectedPlaylist != null) {
                    mainController.openPlaylistView(selectedPlaylist);
                }
            }
        });

        // Aggiorna Ui e deseleziona altri componenti
        playlistList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (mainController.getBtnAddToPlaylist() != null) {
                mainController.getBtnAddToPlaylist().setDisable(newVal == null);
            }
            if (newVal != null && mainController != null) {
                mainController.updateDetailPanel(null);
                if (mainController.getTrackTableController() != null) {
                    mainController.getTrackTableController().clearSelection();
                }
            }
        });
    }

    /**
     * @brief Gestisce l'apertura della finestra per la modifica di una playlist
     *        esistente.
     * @param ev evento di pressione del pulsante modifica
     */
    public void openModPlaylistView(ActionEvent ev) {
        Playlist selectedPlaylist = playlistList.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            mainController.getWindowManager().openPlaylistWindow("/com/View/ModifyPlaylistView.fxml",
                    "Modifica Playlist", selectedPlaylist, mainController);
        } else {
            mainController.getWindowManager().showWarning("Attenzione", "Seleziona prima una playlist da modificare");
        }
    }

    /**
     * @brief Pulisce la selezione corrente all'interno della tabella delle
     *        playlist.
     */
    public void clearSelection() {
        if (playlistList != null) {
            playlistList.getSelectionModel().clearSelection();
        }
    }

    /**
     * @brief recupera la playlist selezionata dall'utente in quel determinato
     *        istante
     * @return oggetto playlist selezionato, o null se nessuna playlist è stata
     *         selezionata
     */
    public Playlist getSelectedPlaylist() {
        return playlistList.getSelectionModel().getSelectedItem();
    }

    /**
     * @brief Gestisce l'evento di eliminazione di una playlist. Se è stata
     *        selezionata una playlist, viene mostrata una finestra di conferma.
     *        Se l'utente conferma, viene eseguito il comando di rimozione della
     *        playlist e aggiornata la vista principale. In caso contrario, viene
     *        mostrato un messaggio di avviso.
     * @param ev evento di pressione del pulsante di eliminazione.
    } */
    public void handleDeletePlaylist(ActionEvent ev){
        Playlist selectedPlaylist = playlistList.getSelectionModel().getSelectedItem();
        if(selectedPlaylist != null){
            Optional<ButtonType> result = mainController.getWindowManager().showConfirmation("Conferma eliminazione", "Sei sicuro di voler eliminare la playlist \"" + selectedPlaylist.getName() + "\"?", null);
            if(result.isPresent() && result.get() == ButtonType.OK){
                ICommand removeCmd = new RemovePlaylist(mainController.getPlaylistCatalog(), selectedPlaylist);
                mainController.getUndoManager().executeCommand(removeCmd);
                mainController.restoreMainLibraryView();
            }
        }else{
            mainController.getWindowManager().showWarning("Attenzione", "Seleziona prima una playlist da eliminare");
        }
    }
}