package com.example.finalexam;

import java.io.Serializable;

public class Track implements Serializable {
    private String track_id;
    private String track_title;
    private String track_duration;
    private String album_title;
    private String artist_name;
    private String cover_small;
    private String date;
    private String preview;
    private boolean isPlaying = false;

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getTrack_id() {
        return track_id;
    }

    public void setTrack_id(String track_id) {
        this.track_id = track_id;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getTrack_title() {
        return track_title;
    }

    public void setTrack_title(String track_title) {
        this.track_title = track_title;
    }

    public String getTrack_duration() {
        return track_duration;
    }

    public void setTrack_duration(String track_duration) {
        this.track_duration = track_duration;
    }

    public String getAlbum_title() {
        return album_title;
    }

    public void setAlbum_title(String album_title) {
        this.album_title = album_title;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getCover_small() {
        return cover_small;
    }

    public void setCover_small(String cover_small) {
        this.cover_small = cover_small;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Track{" +
                "track_title='" + track_title + '\'' +
                ", track_duration='" + track_duration + '\'' +
                ", album_title='" + album_title + '\'' +
                ", artist_name='" + artist_name + '\'' +
                ", cover_small='" + cover_small + '\'' +
                ", date='" + date + '\'' +
                ", preview='" + preview + '\'' +
                '}';
    }
}
