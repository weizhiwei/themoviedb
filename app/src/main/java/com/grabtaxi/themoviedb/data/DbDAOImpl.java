package com.grabtaxi.themoviedb.data;

import com.grabtaxi.themoviedb.Database;

import java.util.List;

class DbDAOImpl implements FavouritesDAO {

    private static int ITEMS_PER_PAGE = 30;

    private int fromRowId;

    @Override
    public void load(int page, DAOCallback<List<Movie>> cb) {
        final int limit = ITEMS_PER_PAGE;

        if (1 == page) {
            fromRowId = Integer.MAX_VALUE;
        }
        List<Movie> movies = Database.getInstance().fetchMoviesInFavourite(fromRowId, limit);
        if (!movies.isEmpty()) {
            fromRowId = Database.getInstance().getRowIdByMovieId(movies.get(movies.size() - 1).id);
        }
        cb.onSuccess(movies);
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
