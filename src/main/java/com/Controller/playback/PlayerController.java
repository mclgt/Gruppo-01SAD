package com.Controller.playback;

import com.Controller.core.MainController;
import com.Controller.playlist.PlaylistController;
import com.Model.ITrackContainer;
import com.Model.Playlist;
import com.Model.Track;

import java.util.List;
import com.Strategy.LoopStrategy;
import com.Strategy.SequentialStrategy;
import com.Strategy.ShuffleStrategy;

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

    /** @brief True se l'auto-avanzamento sequenziale/shuffle è attivo. */
    private boolean sequentialMode = false;
    /** @brief True se la riproduzione in loop del brano corrente è attiva. */
    private boolean loopMode = false;
    /** @brief True se il brano corrente è terminato naturalmente. */
    private boolean trackFinished = false;
    /** @brief Container attivo (libreria o playlist) su cui opera la riproduzione. */
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
     * @brief Imposta lo stato di terminazione del brano corrente.
     * @param finished {@code true} se il brano è terminato, {@code false} altrimenti.
     */
    // quando termina la traccia deve modificare il valore di trackFinished così da
    // poter resettare la UI co nun altro metodo
    public void setTrackFinished(boolean finished) {
        this.trackFinished = finished;
    }

    /**
     * @brief Restituisce true se il brano corrente è terminato naturalmente.
     * @return {@code true} se trackFinished, {@code false} altrimenti.
     */
    public boolean isTrackFinished() {
        return trackFinished;
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
                    && selectedTrack == mainController.getPlayerContext().getCurrentTrack() && !trackFinished) {
                mainController.getWindowManager().showInfo("Già in riproduzione",
                        "Brano selezionato già in riproduzione");
                return;
            }
            activeContainer = mainController.getLibrary();
            trackFinished = false;
            sequentialMode = false;
            loopMode = false;
            startTrackPlayback(selectedTrack);
            return;
        }
        Track playlistViewTrack = getPlaylistViewSelectedTrack();
        if (playlistViewTrack != null) {
            if (mainController.getPlayerContext().isPlaying()
                    && playlistViewTrack == mainController.getPlayerContext().getCurrentTrack() && !trackFinished) {
                mainController.getWindowManager().showInfo("Già in riproduzione",
                        "Brano selezionato già in riproduzione");
                return;
            }
            activeContainer = mainController.getPlaylistController().getCurrentPlaylist();
            trackFinished = false;
            sequentialMode = false;
            loopMode = false;
            startTrackPlayback(playlistViewTrack);
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
            loopMode = false;
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

    /**
     * @brief Avvia la riproduzione in modalità sequenziale
     * @param event pressione del pulsante
     */
    // Questi tre metodi sono simili ma cambia solamente la strategy utilizzata per
    // riprodurre i brani, i controlli sono uguali in quanto devo conoscere il brano
    // corrente per poter determinare quale brano devo proprorre all'utente
    public void sequentialRip(ActionEvent event) {
        Track selectedTrack = mainController.getTrackTableController().getSelectedTrack();
        Playlist selectedPlaylist = mainController.getPlaylistTableController().getSelectedPlaylist();
        if (selectedTrack != null) {
            activeContainer = mainController.getLibrary();
            trackFinished = false;
            sequentialMode = true;
            loopMode = false;
            mainController.getPlayerContext().getPlaybackContext().setStrategy(new SequentialStrategy());
            startTrackPlayback(selectedTrack);
            return;
        }
        Track playlistViewTrack = getPlaylistViewSelectedTrack();
        if (playlistViewTrack != null) {
            activeContainer = mainController.getPlaylistController().getCurrentPlaylist();
            trackFinished = false;
            sequentialMode = true;
            loopMode = false;
            mainController.getPlayerContext().getPlaybackContext().setStrategy(new SequentialStrategy());
            startTrackPlayback(playlistViewTrack);
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
            loopMode = false;
            mainController.getPlayerContext().getPlaybackContext().setStrategy(new SequentialStrategy());
            Track firstTrack = activeContainer.getTracks().get(0);
            startTrackPlayback(firstTrack);
            return;
        }
        mainController.getWindowManager().showWarning("Nessuna selezione",
                "Seleziona una traccia dalla lista per avviare la riproduzione sequenziale.");
        return;

    }

    /**
     * @brief Avvia la riproduzione in modalità loop (stesso brano ripetuto)
     * @param event pressione del pulsante
     */
    public void loopRip(ActionEvent event) {
        Track selectedTrack = mainController.getTrackTableController().getSelectedTrack();
        Playlist selectedPlaylist = mainController.getPlaylistTableController().getSelectedPlaylist();
        if (selectedTrack != null) {
            activeContainer = mainController.getLibrary();
            trackFinished = false;
            sequentialMode = false;
            loopMode = true;
            mainController.getPlayerContext().getPlaybackContext().setStrategy(new LoopStrategy());
            startTrackPlayback(selectedTrack);
            return;
        }
        Track playlistViewTrack = getPlaylistViewSelectedTrack();
        if (playlistViewTrack != null) {
            activeContainer = mainController.getPlaylistController().getCurrentPlaylist();
            trackFinished = false;
            sequentialMode = false;
            loopMode = true;
            mainController.getPlayerContext().getPlaybackContext().setStrategy(new LoopStrategy());
            startTrackPlayback(playlistViewTrack);
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
            loopMode = true;
            mainController.getPlayerContext().getPlaybackContext().setStrategy(new LoopStrategy());
            Track firstTrack = activeContainer.getTracks().get(0);
            startTrackPlayback(firstTrack);
            return;
        }
        mainController.getWindowManager().showWarning("Nessuna selezione",
                "Seleziona una traccia dalla lista per avviare la riproduzione in loop.");
    }

    /**
     * @brief Avvia la riproduzione in modalità shuffle (ordine casuale)
     * @param event pressione del pulsante
     */
    public void shuffleRip(ActionEvent event) {
        Track selectedTrack = mainController.getTrackTableController().getSelectedTrack();
        Playlist selectedPlaylist = mainController.getPlaylistTableController().getSelectedPlaylist();
        if (selectedTrack != null) {
            activeContainer = mainController.getLibrary();
            trackFinished = false;
            sequentialMode = true;
            loopMode = false;
            mainController.getPlayerContext().getPlaybackContext().setStrategy(new ShuffleStrategy());
            startTrackPlayback(selectedTrack);
            return;
        }
        Track playlistViewTrack = getPlaylistViewSelectedTrack();
        if (playlistViewTrack != null) {
            activeContainer = mainController.getPlaylistController().getCurrentPlaylist();
            trackFinished = false;
            sequentialMode = true;
            loopMode = false;
            mainController.getPlayerContext().getPlaybackContext().setStrategy(new ShuffleStrategy());
            startTrackPlayback(playlistViewTrack);
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
            loopMode = false;
            mainController.getPlayerContext().getPlaybackContext().setStrategy(new ShuffleStrategy());
            Track firstTrack = activeContainer.getTracks().get(0);
            startTrackPlayback(firstTrack);
            return;
        }
        mainController.getWindowManager().showWarning("Nessuna selezione",
                "Seleziona una traccia dalla lista per avviare la riproduzione casuale.");
    }

    /**
     * @brief Avvia l'effettivo processo di riproduzione del brano selezionato
     * @param track traccia da riprodurre
     */
    private void startTrackPlayback(Track track) {
        mainController.getPlayerContext().play(track);
        mainController.updatePlayPauseButton(true);
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

    /**
     * @brief Toggle pausa/ripresa della riproduzione corrente.
     *        Se il player è in riproduzione, transisce a @ref PausedState e
     *        congela timer e audio. Se è già in pausa, riprende esattamente
     *        dal punto in cui era stato fermato senza azzerare slider e label.
     */
    public void pauseSong() {
        Track current = mainController.getPlayerContext().getCurrentTrack();
        if (current == null)
            return;

        if (mainController.getPlayerContext().isPlaying()) {
            mainController.getPlayerContext().pause();
            mainController.getTimerManager().pause();
            if (current.getAudioSource() != null) {
                current.getAudioSource().pausePlayback();
            }
            lblNowPlaying.setText("⏸ " + current.getTitle() + " - " + current.getAuthor());
            mainController.updatePlayPauseButton(false);
        } else if (mainController.getPlayerContext().isPaused()) {
            mainController.getPlayerContext().setState(mainController.getPlayerContext().getPlayingState());
            mainController.getTimerManager().resume();
            if (current.getAudioSource() != null) {
                current.getAudioSource().resumePlayback();
            }
            updateNowPlaying();
            mainController.updatePlayPauseButton(true);
        }
    }

    /**
     * @brief Gestisce la logica da eseguire al termine di un brano
     */
    private void handlePlaybackFinished() {
        if (loopMode) {
            Track current = mainController.getPlayerContext().getCurrentTrack();
            if (current != null) {
                startTrackPlayback(current);
            }
            return;
        }
        if (!sequentialMode) {
            trackFinished = true;
            resetUI();
            lblNowPlaying.setText("Canzone terminata");
            mainController.updatePlayPauseButton(false);
            return;
        }
        handleNext(null);
    }

    //handleNext chiede direttamente alla strategy corrente quale sia la traccia successiva,
    //senza passare per playerContext.next() che avrebbe avviato l'audio due volte (bug del double-play).
    //ho aggiunto sequentialMode = true così la traccia che parte continua ad auto-avanzare quando finisce.
    //ho aggiunto selectTrack così la selezione nella tabella segue la canzone in riproduzione,
    //in questo modo se premo loop o shuffle subito dopo, parte dalla traccia corretta e non da quella precedente
    /**
     * @brief Passa al brano successivo nel container attivo
     * @param event pressione sul pulsante
     */
    public void handleNext(ActionEvent event) {
        mainController.getTimerManager().stop();
        Track before = mainController.getPlayerContext().getCurrentTrack();
        mainController.getPlayerContext().next(getActiveQueue(), before);
        Track after = mainController.getPlayerContext().getCurrentTrack();

        if (after != null && (after != before || loopMode)) {
            selectTrackInUI(after);
            startTrackPlayback(after);
        } else {
            if (before != null && before.getAudioSource() != null) {
                before.getAudioSource().stopPlayback();
            }
            mainController.getPlayerContext().setCurrentTrack(null);
            lblNowPlaying.setText("Canzone terminata");
            resetUI();
            mainController.updatePlayPauseButton(false);
        }
    }

    /**
     * @brief Torna al brano precedente nel container attivo
     * @param event pressione sul pulsante
     */
    public void handlePrev(ActionEvent event) {
        mainController.getTimerManager().stop();
        sequentialMode = true;
        loopMode = false;
        Track before = mainController.getPlayerContext().getCurrentTrack();
        mainController.getPlayerContext().previous(getActiveQueue(), before);
        Track after = mainController.getPlayerContext().getCurrentTrack();

        if (after != null && after != before) {
            selectTrackInUI(after);
            startTrackPlayback(after);
        }
    }

    /**
     * @brief Aggiorna l'interfaccia nel caso in cui la traccia venga rimossa dalla
     *        libreria.
     * @param removedIdx indice della traccia rimossa
     */
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
            mainController.updatePlayPauseButton(false);
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

    /**
     * @brief Restituisce il brano selezionato nella vista playlist aperta, se presente.
     *        Interroga il @ref PlaylistController attivo; restituisce {@code null}
     *        se nessuna vista playlist è aperta o nessun brano è selezionato.
     * @return La @ref Track selezionata nella vista playlist, o {@code null}.
     */
    private Track getPlaylistViewSelectedTrack() {
        PlaylistController pc = mainController.getPlaylistController();
        return pc != null ? pc.getSelectedTrack() : null;
    }

    /**
     * @brief Seleziona visivamente un brano nella tabella corretta.
     *        Se il container attivo è una @ref Playlist, aggiorna la selezione
     *        nella vista playlist; altrimenti aggiorna la tabella della libreria.
     * @param track Il @ref Track da evidenziare nell'interfaccia.
     */
    private void selectTrackInUI(Track track) {
        if (activeContainer instanceof Playlist) {
            PlaylistController pc = mainController.getPlaylistController();
            if (pc != null) pc.selectTrack(track);
        } else {
            mainController.getTrackTableController().selectTrack(track);
        }
    }

    /**
     * @brief Restituisce la lista di brani del container attivo.
     *        Se nessun container è stato impostato, utilizza la libreria globale
     *        come fallback.
     * @return La @ref List di @ref Track su cui operano next, prev e auto-avanzamento.
     */
    private List<Track> getActiveQueue() {
        return activeContainer != null ? activeContainer.getTracks() : mainController.getLibrary().getTracks();
    }
}