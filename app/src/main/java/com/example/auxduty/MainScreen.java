package nriviere97.auxduty;

import android.*;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import stanford.androidlib.SimpleActivity;
import nriviere97.auxduty.firebaseHelpers.firebaseSongSelection;

import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import nriviere97.auxduty.firebaseHelpers.firebaseSongSelection;
import nriviere97.auxduty.firebaseHelpers.songInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainScreen extends SimpleActivity {
    private String m_Text;
    private String session_key;
    private int default_song_amount;
    private Context context = this;
    private ImageView fireball;
    private DatabaseReference database;
    private Thread thread;
    private SharedPreferences pref;
    AnimationDrawable fire_animation;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        ImageView fireImage = (ImageView) findViewById(R.id.fireImage);
        fireImage.setBackgroundResource(R.drawable.mainpage_animation);
        fire_animation = (AnimationDrawable) fireImage.getBackground();
        ActivityCompat.requestPermissions(MainScreen.this,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        pref = getPreferences(MODE_PRIVATE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        fire_animation.start();
    }

    public void startSessionClicked(View view) {
        if(pref.getString("sk", "null").equals("null")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Session Key");

            final EditText input = new EditText(this);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                    if(m_Text.equals(".")) {
                        Toast.makeText(context, ". Cannot be used as a key", Toast.LENGTH_LONG).show();
                        return;
                    }
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
        } else {
            createSession(pref.getString("sk", "null"));
        }
    }

    public void joinSessionClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Active Session Key");

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

    private void createSession(final String m_text) {
        database = FirebaseDatabase.getInstance().getReference();
        database.child("Sessions/" + m_text).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (!(snapshot.exists())) {
                        new firebaseSongSelection(context, database, m_text, session_key, pref.getInt("dsa", 10)).execute();
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

    public void settingsClicked(View view) {
        Intent settings_intent = new Intent(context, settingsView.class);
        settings_intent.putExtra("session_key", pref.getString("sk", "null"));
        settings_intent.putExtra("playlist_size", pref.getInt("dsa", 10));
        startActivityForResult(settings_intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        pref.edit().putString("sk", data.getStringExtra("retKey")).commit();
        pref.edit().putInt("dsa", data.getIntExtra("playlist_size", 10)).commit();

    }
}

