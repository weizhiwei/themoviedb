package com.grabtaxi.themoviedb.data;

import com.grabtaxi.themoviedb.Database;

import java.util.List;

class DbDAOImpl implements FavouritesDAO {

    private static int ITEMS_PER_PAGE = 30;

    @Override
    public void load(int page, DAOCallback<List<Movie>> cb) {
        final int offset = (page - 1)*ITEMS_PER_PAGE;
        final int limit = ITEMS_PER_PAGE;

        cb.onSuccess(Database.getInstance().fetchMoviesInFavourite(offset, limit));
    }

    @Override
    public void isFavourite(long movieId, DAOCallback<Boolean> cb) {
        cb.onSuccess(Database.getInstance().isMovieInFavourite(movieId));
    }

    @Override
    public void setFavourite(Movie movie, boolean favourite, DAOCallback<Boolean> cb) {
        if (favourite) {
            Database.getInstance().addMovieToFavourite(movie);
        } else {
            Database.getInstance().removeMovieFromFavourite(movie.id);
        }
        cb.onSuccess(favourite);
    }
}
