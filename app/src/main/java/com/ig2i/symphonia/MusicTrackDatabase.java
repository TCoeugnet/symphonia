package com.ig2i.symphonia;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ig2i.symphonia.MusicTrackReaderContract.MusicTrackReaderDbHelper;
import com.ig2i.symphonia.MusicTrackReaderContract.MusicTrackEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Accès à la BDD
public class MusicTrackDatabase {

    //Contexte, on va rechercher la BDD à chaque changement de contexte
    private static Context context;

    //objet BDD
    private static SQLiteDatabase database;

    //Liste des colonnes à aller chercher dans la BDD (toutes)
    private static String[] columns = new String[] {
            MusicTrackEntry._ID,
            MusicTrackEntry.COL_TITLE,
            MusicTrackEntry.COL_ARTIST,
            MusicTrackEntry.COL_ALBUM,
            MusicTrackEntry.COL_DATE,
            MusicTrackEntry.COL_SPOTIFY_URL,
            MusicTrackEntry.COL_DEEZER_URL,
            MusicTrackEntry.COL_YOUTUBE_URL,
            MusicTrackEntry.COL_LYRICS,
            MusicTrackEntry.COL_COVER,
            MusicTrackEntry.COL_COVER_URI,
    };

    //Réinitialisation de l'objet BDD si nécessaire
    //A executer avant chaque insert/retrieve
    private static void initDb(Context ctx) {
        //Si on change de contexte, ou si la BDD n'est pas initialisée
        if(context != ctx || database == null) {
            context = ctx;
            database = new MusicTrackReaderContract().new MusicTrackReaderDbHelper(ctx).getWritableDatabase();
        } else {
            //Do nothing here
        }
    }

    //Insere une chanson en BDD
    public static Long insert(MusicTrack track, Context ctx) {
        initDb(ctx);

        return database.insert(MusicTrackEntry.TABLE_NAME, null, track.getContentValues());
    }

    //Récupére une chanson en BDD
    public static List<MusicTrack> retrieve(Context ctx, String where, String[] values, String orderBy) {
        initDb(ctx);


        Cursor cursor = database.query(MusicTrackEntry.TABLE_NAME, columns, where, values, null, null, orderBy);
        List<MusicTrack> tracks = new ArrayList<MusicTrack>();
        byte[] coverdata = null;

        if(cursor.moveToFirst()) {
            do { //On boucle sur tous les enregistrements
                MusicTrack track = new MusicTrack();

                track.setId((int) cursor.getLong(cursor.getColumnIndex(columns[0]))); //ID
                track.setTitle(cursor.getString(cursor.getColumnIndex(columns[1]))); //TITLE
                track.setArtist(cursor.getString(cursor.getColumnIndex(columns[2]))); //ARTIST
                track.setAlbum(cursor.getString(cursor.getColumnIndex(columns[3]))); //ALBUM
                track.setDate(new Date(cursor.getLong(cursor.getColumnIndex(columns[4])))); //DATE
                track.setSpotify_uri(cursor.getString(cursor.getColumnIndex(columns[5]))); //SPOTIFY_URL
                track.setDeezer_uri(cursor.getString(cursor.getColumnIndex(columns[6]))); //DEEZER_URL
                track.setYoutube_uri(cursor.getString(cursor.getColumnIndex(columns[7]))); //YOUTUBE_URL
                track.setLyrics(cursor.getString(cursor.getColumnIndex(columns[8]))); //LYRICS
                coverdata = cursor.getBlob(cursor.getColumnIndex(columns[9]));

                if(coverdata != null && coverdata.length > 0) //Il est possible que la cover de l'album n'ait pas été trouvée
                    track.setCover(BitmapFactory.decodeByteArray(coverdata, 0, coverdata.length)); //COVER

                track.setCoverUri(cursor.getString(cursor.getColumnIndex(columns[10]))); //COVER_URI

                tracks.add(track);
            } while (cursor.moveToNext());
        }

        return tracks;
    }

}
