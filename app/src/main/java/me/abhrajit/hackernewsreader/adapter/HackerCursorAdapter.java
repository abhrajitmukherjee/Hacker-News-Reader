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

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import me.abhrajit.hackernewsreader.R;
import me.abhrajit.hackernewsreader.data.DetailColumns;

/**
 * Credit to skyfishjy gist:
 * https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * for the code structure
 */
public class HackerCursorAdapter extends HackerRecyclerViewAdapter<HackerCursorAdapter.ViewHolder> {

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
        if (cursor != null) {
            String rank=cursor.getString(cursor.getColumnIndex(DetailColumns.RANK));
          viewHolder.headLine.setText(rank+"##"+cursor.getString(cursor.getColumnIndex(DetailColumns.TITLE)));
           String imageUrl = cursor.getString(cursor.getColumnIndex(DetailColumns.IMAGE_URL));
            if (!imageUrl.equals("invalid")) {
                System.out.println(imageUrl);
                Picasso.with(mContext).load(imageUrl).into(viewHolder.newsImage);
            }else{
                Picasso.with(mContext).load(R.drawable.filler_image).into(viewHolder.newsImage);
            }

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

        public ViewHolder(View itemView) {
            super(itemView);
            headLine = (TextView) itemView.findViewById(R.id.text_view);
            newsImage = (ImageView) itemView.findViewById(R.id.cardImagePoster);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
