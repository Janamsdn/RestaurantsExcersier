package com.jay.restaurants.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by J
 */
public class RequestQueueImageLoader {
    private static RequestQueueImageLoader mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;
    private ImageLoader mNetWorkImageLoader;

    private RequestQueueImageLoader(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mNetWorkImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(35);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized RequestQueueImageLoader getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestQueueImageLoader(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mNetWorkImageLoader;
    }
}

