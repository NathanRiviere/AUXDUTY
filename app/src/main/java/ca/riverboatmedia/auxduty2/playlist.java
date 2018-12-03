package ca.riverboatmedia.auxduty2;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import ca.riverboatmedia.auxduty2.Adapters.playlistAdapter;
import ca.riverboatmedia.auxduty2.firebaseHelpers.songInfo;

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
    TextView playingSong;

    /*            LISTENERS FOR FREEING               */

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ca.riverboatmedia.auxduty2.R.layout.activity_playlist);

        lv = (ListView) findViewById(ca.riverboatmedia.auxduty2.R.id.playlist);
        Intent intent = getIntent();
        ArrayList<songInfo> songs = new ArrayList<>();
        positions = new HashMap<>();
        player = new ArrayList<>();
        display = new ArrayList<>();
        len = intent.getIntExtra("length", 0);
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        songPostion = new Integer(0);
        playingSong = (TextView) findViewById(ca.riverboatmedia.auxduty2.R.id.playingSong);
        for (int i = 0; i < len; i++) {
            songInfo t = new songInfo(intent.getStringExtra("artist name " + i), intent.getStringExtra("song name " + i), intent.getIntExtra("pri " + i, 0));
            songs.add(t);
        }

        StringBuilder selection = new StringBuilder("");
        selection.append(MediaStore.MediaColumns.DISPLAY_NAME + "=?");

        for(int i = 0; i < len - 1; i++) {
           selection.append(" OR " + MediaStore.MediaColumns.DISPLAY_NAME + "=?");
        }

        String selectionArray[] = new String[len];
        for(int i = 0; i < len; i++) {
            selectionArray[i] = songs.get(i).songName;
        }

        Cursor cursor = this.getContentResolver().query(uri, null, selection.toString(), selectionArray, null, null);
        String song_name, artist_name, fullpath;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    song_name = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                    if (song_name.endsWith(".mp3") | song_name.endsWith(".MID") | song_name.endsWith(".MP3") | song_name.endsWith(".mid") |
                            song_name.endsWith(".M4A") | song_name.endsWith(".m4a") | song_name.endsWith(".AIF") | song_name.endsWith(".aif")) {
                        song_name = song_name.substring(0, song_name.length() - 4);
                    }
                    artist_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                    fullpath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    songInfo temp = new songInfo(artist_name, song_name);
                    Uri tempUri = Uri.parse(fullpath);
                    MediaPlayer tempMedia = MediaPlayer.create(this, tempUri);
                    tempMedia.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            nextClick(null);
                        }
                    });
                    player.add(tempMedia);
                    display.add(temp);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        currIndex = 0;
        if(len == 0) { return; }

        mySort(songs);

        adapter = new playlistAdapter(this, ca.riverboatmedia.auxduty2.R.layout.playlist_display, display, null);
        lv.setAdapter(adapter);

        playingSong.setText(display.get(currIndex).songName);
        player.get(currIndex).start();
    }

    public void nextClick(View view) {
        if(len == 0) { return; }
        int bot = lv.getLastVisiblePosition();
        int top = lv.getFirstVisiblePosition();
        ++currIndex;
        if(currIndex < len) {
            player.get(currIndex - 1).pause();
            player.get(currIndex).seekTo(0);
            player.get(currIndex).start();
            adapter.playing++;
            if((currIndex - 1 >= top) && (currIndex - 1 <= bot)) {
                lv.getChildAt(currIndex - 1 - top).setBackgroundColor(Color.parseColor("#ffffff"));
            }
            if(currIndex >= top && currIndex <= bot) {
                lv.getChildAt(currIndex - top).setBackgroundColor(Color.parseColor("#ffa500"));
            }
            playingSong.setText(display.get(currIndex).songName);
        } else {
            --currIndex;
        }
    }

    public void playPauseClick(View view) {
        if(len == 0) { return; }
        if(player.get(currIndex).isPlaying()) {
            player.get(currIndex).pause();
            songPostion = player.get(currIndex).getCurrentPosition();
        } else {
            player.get(currIndex).seekTo(songPostion);
            player.get(currIndex).start();
        }
    }

    public void prevClick(View view) {
        if(len == 0) { return; }
        int bot = lv.getLastVisiblePosition();
        int top = lv.getFirstVisiblePosition();
        currIndex = currIndex - 1;
        if(currIndex > -1) {
            player.get(currIndex + 1).pause();
            player.get(currIndex).seekTo(0);
            player.get(currIndex).start();
            adapter.playing--;
            if((currIndex + 1 >= top) && (currIndex + 1 <= bot)) {
                lv.getChildAt(currIndex + 1 - top).setBackgroundColor(Color.parseColor("#ffffff"));
            }
            if(currIndex >= top && currIndex <= bot) {
                lv.getChildAt(currIndex - top).setBackgroundColor(Color.parseColor("#ffa500"));
            }
            playingSong.setText(display.get(currIndex).songName);
        } else {
            ++currIndex;
        }
    }

    public void endSeshClicked(View view) {
        if (len == 0) {
            Intent intent = new Intent(this, MainScreen.class);
            startActivity(intent);
        } else {
            player.get(currIndex).pause();
            for(MediaPlayer m : player) {
                m.release();
            }
            player.clear();
            display.clear();
            positions.clear();
            this.adapter.clear();
            this.currIndex = 0;
            this.lv = null;
            this.view = null;
            this.len = 0;
            songPostion = null;
            Intent intent = new Intent(this, MainScreen.class);
            startActivity(intent);
        }
    }

    private void mySort(ArrayList<songInfo> d) {
        int _size = d.size();
        for(int i = 0; i < _size; i++) {
            for(int j = 0; j < _size; j++) {
                if(d.get(i).songName.substring(0, d.get(i).songName.length() - 4).equals(display.get(j).songName)) {
                    songInfo temp = display.get(i);
                    display.set(i, display.get(j));
                    display.set(j, temp);

                    MediaPlayer temp3 = player.get(i);
                    player.set(i, player.get(j));
                    player.set(j, temp3);
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        endSeshClicked(null);
    }
}