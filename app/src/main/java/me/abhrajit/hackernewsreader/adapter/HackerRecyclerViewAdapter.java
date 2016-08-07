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
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;

/**
 *  Credit to skyfishjy gist:
 *    https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * for the CursorRecyclerViewApater.java code and idea.
 */
public abstract  class HackerRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>
{
    private static final String LOG_TAG = HackerRecyclerViewAdapter.class.getSimpleName();
    private Cursor mCursor;
    private boolean dataIsValid;
    private int rowIdColumn;
    private DataSetObserver mDataSetObserver;
    public HackerRecyclerViewAdapter(Context context, Cursor cursor){
        mCursor = cursor;
        dataIsValid = cursor != null;
        rowIdColumn = dataIsValid ? mCursor.getColumnIndex("_id") : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (dataIsValid){
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    public Cursor getCursor(){
        return mCursor;
    }

    @Override
    public int getItemCount(){
        if (dataIsValid && mCursor != null){
            return mCursor.getCount();
        }
        return 0;
    }

    @Override public long getItemId(int position) {
        if (dataIsValid && mCursor != null && mCursor.moveToPosition(position)){
            return mCursor.getLong(rowIdColumn);
        }
        return 0;
    }

    @Override public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        if (!dataIsValid){
            throw new IllegalStateException("This should only be called when Cursor is valid");
        }
        if (!mCursor.moveToPosition(position)){
            throw new IllegalStateException("Could not move Cursor to position: " + position);
        }

        onBindViewHolder(viewHolder, mCursor);
    }

    public Cursor swapCursor(Cursor newCursor){
        if (newCursor == mCursor){
            return null;
        }
        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null){
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (mCursor != null){
            if (mDataSetObserver != null){
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            rowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            dataIsValid = true;
            notifyDataSetChanged();
        }else{
            rowIdColumn = -1;
            dataIsValid = false;
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    private class NotifyingDataSetObserver extends DataSetObserver{
        @Override public void onChanged() {
            super.onChanged();
            dataIsValid = true;
            notifyDataSetChanged();
        }

        @Override public void onInvalidated() {
            super.onInvalidated();
            dataIsValid = false;
            notifyDataSetChanged();
        }
    }
}
