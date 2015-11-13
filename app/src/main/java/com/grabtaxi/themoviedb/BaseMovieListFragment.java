package com.grabtaxi.themoviedb;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.error.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.grabtaxi.themoviedb.data.DAOCallback;
import com.grabtaxi.themoviedb.data.Movie;
import com.grabtaxi.themoviedb.data.MovieListDAO;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseMovieListFragment extends Fragment {

    private MovieListDAO mDao;

    private OnFragmentInteractionListener mListener;


    public BaseMovieListFragment() {
        // Required empty public constructor
    }

    abstract MovieListDAO myDao();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDao = myDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        final MySwipeRefreshLayout reload = (MySwipeRefreshLayout) rootView.findViewById(R.id.reload);
        final GridView grid = (GridView) rootView.findViewById(R.id.grid);

        final MovieListAdapter adapter = new MovieListAdapter(
                inflater, getResources().getDimensionPixelSize(R.dimen.poster_width), getResources().getDimensionPixelSize(R.dimen.poster_height));
        grid.setAdapter(adapter);
        grid.setOnScrollListener(new EndlessScrollListener(5, 1) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                reload.setRefreshing(true);
                mDao.load(page, new DAOCallback<List<Movie>>() {
                    @Override
                    public void onSuccess(List<Movie> movies) {
                        adapter.addMovies(movies);
                        adapter.notifyDataSetChanged();
                        reload.setRefreshing(false);
                        setLoading(false);
                    }

                    @Override
                    public void onFailure(int errCode, String errMsg) {
                        reload.setRefreshing(false);
                        setLoading(false);
                    }
                });
            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra(MovieDetailsActivity.ARG_MOVIE_DETAILS, (Movie) adapter.getItem(position));
                startActivityForResult(intent, 0);
            }
        });

        reload.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener () {
            @Override
            public void onRefresh() {
                mDao.load(1, new DAOCallback<List<Movie>>() {
                    @Override
                    public void onSuccess(List<Movie> movies) {
                        adapter.clear();
                        adapter.addMovies(movies);
                        adapter.notifyDataSetChanged();
                        reload.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(int errCode, String errMsg) {
                        reload.setRefreshing(false);
                    }
                });
            }
        });

        //
        if (adapter.getCount() == 0) {
            reload.triggerReload();
        }

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    private static class MovieListAdapter extends BaseAdapter {

        private final LayoutInflater inflater;
        private final int itemWidth;
        private final int itemHeight;
        private final List<Movie> movies;

        public MovieListAdapter(LayoutInflater inflater, int itemWidth, int itemHeight) {
            this.inflater = inflater;
            this.itemWidth = itemWidth;
            this.itemHeight = itemHeight;
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
        public View getView(int position, View view, ViewGroup parent) {
            final ItemViewHolder holder;
            if (view == null) {
                view = inflater.inflate(R.layout.item_movie_list, parent, false);
                holder = new ItemViewHolder(
                        (ImageView) view.findViewById(R.id.image),
                        (TextView)  view.findViewById(R.id.text));
                view.setLayoutParams(new GridView.LayoutParams(itemWidth, itemHeight));
                view.setTag(holder);
            } else {
                holder = (ItemViewHolder) view.getTag();
            }

            // update
            Movie movie = (Movie) getItem(position);
            holder.text.setText(movie.title);

            holder.image.setVisibility(View.GONE);
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
