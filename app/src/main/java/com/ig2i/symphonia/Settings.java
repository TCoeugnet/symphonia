package com.ig2i.symphonia;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * Class used to handle settings
 *
 * Created by Thomas on 05/05/2015.
 */
public class Settings {

    /**
     * Tag used in LogCat
     */
    private final static String TAG = "SYM/Settings";

    /**
     * Filename in raw resources directory (without extension)
     */
    private final static String FILENAME = "symphonia";

    /**
     * Properties object
     */
    private static Properties props = null;

    /**
     * Initializes settings.
     *
     * Done once, at the launching of the application.
     * Fetches all settings from symhponia.properties file
     *
     * @param ctx The application's context
     */
    public static void init(Context ctx) {
        InputStream stream = null;

        props = new Properties();

        try {

            stream = ctx.getResources().openRawResource(ctx.getResources().getIdentifier(FILENAME, "raw", ctx.getPackageName()));

            if(stream == null) {
                Log.e(TAG, "Unable to load Properties, unable to get resource as stream.");
                props = null;
            } else {
                props.load(stream);
                Log.d(TAG, "Properties loaded");
            }
        } catch(IOException exc) {
            Log.e(TAG, "Unable to load Properties, exception thrown : ");
            Log.e(TAG, exc.getMessage());
            props = null;
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch (IOException exc) {
                    Log.w(TAG, "When loading Properties : unable to close stream, exception thrown : ");
                    Log.w(TAG, exc.getMessage());
                    Log.w(TAG, "Continuing...");
                }
            }
        }
    }

    /**
     *
     * Fetches a parameter in .properties file.
     *
     * @param name The key name
     * @return The associated value
     */
    public static String get(String name) {
        String value = "";

        if(props != null) {
            value = props.getProperty(name);
        } else {
            Log.e(TAG, "Unable to retrieve property value : Context not initalized.");
        }

        return value;
    }
}
