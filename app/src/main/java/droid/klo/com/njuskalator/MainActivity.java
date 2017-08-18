package droid.klo.com.njuskalator;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import droid.klo.com.njuskalator.database.NjusPreferences;
import droid.klo.com.njuskalator.fragments.ListResults;
import droid.klo.com.njuskalator.fragments.ListSearches;
import droid.klo.com.njuskalator.fragments.Options;
import droid.klo.com.njuskalator.service.CrawlerService;
import droid.klo.com.njuskalator.service.NjuskaBroadCastReceiver;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //region variables
    private static final String TAG = "MainActivity";
    public ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private ICrawlAIDE aidl;
    private static final String STATUS_NUMBER_OF_RUNS = "runed_for";
    //endregion


    //region Overrides
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpActionBar();
        Intent i = new Intent(this, CrawlerService.class);
        //startService(i);
       // bindToService();



    }

    @Override
    protected void onStart() {
        super.onStart();
        getFragmentManager().beginTransaction().replace(R.id.fragment_placeholder,new ListSearches(),"ls").commit();
        sendBroadcast(new Intent(this, NjuskaBroadCastReceiver.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
        Log.d(TAG,"MAIN activitiy detroyed");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        syncToggle();
    }


    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "onSupportNavigateUp");
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Log.d(TAG, "onBackPressed/super");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        switch (item.getItemId()){
            case R.id.action_refresh:
                /*Intent i = new Intent(this, CrawlerService.class);
                i.putExtra("refresh", "refresh");
                startService(i);*/
                sendBroadcast(new Intent(this, NjuskaBroadCastReceiver.class));

                return true;
            case R.id.action_status:
                Snackbar.make(findViewById(android.R.id.content), getStatusText(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return true;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_reset_status:
                SharedPreferences sharedPreferences = new NjusPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(STATUS_NUMBER_OF_RUNS,0);
                editor.apply();
                break;
            case R.id.action_clean:
                try {
                    //this.bindToService();
                    this.serviceClean();
                    //this.unbindService();
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
                break;
        }


        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected");
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nd_saved_searches) {
            Bundle b = new Bundle();
            b.putLong("source_id", -1);

            ListResults lr = new ListResults();
            lr.setArguments(b);
            getFragmentManager().beginTransaction().replace(R.id.fragment_placeholder, lr, "ListResults_"+-1).addToBackStack("ListResults_"+-1).commit();
        } else if (id == R.id.nd_excluded_users) {

        } else if (id == R.id.nd_options) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_placeholder,new Options(),"options").addToBackStack("options").commit();
        } else if (id == R.id.nd_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //endregion

    //region custom methods
    public void syncToggle(){
        this.toggle.syncState();
    }

    public void enableHamburgerAsBack(boolean b){
        Log.d(TAG, "enableHamburgerAsBack");
        //desc: order matters....
        if(b){
            this.toggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }else{
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            this.toggle.setDrawerIndicatorEnabled(true);
        }
        syncToggle();
    }
    private void setUpActionBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //desc: ovo treba da bi ack button radio
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "setToolbarNavigationClickListener");
                Log.d(TAG, "view is: " + v.getContentDescription());
                if(v.getContentDescription().equals("Navigate up"))onBackPressed();
            }
        });

        syncToggle();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void refreshCurrentView(){
        /*String fName = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount()-1).getName();
        Fragment f = getFragmentManager().findFragmentByTag(fName);
        getFragmentManager().beginTransaction().detach(f).attach(f);*/
        this.onStart();
    }

    private String getStatusText(){
        SharedPreferences sharedPreferences = new NjusPreferences(this);
        return "Number of runs: " + sharedPreferences.getInt(STATUS_NUMBER_OF_RUNS,0);
    }

    //endregion

    //region AIDL
    private void bindToService(){
        Log.d(TAG,"bindToService");
        //Log.d(TAG,ICrawlAIDE.class.getName());
        //Log.d(TAG,getPackageName());
        //Log.d(TAG,CrawlerService.class.getPackage().toString());
        //Intent i = new Intent(this, CrawlerService.class);
        Intent i = new Intent(this, CrawlerService.class);
        i.setAction("njuskalator.aidl");
        //i.setPackage(CrawlerService.class.getPackage().getName());
        bindService(i,mConnection, Context.BIND_AUTO_CREATE);
    }
    private void unbindService(){
        unbindService(mConnection);

    }
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"onServiceConnected");
            aidl = ICrawlAIDE.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"onServiceDisconnected");
        }
    };
    public void serviceClean(){
        Log.d(TAG,"updateService");
        //bindToService();
        if(aidl != null){
            try {
                aidl.testService();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            Log.d(TAG,"updateService/mAidlStub == null");
        }
    }
    //endregion
}
