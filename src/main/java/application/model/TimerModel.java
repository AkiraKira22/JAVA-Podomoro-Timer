package application.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import javafx.concurrent.Task;

public class TimerModel {
    private final IntegerProperty minutes;
    private final IntegerProperty seconds;
    private Task<Void> timerTask;
    private boolean isRunning;

    public TimerModel() {
        this.minutes = new SimpleIntegerProperty(1); // Default to 25 minutes
        this.seconds = new SimpleIntegerProperty(5);
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
        if (!isRunning) {
            isRunning = true;
            timerTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (isRunning && (minutes.get() > 0 || seconds.get() > 0)) {
                        Thread.sleep(1000);
                        if (!isRunning) break;
                        if (seconds.get() == 0) {
                            if (minutes.get() > 0) {
                                minutes.set(minutes.get() - 1);
                                seconds.set(59);
                            }
                        } else {
                            seconds.set(seconds.get() - 1);
                        }
                    }
                    isRunning = false;
                    // Notify when timer finishes
                    if ((minutes.get() == 0 && seconds.get() == 0) && onTimerFinished != null) {
                        javafx.application.Platform.runLater(onTimerFinished);
                    }
                    return null;
                }
            };
            new Thread(timerTask).start();
        }
    }

    public void pauseTimer() {
        isRunning = false;
    }

    public void resetTimer() {
        isRunning = false;
        minutes.set(25); // Reset to default 25 minutes
        seconds.set(0);
    }

    public void setTimer(int minutes, int seconds) {
        this.minutes.set(minutes);
        this.seconds.set(seconds);
    }

    public String getFormattedTime() {
        return String.format("%02d:%02d", minutes.get(), seconds.get());
    }
}