package com.example.nutter.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.List;
/**
 *
 * Extends the BaseAdapter and loads movie poster thumbnail images
 * (from The Movie DB) into a gridview using the Picasso API
 *
 * @author  Anthony Nutter
 * @version 1.0
 * @since   2015-11-24
 */
public class ImageAdapter extends BaseAdapter
{
    private Context mContext;
    private List<Movie> mMovies;
    private final String BASE_URL = "http://image.tmdb.org/t/p/w185";
    public ImageAdapter(Context c, List<Movie> movies)
    {
        mContext = c;
        mMovies = movies;
    }
    public int getCount()
    {
        return mMovies.size();
    }
    public Object getItem(int position)
    {
        return null;
    }
    public long getItemId(int position)
    {
        return 0;
    }
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView imageView;
        if (convertView == null)
        {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(185, 278));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else
        {
            imageView = (ImageView) convertView;
        }
        // The Picasso API uses background threading
        Picasso.with(mContext).load(BASE_URL + mMovies.get(position).getPosterPath()).into(imageView);
        return imageView;
    }
}
