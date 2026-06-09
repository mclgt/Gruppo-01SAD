package com.Controller.playback;

import com.Controller.core.MainController;
import com.Model.ITrackContainer;
import com.Model.Track;
import com.Model.ITrackContainer;
import com.Model.Playlist;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

/**
 * @class PlayerController
 * 
 * @brief Gestisce la logica della riproduzione audio, il timer e l'interazione
 *        con l'interfaccia. La classe funge da controller per il sistema di
 *        riproduzione. Gestisce il flusso audio e coordina l'aggiornamento
 *        dell'interfaccia grafica in base allo stato del context e dela
 *        strategia di riproduzione selezionata.
 */
public class PlayerController {
    private MainController mainController;

    private Label lblNowPlaying, lblCurrentTime, lblTotalTime;
    private Slider progressSlider;
    private ITrackContainer activeContainer;

    private boolean sequentialMode = false;
    private boolean trackFinished = false;
    private ITrackContainer activeContainer;
    

    /**
     * @brief Inizializza i riferimenti ai componenti grafici e al controller
     *        principale
     * @param mainController controller principale
     * @param lblNowPlaying  label che mostra il brano in riproduzione
     * @param lblCurrentTime label che mostra il tempo trascorso
     * @param lblTotalTime   label che mostra il tempo totale
     * @param progressSlider slider che rappresenta l'avanzamento del brano
     */
    public void init(MainController mainController, Label lblNowPlaying, Label lblCurrentTime, Label lblTotalTime,
            Slider progressSlider) {
        this.mainController = mainController;
        this.lblNowPlaying = lblNowPlaying;
        this.lblCurrentTime = lblCurrentTime;
        this.lblTotalTime = lblTotalTime;
        this.progressSlider = progressSlider;
    }

    /**
     * @brief Imposta lo stato di terminazione del brano corrente
     * @param finished pari a True se il brano è terminato, False altrimenti
     */
    public void setTrackFinished(boolean finished) {
        this.trackFinished = finished;
    }

    /**
     * @brief Gestisce l'avvio della riproduzione per il brano o la playlist
     *        selezionata
     */
    public void playSong() {
        Track selectedTrack = mainController.getTrackTableController().getSelectedTrack();
        Playlist selectedPlaylist = mainController.getPlaylistTableController().getSelectedPlaylist();
        if (selectedTrack != null) {
            if (mainController.getPlayerContext().isPlaying()
                    && selectedTrack == mainController.getPlayerContext().getCurrentTrack()
                    && !trackFinished) {
                mainController.getWindowManager().showInfo("Già in riproduzione",
                        "Brano selezionato già in riproduzione");
                return;
            }
            activeContainer = mainController.getLibrary();
            trackFinished = false;
            sequentialMode = false;
            startTrackPlayback(selectedTrack);
            return;
        }

        if (selectedPlaylist != null) {
            if (selectedPlaylist.getTracksCount() == 0) {
                mainController.getWindowManager().showWarning("Playlist vuota",
                        "La playlist selezionata non ha brani.");
                return;
            }

            activeContainer = selectedPlaylist;
            trackFinished = false;
            sequentialMode = false;
            Track firstTrack = activeContainer.getTracks().get(0);
            if (mainController.getPlayerContext().isPlaying()
                    && firstTrack == mainController.getPlayerContext().getCurrentTrack() && !trackFinished) {
                mainController.getWindowManager().showInfo("Già in riproduzione",
                        "Sto già eseguendo questo brano.");
                return;
            }

            startTrackPlayback(firstTrack);
            return;
        }
        mainController.getWindowManager().showWarning("Nessuna selezione",
                "Seleziona una traccia dalla lista o una playlist per riprodurla.");
    }

    /**
     * @brief Avvia la riproduzione in modalità sequenziale
     * @param event pressione del pulsante
     */
    public void sequentialRip(ActionEvent event) {
        Track selectedTrack = mainController.getTrackTableController().getSelectedTrack();
        Playlist selectedPlaylist = mainController.getPlaylistTableController().getSelectedPlaylist();
        if (selectedTrack != null) {
            activeContainer = mainController.getLibrary();
            trackFinished = false;
            sequentialMode = true;
            startTrackPlayback(selectedTrack);
            return;
        }
        if (selectedPlaylist != null) {
            if (selectedPlaylist.getTracksCount() == 0) {
                mainController.getWindowManager().showWarning("Playlist vuota",
                        "La playlist selezionata non ha brani.");
                return;
            }
            activeContainer = selectedPlaylist;
            trackFinished = false;
            sequentialMode = true;
            Track firstTrack = activeContainer.getTracks().get(0);
            startTrackPlayback(firstTrack);
            return;
        }

        mainController.getWindowManager().showWarning("Nessuna selezione",
                "Seleziona una traccia dalla lista o una playlist per avviare la riproduzione sequenziale.");
    }

