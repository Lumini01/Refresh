package com.example.refresh;

public abstract class CountDownTimer1 extends Object {
    private long millisInFuture;
    private long countDownInterval;
    CountDownTimer1(long millisInFuture, long countDownInterval) {
        setMillisInFuture(millisInFuture);
        setCountDownInterval(countDownInterval);
    }

    public void setMillisInFuture(long millisInFuture) {
        this.millisInFuture = millisInFuture;
    }

    public void setCountDownInterval(long countDownInterval) {
        this.countDownInterval = countDownInterval;
    }

    public long getMillisInFuture() {
        return millisInFuture;
    }

    public long getCountDownInterval() {
        return countDownInterval;
    }
}
