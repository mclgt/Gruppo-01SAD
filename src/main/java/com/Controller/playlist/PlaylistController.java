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
    public void handleAddToPlaylist(ActionEvent ev){
        mainController.openAddTrackToPlaylistView(this.currentPlaylist);
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

    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    public Track getSelectedTrack() {
        return playlistTrackList.getSelectionModel().getSelectedItem();
    }

    public void selectTrack(Track track) {
        playlistTrackList.getSelectionModel().select(track);
    }

    @FXML
    public void handleRemoveFromPlaylist(ActionEvent ev){
        Track selectedTrack = playlistTrackList.getSelectionModel().getSelectedItem();

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