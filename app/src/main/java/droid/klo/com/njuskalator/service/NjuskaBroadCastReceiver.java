package droid.klo.com.njuskalator.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by prpa on 5/3/17.
 */

public class NjuskaBroadCastReceiver extends WakefulBroadcastReceiver {
    public static final String TAG = "NjuskaBroadCastReceiver";
    public static final int REQUEST_CODE = 100;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        Intent startServiceIntent = new Intent(context, CrawlerService.class);
        startWakefulService(context, startServiceIntent);
    }


}
