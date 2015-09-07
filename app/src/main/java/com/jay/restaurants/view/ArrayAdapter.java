package com.jay.restaurants.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.jay.restaurant.R;
import com.jay.restaurants.model.RequestQueueImageLoader;
import com.jay.restaurants.model.Restaurant;

import java.util.Collections;
import java.util.List;



/**
 * This Adapter is used to display the list of restaurants and AsyncTask that downloads the logos.
 */
public class ArrayAdapter extends android.widget.ArrayAdapter {

    public static final String LOG_TAG = ArrayAdapter.class.getSimpleName();
    private final Context context;
    private List<Restaurant> restaurants;

    public ArrayAdapter(Context context, List<Restaurant> restaurants) {
        super(context, R.layout.restaurant_lisview_item, restaurants);
        this.context = context;
        this.restaurants = restaurants;
    }

    public void addRestaurants(List<Restaurant> values) {
        for (Restaurant r : values) {
            restaurants.add(r);
        }
    }

    public void sort(boolean ascending) {
        Collections.sort(restaurants);
        if (!ascending) {
            Collections.reverse(restaurants);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return restaurants.size();
    }

    @Override
    public Restaurant getItem(int position) {
        return restaurants.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RestaurantViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.restaurant_lisview_item, parent, false);
            viewHolder = new RestaurantViewHolder();
            viewHolder.restLogo = (NetworkImageView) convertView.findViewById(R.id.restaurant_logo);
            viewHolder.name = (TextView) convertView.findViewById(R.id.restaurant_name);
            viewHolder.cuisineStyle = (TextView) convertView.findViewById(R.id.restaurant_cuisine);
            viewHolder.address = (TextView) convertView.findViewById(R.id.restaurant_address);
            viewHolder.rating = (RatingBar) convertView.findViewById(R.id.restaurant_rating);
            viewHolder.ratingCount = (TextView) convertView.findViewById(R.id.restaurant_rating_count);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (RestaurantViewHolder) convertView.getTag();
        }
        Restaurant currRestaurant = restaurants.get(position);

        viewHolder.name.setText(currRestaurant.name);
        viewHolder.cuisineStyle.setText(currRestaurant.getCuisineTypeString());
        viewHolder.address.setText(currRestaurant.address);
        viewHolder.rating.setRating(currRestaurant.rating);
        String strCountFormat = context.getResources().getString(R.string.restaurant_list_item_rating_count);
        String strRatingCount = String.format(strCountFormat, currRestaurant.ratingCount);
        viewHolder.ratingCount.setText(strRatingCount);
        ImageLoader mImageLoader = RequestQueueImageLoader.getInstance(context).getImageLoader();
        viewHolder.restLogo.setImageUrl("http://je-cdn-production.s3-eu-west-1.amazonaws.com/uk/images/restaurants/"
                + String.valueOf(currRestaurant.id) + ".gif", mImageLoader);
        return convertView;
    }

    static class RestaurantViewHolder {
        NetworkImageView restLogo;
        TextView name;
        TextView cuisineStyle;
        TextView address;
        RatingBar rating;
        TextView ratingCount;
        int position;
    }

}

