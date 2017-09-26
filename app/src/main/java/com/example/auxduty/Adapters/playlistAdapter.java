package com.example.auxduty.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.auxduty.R;
import com.example.auxduty.firebaseHelpers.songInfo;
import com.example.auxduty.playlist;
import com.example.auxduty.positionCallback;

import java.util.ArrayList;
import java.util.List;

public class playlistAdapter extends ArrayAdapter<songInfo> {

    public positionCallback _cb;
    private boolean inflate;
    public int playing;

    public playlistAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<songInfo> objects, positionCallback cb) {
        super(context, resource, objects);
        _cb = cb;
        inflate = false;
        playing = -1;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        songInfo song = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.playlist_display, parent, false);
        }
        TextView tvArtist = (TextView) convertView.findViewById(R.id.artistname);
        TextView tvSong = (TextView) convertView.findViewById(R.id.songname);
        tvArtist.setText(song.artist);
        if(position == playing) {
            convertView.setBackgroundColor(Color.parseColor("#ffa500"));
        }
        tvSong.setText(song.songName);
      //  _cb.call(position, convertView, inflate);
        inflate = false;
        return convertView;
    }

}
