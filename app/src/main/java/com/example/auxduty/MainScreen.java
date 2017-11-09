package com.example.auxduty;

import android.*;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import stanford.androidlib.SimpleActivity;
import com.example.auxduty.firebaseHelpers.firebaseSongSelection;

import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.auxduty.firebaseHelpers.firebaseSongSelection;
import com.example.auxduty.firebaseHelpers.songInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainScreen extends SimpleActivity {
    private String m_Text;
    private Context context = this;
    private ImageView fireball;
    private DatabaseReference database;
    private Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        ActivityCompat.requestPermissions(MainScreen.this,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }


    public void startSessionClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Session ID");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                createSession(m_Text);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void joinSessionClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Active Session ID");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                checkSession(m_Text);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void checkSession(final String m_text) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Sessions");
        database.child(m_text).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Intent intent = new Intent(context, joinSession.class);
                    intent.putExtra("SessionId", m_text);
                    intent.putExtra("isHost", false);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(context, "Session Not Found", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void createSession(String m_text) {
        database = FirebaseDatabase.getInstance().getReference();
        database.child("Sessions/" + m_text).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (!(snapshot.exists())) {
                        new firebaseSongSelection(context, database, m_Text).execute();
                        // ADD LOADING ANIMATION
                    } else {
                        Toast.makeText(context, "Session ID is already in use, please use another ID.", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted and now can proceed
                    return;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainScreen.this, "Permission denied to read your song storage", Toast.LENGTH_SHORT).show();
                }
                // add other cases for more permissions
            }
        }
    }
}