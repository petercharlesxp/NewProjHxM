package com.NewApp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Peter on 30/07/2015.
 */
public class WebClientVolley {
    private static Context mCtx;
    private RequestQueue mRequestQueue;
    private static WebClientVolley mInstance;

    private WebClientVolley(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        mInstance = this;
//    }

    public static synchronized WebClientVolley getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new WebClientVolley(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToReqQueue(Request<T> req/*, String tag*/) {
        getRequestQueue().add(req);
    }

//    public void cancelPendingReq(Object tag) {
//        if (mRequestQueue != null) {
//            mRequestQueue.cancelAll(tag);
//        }
//    }

}
