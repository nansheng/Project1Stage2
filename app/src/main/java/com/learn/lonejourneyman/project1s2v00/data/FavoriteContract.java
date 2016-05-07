package com.learn.lonejourneyman.project1s2v00.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by lonejourneyman on 4/28/16.
 */
public class FavoriteContract {

    public static final String CONTENT_AUTHORITY = "com.learn.lonejourneyman.project1s2v00";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAVORITE = "favorite";

    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        public  static  Uri buildFavoriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_TMDB_ID = "tmdb_id";
        public static final String COLUMN_POSTERS_URL = "posters_url";
        public static final String COLUMN_RELEASEDATE = "release_date";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RATING = "rating";
    }
}
