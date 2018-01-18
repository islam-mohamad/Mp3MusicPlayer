package com.asis.mp3musicplayer;

import android.graphics.Bitmap;
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
    private long totalTime;
    private long currentTime;
    private boolean isPlaying ;
    private boolean isLooping;
    private Bitmap albumImage;

    public Song(long id, String title, String artist, long totalTime, Bitmap albumImage/*, String path, String info*/) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.totalTime = totalTime;
        this.albumImage = albumImage;
//        this.path = path;
//        this.info = info;
    }

    public Bitmap getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(Bitmap albumImage) {
        this.albumImage = albumImage;
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

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
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
