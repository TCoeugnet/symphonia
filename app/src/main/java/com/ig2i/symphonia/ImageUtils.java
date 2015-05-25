package com.ig2i.symphonia;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageUtils {

    public final static String TAG = "Image";

    //Transformer un objet Bitmap en byte array
    //Réciproque : BitmapFactory.decodeByteArray(bytes, 0, bytes.length)
    public static byte[] BitmapToByteArray(Bitmap bmp) {

        if(bmp == null)
        {
            return null;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bos); //On transforme au format png
        return bos.toByteArray();
    }

    //Récupération d'un Bitmap depuis une URI
    public static Bitmap UriToBitmap(String uri) {

        Bitmap bmp = null;

        try(InputStream is = (InputStream) new URL(uri).openStream()) {
            bmp =  BitmapFactory.decodeStream(is);
        } catch(MalformedURLException exc) {
            Log.e(TAG + "/URL", exc.getMessage());
        } catch (IOException exc) {
            Log.e(TAG + "/IO", exc.getMessage());
        }

        return bmp;
    }
}
