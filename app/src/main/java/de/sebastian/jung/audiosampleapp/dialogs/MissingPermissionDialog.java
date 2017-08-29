package de.sebastian.jung.audiosampleapp.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

/**
 * Created by sebastianjung on 29.08.17.
 */

public class MissingPermissionDialog extends android.support.v4.app.DialogFragment{

    public static boolean settingsOpened = false;
    public static String TAG = "MissPerm";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Der App fehlen ein oder mehrere Berechtigungen ohne die sie nicht funktioniert. Bitte schalten Sie die Berechtigungen für die App ein.")
            .setPositiveButton("Verstanden", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    settingsOpened = true;
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            })
            .setNegativeButton("Nein danke.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
