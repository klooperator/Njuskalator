package droid.klo.com.njuskalator.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import droid.klo.com.njuskalator.database.DaoCP;
import droid.klo.com.njuskalator.database.Result;
import droid.klo.com.njuskalator.database.Source;

/**
 * Created by prpa on 4/17/17.
 */

public class JSoupMain implements Runnable {
    //region varibles
    private final static String TAG = "JSoupMain";
    private Source source;
    private int userAgentNumber;
    private String pickedUA;
    private Context context;
    private List<String> lastResults;
    private List<String> newResults;
    private List<String> newVauResults;
    private List<String> excludedUsers;
    //region user agent array
    private static final String[] uaArray = {
            //source : https://deviceatlas.com/blog/list-of-user-agent-strings
            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",//googlebot
            "Mozilla/5.0 (compatible; bingbot/2.0; +http://www.bing.com/bingbot.htm)",//bing bot
            "Mozilla/5.0 (compatible; Yahoo! Slurp; http://help.yahoo.com/help/us/ysearch/slurp)",//yahoo bot
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246",//Windows 10-based PC using Edge browser
            "Mozilla/5.0 (X11; CrOS x86_64 8172.45.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.64 Safari/537.36",//Chrome OS-based laptop using Chrome browser (Chromebook)
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/601.3.9 (KHTML, like Gecko) Version/9.0.2 Safari/601.3.9",//Mac OS X-based computer using a Safari browser
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36", //Windows 7-based PC using a Chrome browser
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:15.0) Gecko/20100101 Firefox/15.0.1",//Linux-based PC using a Firefox browser
            "Mozilla/5.0 (Linux; Android 6.0.1; SM-G920V Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.98 Mobile Safari/537.36",//Samsung Galaxy S6
            "Mozilla/5.0 (Linux; Android 6.0; HTC One M9 Build/MRA58K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.98 Mobile Safari/537.36",//HTC One M9
            "Mozilla/5.0 (Linux; Android 5.0.2; SAMSUNG SM-T550 Build/LRX22G) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/3.3 Chrome/38.0.2125.102 Safari/537.36",//Samsung Galaxy Tab A
            //https://techblog.willshouse.com/2012/01/03/most-common-user-agents/
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36",//Chrome Generic Win10
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36",//Chrome Generic Win7
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36",//Chrome Generic Win10
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36",//Chrome Generic MacOSX
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/602.4.8 (KHTML, like Gecko) Version/10.0.3 Safari/602.4.8",//Safari Generic MacOSX
            //http://www.useragentstring.com/pages/useragentstring.php?typ=Browser
            "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36",//Chrome 41.0.2228.0, Win 7
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246",//Edge 12.246 Win 10
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1",//Firefox 40.1 Win7
    };
    //endregion
    //endregion

    //region constructors
    public JSoupMain(){
        Log.d(TAG, "constructor.");
    }

    public JSoupMain(Context context, Source source, List<String> latestResults, int userAgentNumber){
        //Log.d(TAG, "constructor...");
        newResults = new ArrayList<String>();
        this.lastResults = latestResults;
        this.source = source;
        this.pickedUA = uaArray[userAgentNumber];
        this.context = context;
        this.newVauResults = new ArrayList<String>();
    }

    public JSoupMain(Context context, Source source, List<String> latestResults, int userAgentNumber, List<String> excluded){
        //Log.d(TAG, "constructor...");
        newResults = new ArrayList<String>();
        this.lastResults = latestResults;
        this.source = source;
        this.pickedUA = uaArray[userAgentNumber];
        this.context = context;
        this.newVauResults = new ArrayList<String>();
        this.excludedUsers=excluded;
    }

    //endregion

    //region getters/setters
    public int getUserAgentNumber() {
        return userAgentNumber;
    }

    public void setUserAgentNumber(int userAgentNumber) {
        this.userAgentNumber = userAgentNumber;
    }

    public static int randomUA(){
        return new Random().nextInt(uaArray.length);
    }

    public static String getPickedUAString(int index){
        return uaArray[index];
    }


    //endregion


    @Override
    public void run() {
        Log.e(TAG, "run("+source.getName()+")");
        //getting new links
        DaoCP dao = new DaoCP(context);
        //this.lastResults = dao.getLastLinks(this.source.getId());
        try {
            Document docS = new GetDoc().execute(new String[]{this.source.getLink(), this.pickedUA}).get();
            //Log.i(TAG, docS.html());
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
                            if (!this.lastResults.contains(a)) {
                                this.newResults.add(a);
                            }
                        } else if (li.hasClass("EntityList--VauVau")) {
                            String a = li.getElementsByTag("a").first().attr("href").toString();
                            if (!this.lastResults.contains(a)) {
                                this.newVauResults.add(a);
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
        //desc: get regular results
        List<Result> resaults = new ArrayList<Result>();
        for(String link : this.newResults){
            try {
                if(!link.contentEquals("")) {
                    Document docR = new GetDoc().execute(new String[]{"http://www.njuskalo.hr" + link, this.pickedUA}).get();
                    Element titleEl = docR.select(".base-entity-title").first();
                    Element phoneEl = docR.select(".link-tel--alpha").first();
                    Element contentEl = docR.select(".passage-standard").first();
                    Element tableEL = docR.select(".wrap-table-summary").first();
                    Element priceEl = docR.select(".price--hrk").first();
                    Element sellerEl = docR.select(".Profile-username").first();


                    Result r = new Result();

                    if(phoneEl != null)r.setPhone_number(phoneEl.text());
                    else r.setPhone_number("N/A");
                    r.setPrice(Integer.parseInt(priceEl.text().replaceAll("\\D+", "")));
                    r.setTitle(titleEl.text());
                    r.setSeller(sellerEl.text());
                    r.setContent(contentEl.text());
                    r.setTable(tableEL.html());
                    r.setLink("http://www.njuskalo.hr" + link);
                    r.setOriginalLink(link);
                    r.setSource_id(this.source.getId());
                    r.setTime(System.currentTimeMillis());
                    r.setIs_viewed(0);
                    r.setIsVau("regular");
                    r.setFavorite(0);

                    Log.v(TAG, source.getName() + " :" + r.getLink() + "[" + r.getTitle() + "]");
                    Log.i(TAG, r.getSeller() + " [" + r.getPhone_number() + "]");
                    Log.d(TAG, r.getPrice() + " KN");

                    if(!excludedUsers.contains(r.getSeller())){
                        if(source.getBottom_value() != -1 || source.getTop_value() !=-1){
                            boolean top = false,bot = false;
                            if((source.getBottom_value() != -1 && r.getPrice() >= source.getBottom_value()) || source.getBottom_value() == -1) bot = true;
                            if((source.getTop_value() != -1 && r.getPrice() <= source.getTop_value()) || source.getTop_value() == -1) top = true;
                            if(bot && top)resaults.add(r);

                        }else resaults.add(r);
                    }
                }else{
                    Log.e(TAG, "contains excluded user, link == " + link);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                Log.e(TAG, "negdje je null, link je: " + link);
                e.printStackTrace();
            }
        }
        for(String link : this.newVauResults){
            try {
                if(!link.contentEquals("") && source.getVauvau()==1) {
                    Document docR = new GetDoc().execute(new String[]{"http://www.njuskalo.hr" + link, this.pickedUA}).get();
                    Element titleEl = docR.select(".base-entity-title").first();
                    Element phoneEl = docR.select(".link-tel--alpha").first();
                    Element contentEl = docR.select(".passage-standard").first();
                    Element tableEL = docR.select(".wrap-table-summary").first();
                    Element priceEl = docR.select(".price--hrk").first();
                    Element sellerEl = docR.select(".Profile-username").first();


                    Result r = new Result();

                    if(phoneEl != null)r.setPhone_number(phoneEl.text());
                    else r.setPhone_number("N/A");
                    r.setPrice(Integer.parseInt(priceEl.text().replaceAll("\\D+", "")));
                    r.setTitle(titleEl.text());
                    r.setSeller(sellerEl.text());
                    r.setContent(contentEl.text());
                    r.setTable(tableEL.html());
                    r.setLink("http://www.njuskalo.hr" + link);
                    r.setOriginalLink(link);
                    r.setSource_id(this.source.getId());
                    r.setTime(System.currentTimeMillis());
                    r.setIs_viewed(0);
                    r.setIsVau("vau");
                    r.setFavorite(0);

                    Log.v(TAG, source.getName() + " :" + r.getLink() + "[" + r.getTitle() + "]");
                    Log.i(TAG, r.getSeller() + " [" + r.getPhone_number() + "]");
                    Log.d(TAG, r.getPrice() + " KN");

                    if(!excludedUsers.contains(r.getSeller()))resaults.add(r);
                }else{
                    Log.e(TAG, "link == " + link);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                Log.e(TAG, "negdje je null, link je: " + link);
                e.printStackTrace();
            }
        }
        if(context==null)Log.d(TAG, "context je null");
        //DaoCP dao = new DaoCP(this.context);
        dao.insertResults(resaults);
        dao.insertLinks(newResults, source.getId());
        dao.insertLinks(newVauResults, source.getId());
        Log.e(TAG, "run("+source.getName()+") - - - END");
    }

    //region methods

    //endregion

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
