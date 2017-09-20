package com.example.auxduty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.auxduty.Adapters.playlistAdapter;
import com.example.auxduty.firebaseHelpers.songInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class passengarPlaylist extends AppCompatActivity {
    playlistAdapter adapter;
    String ID;
    ArrayList<songInfo> arr;
    ListView lv;
    LinearLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passengar_playlist);
      //  container = (LinearLayout) findViewById(R.id.passengar_container);
        lv = (ListView) findViewById(R.id.passPlaylist); // add container
        arr = new ArrayList<>();
        Intent intent = getIntent();
        ID = intent.getStringExtra("ID");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Sessions/" + ID + "/Playlist");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    songInfo song = data.getValue(songInfo.class);
                    arr.add(song);
                }
                adapter = new playlistAdapter(getApplicationContext(), R.layout.playlist_display, arr);
                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
