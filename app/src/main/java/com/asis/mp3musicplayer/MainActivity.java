package com.asis.mp3musicplayer;

import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    SongsAdapter mAdapter;
    ArrayList<Song> list;
    private DrawerLayout drawer;
    private MusicService mMysicService;
    private int currentPosition = -1;
    boolean isBound ;
    private ImageButton btnRepeate,btnPlay,btnPrev, btnNext, btnRev, btnFwd;
    private TextView txtTotalDuration, txtCurrentTime;
    private SeekBar seekbar;
    private int seekbarProgress;
    private ImageView imageViewAlbum;
    private Typeface type;
    private ImageView ivSideBar;
    private TextView textViewArtist,textViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        imageViewAlbum = (ImageView) findViewById(R.id.imageViewAlbum);
        // get Song List
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                list = new ArrayList<>();
                getSongList();
                Collections.sort(list, new Comparator<Song>() {
                    @Override public int compare(Song a, Song b) {
                        return a.getTitle().compareTo(b.getTitle());
                    }
                });
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        initViews();
                        onRecyclerViewItemClick();
                        lastPlayedSong();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void getSongList()
    {
        ContentResolver musicResolver =getContentResolver();
        Uri musicUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor=musicResolver.query(musicUri, null, null, null, null);

        if(musicCursor!=null && musicCursor.moveToFirst())
        {
            int titleColumn =musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn=musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn=musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int durationColumn=musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            do {
                long thisId=musicCursor.getLong(idColumn);
                String thisTitle=musicCursor.getString(titleColumn);
                String thisArtist=musicCursor.getString(artistColumn);
                long thisDuration = musicCursor.getLong(durationColumn);

               list.add(new Song(thisId, thisTitle, thisArtist, thisDuration,musicCursor.getPosition()));
            }
            while(musicCursor.moveToNext());
            musicCursor.close();
        }
    }

    private void onRecyclerViewItemClick() {
        ivSideBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) throws FileNotFoundException {

                        currentPosition = position;
                        setPlayingSong();
                    }
                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbarProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mMysicService!=null) {
                    mMysicService.seekTo(seekbarProgress);
                }
            }
        });

        btnFwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMysicService.forward();
            }
        });

        btnRev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMysicService.backward();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPosition<list.size()-1){
                    ++currentPosition;
                }
                else {
                    currentPosition = 0;
                }
                setPlayingSong();
            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPosition!=0){
                    --currentPosition;
                }
                else {
                    currentPosition = list.size()-1;
                }
                setPlayingSong();
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMysicService!=null&&mMysicService.isPlaying()){
                    mMysicService.pause();
                    btnPlay.setImageResource(R.drawable.hplib_ic_play_download);
                }
                else {
                    if(isBound){
                        mMysicService.resume();
                        btnPlay.setImageResource(R.drawable.hplib_ic_pause);
                    }
                    else {
                        setPlayingSong();
                    }
                }
            }
        });
        btnRepeate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences;
                SharedPreferences.Editor edit;
                switch (getSharedPreferences("asis_mp3_player",MODE_PRIVATE).getString("repeate","")){
                    case "all":
                        sharedPreferences = getSharedPreferences("asis_mp3_player",MODE_PRIVATE);
                        edit = sharedPreferences.edit();
                        edit.putString("repeate","one");
                        edit.apply();
                        btnRepeate.setImageResource(R.drawable.ic_repeate_once);
                        break;
                    case "one":
                        sharedPreferences = getSharedPreferences("asis_mp3_player",MODE_PRIVATE);
                        edit = sharedPreferences.edit();
                        edit.putString("repeate","order");
                        edit.apply();
                        btnRepeate.setImageResource(R.drawable.ic_in_order);
                        break;
                    case "order":
                        sharedPreferences = getSharedPreferences("asis_mp3_player",MODE_PRIVATE);
                        edit = sharedPreferences.edit();
                        edit.putString("repeate","shuffle");
                        edit.apply();
                        btnRepeate.setImageResource(R.drawable.ic_shuffle);
                        break;
                    case "shuffle":
                        sharedPreferences = getSharedPreferences("asis_mp3_player",MODE_PRIVATE);
                        edit = sharedPreferences.edit();
                        edit.putString("repeate","all");
                        edit.apply();
                        btnRepeate.setImageResource(R.drawable.ic_repeate_all);
                        break;
                }

            }
        });
    }

    private void setPlayingSong() {

        updateUI();

        if(isBound){
            unbindService(connection);
            btnPlay.setImageResource(R.drawable.hplib_ic_play_download);
        }

        final Uri songUri = getSongUri(list.get(currentPosition).getId());
        list.get(currentPosition).setSongUri(songUri);

        Intent songIntent = new Intent(getApplicationContext(),MusicService.class);
        songIntent.setAction(list.get(currentPosition).isPlaying()?"pause":"play");
        songIntent.putExtra("uri",songUri.toString());
        songIntent.putExtra("songID",list.get(currentPosition).getId());
        Log.e(TAG,"songID: "+list.get(currentPosition).getId());
        bindService(songIntent,connection, Service.BIND_AUTO_CREATE);

        recyclerView.scrollToPosition(currentPosition);
    }

    private void updateUI() {
        Log.e(TAG,"in updateUI");
        MainActivity.this.runOnUiThread(new Runnable() { //to update in UI
            @Override
            public void run() {
                textViewTitle.setText(list.get(currentPosition).getTitle());
                textViewArtist.setText(list.get(currentPosition).getArtist());
                txtTotalDuration.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes( list.get(currentPosition).getTotalTime()),
                        TimeUnit.MILLISECONDS.toSeconds( list.get(currentPosition).getTotalTime()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes(list.get(currentPosition).getTotalTime()))));

                seekbar.setMax((int)list.get(currentPosition).getTotalTime());


                imageViewAlbum.setImageBitmap(null);
                imageViewAlbum.setBackgroundColor(Color.WHITE);
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (list.get(currentPosition).getAlbumImage() == null) {
//                            gif.setVisibility(View.GONE);
                            ContentResolver musicResolver = getContentResolver();
                            Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                            Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

                            if (musicCursor != null && musicCursor.moveToPosition(list.get(currentPosition).getCursorPosition())) {
                                int column_index = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                                String pathId = musicCursor.getString(column_index);
                                Log.d(TAG, "path id=" + pathId);
                                MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
                                metaRetriver.setDataSource(pathId);
                                Bitmap songImage = null;
                                try {
                                byte[] art = metaRetriver.getEmbeddedPicture();
                                art = metaRetriver.getEmbeddedPicture();
                                BitmapFactory.Options opt = new BitmapFactory.Options();
                                opt.inSampleSize = 2;
                                songImage = BitmapFactory.decodeByteArray(art, 0, art.length, opt);
                            } catch (Exception e) {
                                e.getStackTrace();
                                Log.e(TAG, e.toString());
                            }
                            list.get(currentPosition).setAlbumImage(songImage);
                            musicCursor.close();
                        }
                            if (list.get(currentPosition).getAlbumImage() != null) {
                                imageViewAlbum.setImageBitmap(list.get(currentPosition).getAlbumImage());
                            }
                            else {
                                imageViewAlbum.setImageResource(R.drawable.logo);
                            }
                        }
                        else {
                            imageViewAlbum.setImageBitmap(list.get(currentPosition).getAlbumImage());
                        }
                    }
                });
            }
        });
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void onSongClicked(SongClicked songClicked){
            //update side bar at first time

            int index = getSongIndexByID(songClicked.getSongID());
            list.get(index).setPlaying(songClicked.isPlaying());
            mAdapter.notifyDataSetChanged();

            SharedPreferences sharedPreferences = getSharedPreferences("asis_mp3_player", MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putLong("songID", songClicked.getSongID());
            edit.apply();
    }
    @Subscribe (threadMode = ThreadMode.MAIN)
    public void onSongCompleted(SongCompleted songCompleted){
        if(songCompleted.isCompleted()) {
            String state = getSharedPreferences("asis_mp3_player", MODE_PRIVATE).getString("repeate", "");
            switch (state) {
                case "all":
                    if (currentPosition < list.size() - 1) {
                        ++currentPosition;
                    } else {
                        currentPosition = 0;
                    }
                    setPlayingSong();
                    break;
                case "one":
                    setPlayingSong();
                    // do nothing
                    break;
                case "order":
                    if (currentPosition < list.size() - 1) {
                        ++currentPosition;
                        setPlayingSong();
                    }
                    break;
                case "shuffle":
                    int temp = currentPosition;
                    // to prevent repeating the current song
                    do{
                        currentPosition = (int) (Math.random() * list.size());
                    }
                    while (temp == currentPosition);
                    setPlayingSong();
                    break;
            }
        }
    }
    @Subscribe (threadMode = ThreadMode.MAIN)
    public void onCurrentTimeChanged(UpdateSeekbar updateSeekbar){
            seekbar.setProgress((int)updateSeekbar.getCurrentTime());
            txtCurrentTime.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(updateSeekbar.getCurrentTime()),
                    TimeUnit.MILLISECONDS.toSeconds(updateSeekbar.getCurrentTime()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes(updateSeekbar.getCurrentTime()))));

    }

    int getSongIndexByID (long songID){
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == songID) {
                return i;
            }
        }
        return -1;
    }
    void lastPlayedSong(){
        long songID = getSharedPreferences("asis_mp3_player",MODE_PRIVATE).getLong("songID",-1);
        if(songID == -1){
            currentPosition = 0;
        }
        else{
            currentPosition = getSongIndexByID(songID);
            //if song has been deleted from disk
            if (currentPosition == -1){
                currentPosition = 0;
            }
        }
        updateUI();
    }

    private Uri getSongUri(long songId)
    {
        return ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                songId);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MyMusicBinder musicServiceBinder = (MusicService.MyMusicBinder ) service;
            mMysicService = musicServiceBinder.getService();
            isBound = true;
            btnPlay.setImageResource(R.drawable.hplib_ic_pause);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMysicService =null;
            isBound = false;
        }
    };

    private void initViews() {
        if(getSharedPreferences("asis_mp3_player",MODE_PRIVATE).getString("repeate","").equals("")){
            //at first launch
            SharedPreferences sharedPreferences = getSharedPreferences("asis_mp3_player",MODE_PRIVATE);
            SharedPreferences.Editor edit  = sharedPreferences.edit();
            edit.putString("repeate","all");
            edit.apply();
            btnRepeate.setImageResource(R.drawable.ic_repeate_once);
        }

        type = Typeface.createFromAsset(getAssets(), "fonts/dinnextregular.ttf");

        recyclerView=(RecyclerView) findViewById(R.id.song_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new SongsAdapter(list);
        recyclerView.setAdapter(mAdapter);

        txtCurrentTime = (TextView) findViewById(R.id.txt_currentTime);
        txtCurrentTime.setTypeface(type);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        txtTotalDuration = (TextView) findViewById(R.id.txt_totalDuration);
        txtTotalDuration.setTypeface(type);
        btnRepeate = (ImageButton) findViewById(R.id.btn_repeate);
        btnPrev = (ImageButton) findViewById(R.id.btn_prev);
        btnRev = (ImageButton) findViewById(R.id.btn_rev);
        btnPlay = (ImageButton) findViewById(R.id.btn_play);
        btnFwd = (ImageButton) findViewById(R.id.btn_fwd);
        btnNext = (ImageButton) findViewById(R.id.btn_next);
        ivSideBar = (ImageView) findViewById(R.id.ivSideBar);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(type);
        textViewArtist = (TextView) findViewById(R.id.textViewArtist);
        textViewArtist.setTypeface(type);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isBound) {
            unbindService(connection);
        }
        EventBus.getDefault().unregister(this);
    }
}
