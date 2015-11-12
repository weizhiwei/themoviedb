package com.grabtaxi.themoviedb.data;

public interface MovieDetailsDAO {
    public void getMovieDetails(long movieId, DAOCallback<Movie> cb);
}
