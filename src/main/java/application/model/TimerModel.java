package application.model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import javafx.concurrent.Task;
import javafx.util.Duration;

public class TimerModel {
    private final IntegerProperty minutes;
    private final IntegerProperty seconds;

    private Timeline timeline;
    private boolean isRunning;

    public TimerModel() {
        this.minutes = new SimpleIntegerProperty(25); // Default to 25 minutes
        this.seconds = new SimpleIntegerProperty(0);
        this.isRunning = false;
    }

    private Runnable onTimerFinished;

    public void setOnTimerFinished(Runnable onTimerFinished) {
        this.onTimerFinished = onTimerFinished;
    }

    public IntegerProperty minutesProperty() {
        return minutes;
    }

    public IntegerProperty secondsProperty() {
        return seconds;
    }

    public void startTimer() {
        if (isRunning) return;

        isRunning = true;
        if (timeline == null) {
            timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
            timeline.setCycleCount(Timeline.INDEFINITE);
        }
        timeline.play();
    }


    public void pauseTimer() {
        if (timeline != null) {
            timeline.pause();
        }
        isRunning = false;
    }

    public void setTimer(int minutes, int seconds) {
        this.minutes.set(minutes);
        this.seconds.set(seconds);
    }

    public String getFormattedTime() {
        return String.format("%02d:%02d", minutes.get(), seconds.get());
    }

    private void tick() {
        int sec = seconds.get();
        int min = minutes.get();

        if (sec == 0) {
            if (min == 0) {
                pauseTimer();
                if (onTimerFinished != null) {
                    onTimerFinished.run();
                }
                return;
            }
            else {
                minutes.set(min - 1);
                seconds.set(59);
            }
        }
        else {
            seconds.set(sec - 1);
        }
    }
}