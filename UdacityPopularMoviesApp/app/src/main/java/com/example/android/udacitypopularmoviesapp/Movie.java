package com.example.android.udacitypopularmoviesapp;

public class Movie {

    // Placeholders for movie information.
    private String title;
    private String posterPath;
    private String releaseDate;
    private String overview;
    private String userRating;

    // Movie class, taking in Strings for information.
    public Movie(String title, String posterPath, String releaseDate, String overview, String userRating) {
        this.title = title;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.overview = overview;
        this.userRating = userRating;
    }

    // Methods for getting movie information.
    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public String getUserRating() {
        return userRating;
    }

}
