package com.NewApp;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

//import java.sql.Time;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> mForcastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateweather();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateweather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.execute("1");
    }

    @Override
    public void onStart() {
        super.onStart();
        updateweather();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mForcastAdapter = new ArrayAdapter<String>(
                 getActivity(),
                 R.layout.list_item_forecast,
                 R.id.list_item_forecast_textview,
//                 weekForecast
                new ArrayList<String>()
         );

        View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForcastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecast = mForcastAdapter.getItem(position);
                //Toast.makeText(getActivity(),forecast,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
            }
        });

        //return inflater.inflate(R.layout.fragment_forecast, container, false);
        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

//        private String getReadableDateString(long time) {
//            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
//            return shortenedDateFormat.format(time);
//        }

//        private String formatHighLows(double high, double low) {
//            long roundedHigh = Math.round(high);
//            long roundedLow = Math.round(low);
//
//            String highLowStr = roundedHigh + "/" + roundedLow;
//            return  highLowStr;
//        }

        @Override
        protected void onPostExecute(String[] result) {
            //super.onPostExecute(result);
            if (result != null) {
                mForcastAdapter.clear();
                for (String dayForecastStr : result) {
                    mForcastAdapter.add(dayForecastStr);
                }
            }
        }


        @Override
        protected String[] doInBackground(String... params) {
            // Lesson 2
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
                for (Data data : dataArrayList) {
//                    stringArrayList.add("id:"+data.getId()+" HeatRate:"+data.getHeartRate()+" InstantSpeed:"+data.getInstantSpeed()+" Posted:"+data.getPosted());
                    stringArrayList.add(data.getId()+", "+data.getHeartRate()+", "+data.getInstantSpeed()+", "+data.getPosted());
                }
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
    }
}
