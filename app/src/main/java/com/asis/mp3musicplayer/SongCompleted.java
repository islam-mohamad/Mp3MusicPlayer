package com.asis.mp3musicplayer;

/**
 * Created by ESLAM on 1/21/2018.
 */

public class SongCompleted {
    private boolean isCompleted;

    public SongCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
