package com.NewApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.NewApp.data.HxMContract;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Peter on 07/08/2015.
 */
public class FetchHxMTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchHxMTask.class.getSimpleName();

    private ArrayAdapter<String> mListViewAdapter;
    private final Context mContext;

    public FetchHxMTask(Context mContext, ArrayAdapter<String> mHxMAdapter) {
        this.mListViewAdapter = mHxMAdapter;
        this.mContext = mContext;
    }

    private boolean DEBUG = true;

    @Override
    protected void onPostExecute(String[] result) {
        if (result != null) {
            mListViewAdapter.clear();
            for (String itemListView : result) {
                mListViewAdapter.add(itemListView);
            }
        }
    }

    @Override
    protected String[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr = null;

        try {
            //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
            final String FORECAST_BASE_URL = "http://192.168.2.27/test1/index.php?";
            //"http://api.openweathermap.org/data/2.5/forecast/daily?";
            final String QUERY_PARAM = "id";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "Built URI " + builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            forecastJsonStr = buffer.toString();
            Log.v(LOG_TAG, "Forecast string: " + forecastJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            //e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Data>>(){}.getType();
            ArrayList<Data> dataArrayList = gson.fromJson(forecastJsonStr, type);
            ArrayList<String> stringArrayList = new ArrayList<String>();
            Vector<ContentValues> contentValuesVector = new Vector<>(dataArrayList.size());
            Log.d(LOG_TAG, "content values 0: " + Integer.toString(contentValuesVector.size()));
            for (Data data : dataArrayList) {
//                    stringArrayList.add("id:"+data.getId()+" HeatRate:"+data.getHeartRate()+" InstantSpeed:"+data.getInstantSpeed()+" Posted:"+data.getPosted());
                stringArrayList.add(data.getId() + ", " + data.getHeartRate() + ", " + data.getInstantSpeed() + ", " + data.getPosted());
                Log.v(LOG_TAG, data.getId() + ", " + data.getHeartRate() + ", " + data.getInstantSpeed() + ", " + data.getPosted() + "\n");

                ContentValues hxmValues = new ContentValues();
                hxmValues.put(HxMContract.HxMEntry.COLUMN_HeartRate, data.getHeartRate());
                hxmValues.put(HxMContract.HxMEntry.COLUMN_InstantSpeed, data.getInstantSpeed());
                hxmValues.put(HxMContract.HxMEntry.COLUMN_posted, data.getPosted());
                contentValuesVector.add(hxmValues);
            }
            Log.d(LOG_TAG, "content values 1: " + Integer.toString(contentValuesVector.size()));
            // add to database
            if (contentValuesVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[contentValuesVector.size()];
                contentValuesVector.toArray(cvArray);
                mContext.getContentResolver().bulkInsert(HxMContract.HxMEntry.CONTENT_URI, cvArray);

            }

            String sortOrder = HxMContract.HxMEntry.COLUMN_id + " DESC";
            Uri hxmUri = HxMContract.HxMEntry.CONTENT_URI;
            //Uri hxmUri = HxMContract.HxMEntry.buildHxmUri(1);
            Cursor cur = mContext.getContentResolver().query(hxmUri,
                    null, null, null, /*sortOrder*/null);
            contentValuesVector = new Vector<ContentValues>(cur.getCount());
            if (cur.moveToFirst()) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cur, cv);
                    contentValuesVector.add(cv);
                } while (cur.moveToNext());
            }
            Log.d(LOG_TAG, "content values 2: " + Integer.toString(contentValuesVector.size()));
            Log.d(LOG_TAG, "cur.getCount: " + cur.getCount());
            Log.d(LOG_TAG, "FetchHxMTask Complete. " + contentValuesVector.size() + " Inserted");

            String[] strArray = new String[stringArrayList.size()];
            strArray = stringArrayList.toArray(strArray);
            return strArray;
            //return (String[])stringArrayList.toArray();
            //return getWeatherDataFromJson(forecastJsonStr, numDays);
        } catch (/*JSON*/Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private String[] getHxMDataFromJson(String hxmJsonStr)
        throws JSONException {
        try {
//            JSONObject hxmJson = new JSONObject(hxmJsonStr);
//            JSONArray hxmArray = hxmJson.getJSONArray();
            JSONArray hxmArray = new JSONArray(hxmJsonStr);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Data>>(){}.getType();
            ArrayList<Data> dataArrayList = gson.fromJson(hxmJsonStr, type);
            Vector<ContentValues> contentValuesVector = new Vector<>(dataArrayList.size());

            for (Data data : dataArrayList) {
                ContentValues hxmValues = new ContentValues();
                //hxmValues.put(HxMContract.HxMEntry.COLUMN_id, Integer.parseInt(data.getId()));
                hxmValues.put(HxMContract.HxMEntry.COLUMN_HeartRate, data.getHeartRate());
                hxmValues.put(HxMContract.HxMEntry.COLUMN_InstantSpeed, data.getId());
                hxmValues.put(HxMContract.HxMEntry.COLUMN_posted, data.getPosted());

                contentValuesVector.add(hxmValues);
            }

            if (contentValuesVector.size() > 0) {
                // add to database, bulkInsert
            }

            String sortOder = HxMContract.HxMEntry.COLUMN_id + " DESC";

            Log.d(LOG_TAG, "FetchHxmTask Complete. " + contentValuesVector.size() + " Inserted");

            String[] restultStrs = new String[contentValuesVector.size()];
            restultStrs = contentValuesVector.toArray(restultStrs);
            return restultStrs;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return null;
    }

}
