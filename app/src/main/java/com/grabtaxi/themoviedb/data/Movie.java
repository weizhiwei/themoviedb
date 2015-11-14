package com.grabtaxi.themoviedb.data;

import android.text.TextUtils;

import java.io.Serializable;


public class Movie implements Serializable {

    public final long id;
    public final String title;
    public final String poster;
    public final String backdrop;
    public final String overview;
    public final String tagline;

    public Movie(long id, String title, String poster, String backdrop, String overview, String tagline) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.backdrop = backdrop;
        this.overview = overview;
        this.tagline = tagline;
    }

    public boolean isValid() {
        return id > 0 && !TextUtils.isEmpty(title);
    }
}
