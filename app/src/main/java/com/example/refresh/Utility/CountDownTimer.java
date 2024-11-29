package com.example.refresh.Utility;

/**
 * An abstract class representing a countdown timer.
 * This class allows for setting a future time and countdown interval.
 * Subclasses must implement the logic for counting down the time.
 */
public abstract class CountDownTimer extends Object {
    private long millisInFuture;
    private long countDownInterval;

    /**
     * Constructor to initialize the countdown timer with a future time and countdown interval.
     * @param millisInFuture The time in milliseconds until the timer finishes.
     * @param countDownInterval The interval in milliseconds for the countdown updates.
     */
    CountDownTimer(long millisInFuture, long countDownInterval) {
        setMillisInFuture(millisInFuture);
        setCountDownInterval(countDownInterval);
    }

    /**
     * Sets the time in milliseconds until the timer finishes.
     * @param millisInFuture The time to set.
     */
    public void setMillisInFuture(long millisInFuture) {
        this.millisInFuture = millisInFuture;
    }

    /**
     * Sets the interval in milliseconds between countdown updates.
     * @param countDownInterval The interval to set.
     */
    public void setCountDownInterval(long countDownInterval) {
        this.countDownInterval = countDownInterval;
    }

    /**
     * Gets the time in milliseconds until the timer finishes.
     * @return The time in milliseconds.
     */
    public long getMillisInFuture() {
        return millisInFuture;
    }

    /**
     * Gets the interval in milliseconds between countdown updates.
     * @return The countdown interval in milliseconds.
     */
    public long getCountDownInterval() {
        return countDownInterval;
    }
}
