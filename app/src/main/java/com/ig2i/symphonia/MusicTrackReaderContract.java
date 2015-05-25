package com.ig2i.symphonia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

//Pattern pour gérer la BDD, voir : http://developer.android.com/training/basics/data-storage/databases.html
public final class MusicTrackReaderContract {

    public MusicTrackReaderContract() {}

    //Liste des colonnes en BDD
    public static abstract class MusicTrackEntry implements BaseColumns {
        public static final String TABLE_NAME = "musictrack";
        public static final String COL_TITLE = "title";
        public static final String COL_ARTIST = "artist";
        public static final String COL_ALBUM = "album";
        public static final String COL_DATE = "date";
        public static final String COL_SPOTIFY_URL = "spotify";
        public static final String COL_DEEZER_URL = "deezer";
        public static final String COL_YOUTUBE_URL = "youtube";
        public static final String COL_LYRICS = "lyrics";
        public static final String COL_COVER = "cover";
        public static final String COL_COVER_URI = "cover_uri";

    }

    //Creation de la table
    private static final String CREATE_TABLE = "CREATE TABLE " + MusicTrackEntry.TABLE_NAME + " (" +
                MusicTrackEntry._ID + " INTEGER PRIMARY KEY," +
                MusicTrackEntry.COL_TITLE + " TEXT," +
                MusicTrackEntry.COL_ARTIST + " TEXT," +
                MusicTrackEntry.COL_ALBUM + " TEXT," +
                MusicTrackEntry.COL_DATE + " TEXT," +
                MusicTrackEntry.COL_SPOTIFY_URL + " TEXT," +
                MusicTrackEntry.COL_DEEZER_URL + " TEXT," +
                MusicTrackEntry.COL_YOUTUBE_URL + " TEXT," +
                MusicTrackEntry.COL_LYRICS + " TEXT," +
                MusicTrackEntry.COL_COVER + " BLOB," +
                MusicTrackEntry.COL_COVER_URI + " TEXT" +
                " )";

    //Suppression de la table
    private static final String DELETE_TABLE =
                "DROP TABLE IF EXISTS " + MusicTrackEntry.TABLE_NAME;

    public class MusicTrackReaderDbHelper extends SQLiteOpenHelper {
        // Il faut augmenter le numero de version en cas de changement du schéma
        public static final int DATABASE_VERSION = 1;
        //Fichier de sauvegarde
        public static final String DATABASE_NAME = "FeedReader.db";

        public MusicTrackReaderDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(MusicTrackReaderContract.CREATE_TABLE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(DELETE_TABLE);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
