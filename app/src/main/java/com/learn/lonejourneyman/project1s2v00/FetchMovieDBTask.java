package com.learn.lonejourneyman.project1s2v00;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.learn.lonejourneyman.project1s2v00.data.FavoriteContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lonejourneyman on 4/18/16.
 */
public class FetchMovieDBTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchMovieDBTask.class.getSimpleName();

    //w92/w154/w185/w342/w500/w780/original//
    final String POSTERS_BASE_URL = "http://image.tmdb.org/t/p/w185";
    final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";

    public static Boolean LIST_DB;

    public static List<String> posters = new ArrayList<>();
    public static List<String> releasedates = new ArrayList<>();
    public static List<String> titles = new ArrayList<>();
    public static List<String> plots = new ArrayList<>();
    public static List<String> rates = new ArrayList<>();

    public static List<String> movieid = new ArrayList<>();
    public static List<String> videos = new ArrayList<>();
    public static List<String> videosurl = new ArrayList<>();
    public static List<String> reviews = new ArrayList<>();
    public static List<String> authors = new ArrayList<>();
    public static List<String> reviewpages = new ArrayList<>();
    public static List<String> reviewdisplay = new ArrayList<>();

    private Context mContext;
    private GridView mGridView;
    private ArrayAdapter<String> mAdapter;
    private MainActivityFragment.FragmentCallback mFragmentCallback;

    public FetchMovieDBTask(Context context, GridView gridView, ArrayAdapter<String> adapter, MainActivityFragment.FragmentCallback fragmentCallback) {
        mContext = context;
        mGridView = gridView;
        mAdapter = adapter;
        mFragmentCallback = fragmentCallback;
    }

    @Override
    protected String[] doInBackground(String... params) {

        final String LOG_TAG = FetchMovieDBTask.class.getSimpleName();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String api = mContext.getString(R.string.moviedb_api_key);
        String movieDBJsonStr = null;
        String dataType = params[0];
        String dataSort = params[1];

        switch (dataSort) {
            case "favorites":
                Cursor movieCursor = mContext.getContentResolver().query(FavoriteContract.FavoriteEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                final int count = movieCursor.getCount();
                int numCursor = 0;
                if (movieCursor != null) {
                    LIST_DB = true;
                    String[] resultCur = new String[movieCursor.getCount() + 1];
                    while (movieCursor.moveToNext()) {
                        resultCur[numCursor] = movieCursor.getString(2) + "~" +
                                movieCursor.getString(3) + "~" +
                                movieCursor.getString(4) + "~" +
                                movieCursor.getString(5) + "~" +
                                movieCursor.getString(6) + "~" +
                                movieCursor.getString(1);
                        numCursor = numCursor + 1;
                    }
                    resultCur[movieCursor.getCount()] = dataType;
                    return resultCur;
                }
                break;
            default:
                try {
                    String MOVIEBD_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                    final String SORT_PARAM = "sort_by";
                    final String API_KEY = "api_key";
                    Uri builtUri = Uri.parse(MOVIEBD_BASE_URL).buildUpon()
                            .appendQueryParameter(SORT_PARAM, dataSort)
                            .appendQueryParameter(API_KEY, api)
                            .build();
                    switch (dataType) {
                        case "MovieVideo":
                            MOVIEBD_BASE_URL = "http://api.themoviedb.org/3/movie/" + dataSort + "/videos?";
                            builtUri = Uri.parse(MOVIEBD_BASE_URL).buildUpon().appendQueryParameter(API_KEY, api).build();
                            break;
                        case "MovieReview":
                            MOVIEBD_BASE_URL = "http://api.themoviedb.org/3/movie/" + dataSort + "/reviews?";
                            builtUri = Uri.parse(MOVIEBD_BASE_URL).buildUpon().appendQueryParameter(API_KEY, api).build();
                            break;
                        default:
                            LIST_DB = false;
                            break;
                    }

                    URL url = new URL(builtUri.toString());

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }

                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }

                    movieDBJsonStr = buffer.toString();
                    Log.v(LOG_TAG, "Movie DB JSON String: " + movieDBJsonStr);

                } catch (IOException e) {
                    if (dataType == "MovieDB") Log.e(LOG_TAG, "Error", e);
                    return null;

                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e("PlaceholderFragment", "Error closing stream", e);
                        }
                    }
                }

                try {
                    return getMovieDataFromJSON(dataType, movieDBJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                break;
        }
        return null;
    }

    private String[] getMovieDataFromJSON(String movieDataType, String movieDBJsonStr) throws JSONException {

        final String LOG_TAG = FetchMovieDBTask.class.getSimpleName();

        final String OWM_RESULTS = "results";

        final String OWM_POSTER_PATH = "poster_path";
        final String OWM_PLOT_SYNOPSIS = "overview";
        final String OWM_RELEASE_DATE = "release_date";
        final String OWM_TITLE = "title";
        final String OWM_USER_RATINGS = "vote_average";
        final String OWM_MOVIE_ID = "id";

        final String OWM_VID_KEY = "key";

        final String OWM_REV_AUTHOR = "author";
        final String OWM_REV_CONTENT = "content";
        final String OWM_REV_URL = "url";

        JSONObject movieJson = new JSONObject(movieDBJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(OWM_RESULTS);

        String[] resultStrs = new String[movieArray.length() + 1];

        for (int i = 0; i < movieArray.length(); i++ ) {

            JSONObject movieData = movieArray.getJSONObject(i);

            switch(movieDataType) {
                case "MovieVideo":
                    String videoKey = movieData.getString(OWM_VID_KEY);
                    resultStrs[i] = videoKey;
                    break;
                case "MovieReview":
                    String reviewAuthor = movieData.getString(OWM_REV_AUTHOR);
                    String reviewContent =
                            movieData.getString(OWM_REV_CONTENT).length() > 90 ?
                            movieData.getString(OWM_REV_CONTENT).substring(0, 90).replaceAll("[^ -~]","") + "..." :
                            movieData.getString(OWM_REV_CONTENT).replaceAll("[^ -~]","");
                    String reviewUrl = movieData.getString(OWM_REV_URL);
                    resultStrs[i] = reviewAuthor + "~" + reviewContent + "~" + reviewUrl;
                    break;
                default:
                    String posterPath = movieData.getString(OWM_POSTER_PATH);
                    String releaseDate = movieData.getString(OWM_RELEASE_DATE);
                    String title = movieData.getString(OWM_TITLE);
                    String synopsis = movieData.getString(OWM_PLOT_SYNOPSIS);
                    String ratings = movieData.getString(OWM_USER_RATINGS);
                    String movieID = movieData.getString(OWM_MOVIE_ID);
                    resultStrs[i] = posterPath + "~"
                            + releaseDate + "~"
                            + title + "~"
                            + synopsis + "~"
                            + ratings + "~"
                            + movieID;
                    break;
            }
        }
        resultStrs[movieArray.length()] = movieDataType;
        return resultStrs;
    }

    @Override
    protected void onPostExecute(String[] result) {
        if (result != null) {
            String resultDataType = result[result.length - 1];
            String[] movieDataSplit;
            switch(resultDataType) {
                case "MovieVideo":
                    videos.clear();
                    videosurl.clear();
                    for (int i = 0; i < result.length - 1; i++) {
                        movieDataSplit = result[i].split("~");
                        videos.add("Trailer " + (i + 1));
                        videosurl.add(YOUTUBE_BASE_URL + movieDataSplit[0]);
                    }
                    if (videos.size() > 0) {
                        mAdapter.clear();
                        mAdapter.addAll(videos);
                    }
                    break;
                case "MovieReview":
                    reviews.clear();
                    authors.clear();
                    reviewpages.clear();
                    reviewdisplay.clear();
                    for (int i = 0; i < result.length - 1; i++) {
                        movieDataSplit = result[i].split("~");
                        authors.add(movieDataSplit[0]);
                        reviews.add(movieDataSplit[1]);
                        reviewpages.add(movieDataSplit[2]);
                        if (movieDataSplit[1].trim().length() > 0)
                        reviewdisplay.add(movieDataSplit[1] + "\nBy: " + movieDataSplit[0]);
                    }
                    if (reviews.size() > 0) {
                        mAdapter.clear();
                        mAdapter.addAll(reviewdisplay);
                    }
                    break;
                default:
                    posters.clear();
                    releasedates.clear();
                    titles.clear();
                    plots.clear();
                    rates.clear();
                    movieid.clear();
                    for (int i = 0; i < result.length - 1; i++) {
                        movieDataSplit = result[i].split("~");
                        posters.add(POSTERS_BASE_URL + movieDataSplit[0]);
                        releasedates.add(movieDataSplit[1]);
                        titles.add(movieDataSplit[2]);
                        plots.add(movieDataSplit[3]);
                        rates.add(movieDataSplit[4]);
                        movieid.add(movieDataSplit[5]);
                    }

                    ImageArrayAdapter mAdapter = new ImageArrayAdapter(mContext, posters);
                    mGridView.setAdapter(mAdapter);

                    if (MainActivity.mTwoPane == true && posters.size() > 0) {
                        mFragmentCallback.onTaskDone();
                    }

                    break;
            }
        }
    }
}