    /**
     * @brief Avvia l'effettivo processo di riproduzione del brano selezionato
     * @param track traccia da riprodurre
     */
    private void startTrackPlayback(Track track) {
        trackFinished = false;
        mainController.getPlayerContext().play(track);
        updateNowPlaying();

        PlaybackTimerManager timer = mainController.getTimerManager();
        progressSlider.setMax(track.getDuration());
        progressSlider.setValue(0);
        lblCurrentTime.setText("00:00");
        lblTotalTime.setText(timer.getFormattedTime(track.getDuration()));

        timer.start(track,
                elapsed -> {
                    progressSlider.setValue(elapsed);
                    lblCurrentTime.setText(timer.getFormattedTime(elapsed));
                },
                this::handlePlaybackFinished);
    }

    private void handlePlaybackFinished() {
        if (!sequentialMode) {
            trackFinished = true;
            mainController.getPlayerContext().stop();
            resetUI();
            lblNowPlaying.setText("Canzone terminata");
            return;
        }
        handleNext(null);
    }

    /**
     * @brief Passa al brano successivo nel container attivo
     * @param event pressione sul pulsante
     */
    public void handleNext(ActionEvent event) {
        mainController.getTimerManager().stop();
        Track before = mainController.getPlayerContext().getCurrentTrack();
        ObservableList<Track> queue = (activeContainer != null) ? activeContainer.getTracks()
                : mainController.getLibrary().getTracks();
        mainController.getPlayerContext().next(queue, before);
        Track after = mainController.getPlayerContext().getCurrentTrack();

        if (after != null && after != before) {
            startTrackPlayback(after);
        } else {
            lblNowPlaying.setText("Canzone terminata");
            trackFinished = true;
            mainController.getPlayerContext().stop();
            resetUI();
        }
    }

    /**
     * @brief Torna al brano precedente nel container attivo
     * @param event pressione sul pulsante
     */
    public void handlePrev(ActionEvent event) {
        mainController.getTimerManager().stop();
        Track before = mainController.getPlayerContext().getCurrentTrack();
        ObservableList<Track> queue = (activeContainer != null) ? activeContainer.getTracks()
                : mainController.getLibrary().getTracks();
        mainController.getPlayerContext().previous(queue, before);
        Track after = mainController.getPlayerContext().getCurrentTrack();

        if (after != null && after != before) {
            startTrackPlayback(after);
        }
    }

    /**
     * @brief Aggiorna l'interfaccia nel caso in cui la traccia venga rimossa dalla
     *        libreria.
     * @param removedIdx indice della traccia rimossa
     */
    public void handleTrackRemoval(int removedIdx) {
        ITrackContainer container = (activeContainer != null) ? activeContainer : mainController.getLibrary();

        if (container.getTracks() != null && !container.getTracks().isEmpty()) {
            int nextIdx = Math.min(removedIdx, container.getTracks().size() - 1);
            if(nextIdx >=0){
                Track nextTrack = container.getTracks().get(nextIdx);
                startTrackPlayback(nextTrack);
            }
        } else {
            resetUI();
            lblNowPlaying.setText("Nessuna traccia in riproduzione");
            mainController.getPlayerContext().stop();
            mainController.getPlayerContext().setCurrentTrack(null);
            trackFinished = true;
        }
    }

    /**
     * @brief Reset dei componenti grafici della riproduzione (slider e label del
     *        tempo)
     */
    public void resetUI() {
        progressSlider.setValue(0);
        lblCurrentTime.setText("00:00");
        lblTotalTime.setText("00:00");
    }

    /**
     * @brief Aggiorna la label "Now Playing" in base allo stato del player
     */
    private void updateNowPlaying() {
        Track track = mainController.getPlayerContext().getCurrentTrack();
        if (mainController.getPlayerContext().isPlaying() && track != null) {
            lblNowPlaying.setText("▶ " + track.getTitle() + " - " + track.getAuthor());
        } else if (track == null) {
            lblNowPlaying.setText("Nessuna traccia in riproduzione");
            mainController.getWindowManager().showInfo("Riproduzione terminata", "La riproduzione è stata interrotta.");
            mainController.getTimerManager().stop();
            resetUI();
        }
    }
}