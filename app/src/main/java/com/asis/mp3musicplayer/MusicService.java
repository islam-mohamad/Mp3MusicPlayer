package com.asis.mp3musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private static final String TAG = "MusicService";
    IBinder mBinder = new MyMusicBinder();
    private MediaPlayer mMediaPlayer;
    private int position;
    private long songID;
    private int totlaTime, currentTitme;
    private SongClicked clickedSong;
    private UpdateSeekbar updateTime ;
    private Handler mHandler;
    public MusicService() {}

    @Override
    public void onCreate() {
        mHandler = new Handler();
        initializeMediaPlayer();
    }

    void initializeMediaPlayer()
    {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        final Uri songUri = Uri.parse(intent.getStringExtra("uri"));
        songID =intent.getLongExtra("songID",0);
        play(songUri);
        Log.e(TAG, "in onBind, Inent : "+intent.toString());
        Log.e(TAG, "in onBind,SongID: "+songID);
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.e(TAG, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "in onUnbind");
        if(mMediaPlayer!=null) {
            mMediaPlayer.pause();
            clickedSong.setPlaying(false);
            EventBus.getDefault().post(clickedSong); // song has been stoped or paused
            Log.e(TAG, "in pausing ");
            mHandler.removeCallbacks(updateSeekBar);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        mMediaPlayer.release();
        mMediaPlayer=null;
        Log.e(TAG, "in onDestroy");
    }


    int i = 1;

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e(TAG, "in onPrepared");
        mMediaPlayer.start();
        clickedSong = new SongClicked(songID,true);
        EventBus.getDefault().post(clickedSong );
        updateTime = new UpdateSeekbar();
        mHandler.postDelayed(updateSeekBar,1000);
        totlaTime = mMediaPlayer.getDuration();
//        EventBus.getDefault().post(new SongClicked(songID,true));
    }

    private Runnable updateSeekBar = new Runnable()
    {
        public void run()
        {
//            if (mMediaPlayer.getCurrentPosition()<mMediaPlayer.getDuration()){
                Log.e(TAG,"count:"+(i++));
            updateTime.setCurrentTime(mMediaPlayer.getCurrentPosition());
                EventBus.getDefault().post(updateTime );
//            }
            mHandler.postDelayed(this, 1000);
        }
    };
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e(TAG, "in onCompletion");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "in onError");
        mMediaPlayer.reset();
        return true;
    }

    void play(Uri songUri){
        Log.e(TAG, "in play");
        try {
            Log.e(TAG, "in playing ");
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(getApplicationContext(),songUri);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void pause(){
        mMediaPlayer.pause();
    }
    void resume(){
        mMediaPlayer.start();
    }
    void seekTo(int position){
        mMediaPlayer.seekTo(position);
    }
    void forward(){
        currentTitme = mMediaPlayer.getCurrentPosition();
        if(currentTitme < totlaTime){
            mMediaPlayer.seekTo(currentTitme+10000);
        }
    }
    void backward(){
        currentTitme = mMediaPlayer.getCurrentPosition();
        if(currentTitme > 10000){
            mMediaPlayer.seekTo(currentTitme-10000);
        }
    }

    boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }

     class MyMusicBinder extends Binder{
        MusicService getService(){
            Log.e(TAG, "in getService ");
            return MusicService.this;
        }
    }
}
