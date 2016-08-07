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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;

import me.abhrajit.hackernewsreader.adapter.HackerCursorAdapter;
import me.abhrajit.hackernewsreader.data.DetailColumns;
import me.abhrajit.hackernewsreader.data.HackerNewsProvider;
import me.abhrajit.hackernewsreader.service.NewsGcmService;
import me.abhrajit.hackernewsreader.service.NewsIntentService;

public class MainActivity extends AppCompatActivity implements  android.app.LoaderManager.LoaderCallbacks<Cursor> {
    private int CURSOR_LOADER_ID=1;
    private Cursor mCursor;
    HackerCursorAdapter mCursorAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        Intent mServiceIntent = new Intent(this, NewsIntentService.class);
        startService(mServiceIntent);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        mCursorAdapter = new HackerCursorAdapter(this, null);
        recyclerView.setAdapter(mCursorAdapter);


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
                new String[]{DetailColumns._ID,
                        DetailColumns.TITLE,
                        DetailColumns.NEWS_ID,
                        DetailColumns.IMAGE_URL,
                        DetailColumns.RANK,
                        DetailColumns.URL},
                null,
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
}
