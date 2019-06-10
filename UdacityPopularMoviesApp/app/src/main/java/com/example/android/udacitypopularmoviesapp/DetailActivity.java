package com.example.android.udacitypopularmoviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;

    public static final String EXTRA_POSTER = "extraPoster";
    public static final String EXTRA_TITLE = "extraTitle";
    public static final String EXTRA_DATE = "extraDate";
    public static final String EXTRA_RATING = "extraRating";
    public static final String EXTRA_OVERVIEW = "extraOverview";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Find layout views to place values into later.
        ImageView moviePosterIv = findViewById(R.id.detail_image_view);
        TextView titleTv = findViewById(R.id.detail_title_view);
        TextView releaseDateTv = findViewById(R.id.detail_release_date_view);
        TextView userRatingTv = findViewById(R.id.detail_user_rating_view);
        TextView overviewTv = findViewById(R.id.detail_overview_view);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            closeOnError();
            return;
        }

        // Set title to that of the chosen movie.
        setTitle(intent.getStringExtra(EXTRA_TITLE));

        // Set poster image.
        Picasso.get().load(intent.getStringExtra(EXTRA_POSTER)).into(moviePosterIv);

        // Set text of movie title.
        titleTv.setText(intent.getStringExtra(EXTRA_TITLE));

        // Set text of release date.
        releaseDateTv.setText(intent.getStringExtra(EXTRA_DATE));

        // Set user rating score.
        userRatingTv.setText(intent.getStringExtra(EXTRA_RATING));

        // Set overview text for movie.
        overviewTv.setText(intent.getStringExtra(EXTRA_OVERVIEW));

    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

}
