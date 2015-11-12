package com.grabtaxi.themoviedb.data;

import android.database.Cursor;

import com.grabtaxi.themoviedb.Database;

import java.util.ArrayList;
import java.util.List;

class DbDAOImpl implements FavouritesDAO {

    private static int ITEMS_PER_PAGE = 30;

    @Override
    public void load(int page, DAOCallback<List<Movie>> cb) {
        List<Movie> result = new ArrayList<>();

        final int offset = (page - 1)*ITEMS_PER_PAGE;
        final int limit = ITEMS_PER_PAGE;

    	Cursor cursor = Database.getInstance().getDb().query("favourite", null, null, null, null, null, null, offset + "," + limit);
    	if (cursor != null) {
			cursor.moveToFirst();
			while (cursor.isAfterLast() == false) {

				String label = cursor.getString(cursor.getColumnIndex("label"));
				String nodeUrl = cursor.getString(cursor.getColumnIndex("nodeUrl"));
				String imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
				String story = cursor.getString(cursor.getColumnIndex("story"));


				cursor.moveToNext();
	        }
			cursor.close();
		}
    }

    @Override
    public void isFavourite(long movieId, DAOCallback<Boolean> cb) {
        cb.onSuccess(false);
    }

    @Override
    public void setFavourite(long movieId, boolean favourite, DAOCallback<Boolean> cb) {
        cb.onSuccess(favourite);
    }
}
