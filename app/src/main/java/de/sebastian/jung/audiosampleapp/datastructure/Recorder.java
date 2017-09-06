package de.sebastian.jung.audiosampleapp.datastructure;

import android.media.MediaRecorder;
import android.util.Log;
import java.io.File;
import java.io.IOException;

public class Recorder {

    public static final String FILE_ENDING = ".aac";
    public static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private String lastFileName;
    private MediaRecorder mRecorder = null;

    public void startRecording(String mFileName) {
        lastFileName = mFileName;
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("Recorder", "prepare() failed");
        }
        mRecorder.start();
    }

    public File stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        return new File(lastFileName);
    }

    public void cleanUp() {
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }
}
