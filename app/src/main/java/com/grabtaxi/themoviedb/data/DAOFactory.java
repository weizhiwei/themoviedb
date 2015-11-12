package com.grabtaxi.themoviedb.data;

public class DAOFactory {

    public static MovieListDAO newNowShowingDao() {
        return new RestDAOImpl(RestDAOImpl.URL_NOW_SHOWING);
    }

    public static MovieListDAO newRelatedMoviesDao(long movieId) {
        return new RestDAOImpl(String.format(RestDAOImpl.URL_RELATED_MOVIES, movieId));
    }

    public static FavouritesDAO newFavouritesDao() {
        return new DbDAOImpl();
    }

    public static MovieDetailsDAO newMovieDetailsDao() {
        return new RestDAOImpl(RestDAOImpl.URL_MOVIE_DETAILS);
    }
}
