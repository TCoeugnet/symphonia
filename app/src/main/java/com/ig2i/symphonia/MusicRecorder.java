package com.ig2i.symphonia;

import android.content.Context;

import com.doreso.sdk.DoresoConfig;
import com.doreso.sdk.DoresoListener;
import com.doreso.sdk.DoresoManager;
import com.doreso.sdk.utils.DoresoMusicTrack;
import com.doreso.sdk.utils.DoresoUtils;


//Classe principale pour la reconnaissance de musiques
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
        mDoresoRecord = null; //Configuration de base
    }

    public void setMusicRecordListener(MusicRecordListener listener) {
        mListener = listener;
    }

    //Mise en pause
    public boolean pause() {
        mDoresoRecord.reqCancel();
        mDoresoManager.cancel();

        return true;
    }

    //Démarrage de la reconnaissance
    public boolean start() {
        if (!mProcessing) {
            mProcessing = true;

            if (mDoresoRecord!=null) { //Si déjà démarré, on arrête avant de redémarrer
                mDoresoRecord.reqCancel();
                mDoresoRecord = null;
            }

            //Démarrage
            mDoresoRecord = new DoresoRecord(this, 16*1000);
            mDoresoRecord.start();

            if (!mDoresoManager.startRecognize()) { //On check si ça a démarré
                mProcessing = false;
            }
        }

        return mProcessing;
    }

    //Arrêt de la reconnaissance
    public boolean stop() {
        if (mProcessing) {
            mDoresoRecord.reqStop();
            return true;
        } else {
            return false;
        }
    }

    //Annulation de la reconnaissance
    public boolean cancel() {
        if (mProcessing) {
            mDoresoManager.cancel();
            mProcessing = false;
        }

        return mProcessing; //false
    }

    //Chanson reconnue !
    @Override
    public void onRecognizeSuccess(DoresoMusicTrack[] doresoMusicTracks, String s) {
        mDoresoManager.stopRecognize();
        mProcessing = false;

        if(doresoMusicTracks.length == 1) //Si on a trouvé une seul chanson, c'est qu'on est sûr à 100% d'avoir la bonne
            mListener.onSuccess(doresoMusicTracks[0]);
        else
            mListener.onFailure(); //Sinon on fait comme si on n'avait aucune correspondance
    }

    //Chanson non reconnue
    @Override
    public void onRecognizeFail(int i, String s) {
        mDoresoManager.cancel();
        mProcessing = false;

        mListener.onFailure();
    }

    //Fin de la reconnaissance
    @Override
    public void onRecognizeEnd() {
        mProcessing = false;
        mDoresoRecord.reqCancel();
    }

    //Executé plusieurs fois pendant la reconnaissance
    @Override
    public void onRecording(byte[] buffer) {
        mListener.onProcessing(buffer, DoresoUtils.computeDb(buffer, buffer.length));//On envoie le buffer et le volume en Db
        mDoresoManager.doRecognize(buffer);
    }

    //Erreur : pas internet, serveur injoignable, erreur de micro.... : La reconnaissance n'a pas pu être effectuée
    @Override
    public void onRecordError(int errorcode, String msg) {
        if(errorcode == 4012) //4012 : Aucune correspondance trouvée; ce code est traité comme un code d'erreur alors que c'est simplement une absence de correspondance
            mListener.onFailure();
        else
            mListener.onError(errorcode, msg);
    }

    //En cas d'arrêt
    @Override
    public void onRecordEnd() {
        mDoresoManager.stopRecognize();
        mProcessing = false;
    }
}