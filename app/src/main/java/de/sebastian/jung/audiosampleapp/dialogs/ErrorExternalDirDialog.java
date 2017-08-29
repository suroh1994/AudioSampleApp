package de.sebastian.jung.audiosampleapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by sebastianjung on 29.08.17.
 */

public class ErrorExternalDirDialog extends DialogFragment {

    public static String TAG = "DirErr";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Kein Zugriff auf Dateipfad")
                .setMessage("Es gab einen Fehler beim Zugriff auf den Speicher. Es kann keine Audiodatei angelegt werden!")
                .setNeutralButton("Schade.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
        return builder.create();
    }
}
