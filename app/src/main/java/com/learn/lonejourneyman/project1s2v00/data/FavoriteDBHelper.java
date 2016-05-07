package com.learn.lonejourneyman.project1s2v00.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lonejourneyman on 4/28/16.
 */
public class FavoriteDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "favorite.db";

    public FavoriteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " +
                FavoriteContract.FavoriteEntry.TABLE_NAME + " (" +
                FavoriteContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY," +
                FavoriteContract.FavoriteEntry.COLUMN_TMDB_ID + " TEXT UNIQUE NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_POSTERS_URL + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_RELEASEDATE + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_PLOT + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_RATING + " TEXT NOT NULL " +
                " );";
        db.execSQL(SQL_CREATE_FAVORITE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.FavoriteEntry.TABLE_NAME);
        onCreate(db);

    }
}
