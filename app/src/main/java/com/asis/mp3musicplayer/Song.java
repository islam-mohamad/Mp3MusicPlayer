package com.asis.mp3musicplayer;

import android.net.Uri;

/**
 * Created by ESLAM on 1/17/2018.
 */

public class Song {
    private long id;
    private String title;
    private String artist;
    private Uri songUri;
//    private String info;
    private String totalTime;
    private String currentTime;
    private boolean isPlaying ;
    private boolean isLooping;

    public Song(long id, String title, String artist/*, String path, String info*/) {
        this.id = id;
        this.title = title;
        this.artist = artist;
//        this.path = path;
//        this.info = info;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Uri getSongUri() {
        return songUri;
    }

    public void setSongUri(Uri songUri) {
        this.songUri = songUri;
    }

    //    public String getPath() {
//        return path;
//    }
//
//    public void setPath(String path) {
//        this.path = path;
//    }
//
//    public String getInfo() {
//        return info;
//    }
//
//    public void setInfo(String info) {
//        this.info = info;
//    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public void setLooping(boolean isLooping) {
        this.isLooping = isLooping;
    }
}
