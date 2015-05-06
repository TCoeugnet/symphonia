package com.ig2i.symphonia;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.doreso.sdk.utils.DoresoMusicTrack;

import butterknife.ButterKnife;


public class MainActivity extends ActionBarActivity {

    private MusicRecorder recorder = null;

    private boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this); // Inject all the widgets
        Settings.init(this); // Initialize properties object

        MusicRecorder.APPKEY = Settings.get("DORESO.APPKEY");
        MusicRecorder.APPSECRET = Settings.get("DORESO.APPSECRET");
        recorder = new MusicRecorder(this);

        recorder.setMusicRecordListener(new MusicRecordListener() {
            @Override
            public void onSuccess(DoresoMusicTrack[] doresoMusicTracks) {
                Log.i("SUCCESS", doresoMusicTracks[0].getName());
                if(doresoMusicTracks.length == 1)
                    Toast.makeText(MainActivity.this, "Musique : " + doresoMusicTracks[0].getName(), Toast.LENGTH_LONG).show();
                started = false;
            }

            @Override
            public void onFailure(int errorcode, String msg) {
                Log.e("AA", "Error " + errorcode + " : " + msg);
                if(errorcode != 0 && errorcode != 4012)
                    Toast.makeText(MainActivity.this, "Error " + errorcode + " : " + msg, Toast.LENGTH_LONG).show();
                started = false;
            }

            @Override
            public void onProcessing(byte[] buffer, double volume) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void startRecording(View v) {
        if(!started) {
            started = true;
            recorder.start();
            Toast.makeText(MainActivity.this, "Identification en cours...", Toast.LENGTH_SHORT).show();
        } else {
            recorder.stop();
            recorder = new MusicRecorder(this);
            recorder.setMusicRecordListener(new MusicRecordListener() {
                @Override
                public void onSuccess(DoresoMusicTrack[] doresoMusicTracks) {
                    Log.i("SUCCESS", doresoMusicTracks[0].getName());
                    if(doresoMusicTracks.length == 1)
                        Toast.makeText(MainActivity.this, "Musique : " + doresoMusicTracks[0].getName(), Toast.LENGTH_LONG).show();
                    started = false;
                }

                @Override
                public void onFailure(int errorcode, String msg) {
                    Log.e("AA", "Error " + errorcode + " : " + msg);
                    if(errorcode != 0 && errorcode != 4012)
                        Toast.makeText(MainActivity.this, "Error " + errorcode + " : " + msg, Toast.LENGTH_LONG).show();
                    started = false;
                }

                @Override
                public void onProcessing(byte[] buffer, double volume) {

                }
            });
            started = false;
            Toast.makeText(MainActivity.this, "Arrêté", Toast.LENGTH_SHORT).show();
        }
    }
}
