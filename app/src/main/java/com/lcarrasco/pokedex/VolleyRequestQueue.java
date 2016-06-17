package com.lcarrasco.pokedex;

import android.content.Context;

import com.android.volley.*;
import com.android.volley.toolbox.Volley;

public class VolleyRequestQueue {
    private static VolleyRequestQueue instance;
    private static RequestQueue mRequestQueue;
    private static Context mCtx;

    private VolleyRequestQueue(Context context) {
        mCtx = context.getApplicationContext();
    }

    public static VolleyRequestQueue getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyRequestQueue(context);
        }
        return instance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx);
        }
        return mRequestQueue;
    }

    public <T> void addRequest(Request<T> req) {
        getRequestQueue().add(req);
    }
}