package com.ig2i.symphonia;

/**
 * Created by Thomas on 06/05/2015.
 */
public interface DoresoRecordListener {

    /**
     *
     * @param buffer
     */
    public abstract void onRecording(byte[] buffer);


    /**
     *
     * @param errorcode
     * @param msg
     */
    public abstract void onRecordError(int errorcode, String msg);

    /**
     *
     */
    public abstract void onRecordEnd();
}