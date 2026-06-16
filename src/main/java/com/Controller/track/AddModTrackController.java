package com.Controller.track;

import java.io.File;

import com.Command.AddTrack;
import com.Command.ICommand;
import com.Command.ModifyTrack;
import com.Controller.core.MainController;
import com.Model.Track;
import com.Model.TrackFactory;
import com.Model.TrackTag;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * @brief Controller unico per l'Aggiunta e la Modifica di un brano.
 *        Gestisce l'interazione dell'utente con il form per aggiungere una
 *        traccia o modificarla. Raccoglie i dati dal form, ne verifica
 *        la correttezza e usa la Factory o i metodi della Traccia per creare o
 *        modificare l'oggetto Track.
 */

public class AddModTrackController implements ITrackImporter {
    @FXML
    private TextField txtTitle;
    @FXML
    private TextField txtAuthor;
    @FXML
    private TextField txtGenre;
    @FXML
    private TextField txtDuration;
    @FXML
    private TextField txtYear;
    @FXML
    private TextField txtAlbum;
    @FXML
    private TextField txtFilePath;
    @FXML
    private ComboBox<TrackTag> tagCombo;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnSave;
    private MainController mainController;
    private final TrackFactory factory;
    private Track trackToModify;
    /** @brief Flag per determinare la modalità del form: aggiunta o modifica */
    private boolean isEditMode = false;

    public AddModTrackController(TrackFactory factory) {
        this.factory = factory;
    }

    /**
     * @brief Imposta il riferimento al controller principale. Consente di far
     *        comunicare la schermata con la vista principale
     *        affinché il brano venga mostrato una volta creato.
     * @param mainController
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        ObservableList<TrackTag> tags = FXCollections.observableArrayList(TrackTag.values());
        tagCombo.setItems(tags);
        tagCombo.setPromptText("Seleziona un tag...");
    }

    /**
     * @brief Consente di inizializzare il form in modalità "Aggiunta" o "Modifica"
     *        Se l'oggetto track passato (oggetto selezionato) è non nullo, allora i
     *        campi di testo si aggiornano con i valori della traccia selezionata.
     *        Altrimenti, se non si selaziona alcun brano, i campi rimangono vuoti.
     * @param track il brano selezionato, è null se si vuole aggiungere un brano
     */

    public void setTrack(Track track) {
        this.trackToModify = track;
        if (track != null) {
            this.isEditMode = true;
            txtTitle.setText(track.getTitle());
            txtAuthor.setText(track.getAuthor());
            txtAlbum.setText(track.getAlbum());
            txtGenre.setText(track.getGenre());
            txtFilePath.setText(track.getFilePath());
            txtYear.setText(track.getYear() == 0 ? "" : String.valueOf(track.getYear()));
            txtDuration.setText(track.getDuration() == 0 ? "" : (String.valueOf(track.getFormattedDuration())));
            if (track.getTag() != null) {
                tagCombo.getSelectionModel().select((track.getTag() != null) ? track.getTag() : TrackTag.NONE);
            }
        } else {
            this.isEditMode = false;
            if (tagCombo != null) {
                tagCombo.getSelectionModel().select(TrackTag.NONE);
            }
        }
    }

    /**
     * @brief gestisce l'annullamento dell'azione mentre questa è in corso.
     * 
     * @param e evento generata dalla pressione del pulsante
     */
    @FXML
    public void handleDelete(ActionEvent e) {
        closeWindow();
    }

