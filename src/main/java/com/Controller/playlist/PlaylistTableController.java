package com.Controller.playlist;

import java.util.Optional;

import com.Command.ICommand;
import com.Command.RemovePlaylist;
import com.Controller.core.MainController;
import com.Model.ITrackContainer;
import com.Model.Playlist;
import com.Model.Track;
import com.Observer.IPlayerSubscriber;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

/**
 * @class PLaylistTableController
 * @brief Controller per la gestione della tabella delle playlist. Gestisce
 *        l'elenco delle playlist create dall'utente, consentendo operazioni di
 *        selezione, apertura, modifica ed eliminazione. Implmenta
 *        IPLayerSubscriber per sincronizzare la vista con lo stato di
 *        riproduzione, evidenziando la PLyalist in esecuzione.
 */
public class PlaylistTableController implements IPlayerSubscriber {
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
        mainController.getPlayerContext().subscribe(this);
        nameCol.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        playlistList.setItems(mainController.getPlaylistCatalog().getPlaylists());

        playlistList.setOnMouseClicked((MouseEvent ev) -> {
            if (ev.getClickCount() == 2) {
                Playlist selectedPlaylist = playlistList.getSelectionModel().getSelectedItem();
                if (selectedPlaylist != null) {
                    mainController.openPlaylistView(selectedPlaylist);
                }
            }
        });

        playlistList.setRowFactory(tv -> {
            TableRow<Playlist> row = new TableRow<>();
            row.itemProperty().addListener((observed, oldVal, newVal) -> {
                Track current = mainController.getPlayerContext().getCurrentTrack();
                ITrackContainer activeContainer = mainController.getPlayerController().getActiveContainer();
                if (newVal != null && current != null && newVal.equals(activeContainer)) {
                    row.setStyle("-fx-background-color: #f5a747;"); // cambia colore
                } else {
                    row.setStyle("");
                }

            });
            return row;
        });

        playlistList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (mainController.getBtnAddToPlaylist() != null) {
                mainController.getBtnAddToPlaylist().setDisable(newVal == null);
            }
        });

    }

    /**
     * @brief Apre la vista di modifica per la playlist selezionata attualmente
     * @param ev pressione sul pulsante di modifica
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
     * @brief Recupera la playlist attualmente selezionata nella tabella
     * @return oggetto Playlist selezionato, o null se nessuna selezione è attiva.
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
     */
    public void handleDeletePlaylist(ActionEvent ev) {
        Playlist selectedPlaylist = playlistList.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            Optional<ButtonType> result = mainController.getWindowManager().showConfirmation("Conferma eliminazione",
                    "Eliminazione della playlist",
                    "Sei sicuro di voler eliminare la playlist \"" + selectedPlaylist.getName() + "\"?", null);
            if (result.isPresent() && result.get() == ButtonType.OK) {
                ICommand removeCmd = new RemovePlaylist(mainController.getPlaylistCatalog(), selectedPlaylist, mainController.getPlaylistDAO());
                mainController.getUndoManager().executeCommand(removeCmd);
                mainController.restoreMainLibraryView();

                mainController.updateTop();
            }
        } else {
            mainController.getWindowManager().showWarning("Attenzione", "Seleziona prima una playlist da eliminare");
        }
    }

    /**
     * @brief Callback chiamata ogni volta che la traccia in riproduzione cambia.
     *        Forza il refresh della tabella per aggiornare l'evidenziazione della
     *        playlist in riproduzione.
     * @param newTrack nuova traccia impostata
     */
    @Override
    public void onPlaybackChanged(Track newTrack) {
        Platform.runLater(() -> {
            playlistList.refresh();
        });
    }

    /**
     * @brief RImuove il controller dalla lista dei subsciber del PlayerContext
     */
    public void dispose() {
        mainController.getPlayerContext().unsubscribe(this);
    }

    
}