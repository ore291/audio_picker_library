package com.knighttechapps.audio_picker.models;

import android.net.Uri;
import java.io.Serializable;

public class AudioModel implements Serializable {
    private long id;
    private String title;
    private String artist;
    private String album;
    private long duration;
    private Uri uri;
    private long size;
    private long dateAdded;

    public AudioModel(long id, String title, String artist, String album, 
                     long duration, Uri uri, long size, long dateAdded) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.uri = uri;
        this.size = size;
        this.dateAdded = dateAdded;
    }

    // Getters
    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public long getDuration() { return duration; }
    public Uri getUri() { return uri; }
    public long getSize() { return size; }
    public long getDateAdded() { return dateAdded; }

    // Setters
    public void setId(long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public void setAlbum(String album) { this.album = album; }
    public void setDuration(long duration) { this.duration = duration; }
    public void setUri(Uri uri) { this.uri = uri; }
    public void setSize(long size) { this.size = size; }
    public void setDateAdded(long dateAdded) { this.dateAdded = dateAdded; }

    public String getDurationString() {
        long minutes = duration / 1000 / 60;
        long seconds = (duration / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getSizeString() {
        long kb = size / 1024;
        long mb = kb / 1024;
        if (mb > 0) {
            return mb + "MB";
        } else if (kb > 0) {
            return kb + "KB";
        } else {
            return size + "B";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AudioModel that = (AudioModel) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }
}