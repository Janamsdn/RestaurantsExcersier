package com.jay.restaurants.listener;

import java.util.List;


import com.jay.restaurants.model.Restaurant;

//interface to inform to UI from asyntask
public interface ResponseListener {
	// you can define any parameter as per your requirement
	public void callbackRestaurantResponse(List<Restaurant> restaurantList);
	public void callbackPostCodeResponse(String postCode);
}
