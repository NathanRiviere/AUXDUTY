package com.example.auxduty;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.auxduty.Adapters.songAdapter;
import com.example.auxduty.firebaseHelpers.songInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;

@SuppressWarnings("Since15")
public class joinSession extends AppCompatActivity {

    private ArrayList<songInfo> arr;
    private Context mContext;
    private boolean fireSet, starSet, checkSet = false;
    private int fireballCount, starCount, checkCount, fireballPos;
    private TextView tvFire, tvStar, tvCheck;
    private songAdapter adapter;
    private boolean songFound;
    private ListView list;
    private ArrayList<Integer> starArray;
    private ArrayList<Integer> checkArray;
    private String ID;
    private boolean isHost;
    private ArrayList<songInfo> playlist;
    private int changeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_session);

        mContext = this;
        changeListener = 0;
        fireballPos = -1;
        fireballCount = 1;
        starCount = 3;
        checkCount = 6;
        arr = new ArrayList<songInfo>();
        tvFire = (TextView) findViewById(R.id.fireball_count);
        tvStar = (TextView) findViewById(R.id.star_count);
        tvCheck = (TextView) findViewById(R.id.check_count);
        starArray = new ArrayList<Integer>();
        checkArray = new ArrayList<Integer>();
        playlist = new ArrayList<>();
        Intent intent = getIntent();
        ID = intent.getStringExtra("SessionId");
        isHost = intent.getBooleanExtra("isHost", false);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Sessions/" + ID + "/Host songs");

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    songInfo song = data.getValue(songInfo.class);
                    Log.i("songs", song.songName);
                    arr.add(song);
                }
                list = (ListView) findViewById(R.id.listJoin);
                View.OnClickListener mFireListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int Pos = Integer.parseInt((String) v.getTag(R.id.fireball));
                        int topPos = Integer.parseInt((String) list.getChildAt(0).findViewById(R.id.fireball).getTag(R.id.fireball));
                        View mRow = list.getChildAt(Pos - topPos);
                        if(adapter.selected.containsKey(Pos) && adapter.selected.get(Pos).equals("Orange")) {
                            mRow.setBackgroundColor(Color.parseColor("#ffffff"));
                            adapter.selected.remove(Pos);
                            fireballCount++;
                            fireballPos = -1;
                            tvFire.setText("" + fireballCount);
                        } else if (fireballCount == 1) {
                            if(adapter.selected.containsKey(Pos)) {
                                Toast.makeText(getApplicationContext(), "Remove selection first.", Toast.LENGTH_LONG );
                            } else {
                                mRow.setBackgroundColor(Color.parseColor("#ffa500"));
                                adapter.selected.put(Pos, "Orange");
                                fireballCount--;
                                fireballPos = Pos;
                                tvFire.setText("" + fireballCount);
                            }
                        } else { Toast.makeText(getApplicationContext(), "Remove a Lit song first.", Toast.LENGTH_LONG ); }}};

                View.OnClickListener mStarListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int Pos = Integer.parseInt((String) v.getTag(R.id.star));
                        int topPos = Integer.parseInt((String) list.getChildAt(0).findViewById(R.id.star).getTag(R.id.star));
                        if(adapter.selected.containsKey(Pos) && adapter.selected.get(Pos).equals("Yellow")) {
                            View mView = list.getChildAt(Pos - topPos);
                            mView.setBackgroundColor(Color.parseColor("#ffffff"));
                            adapter.selected.remove(Pos);
                            starCount++;
                            tvStar.setText("" + starCount);
                            starArray.remove(starArray.indexOf(Pos));
                        } else if(!(adapter.selected.containsKey(Pos))) {
                            if(starCount > 0) {
                                View mView = list.getChildAt(Pos - topPos);
                                mView.setBackgroundColor(Color.parseColor("#ffff00"));
                                adapter.selected.put(Pos, "Yellow");
                                starCount--;
                                tvStar.setText("" + starCount);
                                starArray.add(Pos);
                            } else { Toast.makeText(getApplicationContext(), "Remove a favorite first.", Toast.LENGTH_LONG ); }
                        } else {
                            Toast.makeText(getApplicationContext(), "Remove selection first.", Toast.LENGTH_LONG );
                        } }};

                View.OnClickListener mCheckListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int Pos = Integer.parseInt((String) v.getTag(R.id.check));
                        int topPos = Integer.parseInt((String) list.getChildAt(0).findViewById(R.id.check).getTag(R.id.check));
                        if(adapter.selected.containsKey(Pos) && adapter.selected.get(Pos).equals("Green")) {
                            View mView = list.getChildAt(Pos - topPos);
                            mView.setBackgroundColor(Color.parseColor("#ffffff"));
                            adapter.selected.remove(Pos);
                            checkCount++;
                            tvCheck.setText("" + checkCount);
                            checkArray.remove(checkArray.indexOf(Pos));
                        } else if(!(adapter.selected.containsKey(Pos))) {
                            if(checkCount > 0) {
                                View mView = list.getChildAt(Pos - topPos);
                                mView.setBackgroundColor(Color.parseColor("#00ff00"));
                                adapter.selected.put(Pos, "Green");
                                checkCount--;
                                tvCheck.setText("" + checkCount);
                                Log.i("postion", "" + Pos);
                                checkArray.add(Pos);
                            } else { Toast.makeText(getApplicationContext(), "Remove a checked song first.", Toast.LENGTH_LONG ); }
                        } else {
                            Toast.makeText(getApplicationContext(), "Remove selection first.", Toast.LENGTH_LONG );
                        } }};

                adapter = new songAdapter(mContext, R.layout.song_display, arr, mFireListener, mStarListener, mCheckListener);
                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}});
    }

    public void finishClicked(View view) {
        songInfo fireSong = null;
        ArrayList<songInfo> checkSongs = null;
        ArrayList<songInfo> starSongs = null;
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Sessions/" + ID + "/Chosen songs");
        if(checkCount != 6) {
            checkSongs = getSongs(checkArray, 10);
        }
        if(starCount != 3) {
            starSongs = getSongs(starArray, 20);
        }
        if(fireballCount != 1) {
            String songName = arr.get(fireballPos).songName;
            String artistName = arr.get(fireballPos).artist;
            for (songInfo s : arr) {
                if (s.songName.equals(songName) && s.artist.equals(artistName)) {
                    fireSong = s;
                    fireSong.priority = 30;
                    break;
                }
            }
        }
        ArrayList<songInfo>sendToServer = new ArrayList<>();
        if(checkSongs != null) {
            sendToServer.addAll(checkSongs);
        }
        if(starSongs != null) {
            sendToServer.addAll(starSongs);
        }
        if(fireSong != null) {
            sendToServer.add(fireSong);
        }
        String key = db.push().getKey();
        db.child(key).setValue(sendToServer);
        if(isHost) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Press OK to end voting.").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // pull chosen songs from server, create playlist, post playlist to server
                            songFound = false;
                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Sessions/" + ID + "/Chosen songs");
                            db.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    for (DataSnapshot deck : snapshot.getChildren()) {
                                        for (DataSnapshot songSnap : deck.getChildren()) {
                                            songInfo song = songSnap.getValue(songInfo.class);
                                            for(songInfo s : playlist) {
                                                if(s.songName.equals(song.songName) && s.artist.equals(song.artist)) {
                                                    s.priority += song.priority + 15;
                                                    songFound = true;
                                                } else if(s.artist.equals(song.artist) && s.artist != "<unknown>") {
                                                    s.priority += 10;
                                                } else if(s.genre.equals(song.genre) && s.genre != "null") {
                                                    s.priority += 5;
                                                } else if(s.year == song.year && s.year != 0) {
                                                    s.priority += 5;
                                                }
                                            }
                                            if(songFound) {
                                                songFound = false;
                                            } else {
                                                playlist.add(song);
                                            }
                                        }
                                    }
                                    playlist.sort(new Comparator<songInfo>() {
                                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                        @Override
                                        public int compare(songInfo o1, songInfo o2) {
                                            return o2.compareTo(o1);
                                        }
                                    });
                                    ArrayList<songInfo> litPlaylist = new ArrayList<songInfo>();
                                    int length = playlist.size();
                                    for(int i = 0; i < length && i < 10 ; i++) {
                                        litPlaylist.add(playlist.get(i));
                                    }
                                    Intent i = new Intent(getApplicationContext(), playlist.class);
                                    int k = 0;
                                    for(songInfo s : litPlaylist) {
                                        i.putExtra("song name " + k, s.songName);
                                        i.putExtra("artist name " + k, s.artist);
                                        k++;
                                    }
                                    i.putExtra("length", k);
                                    DatabaseReference grab = FirebaseDatabase.getInstance().getReference("Sessions/" + ID + "/Playlist");
                                    grab.setValue(litPlaylist);
                                    grab.getParent().removeValue();
                                    startActivity(i);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }}).show();
        } else {
           DatabaseReference listen = db.getParent().child("Playlist");
            listen.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(changeListener == 0) {
                        changeListener++;
                    } else {
                        Intent in = new Intent(getApplicationContext(), passengarPlaylist.class);
                        in.putExtra("ID", ID);
                        startActivity(in);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Waiting for the host to end voting.");
        }
    }

    private ArrayList<songInfo> getSongs(ArrayList<Integer> intArray, int pri) {
        ArrayList<songInfo> temp = new ArrayList<songInfo>();
        for (Integer i : intArray) {
            arr.get(i).priority = pri;
            temp.add(arr.get(i));
        } /*
            String songName = adapter.objects.get(adapter.objects.indexOf(i)).songName;
            String artistName = adapter.objects.get(adapter.objects.indexOf(i)).artist;
            for (songInfo s : arr) {
                if (s.songName == songName && s.artist == artistName) {
                    s.priority = pri;
                    temp.add(s);
                    break;
                }
            }
        }             */
        return temp;
    }
}