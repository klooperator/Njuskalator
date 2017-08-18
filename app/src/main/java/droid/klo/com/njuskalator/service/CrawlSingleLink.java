package droid.klo.com.njuskalator.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import droid.klo.com.njuskalator.MainActivity;
import droid.klo.com.njuskalator.R;
import droid.klo.com.njuskalator.database.DaoCP;
import droid.klo.com.njuskalator.database.Result;
import droid.klo.com.njuskalator.database.Source;

/**
 * Created by prpa on 5/2/17.
 */

public class CrawlSingleLink implements Runnable {

    private final static String TAG = "CrawlSingleLink";
    private Source source;
    private String link;
    private boolean isVau;
    private List<String> excludedUsers;
    private String pickedUA;
    private Result r;
    private boolean toAddOrNotToAdd;
    private Context context;
    private static final String STATUS_NUMBER_OF_RUNS = "runed_for";

    public CrawlSingleLink(Context context, String link, Source source, boolean isVau, List<String> excludedUsers, String pickedUA){
        this.context = context;
        this.link = link;
        this.source = source;
        this.isVau = isVau;
        this.excludedUsers = excludedUsers;
        this.pickedUA = pickedUA;

        toAddOrNotToAdd = false;
    }


    @Override
    public void run() {
        try {
                Document docR = new GetDoc().execute(new String[]{"http://www.njuskalo.hr" + link, this.pickedUA}).get();
                Element titleEl = docR.select(".base-entity-title").first();
                Element phoneEl = docR.select(".link-tel--alpha").first();
                Element contentEl = docR.select(".passage-standard").first();
                Element tableEL = docR.select(".wrap-table-summary").first();
                Element priceEl = docR.select(".price--hrk").first();
                Element sellerEl = docR.select(".Profile-username").first();


                r = new Result();

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
                if(!isVau)r.setIsVau("regular");
                else r.setIsVau("vau");
                r.setFavorite(0);

                Log.v(TAG, source.getName() + " :" + r.getLink() + "[" + r.getTitle() + "]");
                Log.i(TAG, r.getSeller() + " [" + r.getPhone_number() + "]");
                Log.d(TAG, r.getPrice() + " KN");

                if(!excludedUsers.contains(r.getSeller())){
                    if(source.getBottom_value() != -1 || source.getTop_value() !=-1){
                        boolean top = false,bot = false;
                        if((source.getBottom_value() != -1 && r.getPrice() >= source.getBottom_value()) || source.getBottom_value() == -1) bot = true;
                        if((source.getTop_value() != -1 && r.getPrice() <= source.getTop_value()) || source.getTop_value() == -1) top = true;
                        if(bot && top)toAddOrNotToAdd = true;

                    }else toAddOrNotToAdd = true;
                }else toAddOrNotToAdd = false;

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Log.e(TAG, "negdje je null, link je: " + link);
            e.printStackTrace();
        }
        addToDB();
    }

    private void addToDB(){
        DaoCP dao = new DaoCP(this.context);
        if(toAddOrNotToAdd){
            dao.insertResult(this.r);
            sendNotification();
        }
        dao.insertLink(this.link, this.source.getId());
    }

    private void sendNotification(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context);
        Intent intent = new Intent(this.context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        int id = 788521012;
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        Uri sound = Uri.parse("android.resource://" + this.context.getPackageName() + "/" + R.raw.deep_sonar);
        mBuilder.setSound(sound);
        mBuilder.setContentTitle(this.r.getTitle());
        mBuilder.setContentText(this.r.getPrice()+"kn");



        NotificationManager mNotificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            StatusBarNotification[] currentNotification = mNotificationManager.getActiveNotifications();
            int countNotifs =0;
            Bundle b = new Bundle();

            for(int i = 0; i<currentNotification.length;i++){
                if(currentNotification[i].getId()==788521012){
                    Bundle g = currentNotification[i].getNotification().extras;
                    countNotifs = g.getInt("njuskalator_notification_counter", 0);
                    countNotifs++;
                }
            }
            b.putInt("njuskalator_notification_counter",countNotifs);
            //Log.d(TAG, "count notification = " + countNotifs);
            if(countNotifs>0){
                mBuilder.setContentTitle((countNotifs+1) + " predmeta pronadjeno");
                mBuilder.setContentText("");
               // b.putInt("njuskalator_notification_counter", b.getInt("njuskalator_notification_counter", 0) + 1);

            }
            mBuilder.setExtras(b);
        }else{
            mBuilder.setContentTitle(this.r.getTitle());
            mBuilder.setContentText(this.r.getPrice()+"kn");

        }


//        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
//        mBuilder.setContentTitle(this.r.getTitle());
//        mBuilder.setContentText(this.r.getPrice()+"kn");
//        SharedPreferences sharedPreferences = new NjusPreferences(this.context);
        //int id = sharedPreferences.getInt(STATUS_NUMBER_OF_RUNS,78569852);



        mNotificationManager.notify(id, mBuilder.build());
    }


    //region GetDoc AsyncTask class
    private class GetDoc extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... params) {
            //Log.d(TAG, "GetDoc/doInBackground: "+params[0]);
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
