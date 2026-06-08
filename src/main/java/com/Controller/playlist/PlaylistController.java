package com.Controller.playlist;

import com.Controller.core.MainController;
import com.Model.Playlist;
import com.Model.Track;

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
    private TableView<Track> playlistTrackTable;

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
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setPlaylistData(Playlist playlist) {
        this.currentPlaylist = playlist;
        lblPlaylist.setText(playlist.getName());
        playlistTrackTable.setItems(playlist.getTracks());
    }

    /**
     * @brief Torna alla libreria principale invocando il metodo del MainController.
     */
    @FXML
    public void handleBackToLibrary(){
        if(mainController != null){
            mainController.restoreMainLibraryView();
        }
    }
}