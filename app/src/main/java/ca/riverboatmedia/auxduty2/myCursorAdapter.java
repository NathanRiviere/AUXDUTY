package ca.riverboatmedia.auxduty2;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class myCursorAdapter extends CursorAdapter {
    private static final String TAG = "myCursorAdapter";
    myCursorAdapter(Context context, Cursor c) {
        super(context, c ,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(ca.riverboatmedia.auxduty2.R.layout.song_display, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
            TextView tvSong = (TextView) view.findViewById(ca.riverboatmedia.auxduty2.R.id.song_id);
            TextView tvArtist = (TextView) view.findViewById(ca.riverboatmedia.auxduty2.R.id.artist_id);
            String song = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
            String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST));
            tvSong.setText(song);
            tvArtist.setText(artist);
    }
}
