package com.asis.mp3musicplayer;

import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
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
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    static SongsAdapter mAdapter;
    static ArrayList<Song> list;
    private DrawerLayout drawer;
    private TextView textViewTitle;

    private MusicService mMysicService;
    static int currentPosition = -1;
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
        toggle.syncState();

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
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(getApplicationContext(),MusicService.class),connection, Service.BIND_AUTO_CREATE);
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
//            int artistColumn=musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
//            int artistColumn=musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            do {
                long thisId=musicCursor.getLong(idColumn);
                String thisTitle=musicCursor.getString(titleColumn);
                String thisArtist=musicCursor.getString(artistColumn);
               list.add(new Song(thisId, thisTitle, thisArtist));
            }
            while(musicCursor.moveToNext());
            musicCursor.close();
        }
    }

    private void onRecyclerViewItemClick() {
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) throws FileNotFoundException {
                        if (currentPosition == -1) {
                            //first Time
                            currentPosition = position;
                        }
                        else {
                            list.get(currentPosition).setPlaying(false);
                            currentPosition = position;
                        }
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                        textViewTitle.setText(list.get(position).getTitle());
                            }
                        });
                        final Uri songUri = getSongUri(list.get(position).getId());
                        list.get(position).setSongUri(songUri);
                        mMysicService.play(position);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    }
                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );
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
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMysicService =null;
        }
    };

    private void initViews() {
        recyclerView=(RecyclerView) findViewById(R.id.song_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new SongsAdapter(list);
        recyclerView.setAdapter(mAdapter);
        textViewTitle = (TextView) findViewById(R.id.title);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
