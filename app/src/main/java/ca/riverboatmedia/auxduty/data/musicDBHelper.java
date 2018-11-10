package ca.riverboatmedia.auxduty.data;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import ca.riverboatmedia.auxduty.data.musicDataContract.MusicEntry;

public class musicDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = musicDBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "auxduty.db";

    private static final int DATABASE_VERSION = 1;

    private Context context;

    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + MusicEntry.TABLE_NAME + " ("
            + MusicEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MusicEntry.COLUMN_SONG_NAME + " TEXT NOT NULL, "
            + MusicEntry.COLUMN_ARTIST + " TEXT, "
            + MusicEntry.COLUMN_GENRE + " TEXT, "
            + MusicEntry.COLUMN_RELEASE_YEAR + " INTEGER DEFAULT -1);";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + MusicEntry.TABLE_NAME;

    public musicDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
        getMp3Songs(context, db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    private void getMp3Songs(Context context, SQLiteDatabase db) {
        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String song_name, artist_name, fullpath, genre;
        int year;
        long newRowId;

        Cursor cursor = context.getContentResolver().query(allsongsuri, null, selection, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    song_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    artist_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    year = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR));
                    fullpath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    metaRetriver.setDataSource(fullpath);
                    genre = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);

                    ContentValues values = new ContentValues();
                    values.put(MusicEntry.COLUMN_SONG_NAME, song_name);
                    values.put(MusicEntry.COLUMN_ARTIST, artist_name);
                    values.put(MusicEntry.COLUMN_GENRE, genre);
                    values.put(MusicEntry.COLUMN_RELEASE_YEAR, year);

                    newRowId = db.insert(MusicEntry.TABLE_NAME, null, values);

                    if (newRowId == -1) {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }
}
