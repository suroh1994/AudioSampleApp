package de.sebastian.jung.audiosampleapp;

import android.content.Context;

/**
 * Created by sebastianjung on 29.08.17.
 */

public class PreferencesHandler {

    class UninitializedException extends Exception {
        public UninitializedException() {
            super();
        }

        @Override
        public String getMessage() {
            return "PreferenceHandler has not been initialized.";
        }
    }

    private static PreferencesHandler instance;

    private Context ctx;

    public PreferencesHandler getInstance() {
        if (instance == null) {
            instance = new PreferencesHandler();
        }
        return instance;
    }

    private PreferencesHandler() {

    }

    public void init(Context context) {
        ctx = context;
    }
}
