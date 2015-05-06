package com.ig2i.symphonia;

import com.doreso.sdk.utils.DoresoMusicTrack;

/**
 * Created by Thomas on 06/05/2015.
 */

public interface MusicRecordListener {



    public void onSuccess(DoresoMusicTrack[] doresoMusicTracks);

    public void onFailure(int errorcode, String msg);

    public void onProcessing(byte[] buffer, double volume);
}
