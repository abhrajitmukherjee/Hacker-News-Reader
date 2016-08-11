package me.abhrajit.hackernewsreader.webcalls;
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


import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.abhrajit.hackernewsreader.R;
import me.abhrajit.hackernewsreader.data.DetailColumns;
import me.abhrajit.hackernewsreader.data.HackerNewsProvider;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HnApiCall {
    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
    final String LOGTAG=HnApiCall.class.getName();
    List<String> top500;
    Context mContext;
    public HnApiCall(Context context){

        mContext=context;
    }

    private void setTop500() {
        OkHttpClient client = new OkHttpClient();
        String url="https://hacker-news.firebaseio.com/v0/topstories.json";
        String text=null;
        Response response=null;

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try{
                 response = client.newCall(request).execute();
                text=response.body().string();
            }catch(IOException e){
                Log.d(LOGTAG,e.toString());
                CharSequence error = mContext.getString(R.string.connectivity);
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(mContext, error, duration);
                toast.show();
                return;
            }
         top500= Arrays.asList(text.substring(1,text.length()-1).split(","));

           Log.v(LOGTAG,top500.get(0)+" "+top500.get(top500.size()-1));

    }
    private void parseJson(String Json, int rank){
        JSONObject json=null;
        try{
            json=new JSONObject(Json);
            String url=json.getString("url");

            String imageUrl=(new LinkToImage(url)).getMainImage();
            if (imageUrl==null){
                imageUrl="invalid";
            };
            System.out.println();
            System.out.println(json.getString("title"));
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                    HackerNewsProvider.NewsFeed.CONTENT_URI);
            builder.withValue(DetailColumns.URL,json.getString("url"));
            builder.withValue(DetailColumns.NEWS_ID,json.getString("id"));
            builder.withValue(DetailColumns.TITLE,json.getString("title"));
            builder.withValue(DetailColumns.VALID,"Y");
            builder.withValue(DetailColumns.FAVORITE,"N");
            builder.withValue(DetailColumns.IMAGE_URL,imageUrl);
            builder.withValue(DetailColumns.RANK,rank);

            batchOperations.add(builder.build());


        }catch (JSONException e){
            Log.e(LOGTAG,e.toString());
        }


    }

    public List<String> getNews(){
        Response response=null;
        String text=null;
        OkHttpClient client = new OkHttpClient();
        setTop500();
        ArrayList<String> newsDbList=getNewsIds();

        ContentValues cv=new ContentValues();
        cv.put(DetailColumns.VALID,"N");


        mContext.getContentResolver().update(
                HackerNewsProvider.NewsFeed.CONTENT_URI,
                cv,
                null,
                null);

        int c=0;
        for (int i=0;i<top500.size();i++) {
            String item=top500.get(i);
            c++;

            if(c==10){
                break;
            }

            if(newsDbList.contains(item)){
                System.out.println("Skipped"+item);

                updateRank(item,i+1);
                continue;

            }

            if (c%1==0) {
                writeToDB();
                batchOperations=new ArrayList<>();

            }
            String url="https://hacker-news.firebaseio.com/v0/item/"+item+".json";
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try{
                response = client.newCall(request).execute();
                text=response.body().string();
            }catch(IOException e){
                Log.e(LOGTAG,e.toString());
            }
            parseJson(text,i+1);

        }

        String deleteWhere=DetailColumns.VALID+"='N'";
        mContext.getContentResolver().delete(HackerNewsProvider.NewsFeed.CONTENT_URI,deleteWhere,null);


        return null;

    }

    public ArrayList<String> getNewsIds(){
        ArrayList<String> ids=new ArrayList<>();
        ArrayList<String> imgUrls=new ArrayList<>();
        Cursor cursor;
        cursor=mContext.getContentResolver().query(
                HackerNewsProvider.NewsFeed.CONTENT_URI,
                new String[]{DetailColumns.NEWS_ID,DetailColumns.IMAGE_URL},null,null,null);
        while(cursor.moveToNext()){
            ids.add(cursor.getString(0));
            imgUrls.add(cursor.getString(1));

        }
        cursor.close();
        System.out.println(ids.toString());
        System.out.println(imgUrls.toString());

        return ids;

    }


    public void writeToDB(){

        try{
            mContext.getContentResolver().applyBatch(HackerNewsProvider.AUTHORITY,
                    batchOperations);
        }catch (RemoteException | OperationApplicationException e) {
            Log.e(LOGTAG, e.toString(),e);
        }
    }


    public void updateRank(String newsId,int rank){
        ContentValues cv=new ContentValues();
        cv.put(DetailColumns.RANK,rank);
        cv.put(DetailColumns.VALID,"Y");


            mContext.getContentResolver().update(
                    HackerNewsProvider.NewsFeed.CONTENT_URI,
                    cv,
                    DetailColumns.NEWS_ID+"='"+newsId+"'",
                    null);
        System.out.println("Rank updated for "+newsId+" to "+rank);

    }

}
