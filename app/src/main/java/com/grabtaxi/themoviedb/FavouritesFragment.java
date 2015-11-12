package com.grabtaxi.themoviedb;

import com.grabtaxi.themoviedb.data.DAOFactory;
import com.grabtaxi.themoviedb.data.MovieListDAO;

public class FavouritesFragment extends BaseMovieListFragment {

    @Override
    MovieListDAO myDao() {
        return DAOFactory.newFavouritesDao();
    }
}
