package com.jay.restaurants.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.jay.restaurants.listener.ResponseListener;

import java.io.IOException;
import java.util.List;

     /**
      * Created by J
      * This Task takes a location as a pair of values for Latitude and Longitude,
      * and returns the postcode for that location.
      */
public class AsyncTaskGetPostCode  extends AsyncTask<Double, Void, String> {
    private Context mcontxt;
    private ProgressDialog pd = null;
    public static final String TAG = AsyncTaskGetPostCode.class.getSimpleName();
    private ResponseListener listener;

    public AsyncTaskGetPostCode(Context context, ResponseListener listener) {
        this.mcontxt=context;
        this.listener=listener;

    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(mcontxt);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Retrieving post code ....");
        pd.setCancelable(false);
        pd.show();


    }
    @Override
    protected String doInBackground(Double... locations) {
        if (locations.length == 2) {
            double latitude = locations[0];
            double longitude = locations[1];
            try {
                final Geocoder gcd = new Geocoder(mcontxt.getApplicationContext());
                List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);
                for (Address address : addresses) {
                    if (address.getLocality() != null && address.getPostalCode() != null) {
                        Log.d(TAG, address.getLocality());
                        Log.d(TAG, address.getPostalCode());
                        return address.getPostalCode();
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Exception retrieving async postcode", e);
            }
        }
        return null;
    }

    protected void onPostExecute(String postcode) {
        Log.d(TAG, "Postcode is [" + postcode + "]");
        if (pd != null) {
            pd.dismiss();
        }
        this.listener.callbackPostCodeResponse(postcode);
    }
}