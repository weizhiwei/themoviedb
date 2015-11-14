package com.grabtaxi.themoviedb.data;

public interface MovieDetailsDAO {
    public void getMovieDetails(DAOCallback<Movie> cb);
}
