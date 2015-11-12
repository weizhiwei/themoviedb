package com.grabtaxi.themoviedb.data;

public interface FavouritesDAO extends MovieListDAO {
    public void isFavourite(long movieId, DAOCallback<Boolean> cb);
    public void setFavourite(long movieId, boolean favourite, DAOCallback<Boolean> cb);
}
