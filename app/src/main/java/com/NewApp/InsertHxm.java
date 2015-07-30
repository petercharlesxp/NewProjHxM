package com.NewApp;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

/**
 * Created by Peter on 29/07/2015.
 */
public class InsertHxm extends AsyncTask<String, Void, String> {
    private String LOG_TAG = "InsertHxM";
    private String strResponse;

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        //String strResponse = null;

        try {
            final String BASE_URL = "http://192.168.2.27/test1/inserthxm.php?";
            final String QUERY_PARAM1 = "heartrate";
            final String QUERY_PARAM2 = "instantspeed";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM1, params[0])
                    .appendQueryParameter(QUERY_PARAM2, params[1])
                    .build();
            URL url = new URL(builtUri.toString());

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
            strResponse = buffer.toString();
            Log.v(LOG_TAG, "Response string: " + strResponse);
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
        return strResponse;
    }
}


