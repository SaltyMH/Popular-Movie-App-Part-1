package com.example.android.udacitypopularmoviesapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>> {

    public static final String LOG_TAG = MainActivity.class.getName();

    private MovieAdapter mAdapter;

    private static final String API_KEY = "PLACE YOUR TheMovieDatabase API KEY HERE";

    // TextView that is displayed when the list is empty.
    private TextView mEmptyStateTextView;

    // Spinner to be displayed when running Loader.
    private ProgressBar mLoadingSpinner;

    // Initial URL when loading app.
    private String TheMovieDatabaseApiRequestUrl =
            "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" +
                    API_KEY;

    private static final int ARTICLE_LOADER_ID = 1;

    // The next three methods are REQUIRED when implementing Loaders:  onCreateLoader,
    // onLoadFinished, and onLoaderReset.
    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {

        // Find spinner and make it visible while loader is running.
        mLoadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        mLoadingSpinner.setVisibility(View.VISIBLE);

        // Create a new loader for the given URL
        return new MovieLoader(this, TheMovieDatabaseApiRequestUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {

        // Find spinner and hide it once loader his finished task.
        mLoadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        mLoadingSpinner.setVisibility(View.GONE);

        // Clear the adapter of previous movie data.
        mAdapter.clear();

        // If there is a valid list of Movies, then add them to the adapter's data set.
        // This will trigger the ListView to update.
        if (movies != null && !movies.isEmpty()) {
            mAdapter.addAll(movies);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();

        mEmptyStateTextView.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // To see if the device has a network connection.
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        }
        // Otherwise, display error.
        else {
            // First, hide loading indicator so error message will be visible.
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message.
            mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        // Find a reference to the {@link ListView} in the layout.
        GridView movieGridView = (GridView) findViewById(R.id.gridList);

        // Create a new adapter that takes an empty list of movies as input.
        mAdapter = new MovieAdapter(this, new ArrayList<Movie>());

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        movieGridView.setEmptyView(mEmptyStateTextView);

        // Set the adapter on the {@link ListView} so the list can be populated in the user
        // interface.
        movieGridView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to the DetailActivity.
        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // Find the movie that was clicked on, and extract the information from it.
                Movie movie = mAdapter.getItem(position);
                String posterPath = movie.getPosterPath();
                String fullPosterPath = "http://image.tmdb.org/t/p/w500/" + posterPath;
                String movieTitle = movie.getTitle();
                String movieReleaseDate = movie.getReleaseDate();
                String movieUserRating = movie.getUserRating();
                String movieOverview = movie.getOverview();

                // Send the intent, passing through the information as extras.
                launchDetailActivity(position, fullPosterPath, movieTitle, movieReleaseDate,
                        movieUserRating, movieOverview);
            }
        });
    }

    // Method to send the necessary movie information to DetailActivity as extras via intent.
    private void launchDetailActivity(int position, String poster, String title, String date,
                                      String rating, String overview) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_POSITION, position);
        intent.putExtra(DetailActivity.EXTRA_POSTER, poster);
        intent.putExtra(DetailActivity.EXTRA_TITLE, title);
        intent.putExtra(DetailActivity.EXTRA_DATE, date);
        intent.putExtra(DetailActivity.EXTRA_RATING, rating);
        intent.putExtra(DetailActivity.EXTRA_OVERVIEW, overview);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.movie_sort, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_popular) {
            TheMovieDatabaseApiRequestUrl =
                    "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" +
                            API_KEY;
            runSearch();
            return true;
        }

        if (id == R.id.sort_date) {
            TheMovieDatabaseApiRequestUrl =
                    "http://api.themoviedb.org/3/discover/movie/?vote_count.gte=100&" +
                            "sort_by=vote_average.desc&api_key=" +
                            API_KEY;
            runSearch();
            return true;
        }

        if (id == R.id.sort_alphabetical) {
            TheMovieDatabaseApiRequestUrl =
                    "http://api.themoviedb.org/3/discover/movie?with_genres=27&" +
                            "sort_by=popularity.desc&api_key=" +
                            API_KEY;
            runSearch();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void runSearch() {

        getLoaderManager().restartLoader(1, null, this);

        // To see if the device has a network connection.
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        }
        // Otherwise, display error.
        else {
            // First, hide loading indicator so error message will be visible.
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message.
            mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

}
