package com.example.auxduty;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.annotation.UiThread;
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
    private Context mContext;
    private ListView list;
    private String ID;
    private songAdapter adapter;

    /*          ARRAYS            */
    private ArrayList<songInfo> arr;
    private ArrayList<Integer> starArray;
    private ArrayList<Integer> checkArray;
    private ArrayList<Integer> fireballArray;
    private ArrayList<songInfo> playlist;
    /*********************************************/

    /*          VIEWS            */
    private TextView tvFire, tvStar, tvCheck;

    /*********************************************/


        /*          BOOLEANS            */
    private boolean fireSet, starSet, checkSet = false;
    private boolean passengerFinished;
    private boolean songFound;
    private boolean isHost;
    private boolean backPressed = false;
    private boolean passengerFlag = false;
    private boolean destroyed = false;
    /*********************************************/


        /*          INTEGERS            */
    private int changeListener;
    private int _fireballCount;
    private int _starCount;
    private int _checkCount;
    private int playlistSize;
    private int fireballCount, starCount, checkCount, fireballPos;
    /*********************************************/

    /*          RESOURCES TO BE FREED            */
    private ValueEventListener passengerListener;
    private DatabaseReference db;
    private ValueEventListener evl;
    private DatabaseReference listen;
    private ValueEventListener hostCompleteListener;
    private com.google.firebase.database.Query dbDone;

    /*********************************************/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use run activity to reload state
        runActivity();
    }

    private void runActivity() {
        setContentView(R.layout.activity_join_session);

        passengerFinished = false;
        mContext = this;
        changeListener = 0;
        playlistSize = getIntent().getIntExtra("dsa", 10);

        switch (playlistSize) {
            case 5:
                fireballCount = 1;
                starCount = 1;
                checkCount = 3;
                break;
            case 10:
                fireballCount = 1;
                starCount = 3;
                checkCount = 6;
                break;
            case 15:
                fireballCount = 2;
                starCount = 5;
                checkCount = 8;
                break;
            case 20:
                fireballCount = 2;
                starCount = 8;
                checkCount = 10;
                break;
        }

        fireballPos = -1;

        _fireballCount = fireballCount;
        _starCount = starCount;
        _checkCount = checkCount;

        arr = new ArrayList<songInfo>();
        tvFire = (TextView) findViewById(R.id.fireball_count);
        tvStar = (TextView) findViewById(R.id.star_count);
        tvCheck = (TextView) findViewById(R.id.check_count);
        tvFire.setText("" + fireballCount);
        tvStar.setText("" + starCount);
        tvCheck.setText("" + checkCount);

        starArray = new ArrayList<Integer>();
        checkArray = new ArrayList<Integer>();
        fireballArray = new ArrayList<Integer>();
        playlist = new ArrayList<>();

        final Intent intent = getIntent();
        ID = intent.getStringExtra("SessionId");
        isHost = intent.getBooleanExtra("isHost", false);
        db = FirebaseDatabase.getInstance().getReference("Sessions/" + ID + "/Host songs");
        dbDone = db.getParent().child("done");
        if (!isHost) {
            listen = db.getParent().child("Playlist");
            evl = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (changeListener == 0) {
                        changeListener++;
                    } else {
                        if (passengerFinished) {
                            Intent in = new Intent(getApplicationContext(), passengarPlaylist.class);
                            in.putExtra("ID", ID);
                            changeListener = 0;
                            listen.removeEventListener(this);
                            startActivity(in);
                        } else {
                            listen.removeEventListener(this);
                            Intent in = new Intent(getApplicationContext(), MainScreen.class);
                            startActivity(in);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            listen.addValueEventListener(evl);
        }
        if (!isHost) {
            db.getParent().child("song amount").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("tag", dataSnapshot.getKey());
                    Long pSize = (Long) dataSnapshot.getValue();
                    Log.i("tag", "pSize is " + pSize);
                    String s = "" + pSize;
                    Log.i("tag", "s is " + s);
                    Integer i = Integer.parseInt(s);
                    Log.i("tag", "i is " + i);
                    switch (i) {
                        case 5:
                            fireballCount = 1;
                            starCount = 1;
                            checkCount = 3;
                            break;
                        case 10:
                            fireballCount = 1;
                            starCount = 3;
                            checkCount = 6;
                            break;
                        case 15:
                            fireballCount = 2;
                            starCount = 5;
                            checkCount = 8;
                            break;
                        case 20:
                            fireballCount = 2;
                            starCount = 8;
                            checkCount = 10;
                            break;
                    }
                    _fireballCount = fireballCount;
                    _starCount = starCount;
                    _checkCount = checkCount;
                    tvFire.setText("" + fireballCount);
                    tvStar.setText("" + starCount);
                    tvCheck.setText("" + checkCount);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    songInfo song = data.getValue(songInfo.class);
                    Log.i("songs", song.songName);
                    arr.add(song);
                }
                // TODO
                list = (ListView) findViewById(R.id.listJoin);
                View.OnClickListener mFireListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int Pos = Integer.parseInt((String) v.getTag(R.id.fireball));
                        int topPos = Integer.parseInt((String) list.getChildAt(0).findViewById(R.id.fireball).getTag(R.id.fireball));
                        if (adapter.selected.containsKey(Pos) && adapter.selected.get(Pos).equals("Orange")) {
                            View mView = list.getChildAt(Pos - topPos);
                            mView.setBackgroundColor(Color.parseColor("#ffffff"));
                            adapter.selected.remove(Pos);
                            fireballCount++;
                            tvFire.setText("" + fireballCount);
                            fireballArray.remove(fireballArray.indexOf(Pos));
                        } else if (!(adapter.selected.containsKey(Pos))) {
                            if (fireballCount > 0) {
                                View mView = list.getChildAt(Pos - topPos);
                                mView.setBackgroundColor(Color.parseColor("#ffa500"));
                                adapter.selected.put(Pos, "Orange");
                                fireballCount--;
                                tvFire.setText("" + fireballCount);
                                fireballArray.add(Pos);
                            } else {
                                Toast.makeText(getApplicationContext(), "Remove a lit song first.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Remove selection first.", Toast.LENGTH_LONG).show();
                        }
                    }
                };

                View.OnClickListener mStarListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int Pos = Integer.parseInt((String) v.getTag(R.id.star));
                        int topPos = Integer.parseInt((String) list.getChildAt(0).findViewById(R.id.star).getTag(R.id.star));
                        if (adapter.selected.containsKey(Pos) && adapter.selected.get(Pos).equals("Yellow")) {
                            View mView = list.getChildAt(Pos - topPos);
                            mView.setBackgroundColor(Color.parseColor("#ffffff"));
                            adapter.selected.remove(Pos);
                            starCount++;
                            tvStar.setText("" + starCount);
                            starArray.remove(starArray.indexOf(Pos));
                        } else if (!(adapter.selected.containsKey(Pos))) {
                            if (starCount > 0) {
                                View mView = list.getChildAt(Pos - topPos);
                                mView.setBackgroundColor(Color.parseColor("#ffff00"));
                                adapter.selected.put(Pos, "Yellow");
                                starCount--;
                                tvStar.setText("" + starCount);
                                starArray.add(Pos);
                            } else {
                                Toast.makeText(getApplicationContext(), "Remove a favorite first.", Toast.LENGTH_LONG);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Remove selection first.", Toast.LENGTH_LONG);
                        }
                    }
                };

                View.OnClickListener mCheckListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int Pos = Integer.parseInt((String) v.getTag(R.id.check));
                        int topPos = Integer.parseInt((String) list.getChildAt(0).findViewById(R.id.check).getTag(R.id.check));
                        if (adapter.selected.containsKey(Pos) && adapter.selected.get(Pos).equals("Green")) {
                            View mView = list.getChildAt(Pos - topPos);
                            mView.setBackgroundColor(Color.parseColor("#ffffff"));
                            adapter.selected.remove(Pos);
                            checkCount++;
                            tvCheck.setText("" + checkCount);
                            checkArray.remove(checkArray.indexOf(Pos));
                        } else if (!(adapter.selected.containsKey(Pos))) {
                            if (checkCount > 0) {
                                View mView = list.getChildAt(Pos - topPos);
                                mView.setBackgroundColor(Color.parseColor("#00ff00"));
                                adapter.selected.put(Pos, "Green");
                                checkCount--;
                                tvCheck.setText("" + checkCount);
                                Log.i("postion", "" + Pos);
                                checkArray.add(Pos);
                            } else {
                                Toast.makeText(getApplicationContext(), "Remove a checked song first.", Toast.LENGTH_LONG);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Remove selection first.", Toast.LENGTH_LONG);
                        }
                    }
                };

                adapter = new songAdapter(mContext, R.layout.song_display, arr, mFireListener, mStarListener, mCheckListener);
                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        if (!isHost) {
            passengerListener = db.getParent().child("done").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(passengerFlag) {
                        Intent das = new Intent(getApplicationContext(), MainScreen.class);
                        startActivity(das);
                    } else {
                        passengerFlag = true;
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void finishClicked(View view) {
        songInfo fireSong = null;
        ArrayList<songInfo> checkSongs = null;
        ArrayList<songInfo> starSongs = null;
        ArrayList<songInfo> fireSongs = null;
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Sessions/" + ID + "/Chosen songs");
        if (checkCount != _checkCount) {
            checkSongs = getSongs(checkArray, 10);
        }
        if (starCount != _starCount) {
            starSongs = getSongs(starArray, 20);
        }
        if (fireballCount != _fireballCount) {
            fireSongs = getSongs(fireballArray, 30);

        }
        ArrayList<songInfo> sendToServer = new ArrayList<>();
        if (checkSongs != null) {
            sendToServer.addAll(checkSongs);
        }
        if (starSongs != null) {
            sendToServer.addAll(starSongs);
        }
        if (fireSongs != null) {
            sendToServer.addAll(fireSongs);
        }
        String key = db.push().getKey();
        db.child(key).setValue(sendToServer);
        if (isHost) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choices submitted.").setPositiveButton("End Voting", new DialogInterface.OnClickListener() {
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
                                    Log.i("pri", "Priority before algorithm for song: " + song.songName + " priority is: " + song.priority);
                                    for (songInfo s : playlist) {
                                        if (s.songName.equals(song.songName) && s.artist.equals(song.artist)) {
                                            s.priority += song.priority + 15;
                                            songFound = true;
                                            Log.i("pri", "Song match found for " + s.songName + " and " + song.songName);
                                        } else if (s.artist.equals(song.artist) && (!s.artist.equals("<unknown>"))) {
                                            s.priority += 10;
                                            song.priority += 10;
                                            Log.i("pri", "artist match found for " + s.songName + " and " + song.songName);
                                        } else if (s.genre.equals(song.genre) && (!s.genre.equals("null"))) {
                                            s.priority += 5;
                                            song.priority += 5;
                                            Log.i("pri", "genre match found for " + s.songName + " and " + song.songName);
                                        } else if (s.year == song.year && s.year != 0) {
                                            s.priority += 5;
                                            song.priority += 5;
                                            Log.i("pri", "year match found for " + s.songName + " and " + song.songName);
                                        }
                                    }
                                    if (songFound) {
                                        songFound = false;
                                    } else {
                                        playlist.add(song);
                                    }
                                }
                            }
                            mySort(playlist);
                            ArrayList<songInfo> litPlaylist = new ArrayList<songInfo>();
                            int length = playlist.size();
                            for (int i = 0; i < length && i < playlistSize; i++) {
                                litPlaylist.add(playlist.get(i));
                                Log.i("sort", "spot " + i + " with song name " + playlist.get(i).songName + " and priority " + playlist.get(i).priority);
                            }
                            Intent i = new Intent(getApplicationContext(), playlist.class);
                            int k = 0;
                            for (songInfo s : litPlaylist) {
                                i.putExtra("song name " + k, s.songName);
                                i.putExtra("artist name " + k, s.artist);
                                i.putExtra("pri " + k, s.priority);
                                k++;
                            }
                            i.putExtra("length", k);
                            DatabaseReference grab = FirebaseDatabase.getInstance().getReference("Sessions/" + ID);
                            backPressed = true;
                            grab.removeValue();
                            startActivity(i);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }).show();
        } else {
            passengerFinished = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Your choices have been submitted.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onStop();
                }
            });
            builder.show();
        }
    }

    private void mySort(ArrayList<songInfo> playlist) {
        for (int i = 1; i < playlist.size(); i++) {
            int j = i - 1;
            if (playlist.get(i).priority > playlist.get(j).priority) {
                while ((j >= 0) && (playlist.get(j + 1).priority > playlist.get(j).priority)) {
                    songInfo temp = playlist.get(j);
                    playlist.set(j, playlist.get(j + 1));
                    playlist.set(j + 1, temp);
                    j--;
                }
            }
        }
    }

    private ArrayList<songInfo> getSongs(ArrayList<Integer> intArray, int pri) {
        ArrayList<songInfo> temp = new ArrayList<songInfo>();
        for (Integer i : intArray) {
            arr.get(i).priority = pri;
            temp.add(arr.get(i));
        }
        return temp;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isHost) {
            db.getParent().child("done").setValue("true");
            db.getParent().removeValue();
            finish();
        } else {
            dbDone.removeEventListener(passengerListener);
            listen.removeEventListener(evl);
            finish();
        }
    }

    public void back_to_main(View view) {
        onStop();
    }
}