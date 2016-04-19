package com.wordpress.ab9mamun.sunshine;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    static MainActivity main;
    ArrayAdapter<String> mForecastAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] forecastArray = {"Today-Sunny-88/63",
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


        return rootView;
    }

    public static void setMain(MainActivity main){
        MainActivityFragment.main = main;
    }
}
