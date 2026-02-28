package com.example.moviecollection.bin;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Results  {

    @PrimaryKey(autoGenerate = false)
    private long id;
    private String overview;
    private String poster_path;
    private String backdrop_path;
    private String release_date;
    private String title;
    private double vote_average;

    public Results() {
    }

    @Ignore
    public Results(long id, String overview, String poster_path, String backdrop_path, String release_date, String title, double vote_average) {
        this.id = id;
        this.overview = overview;
        this.poster_path = poster_path;
        this.backdrop_path = backdrop_path;
        this.release_date = release_date;
        this.title = title;
        this.vote_average = vote_average;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }
    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
    public String getOverview() {
        return overview;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }
    public String getPoster_path() {
        return poster_path;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }
    public String getRelease_date() {
        return release_date;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }
    public double getVote_average() {
        return vote_average;
    }


}
