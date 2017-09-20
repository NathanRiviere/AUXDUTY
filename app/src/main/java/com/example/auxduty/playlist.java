package com.example.auxduty;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;

import com.example.auxduty.Adapters.playlistAdapter;
import com.example.auxduty.data.musicDataContract;
import com.example.auxduty.firebaseHelpers.songInfo;

import java.net.URI;
import java.util.ArrayList;

public class playlist extends AppCompatActivity {
    ArrayList<MediaPlayer>player;
    ArrayList<songInfo>display;
    ListView lv;
    LinearLayout view;
    playlistAdapter adapter;
    int currIndex;
    int len;
    Integer songPostion;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        lv = (ListView) findViewById(R.id.playlist);
        Intent intent = getIntent();
        ArrayList<String> songs = new ArrayList<>();
        player = new ArrayList<>();
        display = new ArrayList<>();
        len = intent.getIntExtra("length", 0);
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        songPostion = new Integer(0);

        for (int i = 0; i < len; i++) {
            songs.add(intent.getStringExtra("song name " + i));
        }

        String selection = "" + MediaStore.MediaColumns.DISPLAY_NAME + "=?";

        for(int i = 0; i < len - 1; i++) {
           selection += (" OR " + MediaStore.MediaColumns.DISPLAY_NAME + "=?");
        }

        String selectionArray[] = new String[len];
        for(int i = 0; i < len; i++) {
            selectionArray[i] = songs.get(i);
        }

        Cursor cursor = this.getContentResolver().query(uri, null, selection, selectionArray, null, null);
        String song_name, artist_name, fullpath;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    song_name = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                    artist_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                    fullpath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    songInfo temp = new songInfo(artist_name, song_name);
                    Uri tempUri = Uri.parse(fullpath);
                    MediaPlayer tempMedia = MediaPlayer.create(this, tempUri);
                    tempMedia.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            currIndex++;
                            if(currIndex == len)  { Toast.makeText(getApplicationContext(), "End of playlist", Toast.LENGTH_LONG); }
                            else { player.get(currIndex).start(); }
                        }
                    });
                    player.add(tempMedia);
                    display.add(temp);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

       /* for(int i = 0; i < 1; i++) {
            player.get(i).setNextMediaPlayer(player.get(i+1));
        }
        */

        adapter = new playlistAdapter(this, R.layout.playlist_display, display);
        View footer = LayoutInflater.from(this).inflate(R.layout.buttons_display, lv, false);
        lv.setAdapter(adapter);
        lv.addFooterView(footer);
        currIndex = 0;
        player.get(currIndex).start();
    }

    public void nextClick(View view) {
        ++currIndex;
        if(currIndex < len) {
            player.get(currIndex - 1).pause();
            player.get(currIndex).seekTo(0);
            player.get(currIndex).start();
        } else {
            --currIndex;
        }
    }

    public void playPauseClick(View view) {
        if(player.get(currIndex).isPlaying()) {
            player.get(currIndex).pause();
            songPostion = player.get(currIndex).getCurrentPosition();
        } else {
            player.get(currIndex).seekTo(songPostion);
            player.get(currIndex).start();
        }
    }

    public void prevClick(View view) {
        --currIndex;
        if(currIndex > -1) {
            player.get(currIndex + 1).pause();
            player.get(currIndex).seekTo(0);
            player.get(currIndex).start();
        } else {
            ++currIndex;
        }
    }
}
