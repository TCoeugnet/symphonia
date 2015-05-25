package com.ig2i.symphonia;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

//Affichage de l'historique des chansons
public class HistoryActivity extends ActionBarActivity {

    @InjectView(R.id.listView)
    ListView matchLists;

    //Adapter, pour séparer vue et modèle
    class TrackAdapter extends ArrayAdapter<MusicTrack> {

        HashMap<MusicTrack, Integer> mIdMap = new HashMap<MusicTrack, Integer>();

        public TrackAdapter(Context context, int textViewResourceId,
                                  List<MusicTrack> objects) {
            super(context, textViewResourceId, objects);

            //Stockage des chansons dans une hashmap
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }


        }

        @Override
        public long getItemId(int position) {
            MusicTrack item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.history_item, null); //Le layout est dans un autre fichier, on doit l'inflate
            }

            MusicTrack track = getItem(position); //On récupère la bonne chanson

            //affichage des informations en BDD à l'écran
            if (track != null) {
                TextView trackTitle = (TextView) v.findViewById(R.id.textView4);
                TextView trackArtist = (TextView) v.findViewById(R.id.textView5);
                TextView trackAlbum = (TextView) v.findViewById(R.id.textView7);
                TextView matchedOn = (TextView) v.findViewById(R.id.textView6);
                ImageView album = (ImageView) v.findViewById(R.id.imageView2);

                if(track.getTitle() != null) {
                    trackTitle.setText(track.getTitle());
                }
                if(track.getAlbum() != null) {
                    trackAlbum.setText(track.getAlbum());
                }
                if(track.getArtist() != null) {
                    trackArtist.setText(track.getArtist());
                }
                if(track.getDate() != null) {
                    matchedOn.setText(DateOnlyFormat.format(track.getDate()));
                }
                if(track.getCover() != null) {
                    album.setImageBitmap(track.getCover());
                }
            }

            return v;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ButterKnife.inject(this);

        //Récupération de la liste des chansons passée depuis l'activity précédente
        List<MusicTrack> tracks = this.getIntent().getParcelableArrayListExtra("tracks");

        //Séparation vue/modèle
        matchLists.setAdapter(new TrackAdapter(this, R.id.textView4, tracks));

        matchLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //Lors du clic sur un item de la liste
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //On récupère la chanson
                MusicTrack track = (MusicTrack) parent.getAdapter().getItem(position);

                Intent intent = new Intent(HistoryActivity.this, TrackDetailsActivity.class);
                intent.putExtra("track", track); //On la passe dans l'activity suivant
                startActivity(intent); //On lance l'intent
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
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


}
