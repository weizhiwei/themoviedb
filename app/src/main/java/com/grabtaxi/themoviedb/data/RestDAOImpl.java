package com.grabtaxi.themoviedb.data;

import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.grabtaxi.themoviedb.MyVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


class RestDAOImpl implements MovieListDAO, MovieDetailsDAO {

    static final String URL_NOW_SHOWING = "http://api.themoviedb.org/3/movie/now_playing?api_key=4df263f48a4fe2621749627f5d001bf0&page=%d";
    static final String URL_RELATED_MOVIES = "http://api.themoviedb.org/3/movie/%d/similar?api_key=4df263f48a4fe2621749627f5d001bf0&page=%%d";
    static final String URL_MOVIE_DETAILS = "http://api.themoviedb.org/3/movie/%d?api_key=4df263f48a4fe2621749627f5d001bf0";
    private static final String URL_IMAGE_BASE = "http://image.tmdb.org/t/p/w500/";

    private final String url;

    RestDAOImpl(String url) {
        this.url = url;
    }

    @Override
    public void load(int page, final DAOCallback<List<Movie>> cb) {
        MyVolley.getRequestQueue().add(new JsonObjectRequest(
                String.format(url, page),
                null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        try {
                            JSONArray results = result.getJSONArray("results");
                            List<Movie> movies = new ArrayList<>(results.length());
                            for (int i = 0; i < results.length(); ++i) {
                                movies.add(fromJson(results.getJSONObject(i)));
                            }
                            cb.onSuccess(movies);
                        } catch (JSONException e) {
                            cb.onFailure(0, "Invalid JSON");
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cb.onFailure(1, "Network Error");
                    }
                }
        ));
    }

    @Override
    public void getMovieDetails(long movieId, final DAOCallback<Movie> cb) {
        MyVolley.getRequestQueue().add(new JsonObjectRequest(
                String.format(url, movieId),
                null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        try {
                            cb.onSuccess(fromJson(result));
                        } catch (JSONException e) {
                            cb.onFailure(0, "Invalid JSON");
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cb.onFailure(
                                error.networkResponse.statusCode,
                                error.networkResponse.toString());
                    }
                }
        ));
    }

    /**
     {
     "adult": false,
     "backdrop_path": "/dkMD5qlogeRMiEixC4YNPUvax2T.jpg",
     "belongs_to_collection": {
     "id": 328,
     "name": "Jurassic Park Collection",
     "poster_path": "/jcUXVtJ6s0NG0EaxllQCAUtqdr0.jpg",
     "backdrop_path": "/pJjIH9QN0OkHFV9eue6XfRVnPkr.jpg"
     },
     "budget": 150000000,
     "genres": [
     {
     "id": 28,
     "name": "Action"
     },
     {
     "id": 12,
     "name": "Adventure"
     },
     {
     "id": 878,
     "name": "Science Fiction"
     },
     {
     "id": 53,
     "name": "Thriller"
     }
     ],
     "homepage": "http://www.jurassicworld.com/",
     "id": 135397,
     "imdb_id": "tt0369610",
     "original_language": "en",
     "original_title": "Jurassic World",
     "overview": "Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.",
     "popularity": 45.010952,
     "poster_path": "/jjBgi2r5cRt36xF6iNUEhzscEcb.jpg",
     "production_companies": [
     {
     "name": "Universal Studios",
     "id": 13
     },
     {
     "name": "Amblin Entertainment",
     "id": 56
     },
     {
     "name": "Legendary Pictures",
     "id": 923
     }
     ],
     "production_countries": [
     {
     "iso_3166_1": "US",
     "name": "United States of America"
     }
     ],
     "release_date": "2015-06-12",
     "revenue": 1513528810,
     "runtime": 124,
     "spoken_languages": [
     {
     "iso_639_1": "en",
     "name": "English"
     }
     ],
     "status": "Released",
     "tagline": "The park is open.",
     "title": "Jurassic World",
     "video": false,
     "vote_average": 6.8,
     "vote_count": 2938
     }
     */
    private static Movie fromJson(JSONObject json) throws JSONException {
        return new Movie(
                json.getLong("id"),
                json.getString("title"),
                URL_IMAGE_BASE + json.optString("poster_path"),
                URL_IMAGE_BASE + json.optString("backdrop_path"),
                json.optString("overview"),
                json.optString("tagline")
        );
    }
}
