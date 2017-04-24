package droid.klo.com.njuskalator.database.content_provider;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import droid.klo.com.njuskalator.database.ExcludeUsers;
import droid.klo.com.njuskalator.database.Links;
import droid.klo.com.njuskalator.database.Result;
import droid.klo.com.njuskalator.database.Source;

/**
 * Created by prpa on 4/17/17.
 */

public class DBHelper extends SQLiteOpenHelper {
    //region class variables
    private static final String TAG = "DBHelper";

    private static final int DB_VERSION = 7;
    public static final String DB_NAME = "njuskalator_db";




    public DBHelper(Context context) {
        super(context, DB_NAME, null,  DB_VERSION);
    }
    //endregion

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate()");
        try {
            db.execSQL(Source.CREATE_TABLE_SOURCE);
            db.execSQL(Result.CREATE_TABLE_RESULTS);
            db.execSQL(ExcludeUsers.CREATE_TABLE_EXCLUDE);
            db.execSQL(Links.CREATE_TABLE_RESULTS);
        }catch (SQLException e){
            Log.e(TAG,"error: "+ e);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "onUpgrade DB");

        db.execSQL("DROP TABLE IF EXISTS " + Source.TABLE_SOURCE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Result.TABLE_RESULTS_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ExcludeUsers.TABLE_EXCLUDE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Links.TABLE_LINK_NAME);
        onCreate(db);
    }


}
