package com.jay.restaurants.view;

import android.app.ListActivity;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.jay.restaurant.R;
import com.jay.restaurants.listener.ResponseListener;
import com.jay.restaurants.model.AsyncTaskGetPostCode;
import com.jay.restaurants.model.HttpAsyncTaskGetRestaurants;
import com.jay.restaurants.model.Restaurant;
import java.util.ArrayList;
import java.util.List;


public class RestaurantActivity extends ListActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, ResponseListener {

    public static final String TAG = RestaurantActivity.class.getSimpleName();
      /*
       * Define a request code to send to Google Play services
       * This code is returned in Activity.onActivityResult
       */
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private LocationClient locationClient;
    private ArrayAdapter restaurantArrayAdapter;
    private EditText txtPostcode;
    private HttpAsyncTaskGetRestaurants getRestaurants = null;
    private Button btnFindPostcode = null;


    private void UpdateCurrentLocation() {
        Location currentLocation = locationClient.getLastLocation();
        AsyncTaskGetPostCode postCodeTask = new AsyncTaskGetPostCode(this,RestaurantActivity.this);
        postCodeTask.execute(new Double[]{currentLocation.getLatitude(), currentLocation.getLongitude()});
    }

    private void changePostcodeTextField(final String pPostcode) {
        txtPostcode.setText(pPostcode);
    }

    private void onPostcodeChange() {
        if (getRestaurants == null) {
            btnFindPostcode.setEnabled(false);
            restaurantArrayAdapter = new ArrayAdapter(this, new ArrayList<Restaurant>());
            getListView().setAdapter(restaurantArrayAdapter);
            getRestaurants = new HttpAsyncTaskGetRestaurants(this,RestaurantActivity.this);
            getRestaurants.execute(new String[]{txtPostcode.getText().toString()});
        } else {
            //stop current async task and start again
            getRestaurants.cancel(true);
            getRestaurants = null;
            onPostcodeChange();
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        // array adapter
        restaurantArrayAdapter = new ArrayAdapter(this, new ArrayList<Restaurant>());
        getListView().setAdapter(restaurantArrayAdapter);
        // hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // call location
        locationClient = new LocationClient(this, this, this);
        // UI declaration
        txtPostcode = (EditText) findViewById(R.id.postcode);
        btnFindPostcode = (Button) findViewById(R.id.postcode_find);
        btnFindPostcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostcodeChange();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // connect
        locationClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //  this adds items to the action bar
        getMenuInflater().inflate(R.menu.menu_restaurant, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.gps_location) {
            UpdateCurrentLocation();
            return true;
        } else if (id == R.id.sort_asc) {
            restaurantArrayAdapter.sort(true);
            return true;
        } else if (id == R.id.sort_desc) {
            restaurantArrayAdapter.sort(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void callbackRestaurantResponse(List<Restaurant> restaurantList) {
        Log.d(TAG, "Finished retrieving restaurant data");
            restaurantArrayAdapter.addRestaurants(restaurantList);
            restaurantArrayAdapter.notifyDataSetChanged();
            btnFindPostcode.setEnabled(true);

    }

    @Override
    public void callbackPostCodeResponse(String postCode) {
        if(postCode==null){
            Toast.makeText(this, "Not a valid PostCode", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "Postcode is [" + postCode + "]");
        changePostcodeTextField(postCode);
        onPostcodeChange();
    }

       /*
       * request the current location or start for  updates
       */
    @Override
    public void onConnected(final Bundle dataBundle) {
        // Display the connection status
        Toast.makeText(this, "Current location connected", Toast.LENGTH_SHORT).show();
        UpdateCurrentLocation();
    }

    /*
     *  if the connection to the location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Current location disconnected. Please reconnect.", Toast.LENGTH_SHORT).show();
    }

    /*
     *  if Location Services fails.
     */
    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

                /* Thrown if Google Play services canceled the original
                  PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
           //  If no resolution is available, display a toast to the user with the error.
            Toast.makeText(this, connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();

        }
    }



}

