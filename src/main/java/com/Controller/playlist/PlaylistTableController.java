package com.Controller.playlist;

import com.Controller.core.MainController;
import com.Model.Playlist;

import javafx.event.ActionEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

public class PlaylistTableController {
    private MainController mainController;

    private TableView<Playlist> playlistTable;

    /**
     * @brief Inizializza il controller, impostando i riferimenti necessari e
     *        configurando l'interfaccia utente. Effettua i binding tra la colonna
     *        visualizzata a schermo e la StringProperty contenuta in Playlist
     *        (nome).
     * @param controller   rifeirmento al maincontroller per accedere alle
     *                     funzionalità del WindowManager
     * @param playlistTable riferimento al componente TableView definito
     * @param nameCol      riferimento alla colonna contenente i nomi delle playlist
     */
    public void init(MainController controller, TableView<Playlist> playlistTable,
            TableColumn<Playlist, String> nameCol) {
        this.mainController = controller;
        this.playlistTable = playlistTable;

        nameCol.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        playlistTable.setItems(mainController.getUserPlaylists());

        playlistTable.setOnMouseClicked((MouseEvent ev) -> {
            if(ev.getClickCount() == 2){
                Playlist selectedPlaylist = playlistTable.getSelectionModel().getSelectedItem();
                if(selectedPlaylist != null){
                    mainController.openPlaylistView(selectedPlaylist);
                }
            }
        });
    }

    public void openModPlaylistView(ActionEvent ev){
        Playlist selectedPlaylist = playlistTable.getSelectionModel().getSelectedItem();
        if(selectedPlaylist != null){
            mainController.getWindowManager().openPlaylistWindow("/com/View/ModifyPlaylistView.fxml", "Modifica Playlist", selectedPlaylist);
        }else{
            mainController.getWindowManager().showWarning("Attenzione", "Seleziona prima una playlist da modificare");
        }
    }

    /**
     * @brief Pulisce la selezione corrente all'interno della tabella delle
     *        playlist.
     */
    public void clearSelection() {
        if (playlistTable != null) {
            playlistTable.getSelectionModel().clearSelection();
        }
    }

    public Playlist getSelectedPlaylist(){
        return playlistTable.getSelectionModel().getSelectedItem();
    }
}
