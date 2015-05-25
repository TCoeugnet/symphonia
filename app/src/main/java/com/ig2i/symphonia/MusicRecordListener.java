package com.ig2i.symphonia;

import com.doreso.sdk.utils.DoresoMusicTrack;

/**
 * Created by Thomas on 06/05/2015.
 */

public interface MusicRecordListener {

    //Chanson reconnue
    public void onSuccess(DoresoMusicTrack doresoMusicTrack);

    //Chanson non reconnue
    public void onFailure();

    //Erreur lors de la tentative de reconnaissance
    public void onError(int errorcode, String msg);

    //En cours de reconnaissance
    public void onProcessing(byte[] buffer, double volume);
}
