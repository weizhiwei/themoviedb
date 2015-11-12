package com.grabtaxi.themoviedb.data;

import java.util.List;

public interface MovieListDAO {
    public void load(int page, DAOCallback<List<Movie>> cb);
}
