package com.example.nutter.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
/**
 *
 *  Use a fragment to display a movie's details:
 *    original title
 *    movie poster image thumbnail
 *    A plot synopsis (called overview in the api)
 *    user rating (called vote_average in the api)
 *    release date
 *
 * @author  Anthony Nutter
 * @version 1.0
 * @since   2015-11-24
 */
public class DetailActivity extends ActionBarActivity
{
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }
    public static class DetailFragment extends Fragment
    {
        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
        private String mForecastStr;
        public DetailFragment()
        {
            setHasOptionsMenu(true);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            final String TMDB_TITLE = "original_title";
            final String TMDB_SYNOPSIS = "overview";
            final String TMDB_RATING = "vote_average";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_POSTER_PATH = "poster_path";
            TextView titleView = (TextView) rootView.findViewById(R.id.titleView);
            ImageView posterView = (ImageView) rootView.findViewById(R.id.posterView);
            TextView synopsisView = (TextView) rootView.findViewById(R.id.synopisView);
            TextView ratingView = (TextView) rootView.findViewById(R.id.ratingView);
            TextView releaseDataView = (TextView) rootView.findViewById(R.id.release_date_View);
            // The detail Activity called via intent.  Inspect the intent for forecast data.
            Intent intent = getActivity().getIntent();
            if (intent != null)
            {
                titleView.setText(intent.getStringExtra(TMDB_TITLE));
                Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185" + intent.getStringExtra(TMDB_POSTER_PATH)).into(posterView);
                synopsisView.setText(intent.getStringExtra(TMDB_SYNOPSIS));
                ratingView.setText(intent.getStringExtra(TMDB_RATING));
                releaseDataView.setText(intent.getStringExtra(TMDB_RELEASE_DATE));
            }
            return rootView;
        }
    }
}
