package com.example.auxduty.firebaseHelpers;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import com.example.auxduty.data.musicDataContract;
import com.example.auxduty.joinSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class firebaseSongSelection extends AsyncTask<Void, Void, ArrayList<songInfo>> {
    private Context context;
    private ArrayList<songInfo> arr;
    private String _id;
    private DatabaseReference db;

    public firebaseSongSelection(Context context, DatabaseReference db, String Id) {
        arr = new ArrayList<>();
        this.context = context;
        _id = Id;
        this.db = db;
    }

    @Override
    protected ArrayList<songInfo> doInBackground(Void... params) {
        String[] projection = {
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.AudioColumns.YEAR
        };
        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String song_name, artist_name, genre_name, fullpath;
        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
        int year;
        Cursor cursor = context.getContentResolver().query(allsongsuri, projection, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    song_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    artist_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    fullpath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    metaRetriver.setDataSource(fullpath);
                    genre_name = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
                    if(genre_name == null) { genre_name = "null"; }
                    year = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.YEAR));
                    songInfo s = new songInfo(artist_name, song_name, genre_name, year);
                    arr.add(s);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return arr;
    }

    @Override
    protected void onPostExecute(ArrayList<songInfo> result) {
        db.child("Sessions/" + _id + "/Host songs").setValue(result);
        Intent intent = new Intent(context, joinSession.class);
        intent.putExtra("SessionId", _id);
        intent.putExtra("isHost", true);
        context.startActivity(intent);
    }
}
