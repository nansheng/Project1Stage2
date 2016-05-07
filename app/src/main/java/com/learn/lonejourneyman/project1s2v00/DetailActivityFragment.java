package com.learn.lonejourneyman.project1s2v00;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.learn.lonejourneyman.project1s2v00.data.FavoriteContract;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class DetailActivityFragment extends Fragment {

    private final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private String mPoster;
    private String mReleaseDate;
    private String mTitle;
    private String mPlot;
    private String mRate;
    private String mID;

    ArrayAdapter mReviewAdapter;
    ArrayAdapter mVideoAdapter;

    ToggleButton mFavorite;

    public DetailActivityFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        FetchMovieDBTask movieDBTask;

        if (MainActivity.mTwoPane) {
            Bundle bundle = this.getArguments();
            if (bundle.isEmpty()) {}
            else {
                    mPoster = bundle.getString("POSTER");
                    mReleaseDate = bundle.getString("RELEASEDATE");
                    mTitle = bundle.getString("TITLE");
                    mPlot = bundle.getString("PLOT");
                    mRate = bundle.getString("RATING");
                    mID = bundle.getString("MOVIEID");
            }
        }
        else {
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                mPoster = intent.getStringExtra("POSTER");
                mReleaseDate = intent.getStringExtra("RELEASEDATE");
                mTitle = intent.getStringExtra("TITLE");
                mPlot = intent.getStringExtra("PLOT");
                mRate = intent.getStringExtra("RATING");
                mID = intent.getStringExtra("MOVIEID");
            }
        }


        ((TextView) rootView.findViewById(R.id.detail_title)).setText(mTitle);
        ((TextView) rootView.findViewById(R.id.detail_rating)).setText(mRate + " / 10");
        ((TextView) rootView.findViewById(R.id.detail_release_date)).setText(mReleaseDate);
        ((TextView) rootView.findViewById(R.id.detail_summary)).setText(mPlot);


        ImageView imageView = (ImageView)rootView.findViewById(R.id.detail_poster);
        Picasso.with(getActivity()).load(mPoster).into(imageView);


        mVideoAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.movie_video_list,
                R.id.video_number,
                new ArrayList<String>());
        CustomizeListView vListView = (CustomizeListView) rootView.findViewById(R.id.movie_videos);
        vListView.setAdapter(mVideoAdapter);
        movieDBTask = new FetchMovieDBTask(getActivity(), null, mVideoAdapter, null);
        movieDBTask.execute("MovieVideo", mID);
        vListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(FetchMovieDBTask.videosurl.get(position)));
                startActivity(videoIntent);
            }
        });


        mReviewAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.movie_review,
                R.id.list_movie_review,
                new ArrayList<String>());
        CustomizeListView rListView = (CustomizeListView) rootView.findViewById(R.id.movie_reviews);
        rListView.setAdapter(mReviewAdapter);
        movieDBTask = new FetchMovieDBTask(getActivity(), null, mReviewAdapter, null);
        movieDBTask.execute("MovieReview", mID);
        rListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent reviewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(FetchMovieDBTask.reviewpages.get(position)));
                startActivity(reviewIntent);
            }
        });

        final ContentValues favoriteValues = new ContentValues();
        favoriteValues.put(FavoriteContract.FavoriteEntry.COLUMN_TMDB_ID, mID);
        favoriteValues.put(FavoriteContract.FavoriteEntry.COLUMN_POSTERS_URL, mPoster);
        favoriteValues.put(FavoriteContract.FavoriteEntry.COLUMN_RELEASEDATE, mReleaseDate);
        favoriteValues.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, mTitle);
        favoriteValues.put(FavoriteContract.FavoriteEntry.COLUMN_PLOT, mPlot);
        favoriteValues.put(FavoriteContract.FavoriteEntry.COLUMN_RATING, mRate);

        mFavorite = (ToggleButton) rootView.findViewById(R.id.toggle_button);
        Cursor c = getContext().getContentResolver().query(
                FavoriteContract.FavoriteEntry.CONTENT_URI,
                null,
                FavoriteContract.FavoriteEntry.COLUMN_TMDB_ID + "=" + mID,
                null,
                null);
        if (c.getCount() == 0) {} else mFavorite.setChecked(true);
        c.close();

        mFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String msgToast;
                if (isChecked) {
                    getContext().getContentResolver().insert(
                            FavoriteContract.FavoriteEntry.CONTENT_URI,
                            favoriteValues);
                    msgToast = mTitle + " Added into Favorite list";
//                    Log.d(LOG_TAG, "FetchWeatherTask Complete. Inserted");
                } else {
                    String[] idArgs = new String[] {mID};
                    getContext().getContentResolver().delete(
                            FavoriteContract.FavoriteEntry.CONTENT_URI,
                            FavoriteContract.FavoriteEntry.COLUMN_TMDB_ID + "=?",
                            idArgs);
                    msgToast = mTitle + " Deleted from Favorite list";
//                    Log.d(LOG_TAG, "FetchWeatherTask Complete. Deleted");
                    if (MainActivity.mTwoPane && FetchMovieDBTask.LIST_DB) {
                        getActivity().recreate();
                    }
                }
                Toast toast = Toast.makeText(getContext(), msgToast, Toast.LENGTH_LONG);
                toast.show();
            }
        });

        if (MainActivity.mTwoPane == false) {
            final ScrollView detailScroll = (ScrollView) rootView.findViewById(R.id.detail_scroll);
            detailScroll.postDelayed(new Runnable() {
                @Override
                public void run() {
                    detailScroll.fullScroll(ScrollView.FOCUS_UP);
                }
            }, 100);
        }
        return rootView;
    }
}