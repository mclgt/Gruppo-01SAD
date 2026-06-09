package com.Controller.playback;

import java.util.function.Consumer;

import com.Model.Track;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class PlaybackTimerManager {
    private PauseTransition playbackTimer;
    private Timeline progressTimeline;
    private int elapsedSeconds;

    public void start(Track track, Consumer<Integer> onTick, Runnable onFinished){
        stop();
        elapsedSeconds = 0;
        int total = track.getDuration();

        progressTimeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), e -> {
            elapsedSeconds++;
            onTick.accept(elapsedSeconds);
        }));
        progressTimeline.setCycleCount(total);
        progressTimeline.play();

        playbackTimer = new PauseTransition(Duration.seconds(total));
        playbackTimer.setOnFinished(e -> onFinished.run());
        playbackTimer.play();
    }

    public void stop(){
        if (playbackTimer != null) {
            playbackTimer.stop();
        }
        if (progressTimeline != null) {
            progressTimeline.stop();
        }
    }

    //congela entrambi i timer JavaFX senza resettarli: lo slider e le label rimangono
    //esattamente al secondo in cui si è premuto pausa
    public void pause() {
        if (playbackTimer != null) {
            playbackTimer.pause();
        }
        if (progressTimeline != null) {
            progressTimeline.pause();
        }
    }

    //riprende i timer dal punto esatto in cui erano stati congelati con pause(),
    //il conteggio continua da dove era rimasto
    public void resume() {
        if (playbackTimer != null) {
            playbackTimer.play();
        }
        if (progressTimeline != null) {
            progressTimeline.play();
        }
    }

    public String getFormattedTime(int totalSeconds){
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}