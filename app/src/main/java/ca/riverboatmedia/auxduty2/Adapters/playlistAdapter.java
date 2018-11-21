package ca.riverboatmedia.auxduty2.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ca.riverboatmedia.auxduty2.R;
import ca.riverboatmedia.auxduty2.firebaseHelpers.songInfo;
import ca.riverboatmedia.auxduty2.positionCallback;

import java.util.ArrayList;

public class playlistAdapter extends ArrayAdapter<songInfo> {

    public positionCallback _cb;
    private boolean inflate;
    public int playing;

    public playlistAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<songInfo> objects, positionCallback cb) {
        super(context, resource, objects);
        _cb = cb;
        inflate = false;
        playing = 0;
    }
    // comment
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        songInfo song = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.playlist_display, parent, false);
        }
        TextView tvArtist = (TextView) convertView.findViewById(R.id.artistname);
        TextView tvSong = (TextView) convertView.findViewById(R.id.songname);
        tvArtist.setText(song.artist);
        tvSong.setText(song.songName);
        if(position == playing) {
            convertView.setBackgroundColor(Color.parseColor("#ffa500"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        return convertView;
    }

}
