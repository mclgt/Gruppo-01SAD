package com.Controller.playlist;

import java.util.Optional;

import com.Command.ICommand;
import com.Command.RemovePlaylist;
import com.Controller.core.MainController;
import com.Controller.util.WindowManager;
import com.Model.Playlist;

import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
/**
 * @brief Controller dedicato alla gestione della vista delle playlist.
 *        Si occupa di inizializzare la tabella (che viene chiamata lista in
 *        quanto mostra solo i nomi delle playlist), gestire il binding con le
 *        Playlist e intercettare le azioni dell'utente.
 */
public class PlaylistListController {
    private MainController mainController;
    private WindowManager windowManager;
    private TableView<Playlist> playlistList;

    /**
     * @brief Inizializza il controller, impostando i riferimenti necessari e
     *        configurando l'interfaccia utente. Effettua i binding tra la colonna
     *        visualizzata a schermo e la StringProperty contenuta in Playlist
     *        (nome).
     * @param controller   rifeirmento al maincontroller per accedere alle
     *                     funzionalità del WindowManager
     * @param playlistList riferimento al componente TableView definito
     * @param nameCol      riferimento alla colonna contenente i nomi delle playlist
     */
    public void init(MainController controller, TableView<Playlist> playlistList,
            TableColumn<Playlist, String> nameCol) {
        this.mainController = controller;
        this.windowManager = mainController.getWindowManager();
        this.playlistList = playlistList;
        nameCol.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());

        // mock playlist
        Playlist mock1 = new Playlist("Rock Anni 90");
        Playlist mock2 = new Playlist("Studio & Relax");

        playlistList.getItems().addAll(mock1, mock2);
    }

    /**
     * @brief gestisce l'apertura della finestra di modifica di una playlist.
     *        Recupera la playlist selezioanta, se presente, apre la vista di
     *        modifica, altrimenti mostra un alert.
     * 
     * @param ev evento di pressione del bottone dell'interfaccia
     */
    public void openModPlaylistView(ActionEvent ev) {
        Playlist selectedPlaylist = playlistList.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            windowManager.openPlaylistWindow("/com/View/ModifyPlaylistView.fxml", "Modifica Playlist",
                    selectedPlaylist);
        } else {
            windowManager.showWarning("Attenzione", "Seleziona prima una playlist da modificare");
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

    public void handleDeletePlaylist(ActionEvent ev) {
        Playlist selectedPlaylist = playlistList.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            Optional<ButtonType> result = windowManager.showConfirmation("Conferma eliminazione", "Sei sicuro di voler eliminare la playlist \"" + selectedPlaylist.getName() + "\"?", null);
            if(result.isPresent() && result.get() == ButtonType.OK){
                ICommand removePlaylistCommand = new RemovePlaylist(playlistList, selectedPlaylist);
                mainController.getUndoManager().executeCommand(removePlaylistCommand);
            }
        } else {
            windowManager.showWarning("Attenzione", "Nessuna playlist selezionata");
        }
    }

}
