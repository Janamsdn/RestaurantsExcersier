package com.jay.restaurants.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jay.restaurants.listener.ResponseListener;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by j .
 */
public class HttpAsyncTaskGetRestaurants extends AsyncTask<String, Void, Void> {
    private List<Restaurant> restaurantList;
    private boolean isRunning = false;
    private ResponseListener listener;
    private Context mcontxt;
    private ProgressDialog pd = null;
    public static final String TAG = HttpAsyncTaskGetRestaurants.class.getSimpleName();

    public HttpAsyncTaskGetRestaurants(Context context, ResponseListener listener) {
        this.mcontxt=context;
        this.listener=listener;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(mcontxt);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Retrieving restaurants ....");
        pd.setCancelable(false);
        pd.show();
        isRunning = true;

    }

    @Override
    protected Void doInBackground(String... params) {
        String postcode = params[0];
        Log.d(TAG, "GetRestaurants async task: getting restaurants for [" + postcode + "]");
        postcode = postcode.replace(" ", "");
        String url = RestaurantConstants.URL + postcode;
        Log.d(TAG, "GetRestaurants async task: sending  url [" + url + "]");

        StringBuilder stringBuilder = new StringBuilder();
        // create HttpClient
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        // input header
        httpGet.addHeader(RestaurantConstants.KEY_ACCEPT, RestaurantConstants.VALUE_ACCEPT);
        httpGet.addHeader(RestaurantConstants.KEY_ACCEPT_TENANT, RestaurantConstants.VALUE_ACCEPT_TENANT);
        httpGet.addHeader(RestaurantConstants.KEY_ACCEPT_LANGUAGE,RestaurantConstants.VALUE_ACCEPT_LANGUAGE);
        httpGet.addHeader(RestaurantConstants.KEY_ACCEPT_CHARSET,RestaurantConstants.VALUE_ACCEPT_CHARSET);
        httpGet.addHeader(RestaurantConstants.KEY_AUTHORIZATION,RestaurantConstants.VALUE_AUTHORIZATION);

        try {
            // make GET request to the given URL
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == RestaurantConstants.HTTP_STATUS_CODE) {
                org.apache.http.HttpEntity entity = response.getEntity();
                // receive response as inputStream
                InputStream inputStream = entity.getContent();
                // convert stream to string
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();

                parseRestaurantJson(stringBuilder.toString());
            } else {
                Log.e(TAG, "GetRestaurants async task: fail retrieving list with code [" + statusCode + "] " + statusLine.toString());
            }
        } catch (Exception e) {
            Log.e(TAG, "GetRestaurants async task: exception retrieving list: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Parse the retrieved JSON list of restaurants.
     * This function publishes the retrieved data every 5 restaurants.
     *
     * @param strJson the JSON string containing the restaurants.
     */
    private void parseRestaurantJson(final String strJson) {
        JSONObject jsonResponse;
        restaurantList = new ArrayList<Restaurant>();
        try {
            jsonResponse = new JSONObject(strJson);
            JSONArray jsonMainNode = jsonResponse.optJSONArray(RestaurantConstants.RESTAURANTS);

            int lengthJsonArr = jsonMainNode.length();
            for (int restNum = 0; restNum < lengthJsonArr; restNum++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(restNum);

                final Restaurant restaurant = new Restaurant();
                restaurant.id = Integer.parseInt(jsonChildNode.optString(RestaurantConstants.ID));
                restaurant.name = jsonChildNode.optString(RestaurantConstants.NAME);
                restaurant.address = jsonChildNode.optString(RestaurantConstants.ADDRESS);
                        restaurant.rating = Float.parseFloat(jsonChildNode.optString(RestaurantConstants.RATING));
                                restaurant.ratingCount = Integer.parseInt(jsonChildNode.optString(RestaurantConstants.RATING_COUNT));

                                        JSONArray jsonCuisineArray = jsonChildNode.optJSONArray(RestaurantConstants.CUISINE_TYPES);
                int lengthCuisineArr = jsonCuisineArray.length();
                for (int cuiNum = 0; cuiNum < lengthCuisineArr; cuiNum++) {
                    JSONObject jsonCuisineNode = jsonCuisineArray.getJSONObject(cuiNum);

                    CuisineStyle cuisine = new CuisineStyle();
                    cuisine.id = Integer.parseInt(jsonCuisineNode.optString(RestaurantConstants.ID));
                            cuisine.name = jsonCuisineNode.optString(RestaurantConstants.NAME);
                    cuisine.seoName = jsonCuisineNode.optString(RestaurantConstants.SEO_NAME);

                    restaurant.cuisineStyle.add(cuisine);
                }

                restaurantList.add(restaurant);
                if (restaurantList.size() > 4) {
                    publishProgress();
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "parseRestaurantJson: exception parsing json : " + e.getMessage(), e);
        }
    }



    /**
     * publish final progress.
     * this function adds the parsed restaurants to the ListView's adaptor
     * and calls notifyDataSetChanged so that the ListView will update it's display.
     * <p/>
     * This function also sets the isRunning field to false to indicate that it has finished
     * processing.
     *
     * @param values - void
     */
    @Override
    protected void onPostExecute(Void values) {
        Log.d(TAG, "Finished retrieving restaurant list");
        isRunning = false;
        if (pd != null) {
            pd.dismiss();
        }
        this.listener.callbackRestaurantResponse(restaurantList);

    }

    /**
     * sets the isRunning field to false to indicate that it has finished processing.
     */
    @Override
    protected void onCancelled() {
        super.onCancelled();
        isRunning = false;
    }

    /**
     *
     * @return true if the task is running, false if it has finished (or been canceled)
     */
    public boolean isRunning() {
        return isRunning;
    }
}
