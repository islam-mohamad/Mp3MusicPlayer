package com.asis.mp3musicplayer;

/**
 * Created by ESLAM on 1/18/2018.
 */

public class SongClicked {
    private long songID;
    private boolean isPlaying;
    private long currentTime;

    public SongClicked(long songID, boolean isPlaying) {
        this.songID = songID;
        this.isPlaying = isPlaying;
    }

    public SongClicked(long songID, boolean isPlaying, long currentTime) {
        this.songID = songID;
        this.isPlaying = isPlaying;
        this.currentTime = currentTime;
    }

    public long getSongID() {
        return songID;
    }

    public void setSongID(long songID) {
        this.songID = songID;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }
}
