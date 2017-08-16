package droid.klo.com.njuskalator.database.content_provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Map;

import droid.klo.com.njuskalator.database.ExcludeUsers;
import droid.klo.com.njuskalator.database.Links;
import droid.klo.com.njuskalator.database.NjusPreferences;
import droid.klo.com.njuskalator.database.Result;
import droid.klo.com.njuskalator.database.Source;

/**
 * Created by prpa on 4/17/17.
 */

public class CP extends ContentProvider{
    //region variables
    public static final String TAG = "CP";

    private SQLiteDatabase db;
    private DBHelper myHelper;
    SharedPreferences sharedPreferences;

    public static final String STRING = "string";
    public static final String INTEGER = "int";
    public static final String LONG = "long";
    public static final String BOOLEAN = "bool";
    //URIs
    public static final String AUTHORITY ="droid.klo.com.njuskalator.provider.CP";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri URI_RESULT = Uri.parse("content://" + AUTHORITY + "/" + Result.TABLE_RESULTS_NAME);
    public static final Uri URI_SOURCE = Uri.parse("content://" + AUTHORITY + "/" + Source.TABLE_SOURCE_NAME);
    public static final Uri URI_EXCLUDE = Uri.parse("content://" + AUTHORITY + "/" + ExcludeUsers.TABLE_EXCLUDE_NAME);
    public static final Uri URI_LINKS = Uri.parse("content://" + AUTHORITY + "/" + Links.TABLE_LINK_NAME);
    public static final Uri URI_PREFERENCES = Uri.parse("content://" + AUTHORITY + "/" + "preferences");

    private static final int RESULT_TABLE = 1;
    private static final int RESULT_TABLE_ID = 10;
    private static final int SOURCE_TABLE = 2;
    private static final int SOURCE_TABLE_ID = 20;
    private static final int EXCLUDE_TABLE = 3;
    private static final int EXCLUDE_TABLE_ID = 30;
    private static final int LINK_TABLE = 4;
    private static final int PREFERENCES = 5;


