package com.example.android.udacitypopularmoviesapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {
    // Create a private constructor because no one should ever create a {@link QueryUtils} object.
    // This class is only meant to hold static variables and methods, which can be accessed
    // directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
    private QueryUtils() {
    }

    // Constants.
    private static final int READ_TIMEOUT = 1000;
    private static final int CONNECTION_TIMEOUT = 1500;
    private static final int SLEEP_MILLISECONDS = 2000;

    // Tag for the log messages.
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    // Query the TheMovieDatabase API and extract a list of Movies.
    public static List<Movie> fetchMovieData(String requestUrl) {

        //Simulating a slow network speed to test loading spinner.
        try {
            Thread.sleep(SLEEP_MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back.
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a Movie object.
        List<Movie> movies = extractMovies(jsonResponse);

        // Return the list of Movies.
        return movies;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL.", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT /* milliseconds */);
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200) then read the input stream and
            // parse the response.  The constant, HTTP_OK, can be used for the response code.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                    Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    // Pull movie info from JSON query and place into movie objects, which then go into an arrayList.
    private static List<Movie> extractMovies(String movieJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding Movies to
        List<Movie> movies = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(movieJSON);
            // Extract the JSONArray associated with the key called "results",
            // which represents a list of items (or movies).
            JSONArray movieArray = baseJsonResponse.getJSONArray("results");

            // For each movie in the movieArray, create a Movie object
            for (int i = 0; i < movieArray.length(); i++) {
                // Get a single article at position i within the list of Articles
                JSONObject currentMovie = movieArray.getJSONObject(i);
                // Extract the value for the key called "webTitle"
                String movieTitle = currentMovie.getString("title");
                // Extract the value for the key called "webPublicationDate"
                String moviePosterPath = currentMovie.optString("poster_path");
                // Extract the value for the key called "sectionName"
                String movieUserRating = currentMovie.optString("vote_average");
                // Extract the value for the key called "webUrl"
                String movieOverview = currentMovie.optString("overview");
                // Extract the value for the key called "release_date"
                String movieReleaseDate = currentMovie.optString("release_date");

                // Create a new Movie object with variables from the JSON response.
                Movie movie = new Movie(movieTitle,
                        moviePosterPath,
                        movieReleaseDate,
                        movieOverview,
                        movieUserRating);

                // Add the new Movie to the list of Movies.
                movies.add(movie);

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the movie JSON results", e);
        }

        //Return the list of movies.
        return movies;
    }

}
