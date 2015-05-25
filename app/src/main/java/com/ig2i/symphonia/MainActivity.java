package com.ig2i.symphonia;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.ig2i.symphonia.MusicTrackReaderContract.MusicTrackReaderDbHelper;

import com.doreso.sdk.utils.DoresoMusicTrack;

import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.ig2i.symphonia.MusicTrackReaderContract.MusicTrackReaderDbHelper.*;


public class MainActivity extends Activity {

    @InjectView(R.id.imgtest)
    ImageView imgtest;

    //MulticolorMicroImageView imgtest;

    private MusicRecorder recorder = null;

    private boolean started = false;

    private boolean stopped = false;

    private Vibrator vibrator = null;

    public final static String TAG = "MainActivity";

    public void executeOnEndMusixMatch(MusicTrack trk) {

        if(trk != null ) {
            Toast.makeText(MainActivity.this, "Musique : " + trk.getTitle(), Toast.LENGTH_LONG).show();
        } else {
            Log.e(TAG + "/Track", "Unable to get trackdata");
        }
        vibrator.vibrate(new long[]{0, 300, 300, 300}, -1); //On fait vibrer le téléphone
        started = false;
        stopAnimation(); //On retire le fond du micro

        showTrackDetails(trk);
    }

    //Interface implémentant les actions effectuées pendant et après une identification (progress/success/failure/error)
    private MusicRecordListener matchListener = new MusicRecordListener() {

        //Titre reconnu !
        @Override
        public void onSuccess(DoresoMusicTrack doresoMusicTrack) {
            if(stopped) {
                stopped = false;
                return;
            }

            AsyncTask<String, Void, MusicTrack> tsk = new MusixMatchTask(new MusixMatch(Settings.get("MUSIXMATCH.APPSECRET")), MainActivity.this);

            String trackName = doresoMusicTrack.getName(), artistName = doresoMusicTrack.getArtist(), albumName = doresoMusicTrack.getAlbum();
            trackName = trackName.replaceAll("[(]\\s*[a-zA-Z]+\\s+[vV]ersion\\s*[)]", ""); //On retire (Album version), (Piano Version), (xxx version)
            trackName = trackName.replaceAll("[(]\\s*([^\\s]+\\s+)+[rR]emix\\s*[)]", ""); //On retire (xxx remix)

            artistName = artistName.replaceAll("[(]?\\s*[vV]arious\\s+[Aa]rtists?\\s*[)]?", ""); //On retire various artists


            tsk.execute(trackName, artistName, albumName); //Execution de l'AsyncTask

        }

        //Failure = aucune correspondance trouvée, Error = erreur lors de la reconnaissance
        @Override
        public void onFailure() {
            if(stopped) {
                stopped = false;
                return;
            }

            vibrator.vibrate(800); //Vibration
            Log.i(TAG  + "/TrackFail", "Unable to recognize");
            Toast.makeText(MainActivity.this, "Piste non reconnue", Toast.LENGTH_LONG).show();
            started = false;
            stopAnimation(); //On retire le fond du micro
        }

        //Error = erreur lors de la reconnaissance, Failure = aucune correspondance trouvée
        @Override
        public void onError(int errorcode, String msg) {
            Toast.makeText(MainActivity.this, "Error " + errorcode + " : " + msg, Toast.LENGTH_LONG).show();
            Log.e(TAG + "/Error", "Error[" + errorcode + "]=" + msg);
            started = false;
            stopAnimation();
        }

        @Override
        public void onProcessing(byte[] buffer, double volume) {
            //On peut ici faire quelque chose pendant la recherche
        }
    };

    //Accéder à l'historique en BDD
    //Cet accès doit se faire dans un autre thread car il peut prendre du temps
    private class FindHistoryTask extends  AsyncTask<Void, Void, List<MusicTrack>> {

        @Override
        protected List<MusicTrack> doInBackground(Void... params) {
            //1=1 pour récupérer tous les titres, COL_DATE DESC  pour les trier du plus récent au plus ancien
            return  MusicTrackDatabase.retrieve(MainActivity.this, "1 = 1", null, MusicTrackReaderContract.MusicTrackEntry.COL_DATE + " DESC");
        }

        @Override
        protected void onPostExecute(List<MusicTrack> tracks) {
            super.onPostExecute(tracks);
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            ArrayList<Parcelable> parcelableTracks = new ArrayList<Parcelable>(tracks);
            intent.putParcelableArrayListExtra("tracks", parcelableTracks); //On ajoute les titres en extra pour lancer l'activité d'historique

            startActivity(intent);

        }
    }

    //AsyncTask appelée après la reconnaissance pour préciser l'artiste et trouver l'album, les paroles, la cover....
    private class MusixMatchTask extends AsyncTask<String, Void, MusicTrack> {

        private MusixMatch musix = null;

