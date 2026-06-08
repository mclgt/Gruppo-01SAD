package com.Controller.playlist;

import com.Controller.core.MainController;
import com.Model.Playlist;

import javafx.event.ActionEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

public class PlaylistTableController {
    private MainController mainController;

    private TableView<Playlist> playlistList;

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
    public void init(MainController controller, TableView<Playlist> playlistList,
            TableColumn<Playlist, String> nameCol) {
        this.mainController = controller;
        this.playlistList = playlistList;

        nameCol.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        playlistList.setItems(mainController.getPlaylistCatalog().getPlaylists());

        //Apertura con doppio click
        playlistList.setOnMouseClicked((MouseEvent ev) -> {
            if(ev.getClickCount() == 2){
                Playlist selectedPlaylist = playlistList.getSelectionModel().getSelectedItem();
                if(selectedPlaylist != null){
                    mainController.openPlaylistView(selectedPlaylist);
                }
            }
        });

        //Disattiva il pulsante "Aggiungi brano" se non c'è selezione
        playlistList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if(mainController.getBtnAddToPlaylist() != null){
                mainController.getBtnAddToPlaylist().setDisable(newVal == null);
            }
        });
    }

    public void openModPlaylistView(ActionEvent ev){
        Playlist selectedPlaylist = playlistList.getSelectionModel().getSelectedItem();
        if(selectedPlaylist != null){
            mainController.getWindowManager().openPlaylistWindow("/com/View/ModifyPlaylistView.fxml", "Modifica Playlist", selectedPlaylist, mainController);
        }else{
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

    public Playlist getSelectedPlaylist(){
        return playlistList.getSelectionModel().getSelectedItem();
    }
}