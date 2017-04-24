package droid.klo.com.njuskalator.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import droid.klo.com.njuskalator.ICrawlAIDE;
import droid.klo.com.njuskalator.database.DaoCP;
import droid.klo.com.njuskalator.database.Result;
import droid.klo.com.njuskalator.database.Source;

/**
 * Created by prpa on 4/17/17.
 */

public class CrawlerService extends Service {

    //region variables
    public static final String TAG = "CrawlerService";

    private Handler handler;
    private Runnable mainRunnable;//runnable for crwal
    private List<Runnable> runList;//list of JSoupMain runnables to crawl
    private Runnable cleaningRunnable;
    private Runnable cantRunRunnable;
    //preferences
    private int runTime;
    private boolean justWifi;
    private int cleaning_time;
    private int cleaning_day;
    private long lastCleanUp;
    private boolean runningAllowed;

    private List<String> excluded;
    private List<Source> sourceList;
    private DaoCP dao;
    private int crawlRate;
    private boolean canIRun;

    //options
    private static final String FIRST_RUN = "initialized";//Bool, special
    private static final String LAST_CLEAN_UP = "last_clean_up";
    private static final String SERVICE_SHOULD_RUN = "serviceShouldRun"; //bool, DEFAULT=true
    private static final String ONLY_WIFI = "only_wifi_mode"; //bool, DEFAULT=false
    private static final String CRAWL_MIN_RATE = "rate_of_crawl";//int, minutes, DEFAULT=5
    private static final String CLEAN_DATA_TIME = "cleaning_time_of_day";//int, start hour, mode = 24 hours, DEFAULT=3
    private static final String SAVE_DATA_FOR_NUMBER_OF_DAYS = "save_for_days";//int, days, DEFAULT=1


    //endregion

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        canIRun = false;
        handler = new Handler();
        initService();


    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        canIRun=false;
        handler.removeCallbacks(mainRunnable);
        super.onDestroy();
    }

    private Context getContext(){
        return this;
    }

    private void updateServis(String s){
        Log.d(TAG, "AIDL = "+ s);
        canIRun=false;
        initService();
    }


    private void initService(){
        Log.d(TAG, "initService");
        //set preferences
        initPreferences();
        if(runningAllowed){
            initExcludedUsers();
            initRunningList();
            initCleaningRunnable();
            initMainRunnable();
            if(!justWifi){
                canIRun=true;
            }else{
                if(isWifiOn())canIRun=true;
                else canIRun=false;
            }
            handler.post(mainRunnable);
            handler.post(cleaningRunnable);
        }else {
            canIRun =false;
        }



    }

    private void initRunningList(){
        runList = new ArrayList<Runnable>();
        sourceList = new DaoCP(this).getSources();
        int userAgent = JSoupMain.randomUA();
        List<String> lastLinks = new DaoCP(this).getLastLinks();
        for(Iterator<Source> i = sourceList.iterator(); i.hasNext();){
            Source s = i.next();
            runList.add(new JSoupMain(getApplicationContext(), s, lastLinks, userAgent, excluded));
        }
    }
    private void initPreferences(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("mf_crawl",getApplicationContext().MODE_PRIVATE);
        runTime = pref.getInt(CRAWL_MIN_RATE, 5);
        cleaning_day = pref.getInt(SAVE_DATA_FOR_NUMBER_OF_DAYS, 1);
        cleaning_time = pref.getInt(CLEAN_DATA_TIME, 3);
        justWifi = pref.getBoolean(ONLY_WIFI, false);
        lastCleanUp = pref.getLong(LAST_CLEAN_UP, -1);
        runningAllowed = pref.getBoolean(SERVICE_SHOULD_RUN, true);
    }

    private void initExcludedUsers(){
        excluded = new DaoCP(this).getExcludedList();
    }


    private void initMainRunnable(){
        mainRunnable = new Runnable() {
            @Override
            public void run() {
                //Log.d(TAG, "runnable/run()");
                if(canIRun){
                initRunningList();
                    int count = 0;
                    for(Runnable r : runList){
                        handler.postDelayed(r, 20000*count);
                        count++;
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            initService();
                        }
                    }, runTime * 60 * 1000 + (60 * 1000 * (new Random(System.currentTimeMillis()).nextInt(4) - 2)));
                }else{
                    handler.removeCallbacks(this);
                    initService();
                }
            }
        };
    }

    private void initCleaningRunnable(){
        cleaningRunnable = new Runnable() {
            @Override
            public void run() {
                Calendar cal = Calendar.getInstance();
                if((int)cal.get(Calendar.HOUR_OF_DAY) == cleaning_time){
                    /*if(lastCleanUp != -1 && (long)cal.getTime().getTime()-(long)cleaning_day*86400000L+(long)lastCleanUp>0){//1000ms*60sec*60min*24hours
                    }*/

                }
                if(handler != null){
                    handler.postDelayed(this, 1000L*60L*60L);
                }
            }
        };
    }
    private boolean isWifiOn(){
        return true;
    }

    //region async cleaner
    private class AsyncCleaner extends AsyncTask<Void,Void,Void> {

        //variables
        DaoCP aDao;
        List<Source> aSources;
        Context context;

        public AsyncCleaner() {
            super();
            Log.d(TAG, "AsyncCleaner/construct");
            aDao = new DaoCP(getBaseContext());
        }
        public AsyncCleaner(Context c) {
            super();
            Log.d(TAG, "AsyncCleaner/construct");
            aDao = new DaoCP(c);
            context = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "AsyncCleaner/onPreExecute");
            aSources = aDao.getSources();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "AsyncCleaner/doInBackground");
            int count_old=0;
            int count_total = 0;
            for(Iterator<Source> i = aSources.iterator(); i.hasNext();){
                Source s = i.next();

                //DEBUG
                String[] args = new String[]{Long.toString(s.getId())};
                String selection = Result.SOURCE_ID + "=?";
                List<Result> resualts = aDao.getResults(selection,args);
                if(resualts!=null){
                    /*Log.d(TAG, "AsyncCleaner/doInBackground result list: " + resualts.size() + ", for source: " + s.getName());
                    Log.i(TAG, "Current time: " + new Date(System.currentTimeMillis()).toString());
                    Log.i(TAG, "Current time in ms: " + System.currentTimeMillis());*/
                    for(Iterator<Result> j = resualts.iterator();j.hasNext();){
                        Result temp = j.next();
                        //Log.i(TAG, "Result time in ms: " + temp.getTime());
                        String dateString = DateFormat.format("MM/dd/yyyy", new Date(temp.getTime())).toString();
                        Log.i(TAG, "ID="+temp.getId() + " TIME = " + dateString);
                        if(temp.getTime() <= System.currentTimeMillis()-cleaning_day*86400000L){
                            Log.w(TAG, "starije od jednog dana");
                            count_old++;
                            aDao.deleteResult(temp.getId());
                        }
                        count_total++;
                    }
                }
                else Log.d(TAG, "AsyncCleaner/doInBackground... resaults == null");
            }
            Log.w(TAG, "starih ima: " + count_old);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    //endregion

    //region AIDL
    private final ICrawlAIDE.Stub mBinder = new ICrawlAIDE.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void updateServer(String s) throws RemoteException {
            Log.d("ICrawlAIDE/updateServer", "received: " + s);
            updateServis(s);
        }

        @Override
        public void testService() throws RemoteException {
            Log.d("ICrawlAIDE/testService", "start");
            AsyncCleaner ac = new AsyncCleaner(getContext());
            ac.execute();
        }
    };
    //endregion
}
