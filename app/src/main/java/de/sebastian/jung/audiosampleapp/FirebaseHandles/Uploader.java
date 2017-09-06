package de.sebastian.jung.audiosampleapp.firebasehandles;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

import java.util.UUID;

import de.sebastian.jung.audiosampleapp.MainActivity;
import de.sebastian.jung.audiosampleapp.datastructure.Recorder;
import de.sebastian.jung.audiosampleapp.datastructure.Recording;

public class Uploader {
    private MainActivity main;
    private Toast lastToast;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private UUID uuid;

    public Uploader(MainActivity mainActivity, UUID uuid) {
        this.main = mainActivity;
        this.lastToast = null;
        this.uuid = uuid;
    }

    public void uploadRecording(final Recording rec) {
        main.updateUI(true, false);
        storageRef.child("recordings/"
                + rec.getSentiment()
                + "/" + rec.getGender()
                + "/" + uuid.toString()
                + "_" + String.valueOf(rec.getTime())
                + Recorder.FILE_ENDING)
                .putFile(Uri.fromFile(rec.getFile())).addOnFailureListener(new OnFailureListener() {
            public void onFailure(@NonNull Exception exception) {
                    makeToast("Es ist ein Fehler während dem Upload aufgetreten");
                    main.updateUI(false, false);
                }
            }).addOnSuccessListener(new OnSuccessListener<TaskSnapshot>() {
                public void onSuccess(TaskSnapshot taskSnapshot) {
                    makeToast("Die Datei wurde erfolgreich hoch geladen");
                    main.updateUI(true, true);
                    rec.getFile().delete();
                }
            }).addOnProgressListener(new OnProgressListener<TaskSnapshot>() {
                public void onProgress(TaskSnapshot taskSnapshot) {
                    makeToast(String.valueOf(
                            ((((float) taskSnapshot.getBytesTransferred()) * 100.0f) /
                                    ((float) taskSnapshot.getTotalByteCount())) + "% hochgeladen"));
                }
            });
    }

    private void makeToast(String text) {
        if (lastToast != null) {
            lastToast.cancel();
        }
        lastToast = Toast.makeText(main, text, Toast.LENGTH_SHORT);
        lastToast.show();
    }
}
