package com.asis.mp3musicplayer;

/**
 * Created by ESLAM on 1/18/2018.
 */

public class SongClicked {
    private long songID;
    private boolean isPlaying;

    public SongClicked(long songID, boolean isPlaying) {
        this.songID = songID;
        this.isPlaying = isPlaying;
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
}
