package com.grabtaxi.themoviedb;

import com.grabtaxi.themoviedb.data.DAOFactory;
import com.grabtaxi.themoviedb.data.MovieListDAO;

public class NowShowingFragment extends BaseMovieListFragment {

    @Override
    MovieListDAO myDao() {
        return DAOFactory.newNowShowingDao();
    }
}
