package me.abhrajit.hackernewsreader.adapter;

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

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import me.abhrajit.hackernewsreader.DetailView;
import me.abhrajit.hackernewsreader.R;
import me.abhrajit.hackernewsreader.data.DetailColumns;
import me.abhrajit.hackernewsreader.data.HackerNewsProvider;

/**
 * Credit to skyfishjy gist:
 * https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * for the code structure
 */
public class HackerCursorAdapter extends HackerRecyclerViewAdapter<HackerCursorAdapter.ViewHolder> {
    final String URL_KEY="INTENT_URL";
    final String IMG_URL_KEY="IMAGE_URL";
    final String TITLE="URL_TITLE";

    private static Context mContext;

    public HackerCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        robotoLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_main, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor) {
        String colors[]={"#E91E63","#9C27B0","#2196F3","#8BC34A"};

      //  Random r = new Random();
        int i1 = cursor.getPosition()%4;
        if (cursor != null) {
            String rank=cursor.getString(cursor.getColumnIndex(DetailColumns.RANK));
            final String title=cursor.getString(cursor.getColumnIndex(DetailColumns.TITLE));
            String fav=cursor.getString(cursor.getColumnIndex(DetailColumns.FAVORITE));
          viewHolder.headLine.setText(title);
            viewHolder.urlText.setText(cursor.getString(cursor.getColumnIndex(DetailColumns.URL)));
            viewHolder.rankText.setText("#"+rank);
        //    System.out.println("Favorite="+fav);
            if (fav.equals("N")){
                viewHolder.imageBookmark.setImageResource(R.drawable.ic_bookmark_secondary_24dp);
                viewHolder.textBookmark.setTextColor(mContext.getResources().getColor(R.color.colorGray));
            }else
            {
                viewHolder.imageBookmark.setImageResource(R.drawable.ic_bookmark_primary_24dp);
                viewHolder.textBookmark.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            }
            viewHolder.layoutBookmark.setTag(cursor.getPosition());

           String imageUrl = cursor.getString(cursor.getColumnIndex(DetailColumns.IMAGE_URL));
            if (!(imageUrl.equals("invalid")||imageUrl.equals(""))) {
              //  System.out.println(imageUrl);
                viewHolder.newsImage.setBackgroundColor(Color.WHITE);
                Picasso.with(mContext).load(imageUrl).into(viewHolder.newsImage);
                viewHolder.newsImage.setTag(imageUrl);
                viewHolder.floatingText.setText("");
            }else{
                viewHolder.floatingText.setText(title.substring(0,1));
                viewHolder.newsImage.setImageBitmap(null);
                viewHolder.newsImage.setTag("invalid");
                viewHolder.newsImage.setBackgroundColor(Color.parseColor(colors[i1]));
            }


            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    TextView textUrl=(TextView) v.findViewById(R.id.text_url);
                    String url=textUrl.getText().toString();
                    ImageView iv= (ImageView) v.findViewById(R.id.cardImagePoster);
                    String imageUrl=(String)iv.getTag();
                    Intent intent = new Intent(mContext, DetailView.class);
                    intent.putExtra(URL_KEY, url);
                    intent.putExtra(IMG_URL_KEY, imageUrl);
                    intent.putExtra(TITLE,title);
                    mContext.startActivity(intent);

                }
            });


            viewHolder.layoutBookmark.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Cursor cursor1 = getCursor();
                    LinearLayout btn=(LinearLayout) v.findViewById(R.id.bookmark);
                    int position=(Integer) btn.getTag();
                    cursor1.moveToPosition(position);
                    String newsId=cursor1.getString(cursor.getColumnIndex(DetailColumns.NEWS_ID));
                    String fav=cursor1.getString(cursor.getColumnIndex(DetailColumns.FAVORITE));
                    ContentValues cv=new ContentValues();
                    if (fav.equals("Y")){
                        cv.put(DetailColumns.FAVORITE,"N");
                    }else{
                        cv.put(DetailColumns.FAVORITE,"Y");
                    }


                    mContext.getContentResolver().update(
                            HackerNewsProvider.NewsFeed.CONTENT_URI,
                            cv,
                            DetailColumns.NEWS_ID+"='"+newsId+"'",
                            null);



                }


            }
            );

        }
    }


    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public final TextView headLine;
        public final ImageView newsImage;
        public final TextView floatingText;
        public final TextView urlText;
        public final TextView rankText;
        public final CardView cardView;
        public final LinearLayout layoutBookmark;
        public final ImageView imageBookmark;
        public final TextView textBookmark;

        public ViewHolder(View itemView) {
            super(itemView);
            headLine = (TextView) itemView.findViewById(R.id.text_view);
            newsImage = (ImageView) itemView.findViewById(R.id.cardImagePoster);
            floatingText=(TextView) itemView.findViewById(R.id.floating_Text);
            urlText=(TextView) itemView.findViewById(R.id.text_url);
            rankText=(TextView) itemView.findViewById(R.id.rank_text);
            cardView=(CardView) itemView.findViewById(R.id.card_view);
            layoutBookmark=(LinearLayout) itemView.findViewById(R.id.bookmark);
            imageBookmark=(ImageView) itemView.findViewById(R.id.image_bookmark);
            textBookmark=(TextView) itemView.findViewById(R.id.text_bookmark);
        }

        @Override
        public void onClick(View v) {


        }
    }
}
