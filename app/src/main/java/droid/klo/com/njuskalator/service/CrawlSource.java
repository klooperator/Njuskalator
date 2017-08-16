package droid.klo.com.njuskalator.service;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import droid.klo.com.njuskalator.database.Source;

/**
 * Created by prpa on 5/2/17.
 */

public class CrawlSource implements Runnable {

    private final static String TAG = "CrawlSource";
    private Source source;
    private int userAgentNumber;
    private String pickedUA;
    private Handler handler;//service handler, needed to post new links for single crawl
    private List<String> excludedUsers;//prepare it to past it to single result crawler
    private List<String> latestResult;//avoid crawling crawled links
    private Context context;
    private List<String> newLinks;
    private List<String> newVauLinks;


    public CrawlSource(Context context,String userAgent, Handler handler, Source source, List<String> excluded,List<String> latestResults){
        this.pickedUA = userAgent;
        this.handler = handler;
        this.source = source;
        this.excludedUsers = excluded;
        this.latestResult = latestResults;
        this.context = context;

        this.newLinks = new ArrayList<String>();
        this.newVauLinks = new ArrayList<String>();
    }

    @Override
    public void run() {
        try {
            Document docS = new GetDoc().execute(new String[]{this.source.getLink(), this.pickedUA}).get();
            Elements uls = docS.select(".EntityList--Regular > ul,.EntityList--VauVau > ul");
            for (Element el : uls){
                if(!el.hasClass("EntityList--FeaturedStore")) {
                    Elements lis = el.select("li.EntityList-item");

                    //Log.d(TAG, "numbe of li's: " + lis.size());
                    for (Element li : lis) {
                        //Log.i(TAG, li.html());
                        if (!li.hasClass("EntityList--VauVau") && !li.hasClass("EntityList--banner") && !li.html().contains("BannerFlexEmbed") && !li.hasClass("EntityList-item--banner")) {
                            //Element linkEl = li.getElementsByTag("a").first();
                            String a = li.getElementsByTag("a").first().attr("href");
                            if (!this.latestResult.contains(a)) {
                                this.newLinks.add(a);
                            }
                        } else if (li.hasClass("EntityList--VauVau")) {
                            String a = li.getElementsByTag("a").first().attr("href").toString();
                            if (!this.latestResult.contains(a)) {
                                this.newVauLinks.add(a);
                            }
                        }
                        //Log.w(TAG, "********************");
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        addNewLinksToHandler();
    }

    private void addNewLinksToHandler(){
        //desc: add regural links
        for(String s : newLinks){
            CrawlSingleLink temp = new CrawlSingleLink(this.context, s, this.source, false, this.excludedUsers, this.pickedUA);
            this.handler.post(temp);
        }

        //desc: add vau links
        for(String s : newVauLinks){
            CrawlSingleLink temp = new CrawlSingleLink(this.context, s, this.source, true, this.excludedUsers, this.pickedUA);
            this.handler.post(temp);
        }
    }


    //region GetDoc AsyncTask class
    private class GetDoc extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... params) {
            Log.d(TAG, "GetDoc/doInBackground: "+params[0]);
            Connection connection = Jsoup.connect(params[0]);
            connection.userAgent(params[1]);
            Document doc = null;
            try {
                doc = connection.get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return doc;
        }

    }
    //endregion
}
