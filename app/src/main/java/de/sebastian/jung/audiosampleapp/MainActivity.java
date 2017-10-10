package de.sebastian.jung.audiosampleapp;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Random;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.sebastian.jung.audiosampleapp.firebasehandles.Uploader;
import de.sebastian.jung.audiosampleapp.datastructure.Recorder;
import de.sebastian.jung.audiosampleapp.datastructure.Recording;
import de.sebastian.jung.audiosampleapp.dialogs.ErrorExternalDirDialog;
import de.sebastian.jung.audiosampleapp.dialogs.MissingPermissionDialog;

public class MainActivity extends AppCompatActivity {
    //Statics
    private static final String TAG = "ASApp";
    private String mFileName = null;
    //Views
    @BindView(R.id.button_record)
    public Button mRecordButton;

    @BindView(R.id.button_submit)
    public Button mSubmitButton;

    @BindView(R.id.textView_request)
    public TextView mRequestTextView;

    @BindView(R.id.textView_uploads)
    public TextView mUploadsTextView;

    //Options
    private boolean isMale;
    private boolean lastSentiment = true;
    private boolean isRecording = false;
    private String[] permissions = new String[]{"android.permission.RECORD_AUDIO"};
    //Additional Objects
    private SharedPreferences sp_userData;
    private UUID uuid;
    private Recording lastRecording = null;
    private Recorder recorder;
    private Uploader uploader;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initVariables();

        ActivityCompat.requestPermissions(this, permissions, Recorder.REQUEST_RECORD_AUDIO_PERMISSION);

        initUI();
    }

    @OnClick(R.id.button_record)
    public void onRecordClicked() {
        isRecording = !isRecording;

        if (isRecording) {
            mRecordButton.setText("Aufnahme beenden");
            recorder.startRecording(mFileName);
        } else {
            mRecordButton.setText("Aufnahme starten");
            lastRecording = new Recording(recorder.stopRecording(), lastSentiment, isMale);
            if (!mSubmitButton.isEnabled()) {
                mSubmitButton.setEnabled(true);
            }
        }
    }

    @OnClick(R.id.button_submit)
    public void onSubmitClicked() {
        uploader.uploadRecording(lastRecording);
    }

    @OnClick({ R.id.radio_male, R.id.radio_female})
    public void onGenderChanged(RadioButton radioButton) {
        SharedPreferences.Editor sp_editor = getSharedPreferences(
                Constants.USER_DATA_PREFERENCES, 0).edit();

        isMale = (radioButton.isChecked() && radioButton.getId() == R.id.radio_male);

        sp_editor.putBoolean(Constants.USER_GENDER, isMale);
        sp_editor.apply();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.cleanUp();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permissions[0])
                != PackageManager.PERMISSION_GRANTED
                && MissingPermissionDialog.settingsOpened) {
            MissingPermissionDialog.settingsOpened = false; //else App will never open again
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Recorder.REQUEST_RECORD_AUDIO_PERMISSION /*200*/:
                if (grantResults[0] == 0) { //permission to record has been granted
                    recorder = new Recorder();
                } else {
                    MissingPermissionDialog dialog = new MissingPermissionDialog();
                    dialog.setCancelable(false);
                    dialog.show(this.getSupportFragmentManager(), MissingPermissionDialog.TAG);
                }
                break;
        }
    }

    private void updateRequest() {
        lastSentiment = !lastSentiment;
        SharedPreferences.Editor sp_editor = sp_userData.edit();
        sp_editor.putBoolean(Constants.USER_LAST_SENTIMENT, lastSentiment);
        sp_editor.apply();
        if (lastSentiment) {
            mRequestTextView.setText("Bitte sag 'Toll' auf eine erfreute/fröhliche/glückliche Art.");
        } else {
            mRequestTextView.setText("Bitte sag 'Toll' auf eine genervte/verärgerte/erboste Art.");
        }
    }

    private void initVariables() {
        sp_userData = getSharedPreferences(Constants.USER_DATA_PREFERENCES, 0);

        isMale = sp_userData.getBoolean(Constants.USER_GENDER, true);
        lastSentiment = sp_userData.getBoolean(Constants.USER_LAST_SENTIMENT, true);
        uuid = UUID.fromString(
                sp_userData.getString(Constants.USER_UUID, UUID.randomUUID().toString()));

        if (!sp_userData.contains(Constants.USER_GENDER) ||
                !sp_userData.contains(Constants.USER_LAST_SENTIMENT) ||
                !sp_userData.contains(Constants.USER_UUID)) {
            SharedPreferences.Editor sp_editor = sp_userData.edit();
            sp_editor.putBoolean(Constants.USER_GENDER, isMale);
            sp_editor.putBoolean(Constants.USER_LAST_SENTIMENT, lastSentiment);
            sp_editor.putString(Constants.USER_UUID, uuid.toString());
            sp_editor.apply();
        }

        try{
            mFileName = getExternalCacheDir().getAbsolutePath() + "/audiorecordtest.aac";
        } catch (NullPointerException npe) {
            ErrorExternalDirDialog dialog  = new ErrorExternalDirDialog();
            dialog.setCancelable(false);
            dialog.show(getSupportFragmentManager(), ErrorExternalDirDialog.TAG);
        }

        uploader = new Uploader(this, uuid);
    }

    private void initUI() {
        ((RadioButton) findViewById(R.id.radio_male)).setChecked(isMale);
        ((RadioButton) findViewById(R.id.radio_female)).setChecked(!isMale);

        ((TextView) findViewById(R.id.textView_uuid)).setText(uuid.toString());

        updateRequest();

        updateUI(true, false);
    }

    public void updateUI(boolean disableButton, boolean uploadSuccessful) {
        mSubmitButton.setEnabled(!disableButton);
        int successfulUploads = sp_userData.getInt(Constants.USER_UPLOAD, 0);
        if (uploadSuccessful) {
            updateRequest();
            successfulUploads++;

            SharedPreferences.Editor sp_editor = sp_userData.edit();
            sp_editor.putInt(Constants.USER_UPLOAD, successfulUploads);
            sp_editor.apply();
        }
        mUploadsTextView.setText(String.valueOf(successfulUploads));
    }
}
