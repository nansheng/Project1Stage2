package com.learn.lonejourneyman.project1s2v00;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lonejourneyman on 4/15/16.
 */
public class ReviewArrayAdapter extends ArrayAdapter<String> {

    public ReviewArrayAdapter(Context context, List<String> reviewURLs) {
        super(context, R.layout.movie_review, reviewURLs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.movie_review, parent, false);

        if (customView == null) customView = inflater.inflate(R.layout.movie_review, null);

        String oneReview = getItem(position);
        TextView textReview = (TextView) customView.findViewById(R.id.list_movie_review);

        return customView;
    }
}
