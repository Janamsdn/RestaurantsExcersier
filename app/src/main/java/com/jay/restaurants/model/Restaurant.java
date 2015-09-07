package com.jay.restaurants.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J
 * This class holds restaurant info.
 */
public class Restaurant implements Comparable<Restaurant> {
    public int id;
    public String name;
    public String address;
    public String postcode;
    public String city;
    public String url;
    public float rating;
    public int ratingCount;
    public List<CuisineStyle> cuisineStyle = new ArrayList<CuisineStyle>();


    /**
     * compare rating with another restaurant
     * @param restaurant
     * @return 0 if the ratings and ratings count are equal,
     * 1 if this restaurant's rating is higher than the other restaurant's (or  ratings are equal but rating count is higher)
     * -1 if this restaurant's rating is lower than the other restaurant's (or  ratings are equal but rating count is lower)
     */
    @Override
    public int compareTo(final Restaurant restaurant) {
        if (restaurant == null) {
            return 1;
        }
        if (this.rating > restaurant.rating) {
            return 1;
        } else if (this.rating == restaurant.rating) {
            if (this.ratingCount > restaurant.ratingCount) {
                return 1;
            } else if (this.ratingCount == restaurant.ratingCount) {
                return 0;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }
    /**
     * get the cuisineStyle types as a comma separated string
     * @return a comma separated string of the cuisine types
     */
    public String getCuisineTypeString() {
        StringBuilder sb = new StringBuilder();
        for (CuisineStyle c : cuisineStyle) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(c.name);
        }
        return sb.toString();
    }

}
