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
 * @class AutoPlaylistController
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

    /**
     * @brief Popola i ComboBox con valori dinamici estratti dalla libreria.
     *        Scansiona tutti i brani presenti e utilizza gli Stream di Java per
     *        estrarre valori univoci per l'anno di pubblicazione e il genere.
     *        La lista degli anni viene ordinata in senso decrescente, mentre quella
     *        dei generi in ordine alfabetico. Inserisce di default l'opzione
     *        "Qualsiasi"
     *        in tutti i menu.
     */
    private void populateCombos() {
        List<Track> tracks = mainController.getAppState().getLibrary().getTracks();

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

    /**
     * @brief Gestisce l'evento di pressione sul pulsante di generazione.
     *        Valida il nome della playlist per evitare duplicati o stringhe vuote.
     *        Delega ad AutoPlaylistService il filtraggio dei brani in base ai
     *        ComboBox.
     *        Se non trova brani, mostra un avviso testuale sulla UI e interrompe
     *        l'azione.
     *        Se trova brani, assembla la nuova playlist e utilizza l'UndoManager
     *        per inviare un AddPlaylist Command in modo che l'azione sia
     *        reversibile.
     *        Chiude la finestra e fornisce feedback di successo.
     * @param ev L'evento scatenato dal click sul pulsante "Genera".
     */
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

        ICommand cmd = new AddPlaylist(mainController.getAppState().getPlaylistCatalog(), playlist,
                mainController.getAppState().getPlaylistDAO());
        mainController.getAppState().getUndoManager().executeCommand(cmd);

        closeWindow();
        mainController.getAppState().getWindowManager().showInfo("Playlist creata",
                "La playlist \"" + name + "\" è stata creata con " + filtered.size() + " brani.");
    }

    /**
     * @brief Gestisce l'annullamento dell'operazione.
     *        Invocato dalla pressione del tasto "Annulla", si limita a chiudere
     *        la finestra senza applicare alcuna modifica al sistema.
     * @param ev L'evento scatenato dal click sul pulsante "Annulla".
     */
    @FXML
    public void handleCancel(ActionEvent ev) {
        closeWindow();
    }

    /**
     * @brief Estrae lo Stage corrente a partire dal bottone "Annulla" e lo chiude.
     */
    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
