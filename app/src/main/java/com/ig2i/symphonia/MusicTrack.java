package com.ig2i.symphonia;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

//Classe pour stocker les chansons
public class MusicTrack implements Parcelable {

    private Integer id = null;

    private String title = "";

    private String artist = "";

    private String album = "";

    private Date date = null;

    private String spotify_uri = "";

    private String deezer_uri = "";

    private String youtube_uri = "";

    private String lyrics = "";

    private Bitmap cover = null;

    private String coverUri = "";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if(id != null)  {
            dest.writeInt(id);
        } else {
            dest.writeInt(-1);
        }
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeLong(date == null ? 0 : date.getTime());
        dest.writeString(spotify_uri);
        dest.writeString(deezer_uri);
        dest.writeString(youtube_uri);
        dest.writeString(lyrics);
        dest.writeParcelable(cover, 0);
        dest.writeString(coverUri);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSpotify_uri() {
        return spotify_uri;
    }

    public void setSpotify_uri(String spotify_uri) {
        this.spotify_uri = spotify_uri;
    }

    public String getDeezer_uri() {
        return deezer_uri;
    }

    public void setDeezer_uri(String deezer_uri) {
        this.deezer_uri = deezer_uri;
    }

    public String getYoutube_uri() {
        return youtube_uri;
    }

    public void setYoutube_uri(String youtube_uri) {
        this.youtube_uri = youtube_uri;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public String getCoverUri() {
        return coverUri;
    }

    public void setCoverUri(String coverUri) {
        this.coverUri = coverUri;
    }

    public static final Creator<MusicTrack> CREATOR = new Creator<MusicTrack>() {
        @Override
        public MusicTrack createFromParcel(Parcel source) {
            return new MusicTrack(source);
        }

        @Override
        public MusicTrack[] newArray(int size) {
            return new MusicTrack[size];
        }
    };

    public MusicTrack() {

    }

    //Utilisé pour transmettre l'objet entre les intent
    public MusicTrack(Parcel parcel) {

        id = parcel.readInt();

        title = parcel.readString();

        artist = parcel.readString();

        album  = parcel.readString();

        date = new Date(parcel.readLong());

        spotify_uri = parcel.readString();

        deezer_uri = parcel.readString();

        youtube_uri = parcel.readString();

        lyrics = parcel.readString();

        cover  = parcel.readParcelable(Bitmap.class.getClassLoader());

        coverUri = parcel.readString();
    }

    //Utilisé pour la sauvegarde en BDD
    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();

        cv.put("_ID", id);

        cv.put(MusicTrackReaderContract.MusicTrackEntry.COL_TITLE, title);
        cv.put(MusicTrackReaderContract.MusicTrackEntry.COL_ARTIST, artist);
        cv.put(MusicTrackReaderContract.MusicTrackEntry.COL_ALBUM, album);
        cv.put(MusicTrackReaderContract.MusicTrackEntry.COL_DATE, date.getTime());
        cv.put(MusicTrackReaderContract.MusicTrackEntry.COL_SPOTIFY_URL, spotify_uri);
        cv.put(MusicTrackReaderContract.MusicTrackEntry.COL_DEEZER_URL, deezer_uri);
        cv.put(MusicTrackReaderContract.MusicTrackEntry.COL_YOUTUBE_URL, youtube_uri);
        cv.put(MusicTrackReaderContract.MusicTrackEntry.COL_LYRICS, lyrics);
        cv.put(MusicTrackReaderContract.MusicTrackEntry.COL_COVER, ImageUtils.BitmapToByteArray(cover));
        cv.put(MusicTrackReaderContract.MusicTrackEntry.COL_COVER_URI, coverUri);

        return cv;
    }
}
