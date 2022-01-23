package com.ukrkosenko.cubikrubicktime;

import android.os.Handler;
import android.widget.TextView;

public class StopWatc {
    TextView textView;
    Handler handler = new Handler();
    boolean isRunning;// to keep track of the state of handler to avoid creating multiple threads.
    private int time = 0;

    public StopWatc(TextView textView) {// this textview would be updated by the stop-watch
        this.textView = textView;
    }

    public double getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    private String convertTimeToText(int time) {
        int min = time / 600;
        int secs = (time / 10) % 60;
        int mss = time % 10 * 6;
        String minute = String.format("%02d", min);
        String sec = String.format("%02d", secs);
        String ms = String.format("%02d", mss);


        String timeString = minute + "." + sec + "." + ms;
        return timeString;
    }

    public void start() {

        if (!isRunning) {
            time = 0;
            startTime();
        }
    }

    public void stop() {
        time = 0;
        textView.setText("00.00.00");
        handler.removeCallbacksAndMessages(null);
        isRunning = false;
    }

    public boolean getStatus() {
        return isRunning;
    }

    public void pause() {
        handler.removeCallbacksAndMessages(null);
        isRunning = false;
    }

    public void resume() {
        startTime();
        isRunning = true;
    }

    private void startTime() {
        if (!isRunning) {
            isRunning = true;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    time += 1;
                    textView.setText(convertTimeToText(time));
                    handler.postDelayed(this, 100);
                }
            });
        }
    }
}

