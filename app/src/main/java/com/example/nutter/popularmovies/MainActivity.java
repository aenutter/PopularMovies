package com.example.nutter.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
/**
 *
 * Upon launch, present the user with an grid arrangement of movie posters.
 * Allow your user to change sort order via a setting:
 * The sort order can be by most popular, or by highest-rated
 * Allow the user to tap on a movie poster and transition to a details screen
 * with additional information such as:
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
public class MainActivity extends ActionBarActivity
{
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieFragment())
                    .commit();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}