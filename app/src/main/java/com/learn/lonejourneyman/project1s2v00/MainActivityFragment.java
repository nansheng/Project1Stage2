package com.learn.lonejourneyman.project1s2v00;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    GridView gridView;

    public MainActivityFragment() {}

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    public void updateMovie() {
        FetchMovieDBTask movieDBTask = new FetchMovieDBTask(getActivity(), gridView, null, new FragmentCallback(){
            @Override
            public void onTaskDone() {
                replaceDetailFragment(0);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(getString(R.string.pref_sort_by_key),getString(R.string.pref_sort_by_default));

        movieDBTask.execute("MovieDB", sortBy);
    }

    public interface FragmentCallback {
        void onTaskDone();
    }

    private void replaceDetailFragment(Integer position) {
        Bundle bundle = new Bundle();
        bundle.putString("POSTER", FetchMovieDBTask.posters.get(position));
        bundle.putString("RELEASEDATE", FetchMovieDBTask.releasedates.get(position));
        bundle.putString("TITLE", FetchMovieDBTask.titles.get(position));
        bundle.putString("PLOT", FetchMovieDBTask.plots.get(position));
        bundle.putString("RATING", FetchMovieDBTask.rates.get(position));
        bundle.putString("MOVIEID", FetchMovieDBTask.movieid.get(position));
        Fragment detailFragment = new DetailActivityFragment();
        detailFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.movie_detail_container, detailFragment)
                .commit();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        gridView = (GridView) rootView.findViewById(R.id.posters_grid);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (MainActivity.mTwoPane) {
                    replaceDetailFragment(position);
                } else {
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra("POSTER", FetchMovieDBTask.posters.get(position))
                            .putExtra("RELEASEDATE", FetchMovieDBTask.releasedates.get(position))
                            .putExtra("TITLE", FetchMovieDBTask.titles.get(position))
                            .putExtra("PLOT", FetchMovieDBTask.plots.get(position))
                            .putExtra("RATING", FetchMovieDBTask.rates.get(position))
                            .putExtra("MOVIEID", FetchMovieDBTask.movieid.get(position));
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }
}