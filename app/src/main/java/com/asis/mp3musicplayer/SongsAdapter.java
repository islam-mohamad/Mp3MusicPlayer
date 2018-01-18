package com.asis.mp3musicplayer;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by ESLAM on 1/17/2018.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.MyHolder> {
    ArrayList<Song> list;
    Context context ;

    public SongsAdapter(ArrayList<Song> list) {
        this.list = list;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_item, parent, false);
        context=parent.getContext();
        return new SongsAdapter.MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.textViewArtistName.setText(list.get(position).getArtist());
        holder.textViewSongTitle.setText(list.get(position).getTitle());
        holder.gif.setVisibility(list.get(position).isPlaying()?View.VISIBLE:View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        LinearLayout songLayout;
        TextView textViewSongTitle, textViewArtistName;
        GifImageView gif ;
        public MyHolder(View itemView) {
            super(itemView);
            songLayout = (LinearLayout) itemView.findViewById(R.id.songLayout);
            textViewSongTitle = (TextView) itemView.findViewById(R.id.textViewSongTitle);
            gif = (GifImageView) itemView.findViewById(R.id.gif);
            textViewArtistName = (TextView) itemView.findViewById(R.id.textViewArtistName);
        }
    }
}
