package droid.klo.com.njuskalator.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ToggleButton;

import droid.klo.com.njuskalator.MainActivity;
import droid.klo.com.njuskalator.R;
import droid.klo.com.njuskalator.database.NjusPreferences;

/**
 * Created by prpa on 4/27/17.
 */

public class Options extends Fragment {

    //region Variables
    private EditText crawlRateTxt;
    private EditText cleaningDayTxt;
    private EditText cleaningTimeTxt;
    private ToggleButton wifiToggle;
    private ToggleButton enabledToggle;

    private SharedPreferences sharedPreferences;
    private int runTime;
    private boolean justWifi;
    private int cleaning_time;
    private int cleaning_day;
    private boolean runningAllowed;

    private static final String LAST_CLEAN_UP = "last_clean_up";
    private static final String SERVICE_SHOULD_RUN = "serviceShouldRun"; //bool, DEFAULT=true
    private static final String ONLY_WIFI = "only_wifi_mode"; //bool, DEFAULT=false
    private static final String CRAWL_MIN_RATE = "rate_of_crawl";//int, minutes, DEFAULT=5
    private static final String CLEAN_DATA_TIME = "cleaning_time_of_day";//int, start hour, mode = 24 hours, DEFAULT=3
    private static final String SAVE_DATA_FOR_NUMBER_OF_DAYS = "save_for_days";//int, days, DEFAULT=1
    //endregion


    //region Overrides
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = new NjusPreferences(getActivity());

        runTime = sharedPreferences.getInt(CRAWL_MIN_RATE, 5);
        justWifi = sharedPreferences.getBoolean(ONLY_WIFI,false);
        cleaning_time = sharedPreferences.getInt(CLEAN_DATA_TIME, 3);
        cleaning_day = sharedPreferences.getInt(SAVE_DATA_FOR_NUMBER_OF_DAYS,1);
        runningAllowed = sharedPreferences.getBoolean(SERVICE_SHOULD_RUN, true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //desc: ovo mora biti tu inace se back button nece pretvorit nazad u hamburger
        ((MainActivity)getActivity()).enableHamburgerAsBack(true);
        View view = inflater.inflate(R.layout.f_option,container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        crawlRateTxt = (EditText)getActivity().findViewById(R.id.fo_crawl_rate);
        crawlRateTxt.setText(""+runTime);

        cleaningDayTxt = (EditText)getActivity().findViewById(R.id.fo_cleaning_day);
        cleaningDayTxt.setText(""+cleaning_day);

        cleaningTimeTxt = (EditText)getActivity().findViewById(R.id.fo_cleaning_time);
        cleaningTimeTxt.setText(""+cleaning_time);

        wifiToggle = (ToggleButton)getActivity().findViewById(R.id.fo_wifi);
        if(justWifi)wifiToggle.setChecked(true);
        else wifiToggle.setChecked(false);

        enabledToggle = (ToggleButton)getActivity().findViewById(R.id.fo_allowed);
        if(runningAllowed) enabledToggle.setChecked(true);
        else enabledToggle.setChecked(false);
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(!crawlRateTxt.getText().toString().equals("") && crawlRateTxt.getText()!=null){
            editor.putInt(CRAWL_MIN_RATE, Integer.parseInt(crawlRateTxt.getText().toString()));
        }

        if(!cleaningDayTxt.getText().toString().equals("") && cleaningDayTxt.getText()!=null){
            editor.putInt(SAVE_DATA_FOR_NUMBER_OF_DAYS, Integer.parseInt(cleaningDayTxt.getText().toString()));
        }

        if(!cleaningTimeTxt.getText().toString().equals("") && cleaningTimeTxt.getText()!=null){
            editor.putInt(CLEAN_DATA_TIME, Integer.parseInt(cleaningTimeTxt.getText().toString()));
        }

        if(wifiToggle.isChecked())editor.putBoolean(ONLY_WIFI, true);
        else editor.putBoolean(ONLY_WIFI, false);

        if(enabledToggle.isChecked())editor.putBoolean(SERVICE_SHOULD_RUN, true);
        else editor.putBoolean(SERVICE_SHOULD_RUN, false);

        editor.apply();
    }
    //endregion
}
