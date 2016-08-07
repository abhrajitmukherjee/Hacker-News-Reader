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


import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;


public class DetailColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement
    public static final String _ID = "_id";
    @DataType(DataType.Type.TEXT)
    public static final String BY = "by";
    @DataType(DataType.Type.TEXT)
    public static final String RANK = "news_rank";
    @DataType(DataType.Type.TEXT)
    public static final String NEWS_ID = "news_id";
    @DataType(DataType.Type.TEXT)
    public static final String SCORE = "score";
    @DataType(DataType.Type.TEXT)
    public static final String TIME = "time";
    @DataType(DataType.Type.TEXT)
    public static final String TITLE = "title";
    @DataType(DataType.Type.TEXT)
    public static final String TYPE = "type";
    @DataType(DataType.Type.TEXT)
    public static final String URL = "url";
    @DataType(DataType.Type.TEXT)
    public static final String IMAGE_URL = "image_url";

}