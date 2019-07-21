/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;


import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {
    /** Tag for log messages */
    private static final String LOG_TAG = EarthquakeLoader.class.getName();

    private static final int EARTHQUAKE_LOADER_ID = 1;

    /** URL for earthquake data from the USGS dataset */
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query";

    private EarthquakeAdapter mAdapter;
    private TextView mEmptyStateTextView;

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default)
        );

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        String limit = sharedPrefs.getString(
                getString(R.string.settings_no_of_earthquakes_key),
                getString(R.string.settings_no_of_earthquakes_default)
        );

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", limit);
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        mAdapter.clear();

        if(earthquakes != null && !earthquakes.isEmpty()){
            mAdapter.addAll(earthquakes);
        }

        mEmptyStateTextView.setText(R.string.no_earthquakes);
        ProgressBar progressBar = findViewById(R.id.loading_indicator);
        progressBar.setVisibility(ProgressBar.INVISIBLE);

    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = findViewById(R.id.list);
        mEmptyStateTextView = findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(mEmptyStateTextView);

        // Create a new {@link ArrayAdapter} of earthquakes
        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        // Set a click listener on that View
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake currentEarthquake = mAdapter.getItem(position);
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentEarthquake.getUrl()));
                startActivity(websiteIntent);
            }
        });

        //if(getActiveNetworkInfo() == Progress.CONNECT_SUCCESS);
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        }
        else{
            TextView textView = findViewById(R.id.no_internet);
            textView.setText(R.string.no_internet);
            textView.setVisibility(TextView.VISIBLE);

            ProgressBar loadingBar = findViewById(R.id.loading_indicator);
            loadingBar.setVisibility(ProgressBar.INVISIBLE);

        }
    }


    public static class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>>{

        //Query URL
        private String mUrl;

        EarthquakeLoader(Context context, String url){
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading(){
            forceLoad();
        }

        @Nullable
        @Override
        public List<Earthquake> loadInBackground() {

            if(mUrl == null){
                return null;
            }

            return QueryUtils.fetchEarthquakeData(mUrl);
        }

    }

}
