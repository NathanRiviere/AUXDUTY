package nriviere97.auxduty.data;

import android.provider.BaseColumns;

public class musicDataContract {
    private musicDataContract() {}

    public static final class MusicEntry implements BaseColumns {
        public final static String TABLE_NAME = "music";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_SONG_NAME = "song_name";

        public final static String COLUMN_ARTIST = "artist";

        public final static String COLUMN_GENRE = "genre";

        public final static String COLUMN_RELEASE_YEAR = "release_year";
    }

}
