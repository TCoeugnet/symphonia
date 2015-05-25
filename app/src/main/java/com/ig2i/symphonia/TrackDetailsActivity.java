package com.ig2i.symphonia;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.doreso.sdk.utils.DoresoMusicTrack;

import java.io.InputStream;
import java.net.URL;

import butterknife.ButterKnife;
import butterknife.InjectView;

//Affichage des informations détaillées sur une chanson
public class TrackDetailsActivity extends ActionBarActivity {

    @InjectView(R.id.songname)
    TextView songname = null;

    @InjectView(R.id.artistname)
    TextView artistname = null;

    @InjectView(R.id.albumname)
    TextView albumname = null;

    @InjectView(R.id.imageButton)
    ImageButton albumCover;

    @InjectView(R.id.lyrics)
    TextView lyrics = null;

    private MusicTrack track = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_details);

        ButterKnife.inject(this);

        Intent intent = getIntent();
        MusicTrack trk = (MusicTrack)intent.getParcelableExtra("track"); //Récupération de la chanson


        //On peut ensuite remplir les champs adéquats
        if(trk != null) {
            track = trk;
            songname.setText(track.getTitle());
            albumname.setText(track.getAlbum().length() > 20 ? track.getAlbum().substring(0, 20) + "..." : track.getAlbum());
            artistname.setText(track.getArtist());
            albumCover.setImageBitmap(track.getCover());
            lyrics.setText(track.getLyrics());
        }
        else
            songname.setText("Erreur lors de la récupération de la chanson");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openYoutube(View v) {
        try {
            //Rechercher sur Youtube
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=" + Uri.encode(track.getArtist() + " " + track.getTitle()))));
        } catch(Exception exc) {
            Toast.makeText(this, "Impossible de démarrer Youtube", Toast.LENGTH_LONG).show();
        }
    }

    public void openDeezer(View v) {
        try {
            //Rechercher sur Deezer
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("deezer://www.deezer.com/search/" + Uri.encode(track.getArtist() + " " + track.getTitle()))));
        }catch(Exception exc) {
            Toast.makeText(this, "Impossible de démarrer Deezer", Toast.LENGTH_LONG).show();
        }
    }

    public void openSpotify(View v) {
        try {
            //Rechercher sur Spotify
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
            intent.setComponent(new ComponentName(
                    "com.spotify.music",
                    "com.spotify.music.MainActivity"));
            intent.putExtra(SearchManager.QUERY, track.getArtist() + " " + track.getTitle());
            startActivity(intent);
        }catch(Exception exc) {
            Toast.makeText(this, "Impossible de démarrer Spotify", Toast.LENGTH_LONG).show();
        }
    }
}
