package com.Controller.playlist;

import com.Command.AddPlaylist;
import com.Command.ICommand;
import com.Controller.core.MainController;
import com.Model.AutoPlaylistService;
import com.Model.Playlist;
import com.Model.Track;
import com.Model.TrackTag;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * @brief Controller della finestra "Genera Playlist Automatica".
 *        Gestisce l'input utente, popola i menu a tendina dalla libreria,
 *        delega il filtraggio ad AutoPlaylistService e crea la playlist.
 */
public class AutoPlaylistController {

    @FXML
    private TextField txtName;
    @FXML
    private ComboBox<String> comboAnno;
    @FXML
    private ComboBox<String> comboGenre;
    @FXML
    private ComboBox<String> comboTag;
    @FXML
    private Button btnGenerate;
    @FXML
    private Button btnCancel;
    @FXML
    private Label lblNoTracks;

    private MainController mainController;

    @FXML
    public void initialize() {
        btnGenerate.setDisable(true);
        txtName.textProperty().addListener(
                (obs, oldVal, newVal) -> btnGenerate.setDisable(newVal == null || newVal.trim().isEmpty()));
    }

    /**
     * @brief Imposta il controller principale e popola i menu a tendina.
     * @param mainController riferimento al MainController
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        populateCombos();
    }

    private void populateCombos() {
        List<Track> tracks = mainController.getAppState().getLibrary().getTracks();

        // Anno: valori unici dalla libreria, ordinati decrescenti
        List<String> years = new ArrayList<>();
        years.add(AutoPlaylistService.ANY);
        tracks.stream()
                .map(Track::getYear)
                .filter(y -> y > 0)
                .distinct()
                .sorted((a, b) -> b - a)
                .map(String::valueOf)
                .forEach(years::add);
        comboAnno.setItems(FXCollections.observableArrayList(years));
        comboAnno.setValue(AutoPlaylistService.ANY);

        // Genere: valori unici dalla libreria, ordinati
        List<String> genres = new ArrayList<>();
        genres.add(AutoPlaylistService.ANY);
        tracks.stream()
                .map(Track::getGenre)
                .filter(g -> g != null && !g.trim().isEmpty())
                .distinct()
                .sorted()
                .forEach(genres::add);
        comboGenre.setItems(FXCollections.observableArrayList(genres));
        comboGenre.setValue(AutoPlaylistService.ANY);

        // Tag: valori dall'enum TrackTag
        List<String> tags = new ArrayList<>();
        tags.add(AutoPlaylistService.ANY);
        for (TrackTag tag : TrackTag.values()) {
            if (tag == TrackTag.NONE) {
                tags.add(AutoPlaylistService.TAG_NONE_LABEL);
            } else {
                tags.add(tag.toString());
            }
        }
        comboTag.setItems(FXCollections.observableArrayList(tags));
        comboTag.setValue(AutoPlaylistService.ANY);
    }

    @FXML
    public void handleGenerate(ActionEvent ev) {
        String name = txtName.getText().trim();

        try {
            AutoPlaylistService.validateName(name, mainController.getAppState().getPlaylistCatalog().getPlaylists());
        } catch (IllegalArgumentException ex) {
            mainController.getAppState().getWindowManager().showWarning("Nome non valido", ex.getMessage());
            return;
        }

        List<Track> filtered = AutoPlaylistService.filter(
                mainController.getAppState().getLibrary().getTracks(),
                comboAnno.getValue(),
                comboGenre.getValue(),
                comboTag.getValue());

        if (filtered.isEmpty()) {
            lblNoTracks.setVisible(true);
            lblNoTracks.setManaged(true);
            return;
        }

        Playlist playlist = new Playlist(name);
        for (Track t : filtered) {
            playlist.addTrack(t);
        }

        ICommand cmd = new AddPlaylist(mainController.getAppState().getPlaylistCatalog(), playlist);
        mainController.getAppState().getUndoManager().executeCommand(cmd);

        closeWindow();
        mainController.getAppState().getWindowManager().showInfo("Playlist creata",
                "La playlist \"" + name + "\" è stata creata con " + filtered.size() + " brani.");
    }

    @FXML
    public void handleCancel(ActionEvent ev) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
