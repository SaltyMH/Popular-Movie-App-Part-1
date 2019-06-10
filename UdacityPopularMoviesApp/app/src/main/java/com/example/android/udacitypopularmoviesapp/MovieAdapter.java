package com.example.android.udacitypopularmoviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {

    /**
     * Constructs a new {@link MovieAdapter}.
     *
     * @param context  of the app
     * @param movies is the list of movies, which is the data source of the adapter
     */
    public MovieAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    /**
     * Returns a grid view that displays the movie posters.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new layout.
        View gridItemView = convertView;
        if (gridItemView == null) {
            gridItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Find the Movie at the given position in the list of Movies.
        Movie currentMovie = getItem(position);

        //Arrange the movie poster file patch and attach it to the list item image view.
        ImageView posterView = (ImageView) gridItemView.findViewById(R.id.poster_view);
        String posterPath = currentMovie.getPosterPath();
        String fullPosterPath = "http://image.tmdb.org/t/p/w342/" + posterPath;
        Picasso.get().load(fullPosterPath).into(posterView);

        // Return the grid view that is now showing the appropriate information.
        return gridItemView;
    }

}
