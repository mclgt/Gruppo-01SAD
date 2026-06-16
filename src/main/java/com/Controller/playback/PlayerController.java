package com.Controller.playback;

import java.util.List;

import com.Controller.core.MainController;
import com.Controller.playlist.PlaylistController;
import com.Model.ITrackContainer;
import com.Model.Playlist;

import com.Strategy.LoopPlaylistStrategy;
import com.Model.Track;
import com.Strategy.LoopTrackStrategy;
import com.Strategy.SequentialStrategy;
import com.Strategy.ShuffleStrategy;

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
    /** @brief True se la riproduzione in loop dell'intera playlist è attiva. */
    private boolean loopPlaylistMode = false;
    /** @brief True se l'auto-avanzamento sequenziale/shuffle è attivo. */
    private boolean sequentialMode = false;
    /** @brief True se la riproduzione in loop del brano corrente è attiva. */
    private boolean loopMode = false;
    /** @brief True se il brano corrente è terminato naturalmente. */
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
     * @brief Restituisce true se il brano corrente è terminato naturalmente.
     * @return True se trackFinished,False altrimenti.
     */
    public boolean isTrackFinished() {
        return trackFinished;
    }

    /**
     * @brief Imposta la modalità di riproduzione selezionata dal ComboBox.
     */
    public void setPlaybackMode(String mode) {
        switch (mode) {
            case "Singola":
                sequentialMode = false;
                loopMode = false;
                loopPlaylistMode = false;
                mainController.getPlayerContext().getPlaybackContext().setStrategy(new SequentialStrategy());
                break;
            case "Sequenziale":
                sequentialMode = true;
                loopMode = false;
                loopPlaylistMode = false;
                mainController.getPlayerContext().getPlaybackContext().setStrategy(new SequentialStrategy());
                break;
            case "Loop brano":
                sequentialMode = false;
                loopMode = true;
                loopPlaylistMode = false;
                mainController.getPlayerContext().getPlaybackContext().setStrategy(new LoopTrackStrategy());
                break;
            case "Shuffle":
                sequentialMode = true;
                loopMode = false;
                loopPlaylistMode = false;
                mainController.getPlayerContext().getPlaybackContext().setStrategy(new ShuffleStrategy());
                break;
            default:
                break;
        }
        mainController.updateNextButton();
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
            startTrackPlayback(selectedTrack);
            return;
        }

        Track playlistViewTrack = getPlaylistViewSelectedTrack();
        if (playlistViewTrack != null) {
            if (mainController.getPlayerContext().isPlaying()
                    && playlistViewTrack == mainController.getPlayerContext().getCurrentTrack()
                    && !trackFinished) {
                mainController.getWindowManager().showInfo("Già in riproduzione",
                        "Brano selezionato già in riproduzione");
                return;
            }
            activeContainer = mainController.getPlaylistController().getCurrentPlaylist();
            trackFinished = false;
            startTrackPlayback(playlistViewTrack);
            return;
        }
        PlaylistController openPc = mainController.getPlaylistController();
        if (openPc != null) {
            Playlist current = openPc.getCurrentPlaylist();
            if (current != null && current.getTracksCount() > 0) {
                Track firstTrack = current.getTracks().get(0);
                if (mainController.getPlayerContext().isPlaying()
                        && firstTrack == mainController.getPlayerContext().getCurrentTrack()
                        && !trackFinished) {
                    mainController.getWindowManager().showInfo("Già in riproduzione",
                            "Sto già eseguendo questo brano.");
                    return;
                }
                activeContainer = current;
                trackFinished = false;
                startTrackPlayback(firstTrack);
                return;
            }
        }

        if (selectedPlaylist != null) {
            if (selectedPlaylist.getTracksCount() == 0) {
                mainController.getWindowManager().showWarning("Playlist vuota",
                        "La playlist selezionata non ha brani.");
                return;
            }

            activeContainer = selectedPlaylist;
            trackFinished = false;
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
            loopMode = false;
            loopPlaylistMode = false;
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
        PlaylistController openPc = mainController.getPlaylistController();
        if (openPc != null) {
            Playlist current = openPc.getCurrentPlaylist();
            if (current != null && current.getTracksCount() > 0) {
                activeContainer = current;
                trackFinished = false;
                sequentialMode = true;
                loopMode = false;
                mainController.getPlayerContext().getPlaybackContext().setStrategy(new SequentialStrategy());
                startTrackPlayback(current.getTracks().get(0));
                return;
            }
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
                "Seleziona una traccia dalla lista o una playlist per avviare la riproduzione sequenziale.");
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
            mainController.getPlayerContext().getPlaybackContext().setStrategy(new LoopTrackStrategy());
            startTrackPlayback(selectedTrack);
            return;
        }
        Track playlistViewTrack = getPlaylistViewSelectedTrack();
        if (playlistViewTrack != null) {
            activeContainer = mainController.getPlaylistController().getCurrentPlaylist();
            trackFinished = false;
            sequentialMode = false;
            loopMode = true;
            mainController.getPlayerContext().getPlaybackContext().setStrategy(new LoopTrackStrategy());
            startTrackPlayback(playlistViewTrack);
            return;
        }
        PlaylistController openPc = mainController.getPlaylistController();
        if (openPc != null) {
            Playlist current = openPc.getCurrentPlaylist();
            if (current != null && current.getTracksCount() > 0) {
                activeContainer = current;
                trackFinished = false;
                sequentialMode = false;
                loopMode = true;
                mainController.getPlayerContext().getPlaybackContext().setStrategy(new LoopTrackStrategy());
                startTrackPlayback(current.getTracks().get(0));
                return;
            }
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
            mainController.getPlayerContext().getPlaybackContext().setStrategy(new LoopTrackStrategy());
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
            loopPlaylistMode = false;
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
        PlaylistController openPc = mainController.getPlaylistController();
        if (openPc != null) {
            Playlist current = openPc.getCurrentPlaylist();
            if (current != null && current.getTracksCount() > 0) {
                activeContainer = current;
                trackFinished = false;
                sequentialMode = true;
                loopMode = false;
                mainController.getPlayerContext().getPlaybackContext().setStrategy(new ShuffleStrategy());
                startTrackPlayback(current.getTracks().get(0));
                return;
            }
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
        trackFinished = false;
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

        mainController.updateNextButton();
    }

    /**
     * @brief Toggle pausa/ripresa della riproduzione corrente.
     *        Se il player è in riproduzione, transisce a PausedState e
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
     * @brief Callback invocata al termine naturale di un brano.
     *        Determina il comportamento successivo in base ai flag di modalità:
     *        se @ref loopMode è attivo riavvia il brano corrente, se
     * @ref sequentialMode è attivo chiama @ref handleNext, altrimenti
     *      ferma la riproduzione e resetta l'interfaccia.
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
            stopSong();
            trackFinished = true;
            mainController.getPlayerContext().stop();
            resetUI();
            lblNowPlaying.setText("Canzone terminata");
            mainController.updatePlayPauseButton(false);
            mainController.updateNextButton();
            return;
        }
        Track before = mainController.getPlayerContext().getCurrentTrack();
        Track next = mainController.getPlayerContext().getPlaybackContext().nextTrack(getActiveQueue(), before);
        if (next != null) {
            handleNext(null);
        } else {
            stopSong();
            trackFinished = true;
            mainController.getPlayerContext().stop();
            resetUI();
            lblNowPlaying.setText("Canzone terminata");
            mainController.updatePlayPauseButton(false);
            mainController.updateNextButton();
        }
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
        boolean isLoopStrategy = mainController.getPlayerContext().getPlaybackContext()
                .getStrategy() instanceof LoopPlaylistStrategy;
        if (after != null && (after != before || loopMode || loopPlaylistMode)) {
            selectTrackInUI(after);
            startTrackPlayback(after);
        } else {
            if (before != null && before.getAudioSource() != null) {
                before.getAudioSource().stopPlayback();
            }
            mainController.getPlayerContext().setCurrentTrack(null);
            lblNowPlaying.setText("Canzone terminata");
            trackFinished = true;
            mainController.getPlayerContext().stop();
            resetUI();
            mainController.updatePlayPauseButton(false);
            mainController.updateNextButton();
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
        ITrackContainer container = (activeContainer != null) ? activeContainer : mainController.getLibrary();

        if (container.getTracks() != null && !container.getTracks().isEmpty()) {
            int nextIdx = Math.min(removedIdx, container.getTracks().size() - 1);
            if (nextIdx >= 0) {
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

    /**
     * @brief Restituisce il brano selezionato nella vista playlist aperta, se
     *        presente.
     *        Interroga il PlaylistController attivo; restituisce nul
     *        se nessuna vista playlist è aperta o nessun brano è selezionato.
     * @return La Track selezionata nella vista playlist, o null
     */
    private Track getPlaylistViewSelectedTrack() {
        PlaylistController pc = mainController.getPlaylistController();
        return pc != null ? pc.getSelectedTrack() : null;
    }

    /**
     * @brief Seleziona visivamente un brano nella tabella corretta.
     *        Se il container attivo è una Playlist, aggiorna la selezione
     *        nella vista playlist; altrimenti aggiorna la tabella della libreria.
     * @param track la Track da evidenziare nell'interfaccia.
     */
    private void selectTrackInUI(Track track) {
        if (activeContainer instanceof Playlist) {
            PlaylistController pc = mainController.getPlaylistController();
            if (pc != null)
                pc.selectTrack(track);
        } else {
            mainController.getTrackTableController().selectTrack(track);
        }
    }

    /**
     * @brief Avvia la riproduzione in loop della playlist: scorre i brani in
     *        sequenza e ricomincia dal primo quando si raggiunge l'ultimo.
     *        Se un brano della playlist è già in riproduzione aggiorna solo i flag
     *        e la strategia senza interrompere l'audio corrente.
     * @param playlist La playlist su cui applicare il loop.
     */
    public void loopPlaylistRip(Playlist playlist) {
        if (playlist == null || playlist.getTracksCount() == 0) {
            mainController.getWindowManager().showWarning("Playlist vuota",
                    "La playlist non ha brani.");
            return;
        }
        this.loopMode = false;
        this.sequentialMode = true;
        this.loopPlaylistMode = true;
        mainController.getPlayerContext().getPlaybackContext().setStrategy(new LoopPlaylistStrategy());
        Track currentTrack = mainController.getPlayerContext().getCurrentTrack();
        if (currentTrack != null && playlist.getTracks().contains(currentTrack)
                && mainController.getPlayerContext().isPlaying()) {
            activeContainer = playlist;
            sequentialMode = true;
            loopMode = false;
            return;
        }
        PlaylistController pc = mainController.getPlaylistController();
        Track selectedTrack = pc != null ? pc.getSelectedTrack() : null;
        activeContainer = playlist;
        trackFinished = false;
        sequentialMode = true;
        loopMode = false;
        Track startTrack = selectedTrack != null ? selectedTrack : playlist.getTracks().get(0);
        startTrackPlayback(startTrack);
        mainController.updateNextButton();
    }

    /**
     * @brief Restituisce la lista di brani del container attivo.
     *        Se nessun container è stato impostato, utilizza la libreria globale
     *        come fallback.
     * @return La List di Track su cui operano next, prev e
     *         auto-avanzamento.
     */
    private List<Track> getActiveQueue() {
        return activeContainer != null ? activeContainer.getTracks() : mainController.getLibrary().getTracks();
    }

    /**
     * @brief Ferma la riproduzione e resetta l'interfaccia.
     */
    public void stopSong() {
        mainController.getPlayerContext().stop();
        resetUI();
    }

    /**
     * @brief Ferma la riproduzione se il brano modificato è quello corrente.
     *        Da chiamare dopo ogni modifica tramite ModifyTrack.
     * @param modifiedTrack Il brano appena modificato.
     */
    public void handleTrackModified(Track modifiedTrack) {
        Track current = mainController.getPlayerContext().getCurrentTrack();
        if (current == null || current != modifiedTrack) return;

        mainController.getTimerManager().stop();
        if (current.getAudioSource() != null) {
            current.getAudioSource().stopPlayback();
        }
        mainController.getPlayerContext().stop();
        mainController.getPlayerContext().setCurrentTrack(null);
        trackFinished = true;
        resetUI();
        lblNowPlaying.setText("Nessuna traccia in riproduzione");
        mainController.updatePlayPauseButton(false);
    }

    /**
     * @brief Restituisce il contenitore attivo.
     * @return ITrackContainer corrente.
     */
    public ITrackContainer getActiveContainer() {
        return this.activeContainer;
    }

    public boolean isNextAvailable() {
        if (loopMode || loopPlaylistMode) return true;
        if (mainController.getPlayerContext().getPlaybackContext().getStrategy() instanceof ShuffleStrategy) return true;
        Track current = mainController.getPlayerContext().getCurrentTrack();
        if (current == null) return false;
        List<Track> queue = getActiveQueue();
        int idx = queue.indexOf(current);
        return idx >= 0 && idx < queue.size() - 1;
    }
}