    /**
     * @brief Gestisce la procedura del salvataggio del brano (sia nel caso di
     *        Aggiunta che di Modifica), preleva le stringhe,
     *        controlla se anno e durata sono
     *        inseriti correttamente e istanzia il brano se supera i controlli di
     *        validità.
     *        Inoltre, intercetta eventuali eccezioni legate
     *        all'input dell'utente.
     * @param e evento generata dalla pressione del pulsante
     */
    @FXML
    public void handleSave(ActionEvent e) {
        try {
            String title = txtTitle.getText();
            String author = txtAuthor.getText();
            String genre = txtGenre.getText();
            String album = txtAlbum.getText();

            String originalFilePath = txtFilePath.getText();
            String finalFilePathForDB = originalFilePath;

            TrackTag tag = (tagCombo != null) ? tagCombo.getValue() : TrackTag.NONE;
            int duration = 0;
            if (txtDuration.getText() != null && !txtDuration.getText().isEmpty()) {
                duration = convertSeconds(txtDuration.getText());
            }
            int year = 0;
            if (txtYear.getText() != null && !txtYear.getText().isEmpty()) {
                year = Integer.parseInt(txtYear.getText());
            }

            finalFilePathForDB = originalFilePath;

            if (isEditMode) {
                ICommand modifyCommand = new ModifyTrack(mainController.getAppState().getLibrary(), trackToModify,
                        title, author,
                        year, genre, duration, album, originalFilePath, tag);

                if (mainController != null) {
                    mainController.getAppState().getUndoManager().executeCommand(modifyCommand);
                    mainController.notifyTrackModified(trackToModify);
                }
            } else {
                Track newTrack = this.factory.createTrack(title, author, year, genre, duration, album,
                        finalFilePathForDB, tag);

                if (mainController != null) {
                    ICommand addCommand = new AddTrack(mainController.getAppState().getLibrary(), newTrack);
                    mainController.getAppState().getUndoManager().executeCommand(addCommand);
                }
            }
            closeWindow();
        } catch (NumberFormatException ex) {
            mainController.getAppState().getWindowManager().showError("Errore nell'inserimento dei dati numerici",
                    "Assicurarsi di aver inserito numeri nei campi 'Anno' e 'Durata'");
        } catch (IllegalArgumentException ex) {
            mainController.getAppState().getWindowManager().showError("Dati non vallidi", ex.getMessage());
        }
    }

    /**
     * @brief Apre il FileChooser e, se viene selezionato un file,
     *        ne inserisce il percorso all'interno della casella di testo.
     * @param event evento generato dalla pressione del pulsante
     */
    @FXML
    public void handleSelectFile(ActionEvent event) {
        Window currentWindow = ((Node) event.getSource()).getScene().getWindow();

        File selectedFile = selectAudioFile(currentWindow);

        if (selectedFile != null) {
            txtFilePath.setText(selectedFile.getAbsolutePath());
        }
    }

    /**
     * @brief Mostra il FileChooser nativo del sistema operativo filtrato per file
     *        .mp3 e .wav.
     *        Inizia la navigazione dalla directory "Home" dell'utente.
     *
     * @param ownerWindow Finestra chiamante (blocca l'interazione sottostante
     *                    finché non si chiude).
     * @return File Il file selezionato, oppure null se l'azione viene annullata.
     */
    @Override
    public File selectAudioFile(Window ownerWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importa Brano Audio");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        FileChooser.ExtensionFilter audioFilter = new FileChooser.ExtensionFilter("File Audio (*.mp3, *wav)", "*.mp3",
                "*.wav");
        fileChooser.getExtensionFilters().add(audioFilter);

        return fileChooser.showOpenDialog(ownerWindow);
    }

    /**
     * @brief Metodo per la chiusura della finestra corrente
     */
    private void closeWindow() {
        Stage stage = (Stage) btnDelete.getScene().getWindow();
        stage.close();
    }

    /**
     * @brief Converte una stringa rappresentante il tempo in secondi totali.
     *        Suporta formati lineari o formati divisi da ":" o "." o "-"
     * @param time stringa di tempo inserita dall'utente
     * @return int I secondi totali calcolati
     */

    private int convertSeconds(String time) {
        String[] parts = time.split("[:.,\\- ]+");

        if (parts.length == 1) {
            return Integer.parseInt(parts[0]);
        }

        int minuts = Integer.parseInt(parts[0].trim());
        int seconds = Integer.parseInt(parts[1].trim());

        return (minuts * 60) + seconds;
    }

}