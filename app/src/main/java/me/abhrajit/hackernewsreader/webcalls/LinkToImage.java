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


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LinkToImage {
    private Document doc;


    public LinkToImage(String ur) {

        //   base = uri.getScheme() + "://" + uri.getHost();
        try {
            // System.out.println(uri.toString());
            doc = Jsoup.connect(ur)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                    .maxBodySize(100000)
                    .get();


        } catch (IOException e) {
            handleException(e);
        }

    }

    public String getMainImage(){

        if (doc==null) return null;

        Elements metaImg=doc.select("meta[property=og:image]");
        Elements metaTitle=doc.select("meta[property=og:description]");
        //   System.out.println(metaImg);

        if (metaImg.toString().equals("")){
            return getImageFromBody();
        }
        else{
            return metaImg.attr("content");
        }
    }

    private String getImageFromBody() {
        int width = -1, height = -1;
        int counter=0;
        String linkSt;
        String finalLink=null;
        //   Elements images = doc.getElementsByTag("img");
        Elements images = doc.getElementsByTag("img");
        if (images == null) {
            return null;
        }
        for (Element link : images) {
            counter++;
            if (counter>10)
                break;

            //   System.out.println(link);
            linkSt = link.absUrl("src");
            //   System.out.println("Link="+linkSt);
            if (linkSt==null||linkSt.equals(""))
                continue;
            URL linkUrl = null;

            try {
                linkUrl = new URL(linkSt);

            } catch (MalformedURLException e) {
                handleException(e);
            }

            Bitmap bimg;

            try {
                HttpURLConnection connection = (HttpURLConnection) linkUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bimg = BitmapFactory.decodeStream(input);

            } catch (IOException e) {
                // Log exception
                return null;
            }


            if (linkSt.indexOf("svg") < 0 && linkSt.indexOf("html") <0) {
                // System.out.println(linkUrl);

                if(bimg!=null){
                    width = bimg.getWidth();
                    height = bimg.getHeight();

                }


            }
            if (width > 300 && height>width/2) {
                return linkSt;
            }else if (width > 300 && height>200){
                finalLink=linkSt;

            }


        }
        return finalLink;
    }

    private void handleException(Exception e) {
        System.out.println(e);
    }




}
