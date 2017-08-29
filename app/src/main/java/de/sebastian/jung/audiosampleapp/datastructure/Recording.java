package de.sebastian.jung.audiosampleapp.datastructure;

import java.io.File;
import java.util.Date;

public class Recording {
    private File file;
    private boolean isMale;
    private boolean sentiment;
    private long time = new Date().getTime();

    public Recording(File file, boolean sentiment, boolean isMale) {
        this.file = file;
        this.sentiment = sentiment;
        this.isMale = isMale;
    }

    public String getSentiment() {
        return this.sentiment ? "positive" : "negative";
    }

    public String getGender() {
        return this.isMale ? "male" : "female";
    }

    public File getFile() {
        return this.file;
    }

    public long getTime() {
        return this.time;
    }
}
