package com.Controller.playlist;

import com.Command.ICommand;
import com.Command.RemoveTrack;
import com.Controller.core.MainController;
import com.Model.Playlist;
import com.Model.Track;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class PlaylistController {
    @FXML
    private Label lblPlaylist;
    @FXML
    private TableView<Track> playlistTrackList;

    @FXML
    private TableColumn<Track, String> colTitle, colAuthor, colDuration;
    @FXML
    private Button btnPlay;
    @FXML
    private Button btnRemoveFromPlaylist;

    private Playlist currentPlaylist;
    private MainController mainController;

    @FXML
    public void initialize() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("formattedDuration"));
        playlistTrackList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (mainController != null && newVal != null) {
                mainController.updateDetailPanel(newVal);
                if (mainController.getTrackTableController() != null) {
                    mainController.getTrackTableController().clearSelection();
                }
            }
        });
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setPlaylistData(Playlist playlist) {
        this.currentPlaylist = playlist;
        lblPlaylist.setText(playlist.getName());
        playlistTrackList.setItems(playlist.getTracks());
    }

    @FXML
    public void handleAddToPlaylist(ActionEvent ev) {
        mainController.openAddTrackToPlaylistView(this.currentPlaylist);
    }

    /**
     * @brief Torna alla libreria principale invocando il metodo del MainController.
     */
    @FXML
    public void handleBackToLibrary() {
        if (mainController != null) {
            mainController.restoreMainLibraryView();
            mainController.updateDetailPanel(null);
        }
    }

    @FXML
    public void handleRemoveFromPlaylist(ActionEvent ev){
        Track selectedTrack = playlistTrackList.getSelectionModel().getSelectedItem();
    public void clearSelection() {
        if (playlistTrackList != null) {
            playlistTrackList.getSelectionModel().clearSelection();
        }
    }

    public Track getSelectedTrack() {
        if (playlistTrackList != null) {
            return playlistTrackList.getSelectionModel().getSelectedItem();
        }
        return null;
    }

        if (selectedTrack == null) {
            mainController.getWindowManager().showWarning("Nessuna selezione", "Seleziona prima una traccia da rimuovere dalla playlist");
            return;
        }

        ICommand removeCommand = new RemoveTrack(this.currentPlaylist, selectedTrack);

        boolean wasPlaying = mainController.getPlayerContext().isPlaying()
                && selectedTrack == mainController.getPlayerContext().getCurrentTrack();

        mainController.getDeletedPlayingStack().push(wasPlaying);
        if(wasPlaying){
            mainController.getTimerManager().stop();
        }

        int idx = this.currentPlaylist.getTracks().indexOf(selectedTrack);

        mainController.getUndoManager().executeCommand(removeCommand);
        playlistTrackList.getSelectionModel().clearSelection();

        if(wasPlaying){
            mainController.getPlayerController().handleTrackRemoval(idx);
        }
    }
}