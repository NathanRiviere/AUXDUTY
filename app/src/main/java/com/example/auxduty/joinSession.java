package com.example.auxduty;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.dynamite.*;
import com.example.auxduty.Adapters.songAdapter;
import com.example.auxduty.firebaseHelpers.songInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class joinSession extends AppCompatActivity {
    private ArrayList<songInfo>arr;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_session);
        mContext = this;
        arr = new ArrayList<songInfo>();
        Intent intent = getIntent();
        String ID = intent.getStringExtra("SessionId");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Session/test session");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    songInfo song = data.getValue(songInfo.class);
                    arr.add(song);
                }
                ListView list = (ListView) findViewById(R.id.listJoin);
                songAdapter adapter = new songAdapter(mContext, R.layout.song_display, arr);
                list.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
