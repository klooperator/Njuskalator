package droid.klo.com.njuskalator.service;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import droid.klo.com.njuskalator.database.NjusPreferences;
import droid.klo.com.njuskalator.database.Result;
import droid.klo.com.njuskalator.database.Source;

/**
 * Created by prpa on 4/17/17.
 */

public class CrawlerService extends IntentService {

    //region variables
    public static final String TAG = "CrawlerService";

    private Handler handler;
    //private Runnable mainRunnable;//runnable for crwal
    //private List<Runnable> runList;//list of JSoupMain runnables to crawl
    //private Runnable cleaningRunnable;
    //private Runnable cantRunRunnable;
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
    private Intent receiverIntent;

    //options
    private static final String FIRST_RUN = "initialized";//Bool, special
    private static final String LAST_CLEAN_UP = "last_clean_up";
    private static final String SERVICE_SHOULD_RUN = "serviceShouldRun"; //bool, DEFAULT=true
    private static final String ONLY_WIFI = "only_wifi_mode"; //bool, DEFAULT=false
    private static final String CRAWL_MIN_RATE = "rate_of_crawl";//int, minutes, DEFAULT=5
    private static final String CLEAN_DATA_TIME = "cleaning_time_of_day";//int, start hour, mode = 24 hours, DEFAULT=3
    private static final String SAVE_DATA_FOR_NUMBER_OF_DAYS = "save_for_days";//int, days, DEFAULT=1

    private static final String STATUS_NUMBER_OF_RUNS = "runed_for";




    //endregion

    //region OVerrides


    public CrawlerService(String name) {
        super("CrawlerService");
    }

    public CrawlerService() {
        super("CrawlerService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent");
        //NjuskaBroadCastReceiver.completeWakefulIntent(intent);
        receiverIntent = intent;
        initService();
        //NjuskaBroadCastReceiver.completeWakefulIntent(intent);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        canIRun = false;
        handler = new Handler();
        //initService();


    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }



    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        canIRun=false;
        //handler.removeCallbacks(mainRunnable);
        //desc: scheduale alarm
        Intent intent = new Intent(this, NjuskaBroadCastReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, NjuskaBroadCastReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        //alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000,AlarmManager.INTERVAL_FIFTEEN_MINUTES/3, pIntent);
        if(Build.VERSION.SDK_INT >= 19){
            alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + runTime * 60L * 1000L + (60L * 1000L * (new Random(System.currentTimeMillis()).nextInt(4) - 2)),
                    pIntent);
        }else{
            alarm.set(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + runTime * 60L * 1000L + (60L * 1000L * (new Random(System.currentTimeMillis()).nextInt(4) - 2)),
                    pIntent);
        }


        super.onDestroy();
    }
    //endregion

    private void initService(){
        Log.d(TAG, "initService");
        //set preferences
        initPreferences();
        initExcludedUsers();
        handler.post(initCleaningRunnable());
        initMainRunnable();
    }

    private List<Runnable> getRunningList(){
        List<Runnable> rl = new ArrayList<Runnable>();
        sourceList = new DaoCP(this).getSources();
        int userAgent = JSoupMain.randomUA();

        for(Iterator<Source> i = sourceList.iterator(); i.hasNext();){
            Source s = i.next();
            List<String> lastLinks = new DaoCP(this).getLastLinks(s.getId());
            //rl.add(new JSoupMain(getApplicationContext(), s, lastLinks, userAgent, excluded));
            rl.add(new CrawlSource(this, JSoupMain.getPickedUAString(userAgent), handler, s, excluded, lastLinks));
        }
        return rl;
    }
    private void initPreferences(){
        SharedPreferences pref = new NjusPreferences(this);
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
        Runnable mainRunnable = new Runnable() {
            @Override
            public void run() {
                //Log.d(TAG, "runnable/run()");
                if(canIRun()){
                //initRunningList();
                    int count = 0;
                    for(Runnable r : getRunningList()){
                        handler.post(r);
                        count++;
                    }
                    saveNewStatus();
                }
            }
        };
        handler.post(mainRunnable);
        if(this.receiverIntent != null)NjuskaBroadCastReceiver.completeWakefulIntent(receiverIntent);
    }

    public void cleanResults() {
        Log.d("ICrawlAIDE/testService", "start");
        AsyncCleaner ac = new AsyncCleaner(getContext());
        ac.execute();
    }



    private Runnable initCleaningRunnable(){
        Runnable cleaningRunnable = new Runnable() {
            @Override
            public void run() {
                Calendar cal = Calendar.getInstance();
                Log.d(TAG, "time is: " + cal.get(Calendar.HOUR_OF_DAY));
                if((int)cal.get(Calendar.HOUR_OF_DAY) == cleaning_time){
                    cleanResults();
                }

            }
        };

        return cleaningRunnable;
    }

    //region Can Service Run methods
    private boolean canIRun(){
        boolean b;

        if(prefRunningAllowed()){
            b=true;
            if(!prefWifiOnly())b=true;//ako nije samo wifi
            else if(isWifiOn())b=true;//ako je samo wifi provjeri da li je wifi upaljen
            else b=false;//nije na wifi pa nemremo dalje
        }else b=false;//service zgasen u postavkama
        //TODO chekc cro365 for doc
        return b;
    }

    private boolean prefRunningAllowed(){
        SharedPreferences pref = new NjusPreferences(this);
        return  pref.getBoolean(SERVICE_SHOULD_RUN, true);
    }

    private boolean prefWifiOnly(){
        SharedPreferences pref = new NjusPreferences(this);
        return pref.getBoolean(ONLY_WIFI, false);
    }

    private boolean isWifiOn(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        int mWifi = connManager.getActiveNetworkInfo().getType();

        if (mWifi == ConnectivityManager.TYPE_WIFI) return true;
        else return false;
    }
    //endregion

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
            List<Result> resualts = aDao.getResultTimeOrder();
            List<String> excludes = aDao.getExcludedList();
            if(resualts!=null){
                long timeToClean = System.currentTimeMillis()-cleaning_day*86400000L;


                for(Iterator<Result> j = resualts.iterator();j.hasNext();){
                    Result temp = j.next();
                    String dateString = DateFormat.format("MM/dd/yyyy", new Date(temp.getTime())).toString();
                    Log.i(TAG, "ID="+temp.getId() + " TIME = " + dateString + " and fav==" + temp.getFavorite());
                    if((temp.getTime() <= timeToClean && temp.getFavorite() == 0) || excludes.contains(temp.getSeller())){
                        Log.w(TAG, "starije od jednog dana");
                        count_old++;
                        aDao.deleteResult(temp.getId());
                    }
                    count_total++;
                }
            }
            else Log.d(TAG, "AsyncCleaner/doInBackground... resaults == null");
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
            cleanResults();
        }
    };

    private void updateServis(String s){
        Log.d(TAG, "AIDL = "+ s);
        canIRun=false;
        initService();
    }

    //endregion

    private void saveNewStatus(){
        SharedPreferences sharedPreferences = new NjusPreferences(this);
        int runs = sharedPreferences.getInt(STATUS_NUMBER_OF_RUNS,0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(STATUS_NUMBER_OF_RUNS,++runs);
        editor.apply();
    }
    private Context getContext(){
        return this;
    }
}
