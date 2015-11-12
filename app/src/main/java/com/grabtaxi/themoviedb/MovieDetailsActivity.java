package com.grabtaxi.themoviedb;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.toolbox.ImageLoader;
import com.grabtaxi.themoviedb.data.DAOCallback;
import com.grabtaxi.themoviedb.data.DAOFactory;
import com.grabtaxi.themoviedb.data.FavouritesDAO;
import com.grabtaxi.themoviedb.data.Movie;
import com.grabtaxi.themoviedb.data.MovieDetailsDAO;
import com.grabtaxi.themoviedb.data.MovieListDAO;

import java.util.ArrayList;
import java.util.List;

import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowAdapter;


public class MovieDetailsActivity extends ActionBarActivity {

    public static final String ARG_MOVIE_DETAILS = "movie_details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            final Movie movie = (Movie) bundle.get(ARG_MOVIE_DETAILS);
            final MovieDetailsDAO movieDetailsDao = DAOFactory.newMovieDetailsDao();
            final FavouritesDAO favouritesDao = DAOFactory.newFavouritesDao();
            final MovieListDAO relatedMoviesDao = DAOFactory.newRelatedMoviesDao(movie.id);
            setTitle(movie.title);

            MyVolley.getImageLoader().get(movie.backdrop,
                    ImageLoader.getImageListener((ImageView) findViewById(R.id.backdrop),
                            R.drawable.background_tab,
                            R.drawable.background_tab));
            MyVolley.getImageLoader().get(movie.poster,
                    ImageLoader.getImageListener((ImageView) findViewById(R.id.poster),
                            R.drawable.background_tab,
                            R.drawable.background_tab));

            final TextView title = (TextView) findViewById(R.id.title);
            title.setText(movie.title);

            final ToggleButton favourite = (ToggleButton) findViewById(R.id.favourite);
            favourite.setEnabled(false);
            favouritesDao.isFavourite(movie.id, new DAOCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean isFavourite) {
                    favourite.setChecked(isFavourite);
                    favourite.setEnabled(true);
                }

                @Override
                public void onFailure(int errCode, String errMsg) {
                    favourite.setEnabled(true);
                }
            });
            favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                    favouritesDao.setFavourite(movie.id, isChecked, new DAOCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean isFavourite) {
                            favourite.setChecked(isFavourite);
                            favourite.setEnabled(true);
                        }

                        @Override
                        public void onFailure(int errCode, String errMsg) {
                            favourite.setEnabled(true);
                        }
                    });
                }
            });

            ((TextView) findViewById(R.id.overview)).setText(movie.overview);

            final RelatedMoviesAdapter relatedMoviesAdapter = new RelatedMoviesAdapter(getLayoutInflater());

            final FancyCoverFlow relatedMovies = (FancyCoverFlow) findViewById(R.id.related_movies);
            relatedMovies.setReflectionEnabled(true);
            relatedMovies.setReflectionRatio(0.3f);
            relatedMovies.setReflectionGap(0);
            relatedMovies.setAdapter(relatedMoviesAdapter);
            relatedMovies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            relatedMoviesDao.load(1, new DAOCallback<List<Movie>>() {
                @Override
                public void onSuccess(List<Movie> movies) {
                    relatedMoviesAdapter.addMovies(movies);
                    relatedMoviesAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(int errCode, String errMsg) {

                }
            });

            movieDetailsDao.getMovieDetails(movie.id, new DAOCallback<Movie>() {
                @Override
                public void onSuccess(Movie m) {
                    title.setText(m.title + m.tagline);
                }

                @Override
                public void onFailure(int errCode, String errMsg) {
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class RelatedMoviesAdapter extends FancyCoverFlowAdapter {

        private final LayoutInflater inflater;
        private final List<Movie> movies;

        RelatedMoviesAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
            this.movies = new ArrayList<>();
        }

        public void addMovies(List<Movie> movies) {
            this.movies.addAll(movies);
        }

        public void clear() {
            movies.clear();
        }

        @Override
        public int getCount() {
            return movies.size();
        }

        @Override
        public Object getItem(int position) {
            return movies.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getCoverFlowItem(int position, View view, ViewGroup parent) {
            final ItemViewHolder holder;
            if (view == null) {
                view = inflater.inflate(R.layout.item_movie_list, parent, false);
                holder = new ItemViewHolder(
                        (ImageView) view.findViewById(R.id.image),
                        (TextView)  view.findViewById(R.id.text));
                view.setTag(holder);
            } else {
                holder = (ItemViewHolder) view.getTag();
            }

            // update
            Movie movie = (Movie) getItem(position);
            holder.text.setText(movie.title);
            MyVolley.getImageLoader().get(movie.poster,
                    ImageLoader.getImageListener(holder.image,
                            R.drawable.background_tab,
                            R.drawable.background_tab));

            return view;
        }

        private static class ItemViewHolder {
            final ImageView image;
            final TextView text;

            public ItemViewHolder(ImageView image, TextView text) {
                this.image = image;
                this.text = text;
            }
        }
    }
}
