package com.example.nutter.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
 * Encapsulates fetching the movies from The Movie DB and displays them
 * in a  GridView layout using the ImageAdapter class
 *
 * @author  Anthony Nutter
 * @version 1.0
 * @since   2015-11-24
 */
public class MovieFragment extends Fragment
{
    // a list of Movies passed to the image adapter class
    private List<Movie> movieArray = new ArrayList<>();
    // the adapter that displays the movies in a gridview
    private ImageAdapter adapter;
    //constants
    private final String TMDB_TITLE = "original_title";
    private final String TMDB_SYNOPSIS = "overview";
    private final String TMDB_RATING = "vote_average";
    private final String TMDB_RELEASE_DATE = "release_date";
    private final String TMDB_POSTER_PATH = "poster_path";

    //constructor
    public MovieFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the GridView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        adapter = new ImageAdapter(getContext(), movieArray);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                // get the movie details and pass them to the detail activity for display
                // in a seperate fragment
                String title = movieArray.get(position).getTitle();
                String posterPath = movieArray.get(position).getPosterPath();
                String synopsis = movieArray.get(position).getSynopsis();
                String rating = movieArray.get(position).getRating();
                String releaseDate = movieArray.get(position).getReleaseDate();

                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(TMDB_TITLE, title)
                        .putExtra(TMDB_POSTER_PATH, posterPath)
                        .putExtra(TMDB_SYNOPSIS, synopsis)
                        .putExtra(TMDB_RATING, rating)
                        .putExtra(TMDB_RELEASE_DATE, releaseDate);
                startActivity(intent);
            }
        });
        return rootView;
    }

    private void updateMoviews()
    {
        FetchMovieTask movieTask = new FetchMovieTask();
        // determine if the movies should be sorted by popularity or rating
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortParam = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
        // call the Asynch Task to fetch the movies
        movieTask.execute(sortParam);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMoviews();
    }

    public class FetchMovieTask extends AsyncTask<String, Void, List<Movie>>
    {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        /**
        * Take the String representing all Movie details in JSON Format and
        * pull out the data we need to construct the Strings needed for the wireframes.
        *
        * Fortunately parsing is easy:  constructor takes the JSON string and converts it
        * into an Object hierarchy for us.
        * @throws JSONException On input error.
        * @see JSONException
        */
        private List<Movie> getMovieDataFromJson(String movieJsonStr)
                throws JSONException
        {
            // temporary list of Movies
            List<Movie> movieList = new ArrayList<>();
            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS = "results";
            JSONObject movieJsonObjectStr = new JSONObject(movieJsonStr);
            JSONArray movieJsonArray = movieJsonObjectStr.getJSONArray(TMDB_RESULTS);

            // The Movie DB API returns the details for 20 movies
            String originalTitle;
            String synopsis;
            String releaseDate;
            String posterPath;
            String rating;
            JSONObject movieJsonObject;
            // process the Json array of movies and extract details for each Movie
            for(int i = 0; i < movieJsonArray.length(); i++)
            {
                // Get the JSON object representing one Movie
                movieJsonObject = movieJsonArray.getJSONObject(i);
                // extract the Movie: title, synopsis, release date, thumbnail and rating
                originalTitle = movieJsonObject.getString(TMDB_TITLE);
                synopsis = movieJsonObject.getString(TMDB_SYNOPSIS);
                releaseDate = movieJsonObject.getString(TMDB_RELEASE_DATE);
                posterPath = movieJsonObject.getString(TMDB_POSTER_PATH);
                rating = movieJsonObject.getString(TMDB_RATING);
                Movie movie = new Movie(originalTitle, synopsis, releaseDate, posterPath, rating);
                movieList.add(movie);
                //Log.e(LOG_TAG, "Movie #:  " + i + "  Relase Date:  " + movie.getReleaseDate());
            }
            return movieList;

        }
        /**
         * Query "The Movie DB" using an http reuest like:
         * "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc"
         * In the http request, the "sort_by" param is stored in the pref_general.xml
         * preference file and passed here in the params[0] parameter
         * The JSON result is read into a buffer
         */
        @Override
        protected List<Movie> doInBackground(String... params)
        {

            // If there's no sort_by, there's nothing to look up.  Verify size of params.
            if (params.length == 0)
            {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try
            {
                // Construct the URL for the The Movie DB query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://docs.themoviedb.apiary.io/#reference/configuration/configuration/get?console=1
                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String SORT_ORDER = ".desc";
                final String APPID_PARAM = "api_key";

                //create the query string
                Uri builtUri = Uri.parse(FORECAST_BASE_URL + SORT_PARAM + "=" + params[0] +
                        SORT_ORDER + "&" + APPID_PARAM + "=" + BuildConfig.THE_MOVIE_DB_API_KEY)
                        .buildUpon().build();
                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null)
                {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null)
                {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0)
                {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
            } catch (IOException e)
            {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the Movie data, there's no point in attemping
                // to parse it.
                return null;
            } finally
            {
                if (urlConnection != null)
                {
                    urlConnection.disconnect();
                }
                if (reader != null)
                {
                    try
                    {
                        reader.close();
                    } catch (final IOException e)
                    {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try
            {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e)
            {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the movie data.
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> result)
        {
            if (result != null)
            {
                // New data is back from the server.  Hooray!
                movieArray.clear();

                // add all the Movies from the result param and
                // notify the adapter of the changed data
                for(Movie dayForecastStr : result)
                {
                    movieArray.add(dayForecastStr);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}
