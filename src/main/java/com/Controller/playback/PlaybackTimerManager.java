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

    public String getFormattedTime(int totalSeconds){
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}