package ca.riverboatmedia.auxduty.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import ca.riverboatmedia.auxduty.R;
import ca.riverboatmedia.auxduty.firebaseHelpers.songInfo;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class songAdapter extends ArrayAdapter<songInfo> {
    private int fireballCount, starCount, checkCount;
    private View.OnClickListener mFire, mStar, mCheck;
    public ArrayList<songInfo> objects;
    public HashMap<Integer, String> selected;
    public songAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<songInfo> objects, View.OnClickListener mFire,
                       View.OnClickListener mStar,View.OnClickListener mCheck) {
                   //    TextView fire, TextView star, TextView check) {
        super(context, resource, objects);
        this.mFire = mFire;
        this.mStar = mStar;
        this.mCheck = mCheck;
        this.objects = objects;
        selected = new HashMap<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        songInfo song = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_display, parent, false);
        }
        // Lookup view for data population
        TextView tvArtist = (TextView) convertView.findViewById(R.id.artist_id);
        TextView tvSong = (TextView) convertView.findViewById(R.id.song_id);
        // Populate the data into the template view using the data object
        tvArtist.setText(song.artist);
        tvSong.setText(song.songName);
        ImageButton fireballButton = (ImageButton) convertView.findViewById(R.id.fireball);
        ImageButton starButton = (ImageButton) convertView.findViewById(R.id.star);
        ImageButton checkButton = (ImageButton) convertView.findViewById(R.id.check);
        if(selected.containsKey(position)) {
            String type = selected.get(position);
            switch(type) {
                case "Orange":convertView.setBackgroundColor(Color.parseColor("#ffa500"));
                              break;
                case "Yellow":convertView.setBackgroundColor(Color.parseColor("#ffff00"));
                              break;
                case "Green":convertView.setBackgroundColor(Color.parseColor("#00ff00"));
                              break;
            }
        } else {
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        fireballButton.setTag(R.id.fireball, "" + position);
        starButton.setTag(R.id.star, "" + position);
        checkButton.setTag(R.id.check, "" + position);
        fireballButton.setOnClickListener(mFire);
        starButton.setOnClickListener(mStar);
        checkButton.setOnClickListener(mCheck);
        // Return the completed view to render on screen
        return convertView;
    }
}