        private Context ctx = null;

        public MusixMatchTask(MusixMatch mmusix, Context ctx) {
            this.musix = mmusix;
            this.ctx = ctx;
        }

        @Override
        protected MusicTrack doInBackground(String... params) {

            if(params == null) {
                return null;
            }

            String song = params[0], artist = params[1], album = params[2];
            MusicTrack trk = new MusicTrack();

            try {
                List<Track> tracks = musix.searchTracks("", artist, song, 0, 1, true); //Recherche par artiste + nom du titre

                if(tracks.size() > 0) {
                    //Ici, on fera plus confiance aux valeurs renvoyées par MusixMatch que par celles renvoyées par Doreso car elles s'avèrent souvent plus proches de la réalité
                    TrackData data = tracks.get(0).getTrack();

                    trk.setAlbum(data.getAlbumName());
                    trk.setArtist(data.getArtistName());
                    trk.setCoverUri(data.getCoverArt());
                    trk.setDate(new Date());
                    trk.setTitle(data.getTrackName());

                    //Les paroles se téléchargent séparément
                    trk.setLyrics(musix.getLyrics(data.getTrackId()).getLyricsBody());

                    //De même que la cover de l'album
                    trk.setCover(ImageUtils.UriToBitmap(trk.getCoverUri()));

                    trk.setId((int)(long)MusicTrackDatabase.insert(trk, MainActivity.this));
                }
            } catch (Exception exc)  {
                Log.e(TAG + "/MMError", exc.getMessage());
                trk = new MusicTrack();
            }

            return trk;
        }

        @Override
        protected void onPostExecute(MusicTrack track) {
            super.onPostExecute(track);

            MainActivity.this.executeOnEndMusixMatch(track);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this); // Inject all the widgets
        Settings.init(this); // Initialize properties object

        //Récupérationd des keys dans le fichier de paramètres
        MusicRecorder.APPKEY = Settings.get("DORESO.APPKEY");
        MusicRecorder.APPSECRET = Settings.get("DORESO.APPSECRET");

        recorder = new MusicRecorder(this);
        recorder.setMusicRecordListener(matchListener);

        //Initialisation du service de vibration
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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

    //Lancé lors d'un appui sur le micro
    public void startRecording(View v) {
        
        if(!started) {
            started = true;
            recorder.start(); //Démarrage de la reconnaissance
            Toast.makeText(MainActivity.this, "Identification en cours...", Toast.LENGTH_SHORT).show();

            startAnimation(); //Changement du fond du micro
        } else {
            recorder.stop(); //Arrêt de la reconnaissance
            stopAnimation();
            recorder = new MusicRecorder(this); //Réinitialisation de l'objet de reconnaissance pour éviter toute erreur
            recorder.setMusicRecordListener(matchListener);
            started = false;
            stopped = true;
            Toast.makeText(MainActivity.this, "Arrêté", Toast.LENGTH_SHORT).show();
        }
    }

    //Affichage des informations détaillées sur la chanson
    public void showTrackDetails(MusicTrack track) {
        Intent intent = new Intent(this, TrackDetailsActivity.class);
        intent.putExtra("track", track);
        startActivity(intent);
    }

    //Affichage du fond sur le micro
    //Nous souhaitions faire une animation mais avons finalement opté pour un fond coloré statique, sans changer le nom de la fonction
    //Ici nous faisons un masque de la forme du micro sur un fond multicolore, puis nous collons ce fond sous l'image originale du micro
    public void startAnimation() {
        Canvas cv = new Canvas();
        Bitmap mainImage = null;
        Bitmap maskImage = null;
        Bitmap result = null;
        Paint paint;

        //Chargement des images
        mainImage = BitmapFactory.decodeResource(getResources(), R.mipmap.colors); //fond coloré
        maskImage = BitmapFactory.decodeResource(getResources(), R.mipmap.micro_mask); //masque
        result = Bitmap.createBitmap(maskImage.getWidth(), maskImage.getHeight(), Bitmap.Config.ARGB_8888); //résultat


        cv.setBitmap(result);
        paint = new Paint();
        paint.setFilterBitmap(false);

        cv.drawBitmap(mainImage, 0, 0, paint); //On dessine le fond coloré
        //On applique le masque
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cv.drawBitmap(maskImage, 0, 0, paint);
        //On réinitialise le masque
        paint.setXfermode(null);

        //On affiche l'image sous l'image initiale du micro
        imgtest.setImageBitmap(result);
    }

    //Masquer le fond
    public void stopAnimation() {
        //On réinitialise simplement l'image sous le micro
        imgtest.setImageBitmap(null);
    }

    public void showHistory(View v) {
        //Voir FindHistoryTask (recherche des chansons en BDD puis lancement de l'intent)
       new FindHistoryTask().execute((Void)null);
    }
}
