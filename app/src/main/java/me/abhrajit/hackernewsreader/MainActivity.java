package me.abhrajit.hackernewsreader;
/*
    Copyright 2016 Abhrajit Mukherjee

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/


import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;

import me.abhrajit.hackernewsreader.adapter.HackerCursorAdapter;
import me.abhrajit.hackernewsreader.data.DetailColumns;
import me.abhrajit.hackernewsreader.data.HackerNewsProvider;
import me.abhrajit.hackernewsreader.service.AnalyticsApplication;
import me.abhrajit.hackernewsreader.service.NewsGcmService;
import me.abhrajit.hackernewsreader.service.NewsIntentService;
import me.abhrajit.hackernewsreader.webcalls.ConnectivityCheck;

public class MainActivity extends AppCompatActivity
        implements  android.app.LoaderManager.LoaderCallbacks<Cursor>,
        NavigationView.OnNavigationItemSelectedListener
{
    private int CURSOR_LOADER_ID=1;
    private Cursor mCursor;
    HackerCursorAdapter mCursorAdapter;
    String mFilterCursor=null;
    private Tracker mTracker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int selected=-1;
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        if (savedInstanceState != null) {
            selected = savedInstanceState.getInt(getString(R.string.nav_Selected));
            navigationView.getMenu().getItem(selected).setChecked(true);
            onNavigationItemSelected(navigationView.getMenu().getItem(selected));

        }else{
            navigationView.getMenu().getItem(0).setChecked(true);
        }

        ConnectivityCheck cCheck=new ConnectivityCheck(this);
        if (!cCheck.isConnected()){

            String text = getString(R.string.noInternet);
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();

        }



        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        Log.v("Default tracker",mTracker.toString());
        GoogleAnalytics.getInstance(this).getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);

        long period = 3600L;
        long flex = 10L;
        String periodicTag = "periodic";

       PeriodicTask periodicTask = new PeriodicTask.Builder()
                .setService(NewsGcmService.class)
                .setPeriod(period)
                .setFlex(flex)
                .setTag(periodicTag)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setRequiresCharging(false)
                .build();
        GcmNetworkManager.getInstance(this).schedule(periodicTask);

        int noOfGrid=Integer.parseInt(getString(R.string.no_of_grid));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this,noOfGrid ));
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        mCursorAdapter = new HackerCursorAdapter(this, null);
        recyclerView.setAdapter(mCursorAdapter);



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_trending) {
            mFilterCursor=null;
            getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
        } else if (id == R.id.nav_bookmark) {
            mFilterCursor = DetailColumns.FAVORITE + "='Y'";
            getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);

        }
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart(){
        super.onStart();
        mTracker.setScreenName("Main Screen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        Intent mServiceIntent = new Intent(this, NewsIntentService.class);
        startService(mServiceIntent);


    }

    @Override
    public void onResume() {
        super.onResume();


        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);

    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                this,
                HackerNewsProvider.NewsFeed.CONTENT_URI,
                null,
                mFilterCursor,
                null,
                DetailColumns.RANK+ " ASC");


    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
        mCursor = cursor;

    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        int selected=-1;
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.isChecked()) {
                selected=i;
                break;
            }
        }
        savedInstanceState.putInt(getString(R.string.nav_Selected),selected);
        super.onSaveInstanceState(savedInstanceState);
    }
}
