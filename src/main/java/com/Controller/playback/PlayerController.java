package com.Controller.playback;

import com.Controller.core.MainController;
import com.Model.ITrackContainer;
import com.Model.Playlist;
import com.Model.Track;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class PlayerController {
    private MainController mainController;

    private Label lblNowPlaying, lblCurrentTime, lblTotalTime;
    private Slider progressSlider;

    private boolean sequentialMode = false;
    private boolean trackFinished = false;
    private ITrackContainer activeContainer;

    public void init(MainController mainController, Label lblNowPlaying, Label lblCurrentTime, Label lblTotalTime,
            Slider progressSlider) {
        this.mainController = mainController;
        this.lblNowPlaying = lblNowPlaying;
        this.lblCurrentTime = lblCurrentTime;
        this.lblTotalTime = lblTotalTime;
        this.progressSlider = progressSlider;
    }

    public void setTrackFinished(boolean finished) {
        this.trackFinished = finished;
    }

    public void playSong() {
        Track selectedTrack = mainController.getTrackTableController().getSelectedTrack();
        Playlist selectedPlaylist = mainController.getPlaylistTableController().getSelectedPlaylist();

        if (selectedTrack != null) {
            if (mainController.getPlayerContext().isPlaying()
                    && selectedTrack == mainController.getPlayerContext().getCurrentTrack() && !trackFinished) {
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
            mainController.getPlayerContext().setCurrentTrack(null);
            mainController.getPlayerContext().next(activeContainer.getTracks(), null);
            Track firstTrack = mainController.getPlayerContext().getCurrentTrack();
            if (firstTrack != null) {
                startTrackPlayback(firstTrack);
            }
            return;
        }
        mainController.getWindowManager().showWarning("Nessuna selezione",
                "Seleziona una traccia dalla lista per riprodurla.");
    }

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
                "Seleziona una traccia dalla lista per avviare la riproduzione sequenziale.");
        return;

    }

    private void startTrackPlayback(Track track) {
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
            resetUI();
            lblNowPlaying.setText("Canzone terminata");
            return;
        }
        handleNext(null);
    }

    public void handleNext(ActionEvent event) {
        mainController.getTimerManager().stop();
        Track before = mainController.getPlayerContext().getCurrentTrack();
        mainController.getPlayerContext().next(mainController.getLibrary().getTracks(), before);
        Track after = mainController.getPlayerContext().getCurrentTrack();

        if (after != null && after != before) {
            startTrackPlayback(after);
        } else {
            lblNowPlaying.setText("Canzone terminata");
        }
    }

    public void handlePrev(ActionEvent event) {
        mainController.getTimerManager().stop();
        Track before = mainController.getPlayerContext().getCurrentTrack();
        mainController.getPlayerContext().previous(mainController.getLibrary().getTracks(), before);
        Track after = mainController.getPlayerContext().getCurrentTrack();

        if (after != null && after != before) {
            startTrackPlayback(after);
        }
    }

    public void handleTrackRemoval(int removedIdx) {
        if (!mainController.getLibrary().getTracks().isEmpty()) {
            int nextIdx = Math.min(removedIdx, mainController.getLibrary().getTracks().size() - 1);
            Track nextTrack = mainController.getLibrary().getTracks().get(nextIdx);
            startTrackPlayback(nextTrack);
        } else {
            resetUI();
            lblNowPlaying.setText("Nessuna traccia in riproduzione");
            mainController.getPlayerContext().stop();
            mainController.getPlayerContext().setCurrentTrack(null);
        }
    }

    public void resetUI() {
        progressSlider.setValue(0);
        lblCurrentTime.setText("00:00");
        lblTotalTime.setText("00:00");
    }

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