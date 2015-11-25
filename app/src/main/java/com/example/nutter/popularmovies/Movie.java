package com.example.nutter.popularmovies;

/**
 * Class to represent a Movie object.
 *
 * @author  Anthony Nutter
 * @version 1.0
 * @since   2015-11-24
*/
public class Movie {
    private String originalTitle;
    private String synopsis;
    private String releaseDate;
    private String posterPath;
    private String rating;
/**
 * accessors
*/
    public String getTitle() {
        return originalTitle;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getRating() {
        return rating;
    }

/**
 * Constructor for a Movie object
*/
    public Movie(String _originalTitle, String _synopsis, String _releaseDate, String _posterPath, String _rating)
    {
        originalTitle = _originalTitle;
        synopsis = _synopsis;
        releaseDate = _releaseDate;
        posterPath = _posterPath;
        rating = _rating;
    }
}
