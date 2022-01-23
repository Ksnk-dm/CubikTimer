package com.ukrkosenko.cubikrubicktime.empty;

public class Records {
    private String time;

    public Records() {
    }

    public Records(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return time;
    }
}
