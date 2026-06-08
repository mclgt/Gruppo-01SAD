package com.Controller.playlist;

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

/**
 * @class PlaylistController
 * @brief Controller per la vista di dettaglio di una singola playlist.
 *
 *        Gestisce la tabella dei brani contenuti nella playlist aperta,
 *        la selezione del brano corrente, l'aggiunta di nuovi brani e il
 *        ritorno alla libreria principale. Viene caricato dinamicamente
 *        tramite FXMLLoader quando l'utente apre una playlist.
 *
 * @see MainController
 * @see PlaylistTableController
 */
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

    /**
     * @brief Inizializza il binding delle colonne e il listener di selezione.
     *        Collega le colonne alle property di @ref Track e, alla selezione
     *        di un brano, aggiorna il pannello dettagli e deseleziona la tabella
     *        principale della libreria.
     */
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

    /**
     * @brief Imposta il riferimento al controller principale.
     * @param mainController Il @ref MainController dell'applicazione.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * @brief Carica i dati della playlist nella vista.
     *        Aggiorna la label del titolo e collega i brani alla tabella.
     * @param playlist La @ref Playlist da visualizzare.
     */
    public void setPlaylistData(Playlist playlist) {
        this.currentPlaylist = playlist;
        lblPlaylist.setText(playlist.getName());
        playlistTrackList.setItems(playlist.getTracks());
    }

    /**
     * @brief Apre la finestra per aggiungere brani alla playlist corrente.
     * @param ev Evento di pressione del pulsante.
     */
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

    /**
     * @brief Rimuove la selezione corrente dalla tabella della playlist.
     */
    public void clearSelection() {
        if (playlistTrackList != null) {
            playlistTrackList.getSelectionModel().clearSelection();
        }
    }

    /**
     * @brief Restituisce il brano attualmente selezionato nella tabella.
     * @return La @ref Track selezionata, o {@code null} se nessuna selezione è attiva.
     */
    public Track getSelectedTrack() {
        if (playlistTrackList != null) {
            return playlistTrackList.getSelectionModel().getSelectedItem();
        }
        return null;
    }

    /**
     * @brief Restituisce la playlist attualmente visualizzata.
     * @return La @ref Playlist corrente gestita da questo controller.
     */
    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    /**
     * @brief Seleziona visivamente un brano nella tabella della playlist.
     *        Usato da @ref PlayerController per sincronizzare la riga evidenziata
     *        con il brano in riproduzione durante la navigazione next/prev.
     * @param track Il @ref Track da selezionare nella tabella.
     */
    public void selectTrack(Track track) {
        if (playlistTrackList != null) {
            playlistTrackList.getSelectionModel().select(track);
        }
    }

}