package com.asis.mp3musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private static final String TAG = "MusicService";
    IBinder mBinder = new MyMusicBinder();
    private MediaPlayer mMediaPlayer;
    private  int position;
    public MusicService() {}

    @Override
    public void onCreate() {
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
        Log.e(TAG, "in onBind");
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
        return true;
    }

    @Override
    public void onDestroy() {
        mMediaPlayer.release();
        mMediaPlayer=null;
        Log.e(TAG, "in onDestroy");
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e(TAG, "in onPrepared");
        mMediaPlayer.start();
//        EventBus.getDefault().post();
        MainActivity.list.get(position).setPlaying(true);
    }


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

    void play(int position){
        this.position = position;
        Log.e(TAG, "in play");
        Log.e(TAG, "position is: "+position+" is Playing? "+MainActivity.list.get(position).isPlaying());
        if(mMediaPlayer.isPlaying()&&MainActivity.list.get(position).isPlaying()){
            mMediaPlayer.pause();
            MainActivity.list.get(position).setPlaying(false);
            Log.e(TAG, "in pausing ");
        }
        else {
            try {
                Log.e(TAG, "in playing ");
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(getApplicationContext(), MainActivity.list.get(position).getSongUri());
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

     class MyMusicBinder extends Binder{
        MusicService getService(){
            Log.e(TAG, "in getService ");
            return MusicService.this;
        }
    }
}
