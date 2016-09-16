package me.abhrajit.hackernewsreader.widget;

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

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import me.abhrajit.hackernewsreader.DetailView;
import me.abhrajit.hackernewsreader.R;
import me.abhrajit.hackernewsreader.data.DetailColumns;
import me.abhrajit.hackernewsreader.data.HackerNewsProvider;

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    final String URL_KEY="INTENT_URL";
    final String IMG_URL_KEY="IMAGE_URL";
    final String TITLE="URL_TITLE";

    private static final String TAG = "WidgetDataProvider";

    ArrayList<ArrayList<String>> mCollection = new ArrayList<>();
    Context mContext = null;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mCollection.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
//        RemoteViews view = new RemoteViews(mContext.getPackageName(),
        //android.R.layout.simple_list_item_1);
//        view.setTextViewText(android.R.id.text1, mCollection.get(position));

        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.news_list_layout);
        view.setTextViewText(R.id.widget_text_title,mCollection.get(position).get(0));
        view.setTextViewText(R.id.widget_text_url,mCollection.get(position).get(1));
        view.setTextViewText(R.id.widget_text_rank,mCollection.get(position).get(2));


        Intent intent = new Intent(mContext, DetailView.class);
        intent.putExtra(URL_KEY,mCollection.get(position).get(1));
        intent.putExtra(IMG_URL_KEY,mCollection.get(position).get(3));
        intent.putExtra(TITLE,mCollection.get(position).get(0));
        view.setOnClickFillInIntent(R.id.news_list_layout, intent);
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        mCollection.clear();


        Cursor c = mContext.getContentResolver().query(HackerNewsProvider.NewsFeed.CONTENT_URI,
                new String[]{DetailColumns._ID,
                        DetailColumns.TITLE,
                        DetailColumns.NEWS_ID,
                        DetailColumns.IMAGE_URL,
                        DetailColumns.RANK,
                        DetailColumns.URL},
                null,
                null,
                DetailColumns.RANK+ " ASC");

        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            ArrayList<String> lst=new ArrayList<>();
            lst.add(c.getString(c.getColumnIndex(DetailColumns.TITLE)));
            lst.add(c.getString(c.getColumnIndex(DetailColumns.URL)));
            lst.add(c.getString(c.getColumnIndex(DetailColumns.RANK)));
            lst.add(c.getString(c.getColumnIndex(DetailColumns.IMAGE_URL)));
            mCollection.add(lst);
            c.moveToNext();
        }
        c.close();
    }

}