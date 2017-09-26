package com.example.auxduty;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;
import com.example.auxduty.Adapters.playlistAdapter;
import com.example.auxduty.data.musicDataContract;
import com.example.auxduty.firebaseHelpers.songInfo;
import com.example.auxduty.positionCallback;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

public class playlist extends AppCompatActivity {
    ArrayList<MediaPlayer>player;
    ArrayList<songInfo>display;
    ListView lv;
    LinearLayout view;
    playlistAdapter adapter;
    int currIndex;
    int len;
    Integer songPostion;
    HashMap<Integer, Integer>positions;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        lv = (ListView) findViewById(R.id.playlist);
        Intent intent = getIntent();
        ArrayList<String> songs = new ArrayList<>();
        positions = new HashMap<>();
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
                            if(currIndex == len)  { Toast.makeText(getApplicationContext(), "End of playlist", Toast.LENGTH_LONG).show(); }
                            else {
                                player.get(currIndex).start();
                            /*      if(currIndex >= lv.getFirstVisiblePosition() && currIndex <= lv.getLastVisiblePosition()) {
                                    makeOrange(currIndex, currIndex - 1);
                                    adapter.playing = currIndex;
                                } else {
                                    adapter.playing = currIndex;
                                    lv.getChildAt(currIndex - 1).setBackgroundColor(Color.parseColor("#ffffff"));
                                }
                            //    lv.getChildAt(positions.get(currIndex)).setBackgroundColor(Color.parseColor("#ffa500")); */
                            }
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
        positionCallback cb = new positionCallback(this, lv, positions);
        adapter = new playlistAdapter(this, R.layout.playlist_display, display, cb);
      //  View footer = LayoutInflater.from(this).inflate(R.layout.buttons_display, lv, false);
        lv.setAdapter(adapter);
 /*       lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;
            private LinearLayout lBelow;


            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;


            }

            private void isScrollCompleted() {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE) {
                    if(currIndex >= lv.getFirstVisiblePosition() && currIndex <= lv.getLastVisiblePosition()) {
                        Log.i("sadsd", "" + lv.getFirstVisiblePosition());
                       // lv.getChildAt(currIndex - lv.getFirstVisiblePosition()).setBackgroundColor(Color.parseColor("ffa500"));
                    }


                }
            }
        });
      //  lv.addFooterView(footer);
      */
        currIndex = 0;
        positions.put(0, 0);
        player.get(currIndex).start();
    }

    public void nextClick(View view) {
        ++currIndex;
        if(currIndex < len) {
            player.get(currIndex - 1).pause();
            player.get(currIndex).seekTo(0);
            player.get(currIndex).start();
        /*  if(lv.getLastVisiblePosition() == (currIndex - 1)) {
                lv.scrollBy(0, lv.getChildAt(0).getHeight());
                lv.getChildAt(lv.getLastVisiblePosition()).setBackgroundColor(Color.parseColor("#ffa500"));
                lv.getChildAt(lv.getLastVisiblePosition() - 1).setBackgroundColor(Color.parseColor("#ffffff"));
                positions.put(currIndex, lv.getLastVisiblePosition());
            } else {
                lv.getChildAt(positions.get(currIndex)).setBackgroundColor(Color.parseColor("#ffa500"));
                lv.getChildAt(positions.get(currIndex) - 1).setBackgroundColor(Color.parseColor("#ffffff"));
            } */
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
        currIndex = currIndex - 1;
        if(currIndex > -1) {
            player.get(currIndex + 1).pause();
            player.get(currIndex).seekTo(0);
            player.get(currIndex).start();
        /*     if(currIndex >= lv.getFirstVisiblePosition() && currIndex <= lv.getLastVisiblePosition()) {
                Log.i("fuck", "sdadsa: " + currIndex);
                makeOrange(currIndex - lv.getFirstVisiblePosition(), currIndex + 1 - lv.getFirstVisiblePosition());
                adapter.playing = currIndex;
            } else {
                adapter.playing = currIndex;
                if(currIndex + 1 >= lv.getFirstVisiblePosition() && currIndex + 1 <= lv.getLastVisiblePosition()) {
                    lv.getChildAt(currIndex + 1).setBackgroundColor(Color.parseColor("#ffffff"));
                }
            }
           if(lv.getFirstVisiblePosition() == (currIndex + 1)) {
                lv.scrollBy(0, (-1) * lv.getChildAt(0).getHeight());
                lv.getChildAt(lv.getFirstVisiblePosition()).setBackgroundColor(Color.parseColor("#ffa500"));
                lv.getChildAt(lv.getFirstVisiblePosition() + 1).setBackgroundColor(Color.parseColor("#ffffff"));
                positions.put(currIndex, lv.getFirstVisiblePosition());
            } else {
                lv.getChildAt(positions.get(currIndex)).setBackgroundColor(Color.parseColor("#ffa500"));
                lv.getChildAt(positions.get(currIndex) + 1).setBackgroundColor(Color.parseColor("#ffffff"));
            } */
        } else {
            ++currIndex;
        }
    }

    public void endSeshClicked(View view) {
        player.get(currIndex).pause();
        player.clear();
        display.clear();
        positions.clear();
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }

    private void makeOrange(int Opos, int Wpos) {
        lv.getChildAt(Opos).setBackgroundColor(Color.parseColor("#ffa500"));
        lv.getChildAt(Wpos).setBackgroundColor(Color.parseColor("#ffffff"));
    }
}