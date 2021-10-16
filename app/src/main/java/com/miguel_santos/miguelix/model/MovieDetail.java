package com.miguel_santos.miguelix.model;

import java.util.List;

public class MovieDetail {

    private final Movie movie;
    private final List<Movie> similarMoviesList;

    public MovieDetail(Movie movie, List<Movie> similarMoviesList) {
        this.movie = movie;
        this.similarMoviesList = similarMoviesList;
    }

    public Movie getMovie() {
        return movie;
    }

    public List<Movie> getSimilarMoviesList() {
        return similarMoviesList;
    }

}
