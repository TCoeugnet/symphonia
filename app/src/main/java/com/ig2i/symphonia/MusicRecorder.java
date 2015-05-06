package com.ig2i.symphonia;

import android.content.Context;

import com.doreso.sdk.DoresoConfig;
import com.doreso.sdk.DoresoListener;
import com.doreso.sdk.DoresoManager;
import com.doreso.sdk.utils.DoresoMusicTrack;
import com.doreso.sdk.utils.DoresoUtils;

/**
 * Created by Thomas on 06/05/2015.
 */
public class MusicRecorder implements DoresoRecordListener, DoresoListener {

    private DoresoRecord mDoresoRecord;

    private DoresoManager mDoresoManager;

    private DoresoConfig mConfig;

    private MusicRecordListener mListener;

    private boolean mProcessing;

    public static String APPKEY = "";

    public static String APPSECRET = "";

    public MusicRecorder(Context ctx) {
        mConfig = new DoresoConfig();
        mConfig.appkey = APPKEY;
        mConfig.appSecret = APPSECRET;
        mConfig.listener = this;
        mConfig.context = ctx;
        mDoresoManager = new DoresoManager(mConfig);
        mDoresoRecord = new DoresoRecord(this, 16 * 1000);
    }

    public void setMusicRecordListener(MusicRecordListener listener) {
        mListener = listener;
    }

    public boolean pause() {
        mDoresoRecord.reqCancel();
        mDoresoManager.cancel();

        return true;
    }

    public boolean start() {
        if (!mProcessing) {
            mProcessing = true;

            if (mDoresoRecord!=null) {
                mDoresoRecord.reqCancel();
                mDoresoRecord = null;
            }
            mDoresoRecord = new DoresoRecord(this, 15*1024);
            mDoresoRecord.start();
            if (!mDoresoManager.startRecognize()) {
                mProcessing = false;
            }
        }

        return mProcessing;
    }

    public boolean stop() {
        if (mProcessing) {
            mDoresoRecord.reqStop();
            return true;
        } else {
            return false;
        }
    }

    public boolean cancel() {
        if (mProcessing) {
            mDoresoManager.cancel();
            mProcessing = false;
        }

        return mProcessing;
    }

    @Override
    public void onRecognizeSuccess(DoresoMusicTrack[] doresoMusicTracks, String s) {
        mDoresoManager.stopRecognize();
        mProcessing = false;

        mListener.onSuccess(doresoMusicTracks);
    }

    @Override
    public void onRecognizeFail(int i, String s) {
        mDoresoManager.cancel();
        mProcessing = false;

        mListener.onFailure(i, s);
    }

    @Override
    public void onRecognizeEnd() {
        mProcessing = false;
        mDoresoRecord.reqCancel();
    }

    @Override
    public void onRecording(byte[] buffer) {
        mListener.onProcessing(buffer, DoresoUtils.computeDb(buffer, buffer.length));
        mDoresoManager.doRecognize(buffer);
    }

    @Override
    public void onRecordError(int errorcode, String msg) {
        mListener.onFailure(errorcode, msg);
    }

    @Override
    public void onRecordEnd() {
        mDoresoManager.stopRecognize();
        mProcessing = false;
        mListener.onFailure(0, "Record ended");
    }
}