package com.grabtaxi.themoviedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.grabtaxi.themoviedb.data.Movie;

import java.util.ArrayList;
import java.util.List;

public class Database {
	private static final String TAG = "Database";

	private static final String DATABASE_NAME = "themovie.db";
	private static final int DATABASE_VERSION = 1;

	private Context mCtx;
	private DatabaseOpenHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String TABLE_FAVOURITE = "favourite";
    private static final String DATABASE_CREATE =
            "CREATE TABLE "+ TABLE_FAVOURITE +" (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id INTEGER NOT NULL," +
            "title TEXT NOT NULL," +
            "poster TEXT);";

    private volatile static Database instance;

	/** Returns singleton class instance */
	public static Database getInstance() {
		if (instance == null) {
			synchronized (Database.class) {
				if (instance == null) {
					instance = new Database();
				}
			}
		}
		return instance;
	}

    protected Database() {}
    
    public synchronized void open(Context context) {
    	mCtx = context;
    	mDbHelper = new DatabaseOpenHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    public long addMovieToFavourite(Movie movie) {
        removeMovieFromFavourite(movie.id);

        ContentValues initialValues = new ContentValues();
        initialValues.put("id", movie.id);
        initialValues.put("title", movie.title);
        initialValues.put("poster", movie.poster);
        return mDb.insert(TABLE_FAVOURITE, null, initialValues);
    }

    public boolean removeMovieFromFavourite(long id) {
    	return mDb.delete(TABLE_FAVOURITE, "id='"+id+"'", null) > 0;
    }

    public boolean isMovieInFavourite(long id) {
		Cursor mCursor = mDb.query(true, TABLE_FAVOURITE, new String[] {"id"}, "id='"+id+"'", null, null, null, null, "1");
		boolean isIn = (mCursor != null && mCursor.moveToFirst());
		if (mCursor != null) {
			mCursor.close();
		}
		return isIn;
    }

    public List<Movie> fetchMoviesInFavourite(int offset, int limit) {
    	List<Movie> favourites = new ArrayList<>();

    	Cursor cursor = mDb.query(TABLE_FAVOURITE, null, null, null, null, null, null, offset+","+limit);
    	if (cursor != null) {
			cursor.moveToFirst();
			while (cursor.isAfterLast() == false) {
                favourites.add(new Movie(
                        cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("title")),
                        cursor.getString(cursor.getColumnIndex("poster")),
                        null,
                        null,
                        null
                ));
				cursor.moveToNext();
	        }
			cursor.close();
		}

    	return favourites;
    }
    
    private static class DatabaseOpenHelper extends SQLiteOpenHelper {
    	DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITE);
            onCreate(db);
        }        
    }
}
