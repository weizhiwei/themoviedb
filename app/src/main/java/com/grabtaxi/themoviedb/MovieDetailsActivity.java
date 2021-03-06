package com.grabtaxi.themoviedb;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
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

import com.android.volley.error.VolleyError;
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

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            final Movie movie = (Movie) bundle.get(ARG_MOVIE_DETAILS);
            assert movie.id > 0 &&
                   !TextUtils.isEmpty(movie.title);

            updateMovieDetailsView(movie);
            setUpFavouriteButton(movie);
            setUpRelatedMovies(movie);
            // this must be called last
            setUpReload(movie);
        }
    }

    private void setUpRelatedMovies(final Movie movie) {
        final RelatedMoviesAdapter adapter = new RelatedMoviesAdapter(
                getLayoutInflater(),
                getResources().getDimensionPixelSize(R.dimen.poster_width),
                getResources().getDimensionPixelSize(R.dimen.poster_height)
        );

        final DAOCallback<List<Movie>> loadRelatedMoviesCb = new DAOCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> movies) {
                adapter.addMovies(movies);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int errCode, String errMsg) {

            }
        };

        final FancyCoverFlow relatedMovies = (FancyCoverFlow) findViewById(R.id.related_movies);
        relatedMovies.setReflectionEnabled(true);
        relatedMovies.setReflectionRatio(0.3f);
        relatedMovies.setReflectionGap(0);
        relatedMovies.setAdapter(adapter);
        relatedMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (relatedMovies.getSelectedItemPosition() != position) {
                    return;
                }
                final Movie m = (Movie) relatedMovies.getSelectedItem();
                if (null != m) {
                    Intent intent = new Intent(MovieDetailsActivity.this, MovieDetailsActivity.class);
                    intent.putExtra(MovieDetailsActivity.ARG_MOVIE_DETAILS, m);
                    startActivityForResult(intent, 0);
                }
            }
        });
        relatedMovies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > adapter.getCount() - 5) {
                    final MovieListDAO relatedMoviesDao = DAOFactory.newRelatedMoviesDao(movie.id);
                    relatedMoviesDao.load(adapter.getNextPage(), loadRelatedMoviesCb);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setUpReload(final Movie movie) {
        final MySwipeRefreshLayout reload = (MySwipeRefreshLayout) findViewById(R.id.reload);
        final TextView title = (TextView) findViewById(R.id.related_movies_title);
        final FancyCoverFlow relatedMovies = (FancyCoverFlow) findViewById(R.id.related_movies);
        final RelatedMoviesAdapter relatedMoviesAdapter = (RelatedMoviesAdapter) relatedMovies.getAdapter();

        final DAOCallback<Movie> getMovieDetailsCb = new DAOCallback<Movie>() {
            @Override
            public void onSuccess(Movie m) {
                reload.setRefreshing(false);
                updateMovieDetailsView(m);
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                reload.setRefreshing(false);
            }
        };

        final DAOCallback<List<Movie>> loadRelatedMoviesCb = new DAOCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> movies) {
                if (!movies.isEmpty()) {
                    title.setVisibility(View.VISIBLE);
                    relatedMovies.setVisibility(View.VISIBLE);
                }

                relatedMoviesAdapter.clear();
                relatedMoviesAdapter.addMovies(movies);
                relatedMoviesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int errCode, String errMsg) {

            }
        };

        reload.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final MovieDetailsDAO movieDetailsDao = DAOFactory.newMovieDetailsDao(movie.id);
                final MovieListDAO relatedMoviesDao = DAOFactory.newRelatedMoviesDao(movie.id);
                movieDetailsDao.getMovieDetails(getMovieDetailsCb);
                relatedMoviesDao.load(1, loadRelatedMoviesCb);
            }
        });

        // trigger a initial reload
        reload.triggerReload();
    }

    private void setUpFavouriteButton(final Movie movie) {
        final FavouritesDAO favouritesDao = DAOFactory.newFavouritesDao();
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

        final DAOCallback<Boolean> setFavouriteCb = new DAOCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean isFavourite) {
                favourite.setChecked(isFavourite);
                favourite.setEnabled(true);
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                favourite.setEnabled(true);
            }
        };
        favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                favouritesDao.setFavourite(movie, isChecked, setFavouriteCb);
            }
        });
    }

    private void updateMovieDetailsView(Movie movie) {
        final TextView title = (TextView) findViewById(R.id.title);
        title.setText(movie.title);

        final TextView tagline = (TextView) findViewById(R.id.tagline);
        if (!TextUtils.isEmpty(movie.tagline)) {
            tagline.setVisibility(View.VISIBLE);
            tagline.setText(movie.tagline);
        } else {
            tagline.setVisibility(View.INVISIBLE);
        }

        final TextView overview = (TextView) findViewById(R.id.overview);
        if (!TextUtils.isEmpty(movie.overview)) {
            overview.setVisibility(View.VISIBLE);
            overview.setText(movie.overview);
        } else {
            overview.setVisibility(View.GONE);
        }

        App.updateImageView((ImageView) findViewById(R.id.backdrop), movie.backdrop);
        App.updateImageView((ImageView) findViewById(R.id.poster), movie.poster);
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
        private final int itemWidth;
        private final int itemHeight;
        private final List<Movie> movies;
        private int nextPage;

        RelatedMoviesAdapter(LayoutInflater inflater, int itemWidth, int itemHeight) {
            this.inflater = inflater;
            this.itemWidth = itemWidth;
            this.itemHeight = itemHeight;
            this.movies = new ArrayList<>();
            this.nextPage = 1;
        }

        public void addMovies(List<Movie> movies) {
            if (movies.isEmpty()) {
                return;
            }
            this.movies.addAll(movies);
            ++nextPage;
        }

        public void clear() {
            movies.clear();
            nextPage = 1;
        }

        public int getNextPage() {
            return nextPage;
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
                view.setLayoutParams(new FancyCoverFlow.LayoutParams(itemWidth, itemHeight));
                view.setMinimumWidth(itemWidth);
                view.setMinimumHeight(itemHeight);
                view.setTag(holder);
            } else {
                holder = (ItemViewHolder) view.getTag();
            }

            // update
            final Movie movie = (Movie) getItem(position);

            holder.text.setText(movie.title);

            holder.image.setVisibility(View.INVISIBLE);
            App.updateImageView(holder.image, movie.poster);

            return view;
        }
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
