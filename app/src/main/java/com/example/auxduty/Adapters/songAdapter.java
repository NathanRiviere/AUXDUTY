package com.example.auxduty.Adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.auxduty.R;
import com.example.auxduty.firebaseHelpers.songInfo;

import java.util.ArrayList;
import java.util.List;

public class songAdapter extends ArrayAdapter<songInfo> {

    public songAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<songInfo> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        songInfo song = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_display, parent, false);
        // Lookup view for data population
        TextView tvArtist = (TextView) convertView.findViewById(R.id.artist_id);
        TextView tvSong = (TextView) convertView.findViewById(R.id.song_id);
        // Populate the data into the template view using the data object
        tvArtist.setText(song.artist);
        tvSong.setText(song.songName);
        // Return the completed view to render on screen
        return convertView;
    }
}
