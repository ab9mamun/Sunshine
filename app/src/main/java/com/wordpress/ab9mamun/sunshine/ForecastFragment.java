package com.wordpress.ab9mamun.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {
    ArrayAdapter<String> mForecastAdapter;
    TextView maxTodayText;
    TextView minTodayText;
    TextView mainTodayText;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_refresh){
           // Log.i("ForecastFragment", "refreshed");
            new FetchWeatherTask().execute("Dhaka,BD", null, null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] forecastArray =
                {"Today-Sunny-88/63",
                        "Tomorrow-Sunny-78/58",
                        "Monday-Cloudy-72/54",
                        "Tuesday-Foggy-70/53",
                        "Friday-Sunny-76/59"};

        ArrayList<String> weekForecast = new ArrayList<>(Arrays.asList(forecastArray));
        mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast,
                R.id.list_item_forecast_textview, weekForecast);

        ListView forecast_listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        forecast_listView.setAdapter(mForecastAdapter);
        // mForecastAdapter.notifyDataSetChanged();

        maxTodayText = (TextView) rootView.findViewById(R.id.maxToday);
        minTodayText = (TextView) rootView.findViewById(R.id.minToday);
        mainTodayText = (TextView) rootView.findViewById(R.id.mainToday);




        return rootView;
    }


    public class FetchWeatherTask extends AsyncTask<String, Void, Void> {
        double maxToday;
        double minToday;
        String mainToday;

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params) {

            //-------------------github codes---------------------------------------------------------------------------------------

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            String api_key = "6e64abbbaa3a5a53118584c4c0c69f12";
            int numDays = 7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APIKEY_PARAM = "appid";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(DAYS_PARAM, numDays+"")
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(APIKEY_PARAM, api_key).build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Build Uri: "+builtUri.toString());

               // URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&APPID=6e64abbbaa3a5a53118584c4c0c69f12");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Log.v("hi", "connected");

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    Log.v("hi", "input stream was null");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
               // System.out.println("hey"+forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
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
            //----------------------------------end github codes------------------------------------------------------------------------

            Log.v(LOG_TAG, forecastJsonStr);
        try {
            JSONObject weatherForecast = new JSONObject(forecastJsonStr);
            JSONObject curr = weatherForecast.getJSONArray("list").getJSONObject(0);
            JSONObject temp = curr.getJSONObject("temp");
              ;
            maxToday = temp.getDouble("max");
            minToday = temp.getDouble("min");
            mainToday = curr.getJSONObject("weather").getString("main");

        }catch (JSONException e){
            Log.d(LOG_TAG, "error parsing json");
        }



            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            maxTodayText.setText(maxToday+"");
            minTodayText.setText(minToday+"");
            mainTodayText.setText(mainToday);
        }
    }
}
