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

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by abhrajit on 8/6/16.
 */
@ContentProvider(authority = HackerNewsProvider.AUTHORITY, database = HackerNewsDatabase.class)
public class HackerNewsProvider {



        public static final String AUTHORITY = "me.abhrajit.hackernewsreader.data.HackerNewsProvider";

        @TableEndpoint(table = HackerNewsDatabase.NEWS_FEED) public static class NewsFeed {

            @ContentUri(
                    path = "newsfeed" ,
                    type = "vnd.android.cursor.dir/newsfeed")
            public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/newsfeed");
        }


}
