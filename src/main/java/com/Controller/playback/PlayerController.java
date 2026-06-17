package com.Controller.playback;

import com.Controller.core.AppState;
import com.Controller.core.MainController;
import com.Controller.playlist.PlaylistController;
import com.Model.ITrackContainer;
import com.Model.Playlist;
import com.Model.Track;
import com.Observer.IPlayerSubscriber;
import com.State.PlayerContext;
import com.Strategy.LoopPlaylistStrategy;
import com.Strategy.LoopTrackStrategy;
import com.Strategy.SequentialStrategy;
import com.Strategy.ShuffleStrategy;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;

/**
 * @class PlayerController
 * 
 * @brief Gestisce la logica della riproduzione audio, il timer e l'interazione
 *        con l'interfaccia. La classe funge da controller per il sistema di
 *        riproduzione. Gestisce il flusso audio e coordina l'aggiornamento
 *        dell'interfaccia grafica in base allo stato del context e della
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
     * @brief Gestisce il tasto Play/Pausa.
     */
    public void togglePlayPause(PlayerContext context, TableView<Track> trackTable) {
        AppState appState = mainController.getAppState();
        var timerManager = appState.getTimerManager();
        var playerContext = appState.getPlayerContext();
        Track selectedTrack = trackTable.getSelectionModel().getSelectedItem();
        if (playerContext.isPlaying()) {
            playerContext.pause();
            timerManager.pause();
            mainController.updatePlayPauseButton(false);
        } else {
            if (selectedTrack != null) {
                playSong();
            } else if (!appState.getLibrary().getTracks().isEmpty()) {
                activeContainer = appState.getLibrary();
                setupEngineAndPlay((ObservableList<Track>) appState.getLibrary().getTracks(),
                        appState.getLibrary().getTracks().get(0));

            }
        }
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
     * @param mode Stringa che rappresenta la modalità scelta
     */
    public void setPlaybackMode(String mode) {
        var playerContext = mainController.getAppState().getPlayerContext();
        Track currenTrack = playerContext.getCurrentTrack();
        switch (mode) {
            case "Singola":
                sequentialMode = false;
                loopMode = false;
                loopPlaylistMode = false;
                playerContext.getPlaybackContext().setStrategy(new SequentialStrategy(),
                        currenTrack);
                break;
            case "Sequenziale":
                sequentialMode = true;
                loopMode = false;
                loopPlaylistMode = false;
                playerContext.getPlaybackContext().setStrategy(new SequentialStrategy(),
                        currenTrack);
                break;
            case "Loop brano":
                sequentialMode = false;
                loopMode = true;
                loopPlaylistMode = false;
                playerContext.getPlaybackContext().setStrategy(new LoopTrackStrategy(),
                        currenTrack);
                break;
            case "Shuffle":
                sequentialMode = true;
                loopMode = false;
                loopPlaylistMode = false;
                playerContext.getPlaybackContext().setStrategy(new ShuffleStrategy(), currenTrack);
                break;
            default:
                break;
        }
    }

    /**
     * @brief Recupera la coda di riproduzione corretta basandosi sulla selezione
     *        corrente dell'interfaccia utente.
     *        Controlla in ordine gerarchico se è selezionata una traccia nella
     *        libreria generale,
     *        una traccia nella vista della playlist aperta, o una playlist intera
     *        dalla tabella.
     * @return L'ObservableList di oggetti Track associata alla selezione corrente,
     *         oppure null.
     */
    private ObservableList<Track> getSelectedQueueFromUI() {
        var appState = mainController.getAppState();
        if (appState.getTrackTableController().getSelectedTrack() != null) {
            return (ObservableList<Track>) appState.getLibrary().getTracks();
        }
        PlaylistController pc = appState.getPlaylistController();
        if (pc != null && pc.getCurrentPlaylist() != null) {
            return (ObservableList<Track>) pc.getCurrentPlaylist().getTracks();
        }
        Playlist selectedPlaylist = appState.getPlaylistTableController().getSelectedPlaylist();
        if (selectedPlaylist != null) {
            return (ObservableList<Track>) selectedPlaylist.getTracks();
        }
        return activeContainer != null ? (ObservableList<Track>) activeContainer.getTracks() : null;
    }

    /**
     * @brief Configura il motore di riproduzione e avvia il brano.
     *        Resetta lo stato di traccia terminata, imposta la coda corrente nel
     *        contesto di riproduzione
     *        e nel contesto del lettore, avviando infine l'effettiva esecuzione
     *        audio.
     * @param queue La lista osservabile che costituisce la coda di riproduzione
     *              corrente.
     * @param track La traccia specifica da avviare.
     */
    private void setupEngineAndPlay(ObservableList<Track> queue, Track track) {
        trackFinished = false;
        var playerContext = mainController.getAppState().getPlayerContext();
        playerContext.getPlaybackContext().setCurrentQueue(queue, track);
        playerContext.setCurrentTrack(track);
        startTrackPlayback(track);
    }

    /**
     * @brief Gestisce l'avvio della riproduzione per il brano o la playlist
     *        selezionata
     */
    public void playSong() {
        AppState appState = mainController.getAppState();
        Track selectedTrack = appState.getTrackTableController().getSelectedTrack();
        Playlist selectedPlaylist = appState.getPlaylistTableController().getSelectedPlaylist();
        if (selectedTrack != null) {
            if (appState.getPlayerContext().isPlaying()
                    && selectedTrack == appState.getPlayerContext().getCurrentTrack()
                    && !trackFinished) {
                appState.getWindowManager().showInfo("Già in riproduzione",
                        "Brano selezionato già in riproduzione");
                return;
            }
            activeContainer = appState.getLibrary();
            setupEngineAndPlay(getSelectedQueueFromUI(), selectedTrack);
            return;
        }

        Track playlistViewTrack = getPlaylistViewSelectedTrack();
        if (playlistViewTrack != null) {
            if (appState.getPlayerContext().isPlaying()
                    && playlistViewTrack == appState.getPlayerContext().getCurrentTrack()
                    && !trackFinished) {
                appState.getWindowManager().showInfo("Già in riproduzione",
                        "Brano selezionato già in riproduzione");
                return;
            }
            activeContainer = appState.getPlaylistController().getCurrentPlaylist();
            setupEngineAndPlay(getSelectedQueueFromUI(), playlistViewTrack);
            return;
        }
        PlaylistController openPc = appState.getPlaylistController();
        if (openPc != null) {
            Playlist current = openPc.getCurrentPlaylist();
            if (current != null && current.getTracksCount() > 0) {
                Track firstTrack = current.getTracks().get(0);
                if (appState.getPlayerContext().isPlaying()
                        && firstTrack == appState.getPlayerContext().getCurrentTrack()
                        && !trackFinished) {
                    appState.getWindowManager().showInfo("Già in riproduzione",
                            "Sto già eseguendo questo brano.");
                    return;
                }
                activeContainer = current;
                setupEngineAndPlay(getSelectedQueueFromUI(), firstTrack);
                return;
            }
        }

        if (selectedPlaylist != null) {
            if (selectedPlaylist.getTracksCount() == 0) {
                appState.getWindowManager().showWarning("Playlist vuota",
                        "La playlist selezionata non ha brani.");
                return;
            }

            activeContainer = selectedPlaylist;
            trackFinished = false;
            Track firstTrack = activeContainer.getTracks().get(0);
            if (appState.getPlayerContext().isPlaying()
                    && firstTrack == appState.getPlayerContext().getCurrentTrack() && !trackFinished) {
                appState.getWindowManager().showInfo("Già in riproduzione",
                        "Sto già eseguendo questo brano.");
                return;
            }
            setupEngineAndPlay(getSelectedQueueFromUI(), firstTrack);
            return;
        }
        appState.getWindowManager().showWarning("Nessuna selezione",
                "Seleziona una traccia dalla lista o una playlist per riprodurla.");
    }

    /**
     * @brief Avvia l'effettivo processo di riproduzione del brano selezionato
     * @param track traccia da riprodurre
     */
    private void startTrackPlayback(Track track) {
        trackFinished = false;
        AppState appState = mainController.getAppState();
        Track current = appState.getPlayerContext().getCurrentTrack();
        if (current != null && current != track && current.getAudioSource() != null) {
            current.getAudioSource().stopPlayback();
        }
        appState.getPlayerContext().play(track);
        mainController.updatePlayPauseButton(true);
        updateNowPlaying();

        PlaybackTimerManager timer = appState.getTimerManager();
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
        AppState appState = mainController.getAppState();
        Track current = appState.getPlayerContext().getCurrentTrack();
        if (current == null)
            return;

        if (appState.getPlayerContext().isPlaying()) {
            appState.getPlayerContext().pause();
            appState.getTimerManager().pause();
            if (current.getAudioSource() != null) {
                current.getAudioSource().pausePlayback();
            }
            lblNowPlaying.setText("⏸ " + current.getTitle() + " - " + current.getAuthor());
            mainController.updatePlayPauseButton(false);
        } else if (appState.getPlayerContext().isPaused()) {
            appState.getPlayerContext().setState(appState.getPlayerContext().getPlayingState());
            appState.getTimerManager().resume();
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
        AppState appState = mainController.getAppState();
        Track current = appState.getPlayerContext().getCurrentTrack();
        if (current != null) {
            current.incrementPlayCount();
        }
        if (loopMode) {
            if (current != null) {
                startTrackPlayback(current);
            }
            return;
        }
        if (!sequentialMode) {
            stopSong();
            trackFinished = true;
            appState.getPlayerContext().stop();
            resetUI();
            lblNowPlaying.setText("Canzone terminata");
            mainController.updatePlayPauseButton(false);
            mainController.updateNextButton();
            return;
        }
        handleNext(null);
        ITrackContainer container = getActiveContainer();
        if (container instanceof Playlist) {
            Playlist activePlaylist = (Playlist) container;
            activePlaylist.incrementPlayCount();
        }
    }

    /**
     * @brief Passa al brano successivo nel container attivo
     * @param event pressione sul pulsante
     */
    public void handleNext(ActionEvent event) {
        AppState appState = mainController.getAppState();
        appState.getTimerManager().stop();
        Track before = appState.getPlayerContext().getCurrentTrack();
        appState.getPlayerContext().next();
        Track after = appState.getPlayerContext().getCurrentTrack();
        boolean isLoopStrategy = appState.getPlayerContext().getPlaybackContext()
                .getStrategy() instanceof LoopPlaylistStrategy;
        if (after != null && (after != before || loopMode || loopPlaylistMode)) {
            selectTrackInUI(after);
            startTrackPlayback(after);
        } else {
            if (before != null && before.getAudioSource() != null) {
                before.getAudioSource().stopPlayback();
            }
            appState.getPlayerContext().setCurrentTrack(null);
            lblNowPlaying.setText("Canzone terminata");
            trackFinished = true;
            appState.getPlayerContext().stop();
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
        AppState appState = mainController.getAppState();
        appState.getTimerManager().stop();
        Track before = appState.getPlayerContext().getCurrentTrack();
        appState.getPlayerContext().previous();
        Track after = appState.getPlayerContext().getCurrentTrack();

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
        AppState appState = mainController.getAppState();
        ITrackContainer container = (activeContainer != null) ? activeContainer : appState.getLibrary();

        if (container.getTracks() != null && !container.getTracks().isEmpty()) {
            int nextIdx = Math.min(removedIdx, container.getTracks().size() - 1);
            if (nextIdx >= 0) {
                Track nextTrack = container.getTracks().get(nextIdx);
                startTrackPlayback(nextTrack);
            }
        } else {
            resetUI();
            lblNowPlaying.setText("Nessuna traccia in riproduzione");
            appState.getPlayerContext().stop();
            appState.getPlayerContext().setCurrentTrack(null);
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
        AppState appState = mainController.getAppState();
        Track track = appState.getPlayerContext().getCurrentTrack();
        if (appState.getPlayerContext().isPlaying() && track != null) {
            lblNowPlaying.setText("▶ " + track.getTitle() + " - " + track.getAuthor());
        } else if (track == null) {
            lblNowPlaying.setText("Nessuna traccia in riproduzione");
            appState.getWindowManager().showInfo("Riproduzione terminata", "La riproduzione è stata interrotta.");
            appState.getTimerManager().stop();
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
        PlaylistController pc = mainController.getAppState().getPlaylistController();
        return pc != null ? pc.getSelectedTrack() : null;
    }

    /**
     * @brief Seleziona visivamente un brano nella tabella corretta.
     *        Se il container attivo è una Playlist, aggiorna la selezione
     *        nella vista playlist; altrimenti aggiorna la tabella della libreria.
     * @param track la Track da evidenziare nell'interfaccia.
     */
    private void selectTrackInUI(Track track) {
        AppState appState = mainController.getAppState();
        if (activeContainer instanceof Playlist) {
            PlaylistController pc = appState.getPlaylistController();
            if (pc != null)
                pc.selectTrack(track);
        } else {
            appState.getTrackTableController().selectTrack(track);
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
        AppState appState = mainController.getAppState();
        if (playlist == null || playlist.getTracksCount() == 0) {
            appState.getWindowManager().showWarning("Playlist vuota",
                    "La playlist non ha brani.");
            return;
        }
        this.loopMode = false;
        this.sequentialMode = true;
        this.loopPlaylistMode = true;
        appState.getPlayerContext().getPlaybackContext().setStrategy(new LoopPlaylistStrategy(),
                appState.getPlayerContext().getCurrentTrack());
        Track currentTrack = appState.getPlayerContext().getCurrentTrack();
        if (currentTrack != null && playlist.getTracks().contains(currentTrack)
                && appState.getPlayerContext().isPlaying()) {
            activeContainer = playlist;
            sequentialMode = true;
            loopMode = false;
            return;
        }
        PlaylistController pc = appState.getPlaylistController();
        Track selectedTrack = pc != null ? pc.getSelectedTrack() : null;
        activeContainer = playlist;
        sequentialMode = true;
        loopMode = false;
        Track startTrack = selectedTrack != null ? selectedTrack : playlist.getTracks().get(0);
        setupEngineAndPlay(playlist.getTracks(), startTrack);
        mainController.updateNextButton();
    }

    /**
     * @brief Ferma la riproduzione e resetta l'interfaccia.
     */
    public void stopSong() {
        mainController.getAppState().getPlayerContext().stop();
        resetUI();
    }

    /**
     * @brief Ferma la riproduzione se il brano modificato è quello corrente.
     *        Da chiamare dopo ogni modifica tramite ModifyTrack.
     * @param modifiedTrack Il brano appena modificato.
     */
    public void handleTrackModified(Track modifiedTrack) {
        AppState appState = mainController.getAppState();
        Track current = appState.getPlayerContext().getCurrentTrack();
        if (current == null || current != modifiedTrack)
            return;

        appState.getTimerManager().stop();
        if (current.getAudioSource() != null) {
            current.getAudioSource().stopPlayback();
        }
        appState.getPlayerContext().stop();
        appState.getPlayerContext().setCurrentTrack(null);
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
        AppState appState = mainController.getAppState();
        if (loopMode || loopPlaylistMode)
            return true;
        if (appState.getPlayerContext().getPlaybackContext().getStrategy() instanceof ShuffleStrategy)
            return true;
        Track current = appState.getPlayerContext().getCurrentTrack();
        if (current == null)
            return false;
        ObservableList<Track> queue = getSelectedQueueFromUI();
        if (queue == null || queue.isEmpty()) {
            return false;
        }
        int idx = queue.indexOf(current);
        return idx >= 0 && idx < queue.size() - 1;

    }

}