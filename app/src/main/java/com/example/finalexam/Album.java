package com.example.finalexam;

import java.io.Serializable;
import java.util.Map;

public class Album implements Serializable {
    private String album_id;
    private String title;
    private String cover_small;
    private String cover_big;
    private String nb_tracks;
    private String artist_name;
    private String artist_picture;

    public String getArtist_picture() {
        return artist_picture;
    }

    public void setArtist_picture(String artist_picture) {
        this.artist_picture = artist_picture;
    }

    public String getCover_big() {
        return cover_big;
    }

    public void setCover_big(String cover_big) {
        this.cover_big = cover_big;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover_small() {
        return cover_small;
    }

    public void setCover_small(String cover_small) {
        this.cover_small = cover_small;
    }

    public String getNb_tracks() {
        return nb_tracks;
    }

    public void setNb_tracks(String nb_tracks) {
        this.nb_tracks = nb_tracks;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    @Override
    public String toString() {
        return "Album{" +
                "album_id='" + album_id + '\'' +
                ", title='" + title + '\'' +
                ", cover_small='" + cover_small + '\'' +
                ", cover_big='" + cover_big + '\'' +
                ", nb_tracks='" + nb_tracks + '\'' +
                ", artist_name='" + artist_name + '\'' +
                '}';
    }
}