    private static UriMatcher getUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, Result.TABLE_RESULTS_NAME, RESULT_TABLE);
        uriMatcher.addURI(AUTHORITY, Source.TABLE_SOURCE_NAME, SOURCE_TABLE);
        uriMatcher.addURI(AUTHORITY, ExcludeUsers.TABLE_EXCLUDE_NAME, EXCLUDE_TABLE);
        uriMatcher.addURI(AUTHORITY, Result.TABLE_RESULTS_NAME + "/#", RESULT_TABLE_ID);
        uriMatcher.addURI(AUTHORITY, Source.TABLE_SOURCE_NAME + "/#", SOURCE_TABLE_ID);
        uriMatcher.addURI(AUTHORITY, ExcludeUsers.TABLE_EXCLUDE_NAME + "/#", EXCLUDE_TABLE_ID);
        uriMatcher.addURI(AUTHORITY, Links.TABLE_LINK_NAME, LINK_TABLE);
        uriMatcher.addURI(AUTHORITY, "preferences", PREFERENCES);
        return uriMatcher;
    }

    //endregion

    //region overrides
    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate");
        myHelper = new DBHelper(getContext());
        db = myHelper.getWritableDatabase();
        Log.d(TAG, "onCreate/myHelper= "+myHelper.toString());
        Log.d(TAG, "onCreate/db= "+db.toString());
        //sharedPreferences = getContext().getSharedPreferences("mf_crawl",getContext().MODE_PRIVATE);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query");
        //db = myHelper.getWritableDatabase();
        switch (getUriMatcher().match(uri)){

            case RESULT_TABLE:
                try{
                    Cursor c = db.query(Result.TABLE_RESULTS_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                    //db.close();
                    return c;
                }catch (Exception e){
                    Log.e(TAG, e.getLocalizedMessage(),e);
                }
                break;

            case SOURCE_TABLE:
                try{
                    Cursor c = db.query(Source.TABLE_SOURCE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                    //db.close();
                    return c;
                }catch (Exception e){
                    Log.e(TAG, e.getLocalizedMessage(),e);
                }
                break;

            case EXCLUDE_TABLE:
                try{
                    Cursor c = db.query(ExcludeUsers.TABLE_EXCLUDE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                    //db.close();
                    return c;
                }catch (Exception e){
                    Log.e(TAG, e.getLocalizedMessage(),e);
                }
                break;

            case LINK_TABLE:
                try{
                    Cursor c = db.query(Links.TABLE_LINK_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                    //db.close();
                    return c;
                }catch (Exception e){
                    Log.e(TAG, e.getLocalizedMessage(),e);
                }
                break;

            case RESULT_TABLE_ID:

                break;

            case SOURCE_TABLE_ID:

                break;

            case EXCLUDE_TABLE_ID:

                break;

            case PREFERENCES:

                    SharedPreferences sp = getContext().getSharedPreferences(NjusPreferences.PREFERENCES_FILE, getContext().MODE_PRIVATE);
                MatrixCursor c = new MatrixCursor(projection);
                //debug
                if(sp == null){
                    Log.d(TAG,"sp == null");
                }
                //debug end
                if (!sp.contains(projection[0]))
                    return null;
                MatrixCursor.RowBuilder rowBuilder = c.newRow();
                    switch (selection){
                        case STRING:
                            rowBuilder.add(sp.getString(projection[0],null));
                            break;
                        case INTEGER:
                            rowBuilder.add(sp.getInt(projection[0],0));
                            break;
                        case LONG:
                            rowBuilder.add(sp.getLong(projection[0],0l));
                            break;
                        case BOOLEAN:
                            rowBuilder.add((sp.getBoolean(projection[0],false))?1:0);
                            break;
                    }
                    return c;

        }
        Log.d(TAG, "will return NULL");
        return null;
    }



    @Nullable
    @Override
    public String getType(Uri uri) {
        Log.d(TAG, "getType");
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "insert");
        //db = myHelper.getWritableDatabase();
        switch (getUriMatcher().match(uri)){

            case RESULT_TABLE:
                Log.d(TAG, "insert/(case)RESULT_TABLE");
                try{
                    long _id = db.insert(Result.TABLE_RESULTS_NAME, null, values);
                    //db.close();
                    return ContentUris.withAppendedId(URI_SOURCE, _id);
                }catch (Exception e){
                    Log.e(TAG, e.getLocalizedMessage(),e);
                }
                break;
            //***********************************

            case RESULT_TABLE_ID:
                Log.d(TAG, "insert/(case)RESULT_TABLE_ID");

                break;
            //***********************************

            case SOURCE_TABLE:
                Log.d(TAG, "insert/(case)SOURCE_TABLE");
                try{
                    long _id = db.insert(Source.TABLE_SOURCE_NAME, null, values);
                    //db.close();
                    return ContentUris.withAppendedId(URI_SOURCE, _id);
                }catch (Exception e){
                    Log.e(TAG, e.getLocalizedMessage(),e);
                }
                break;
            //***********************************

            case SOURCE_TABLE_ID:
                Log.d(TAG, "insert/(case)SOURCE_TABLE_ID");
                break;
            //***********************************

            case EXCLUDE_TABLE:
                Log.d(TAG, "insert/(case)EXCLUDE_TABLE");
                try{
                    long _id = db.insert(ExcludeUsers.TABLE_EXCLUDE_NAME, null, values);
                    //db.close();
                    return ContentUris.withAppendedId(URI_SOURCE, _id);
                }catch (Exception e){
                    Log.e(TAG, e.getLocalizedMessage(),e);
                }
                break;
            //***********************************

            case EXCLUDE_TABLE_ID:
                Log.d(TAG, "insert/(case)EXCLUDE_TABLE_ID");
                break;
            //***********************************

            case LINK_TABLE:
                try{
                    long _id = db.insert(Links.TABLE_LINK_NAME, null, values);
                    //db.close();
                    return ContentUris.withAppendedId(URI_SOURCE, _id);
                }catch (Exception e){
                    Log.e(TAG, e.getLocalizedMessage(),e);
                }
                break;
            //***********************************

            case PREFERENCES:
                Log.d(TAG, "insert/(case)PREFERENCES");
                SharedPreferences sp = getContext().getSharedPreferences(NjusPreferences.PREFERENCES_FILE, getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                for (Map.Entry<String, Object> entry : values.valueSet()) {
                    final Object value = entry.getValue();
                    final String key = entry.getKey();
                    if(value == null){
                        editor.remove(key);
                    }else if (value instanceof String)
                        editor.putString(key, (String) value);
                    else if (value instanceof Boolean)
                        editor.putBoolean(key, (Boolean) value);
                    else if (value instanceof Long)
                        editor.putLong(key, (Long) value);
                    else if (value instanceof Integer)
                        editor.putInt(key, (Integer) value);
                    else if (value instanceof Float)
                        editor.putFloat(key, (Float) value);
                    else {
                        throw new IllegalArgumentException("Unsupported type " + uri);
                    }
                }
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO){
                    editor.apply();
                }else{
                    editor.commit();
                }

                break;

        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete");
        //db = myHelper.getWritableDatabase();
        switch (getUriMatcher().match(uri)){
            case RESULT_TABLE:
                Log.d(TAG, "delete/(case)RESULT_TABLE");
                try{
                    int _id = db.delete(Result.TABLE_RESULTS_NAME, selection, selectionArgs);
                    return _id;

                }catch (Exception e){
                    Log.e(TAG, e.getLocalizedMessage(),e);
                }
                break;
            //***********************************
            case RESULT_TABLE_ID:
                Log.d(TAG, "delete/(case)RESULT_TABLE_ID");
                break;
            //***********************************
            case SOURCE_TABLE:
                Log.d(TAG, "delete/(case)SOURCE_TABLE");
                try{

                    int _id = db.delete(Source.TABLE_SOURCE_NAME,  selection, selectionArgs);
                    //db.close();
                    return _id;
                }catch (Exception e){
                    Log.e(TAG, e.getLocalizedMessage(),e);
                }
                break;
            //***********************************
            case SOURCE_TABLE_ID:
                Log.d(TAG, "delete/(case)SOURCE_TABLE_ID");
                break;
            //***********************************
            case EXCLUDE_TABLE:
                Log.d(TAG, "delete/(case)EXCLUDE_TABLE");
                try{
                    int _id = db.delete(ExcludeUsers.TABLE_EXCLUDE_NAME, selection, selectionArgs);
                    //db.close();
                    return _id;
                }catch (Exception e){
                    Log.e(TAG, e.getLocalizedMessage(),e);
                }
                break;
            //***********************************
            case EXCLUDE_TABLE_ID:
                Log.d(TAG, "delete/(case)EXCLUDE_TABLE_ID");

                break;
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update");
        switch (getUriMatcher().match(uri)){
            case RESULT_TABLE:
                try{
                    return db.update(Result.TABLE_RESULTS_NAME, values, selection, selectionArgs);
                }catch (Exception e){
                    Log.e(TAG, e.getLocalizedMessage(),e);
                }
                break;

            case SOURCE_TABLE:
                try{
                    return db.update(Source.TABLE_SOURCE_NAME, values, selection, selectionArgs);
                }catch (Exception e){
                    Log.e(TAG, e.getLocalizedMessage(),e);
                }
                break;

            case EXCLUDE_TABLE:
                try{
                    //Cursor c = db.query(Source.TABLE_SOURCE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                    //db.close();
                    return  0;
                }catch (Exception e){
                    Log.e(TAG, e.getLocalizedMessage(),e);
                }
                break;

            case RESULT_TABLE_ID:

                break;
            case SOURCE_TABLE_ID:

                break;
            case EXCLUDE_TABLE_ID:

                break;
        }
        return 0;
    }
    //endregion



    //region custom methods
    private void open_db(){
        Log.d(TAG, "open_db");
        if(db!=null){
            Log.d(TAG, "open_db/db is NOT NULL");
            if(db.isOpen())return;
            else {
                if(myHelper!=null)
                    db = myHelper.getWritableDatabase();
            }
        }else{
            Log.d(TAG, "open_db/db is NULL");
            db = myHelper.getWritableDatabase();
        }
        Log.d(TAG, "open_db=END");
    }
    //endregion
}
