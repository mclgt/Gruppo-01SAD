package com.Controller.playback;

import com.Controller.core.MainController;
import com.Model.Track;
import com.Strategy.LoopStrategy;
import com.Strategy.SequentialStrategy;
import com.Strategy.ShuffleStrategy;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class PlayerController {
    private MainController mainController;

    private Label lblNowPlaying, lblCurrentTime, lblTotalTime;
    private Slider progressSlider;

    private boolean sequentialMode = false;
    private boolean trackFinished = false;

    public void init(MainController mainController, Label lblNowPlaying, Label lblCurrentTime, Label lblTotalTime,
            Slider progressSlider) {
        this.mainController = mainController;
        this.lblNowPlaying = lblNowPlaying;
        this.lblCurrentTime = lblCurrentTime;
        this.lblTotalTime = lblTotalTime;
        this.progressSlider = progressSlider;
    }

    // quando termina la traccia deve modificare il valore di trackFinished così da
    // poter resettare la UI co nun altro metodo
    public void setTrackFinished(boolean finished) {
        this.trackFinished = finished;
    }

    // questa è la riproduzione singola e controlla che io abbia selezionato un
    // brano e se il brano già sia in esecuzion
    public void playSong() {
        Track selected = mainController.getTrackTableController().getSelectedTrack();
        if (selected == null) {
            mainController.getWindowManager().showWarning("Nessuna selezione",
                    "Seleziona una traccia dalla lista per riprodurla.");
            return;
        }

        if (mainController.getPlayerContext().isPlaying()
                && selected == mainController.getPlayerContext().getCurrentTrack() && !trackFinished) {
            mainController.getWindowManager().showInfo("Già in riproduzione", "Sto già eseguendo questo brano.");
            return;
        }

        // Modifica successiva: se il brano è in pausa e si preme Play
        // sulla stessa traccia, riprende dal punto in cui era stata messa in pausa
        // anziché ricominciare dall'inizio
        // ovviamente è un tipo di controllo che si può modificare in futuro
        if (mainController.getPlayerContext().isPaused()
                && selected == mainController.getPlayerContext().getCurrentTrack()) {
            mainController.getPlayerContext().setState(mainController.getPlayerContext().getPlayingState());
            mainController.getTimerManager().resume();
            if (selected.getAudioSource() != null) {
                selected.getAudioSource().resumePlayback();
            }
            updateNowPlaying();
            return;
        }

        trackFinished = false;
        sequentialMode = false;
        startTrackPlayback(selected);
    }

    // Questi tre metodi sono simili ma cambia solamente la strategy utilizzata per
    // riprodurre i brani, i controlli sono uguali in quanto devo conoscere il brano
    // corrente per poter determinare quale brano devo proprorre all'utente
    public void sequentialRip(ActionEvent event) {
        Track selected = mainController.getTrackTableController().getSelectedTrack();
        if (selected == null) {
            mainController.getWindowManager().showWarning("Nessuna selezione",
                    "Seleziona una traccia dalla lista per avviare la riproduzione sequenziale.");
            return;
        }

        //ho aggiunto il set esplicito della SequentialStrategy perché senza questo, se prima avevo
        //attivato shuffle o loop, la strategy rimaneva quella precedente anche premendo il tasto sequenziale
        mainController.getPlayerContext().getPlaybackContext().setStrategy(new SequentialStrategy());
        trackFinished = false;
        sequentialMode = true;
        startTrackPlayback(selected);
    }

    // questo metodo, poichè si occupa del loop ha gli stessi contorlli del metodo
    // precedente
    // alla fine delego al playbackcontext che setta la strategy a LoopStrategy che
    // direttamente lavora per selezionare
    // la prossima traccia da proporre all'utente(in questo caso la stessa siccome è
    // un loop)
    public void loopRip(ActionEvent event) {
        Track selected = mainController.getTrackTableController().getSelectedTrack();
        if (selected == null) {
            mainController.getWindowManager().showWarning("Nessuna selezione",
                    "Seleziona una traccia dalla lista per avviare il loop.");
            return;
        }

        mainController.getPlayerContext().getPlaybackContext().setStrategy(new LoopStrategy());
        trackFinished = false;
        sequentialMode = true;
        startTrackPlayback(selected);
    }

    //shuffle funziona come loop e sequential, imposta la strategy a ShuffleStrategy che sceglie
    //una traccia casuale diversa da quella corrente ogni volta che viene chiamata nextTrack
    public void shuffleRip(ActionEvent event) {
        Track selected = mainController.getTrackTableController().getSelectedTrack();
        if (selected == null) {
            mainController.getWindowManager().showWarning("Nessuna selezione",
                    "Seleziona una traccia dalla lista per avviare lo shuffle.");
            return;
        }

        mainController.getPlayerContext().getPlaybackContext().setStrategy(new ShuffleStrategy());
        trackFinished = false;
        sequentialMode = true;
        startTrackPlayback(selected);
    }

    // questo metodo ha il compito di settare le label dello slider per rendere
    // l'interfaccia utente dinamica, inoltre gli devo passare la canzone attuale
    // altrimenti non sa la durata e non sa il titolo e l'autore con cui modificare
    // le label grafiche
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

    //questo metodo funge da toggle per la pausa: se il player sta suonando mette in pausa,
    //se è già in pausa riprende dal punto esatto in cui era stato fermato
    //il timer JavaFX viene congelato così le label e lo slider non si resettano
    //anche l'audio viene fermato/ripreso preservando la posizione nel file
    public void pauseSong() {
        Track current = mainController.getPlayerContext().getCurrentTrack();
        if (current == null)
            return;

        if (mainController.getPlayerContext().isPlaying()) {
            //transizione allo stato PausedState, congelo timer e audio
            mainController.getPlayerContext().pause();
            mainController.getTimerManager().pause();
            if (current.getAudioSource() != null) {
                current.getAudioSource().pausePlayback();
            }
            lblNowPlaying.setText("⏸ " + current.getTitle() + " - " + current.getAuthor());
        } else if (mainController.getPlayerContext().isPaused()) {
            //torno a PlayingState e riprendo timer e audio da dove erano
            mainController.getPlayerContext().setState(mainController.getPlayerContext().getPlayingState());
            mainController.getTimerManager().resume();
            if (current.getAudioSource() != null) {
                current.getAudioSource().resumePlayback();
            }
            updateNowPlaying();
        }
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

    //handleNext chiede direttamente alla strategy corrente quale sia la traccia successiva,
    //senza passare per playerContext.next() che avrebbe avviato l'audio due volte (bug del double-play).
    //ho aggiunto sequentialMode = true così la traccia che parte continua ad auto-avanzare quando finisce.
    //ho aggiunto selectTrack così la selezione nella tabella segue la canzone in riproduzione,
    //in questo modo se premo loop o shuffle subito dopo, parte dalla traccia corretta e non da quella precedente
    public void handleNext(ActionEvent event) {
        mainController.getTimerManager().stop();
        sequentialMode = true;
        Track current = mainController.getPlayerContext().getCurrentTrack();
        Track next = mainController.getPlayerContext().getPlaybackContext()
                .nextTrack(mainController.getLibrary().getLibrary(), current);

        if (next != null) {
            mainController.getTrackTableController().selectTrack(next);
            startTrackPlayback(next);
        } else {
            if (current != null && current.getAudioSource() != null) {
                current.getAudioSource().stopPlayback();
            }
            mainController.getPlayerContext().setCurrentTrack(null);
            lblNowPlaying.setText("Canzone terminata");
            resetUI();
        }
    }

    //stesso ragionamento di handleNext ma per la traccia precedente
    public void handlePrev(ActionEvent event) {
        mainController.getTimerManager().stop();
        sequentialMode = true;
        Track current = mainController.getPlayerContext().getCurrentTrack();
        Track prev = mainController.getPlayerContext().getPlaybackContext()
                .previousTrack(mainController.getLibrary().getLibrary(), current);

        if (prev != null) {
            mainController.getTrackTableController().selectTrack(prev);
            startTrackPlayback(prev);
        }
    }

    public void handleTrackRemoval(int removedIdx) {
        if (!mainController.getLibrary().getLibrary().isEmpty()) {
            int nextIdx = Math.min(removedIdx, mainController.getLibrary().getLibrary().size() - 1);
            Track nextTrack = mainController.getLibrary().getLibrary().get(nextIdx);
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