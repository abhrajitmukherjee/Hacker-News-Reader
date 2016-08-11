package me.abhrajit.hackernewsreader.data;
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
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;


@Database(version = HackerNewsDatabase.VERSION)
public final class HackerNewsDatabase {

    public static final int VERSION = 21;


    @Table(DetailColumns.class) public static final String NEWS_FEED = "newsfeed";

    @OnCreate public static void onCreate(Context context, SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + NEWS_FEED);
        db.execSQL(
                    "CREATE TABLE " + NEWS_FEED +"("+
                            DetailColumns._ID + " INTEGER PRIMARY KEY, " +
                            DetailColumns.BY+" TEXT,"+
                            DetailColumns.RANK+" INTEGER,"+
                            DetailColumns.NEWS_ID+" TEXT," +
                            DetailColumns.VALID+" TEXT," +
                            DetailColumns.FAVORITE+" TEXT," +
                            DetailColumns.SCORE+" TEXT," +
                            DetailColumns.TIME+" TEXT," +
                            DetailColumns.TYPE+" TEXT," +
                            DetailColumns.TITLE+" TEXT," +
                            DetailColumns.IMAGE_URL+" TEXT," +
                            DetailColumns.URL+" TEXT);"
          );
    }

    @OnUpgrade
    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
                                 int newVersion) {
        onCreate(context,db);
    }



}

