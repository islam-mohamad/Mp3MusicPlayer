package com.asis.mp3musicplayer;

/**
 * Created by Eslam on 20/01/2018.
 */

public class UpdateSeekbar {
    private long currentTime;

    public UpdateSeekbar() {
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }
}
