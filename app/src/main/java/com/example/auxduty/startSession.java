package com.example.auxduty;

import android.Manifest;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import com.example.auxduty.data.musicDataContract.MusicEntry;

import com.example.auxduty.data.musicDBHelper;
import com.example.auxduty.data.musicDataContract;

public class startSession extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "startSession";
    private musicDBHelper mDbHelper;
    private static final int MUSIC_LOADER = 0;

    myCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Starting Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_session);
        ListView list = (ListView) findViewById(R.id.list);
        mCursorAdapter = new myCursorAdapter(this, null);
        list.setAdapter(mCursorAdapter);
        Log.i(TAG, "NULL cursor set");
        ActivityCompat.requestPermissions(startSession.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        Log.i(TAG, "Finish onCreate");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BaseColumns._ID,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.MediaColumns.DISPLAY_NAME
        };
        Log.i(TAG, "Projection set up for CursorLoader");
        return new CursorLoader(this,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
        Log.i(TAG, "Load Finished, Cursor Swapped");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted and now can proceed
                    getLoaderManager().initLoader(MUSIC_LOADER, null, this);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(startSession.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                // add other cases for more permissions
            }
        }
    }
}