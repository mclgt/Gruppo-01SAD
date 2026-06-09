package com.Controller.track;

import java.util.Optional;

import com.Command.ICommand;
import com.Command.RemoveTrack;
import com.Controller.core.MainController;
import com.Model.Track;

import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class TrackTableController {
    private MainController mainController;

    private TableView<Track> trackTable;
    private VBox detailPanel;
    private Label lblTitle, lblAuthor, lblAlbum, lblGenre, lblDuration, lblYear;

    public void init(MainController mainController, TableView<Track> trackTable, TableColumn<Track, String> titleCol,
            TableColumn<Track, String> authorCol, TableColumn<Track, String> genreCol, VBox detailPanel,
            Label lblTitle, Label lblAuthor, Label lblAlbum, Label lblGenre, Label lblDuration, Label lblYear) {
        this.mainController = mainController;
        this.trackTable = trackTable;
        this.detailPanel = detailPanel;
        this.lblTitle = lblTitle;
        this.lblAuthor = lblAuthor;
        this.lblAlbum = lblAlbum;
        this.lblGenre = lblGenre;
        this.lblDuration = lblDuration;
        this.lblYear = lblYear;

        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));

        trackTable.setItems(mainController.getLibrary().getTracks());
        detailPanel.setVisible(false);

        trackTable.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
            updateDetailPanel(newVal);
        });
    }

    public Track getSelectedTrack() {
        return trackTable.getSelectionModel().getSelectedItem();
    }

    public void clearSelection() {
        trackTable.getSelectionModel().clearSelection();
    }

    // aggiorna la selezione visiva nella tabella quando si preme next o prev,
    // così la riga evidenziata corrisponde sempre alla traccia in riproduzione
    public void selectTrack(Track track) {
        trackTable.getSelectionModel().select(track);
    }

    private void updateDetailPanel(Track track) {
        if (track == null) {
            detailPanel.setVisible(false);
        } else {
            lblTitle.setText(track.getTitle());
            lblAuthor.setText(track.getAuthor());
            String album = track.getAlbum();
            lblAlbum.setText((album == null || album.trim().isEmpty()) ? "-" : album);
            String genre = track.getGenre();
            lblGenre.setText((genre == null || genre.trim().isEmpty()) ? "-" : genre);
            lblYear.setText(track.getYear() == 0 ? "-" : String.valueOf(track.getYear()));
            lblDuration.setText(track.getFormattedDuration());

            detailPanel.setVisible(true);
        }
    }

    public void openAddTrackWindow(ActionEvent ev) {
        mainController.getWindowManager().openWindow("/com/View/AddTrackView.fxml", "Aggiungi nuovo brano", null);
    }

    public void openModifyTrackView(ActionEvent ev) {
        Track selectedTrack = getSelectedTrack();
        if (selectedTrack == null) {
            mainController.getWindowManager().showWarning("Nessuna selezione",
                    "Seleziona una traccia dalla tabella da modificare.");
            return;
        }

        mainController.getWindowManager().openWindow("/com/View/ModifyTrackView.fxml",
                "Modifica Brano - " + selectedTrack.getTitle(), selectedTrack);
    }

    public void handleRemoveTrack(ActionEvent ev) {
        Track selectedTrack = getSelectedTrack();
        if (selectedTrack == null) {
            mainController.getWindowManager().showWarning("Nessuna selezione",
                    "Seleziona una traccia dalla tabella da rimuovere.");
            return;
        }
        Optional<ButtonType> result = mainController.getWindowManager().showConfirmation("Conferma rimozione",
                "Rimozione dalla libreria e da tutte le playlist",
                "Sei sicuro di voler rimuovere" + selectedTrack.getTitle() + "?", null);

        if (result.isPresent() && result.get() == ButtonType.OK) {
            ICommand removeCommand = new RemoveTrack(mainController.getLibrary(), selectedTrack,
                    mainController.getPlaylistCatalog());

            boolean wasPlaying = mainController.getPlayerContext().isPlaying()
                    && selectedTrack == mainController.getPlayerContext().getCurrentTrack();

            mainController.getDeletedPlayingStack().push(wasPlaying);
            if (wasPlaying) {
                mainController.getTimerManager().stop();
            }

            int idx = mainController.getLibrary().getTracks().indexOf(selectedTrack);
            mainController.getUndoManager().executeCommand(removeCommand);
            clearSelection();

            if (wasPlaying) {
                mainController.getPlayerController().handleTrackRemoval(idx);
            }
        }
    }
}