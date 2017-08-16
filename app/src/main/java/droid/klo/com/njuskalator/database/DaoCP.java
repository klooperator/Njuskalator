package droid.klo.com.njuskalator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import droid.klo.com.njuskalator.database.content_provider.CP;

/**
 * Created by prpa on 4/17/17.
 */

public class DaoCP {
    //region globals
    private static final String TAG = "DaoCP";
    //private CP cp;
    private Context context;

    //endregion

    //region constructors
    public DaoCP (Context context){
        Log.d(TAG, "Construct");
        this.context = context;
    }
    //endregion

    //region inserters
    public void insertExcludedUser(ExcludeUsers e){
        Log.d(TAG, "insertExcludedUser");
        Uri uri = context.getContentResolver().insert(CP.URI_EXCLUDE, e.getContentValues());
    }
    public void insertSource(Source s){
        Log.d(TAG, "insertSource()");
        Uri uri = context.getContentResolver().insert(CP.URI_SOURCE, s.getContentValues());
    }

    public void insertResult(Result r){
        Log.d(TAG, "insertResult()");
        Uri uri = context.getContentResolver().insert(CP.URI_RESULT, r.getContentValues());
    }


    public void insertResults(List<Result> rList){
        Log.d(TAG, "insertResults");
        ContentValues[] values = new ContentValues[rList.size()];
        int count = 0;
        for(Result r : rList){
            values[count] = r.getContentValues();
            count++;
        }
        int rows = context.getContentResolver().bulkInsert(CP.URI_RESULT, values);
        Log.w(TAG, rows + " rows created");

    }

    public void insertLinks(List<String> rList, long sourceId){
        Log.d(TAG, "insertResults");
        ContentValues[] values = new ContentValues[rList.size()];
        int count = 0;
        for(String r : rList){
            ContentValues cv=new ContentValues();
            cv.put(Links.LINK, r);
            cv.put(Links.TIME, System.currentTimeMillis());
            cv.put(Links.SOURCE_ID, sourceId);
            values[count] = cv;
            count++;
        }
        int rows = context.getContentResolver().bulkInsert(CP.URI_LINKS, values);
        Log.w(TAG, rows + " rows created");

    }

    public void insertLink(String link, long sourceId){
        ContentValues cv = new ContentValues();
        cv.put(Links.LINK, link);
        cv.put(Links.TIME, System.currentTimeMillis());
        cv.put(Links.SOURCE_ID, sourceId);
        context.getContentResolver().insert(CP.URI_LINKS, cv);
    }
    //endregion

    //region updaters
    public void updateResultIsVIewed(long sourceId){

    }

    public void updateIsFavorite(long id, boolean isFavorite){
        ContentValues cv = new ContentValues();
        if(isFavorite)cv.put(Result.FAVORITE,1);
        else cv.put(Result.FAVORITE, 0);
        context.getContentResolver().update(CP.URI_RESULT, cv, Result.ID + "=?", new String[] {Long.toString(id)});
    }
    //endregion

    //region geters
    public Result getResult(long id){
        Uri mTableName = CP.URI_RESULT;
        String[] mProjection = Result.resultColumns;
        String mSelection = Result.ID+"=?";
        String[] mSelctionArgs = new String[]{Long.toString(id)};
        String mSortOrder = null;
        Cursor c = context.getContentResolver().query(mTableName, mProjection, mSelection, mSelctionArgs, mSortOrder);
        c.moveToFirst();
        return cursorToResult(c);
    }

    public List<Result> getResultTimeOrder(){
        List<Result> s = new ArrayList<Result>();
        Uri mTableName = CP.URI_RESULT;
        String[] mProjection = Result.resultColumns;
        String mSelection = null;
        String[] mSelctionArgs = null;
        String mSortOrder = Result.TIME + " ASC";

        Cursor c = context.getContentResolver().query(mTableName, mProjection, mSelection, mSelctionArgs, mSortOrder);
        c.moveToFirst();
        while(!c.isAfterLast()){
            //DEBUG - prebaceno u funkciju...
            s.add(cursorToResult(c));
            c.moveToNext();
        }
        c.close();
        return s;
    }

    public List<Result> getFavorites(){
        Log.d(TAG, "getNewResults");
        List<Result> s = new ArrayList<Result>();

        Uri mTableName = CP.URI_RESULT;
        String[] mProjection = Result.resultColumns;
        String mSelection = Result.FAVORITE+"=?";
        String[] mSelctionArgs = new String[]{"1"};
        String mSortOrder = Result.TIME + " DESC";


        Cursor c = context.getContentResolver().query(mTableName, mProjection, mSelection, mSelctionArgs, mSortOrder);
        c.moveToFirst();
        while(!c.isAfterLast()){
            //DEBUG - prebaceno u funkciju...
            s.add(cursorToResult(c));
            c.moveToNext();
        }
        c.close();
        return s;
    }

    public List<Result> getResults(long sourceID, int offset, int limit){
        Log.d(TAG, "getNewResults");
        List<Result> s = new ArrayList<Result>();

        Uri mTableName = CP.URI_RESULT;
        String[] mProjection = Result.resultColumns;
        String mSelection = Result.SOURCE_ID+"=?";
        String[] mSelctionArgs = new String[]{Long.toString(sourceID)};
        String mSortOrder = Result.TIME + " DESC";
        String mLimit = " LIMIT "+limit;
        String mOffset = " OFFSET " + offset;

        Cursor c = context.getContentResolver().query(mTableName, mProjection, mSelection, mSelctionArgs, mSortOrder + mLimit + mOffset);
        c.moveToFirst();
        while(!c.isAfterLast()){
            //DEBUG - prebaceno u funkciju...
            s.add(cursorToResult(c));
            c.moveToNext();
        }
        c.close();
        return s;
    }

    public List<Result> getResults( String mSelection, String[] mSelctionArgs){
        Log.d(TAG, "getNewResults");
        List<Result> out = new ArrayList<Result>();

        Uri mTableName = CP.URI_RESULT;
        String[] mProjection = Result.resultColumns;
        String mSortOrder = Result.TIME + " DESC";
        Cursor c = context.getContentResolver().query(mTableName, mProjection, mSelection, mSelctionArgs, mSortOrder);
        if(c != null){
            c.moveToFirst();
            while(!c.isAfterLast()){
                out.add(cursorToResult(c));
                c.moveToNext();
            }
            c.close();
            return out;
        }

        return null;
    }

    public String getNotViewedResults(long sourceID){
        Log.d(TAG, "getNotViewedResults");

        Uri mTableName = CP.URI_RESULT;
        String[] mProjection = Result.resultColumns;
        String mSelection = Result.SOURCE_ID+"=? AND "+ Result.IS_VIEWED + "=?";
        String[] mSelctionArgs = new String[]{Long.toString(sourceID),"0"};
        String mSortOrder = Result.ID + " DESC";
        String mLimit = "20";

        Cursor c = context.getContentResolver().query(mTableName, mProjection, mSelection, mSelctionArgs, mSortOrder);
        String s;
        if(c!=null){
            s = ""+c.getCount();
            c.close();
        }
        else s="";
        Log.i(TAG, s);

        return s;
    }

    public List<String> getExcludedList(){
        Log.d(TAG, "getExcludedList");
        List<String> s = new ArrayList<String>();

        Uri mTableName = CP.URI_EXCLUDE;
        String[] mProjection = ExcludeUsers.excludedUsersColumns;
        String mSelection = null;
        String[] mSelectionArgs =null;
        String mSortOrder = null;
        String mLimit = null;

        Cursor c = context.getContentResolver().query(mTableName, mProjection,mSelection,mSelectionArgs,mSortOrder);
        c.moveToFirst();
        while(!c.isAfterLast()){
            s.add(c.getString(c.getColumnIndex(ExcludeUsers.USER)));
            c.moveToNext();
        }
        c.close();
        return s;
    }

    public List<String> getLastLinks(){
        Log.d(TAG, "getLastLinks");
        List<String> s = new ArrayList<String>();

        Uri mTableName = CP.URI_LINKS;
        String[] mProjection = Links.linkColumns;
        String mSelection = null;
        String[] mSelctionArgs = null;
        String mSortOrder = null;

        Cursor c = context.getContentResolver().query(mTableName, mProjection, mSelection, mSelctionArgs, mSortOrder);
        c.moveToFirst();
        while(!c.isAfterLast()){
            s.add(c.getString(c.getColumnIndex(Links.LINK)));
            c.moveToNext();
        }
        c.close();
        return s;
    }

    public List<String> getLastLinks(long id){
        Log.d(TAG, "getLastLinks");
        List<String> s = new ArrayList<String>();

        Uri mTableName = CP.URI_LINKS;
        String[] mProjection = Links.linkColumns;
        String mSelection = Links.SOURCE_ID+"=?";
        String[] mSelectionArgs = new String[]{Long.toString(id)};
        String mSortOrder = null;

        Cursor c = context.getContentResolver().query(mTableName, mProjection, mSelection, mSelectionArgs, mSortOrder);
        c.moveToFirst();
        while(!c.isAfterLast()){
            s.add(c.getString(c.getColumnIndex(Links.LINK)));
            c.moveToNext();
        }
        c.close();
        return s;
    }

    public Source getSource(String name){
        Log.d(TAG, "getSource");
        Uri mTableName = CP.URI_SOURCE;
        String[] mProjection = Source.sourceColumns;
        String mSelection = Source.NAME+"=?";
        String[] mSelectionArgs = new String[]{name};
        String mSortOrder = null;

        Cursor c = context.getContentResolver().query(mTableName, mProjection,mSelection,mSelectionArgs,mSortOrder);
        c.moveToFirst();
        while(!c.isAfterLast()){
            Source temp = new Source();
            temp.setId(c.getInt(c.getColumnIndex(Source.ID)));
            temp.setName(c.getString(c.getColumnIndex(Source.NAME)));
            temp.setLink(c.getString(c.getColumnIndex(Source.LINK)));
            temp.setTop_value(c.getInt(c.getColumnIndex(Source.TOP_VALUE)));
            temp.setBottom_value(c.getInt(c.getColumnIndex(Source.BOTTOM_VALUE)));
            temp.setVauvau(c.getInt(c.getColumnIndex(Source.VAU)));
            temp.setColor(c.getString(c.getColumnIndex(Source.COLOR)));
            return temp;
        }
        c.close();
        return null;
    }
    public Source getSource(long id){
        Log.d(TAG, "getSource");
        Uri mTableName = CP.URI_SOURCE;
        String[] mProjection = Source.sourceColumns;
        String mSelection = Source.ID+"=?";
        String[] mSelectionArgs = new String[]{Long.toString(id)};
        String mSortOrder = null;

        Cursor c = context.getContentResolver().query(mTableName, mProjection,mSelection,mSelectionArgs,mSortOrder);
        c.moveToFirst();
        while(!c.isAfterLast()){
            Source temp = new Source();
            temp.setId(c.getInt(c.getColumnIndex(Source.ID)));
            temp.setName(c.getString(c.getColumnIndex(Source.NAME)));
            temp.setLink(c.getString(c.getColumnIndex(Source.LINK)));
            temp.setTop_value(c.getInt(c.getColumnIndex(Source.TOP_VALUE)));
            temp.setBottom_value(c.getInt(c.getColumnIndex(Source.BOTTOM_VALUE)));
            temp.setVauvau(c.getInt(c.getColumnIndex(Source.VAU)));
            temp.setColor(c.getString(c.getColumnIndex(Source.COLOR)));
            return temp;
        }
        c.close();
        return null;
    }

    public List<Source> getSources(){
        Log.d(TAG, "getSources()");
        List<Source> s = new ArrayList<Source>();

        Uri mTableName = CP.URI_SOURCE;
        String[] mProjection = Source.sourceColumns;
        String mSelection = null;
        String[] mSelectionArgs = null;
        String mSortOrder = null;

        Cursor c = context.getContentResolver().query(mTableName, mProjection,mSelection,mSelectionArgs,mSortOrder);
        c.moveToFirst();
        while(!c.isAfterLast()){
            Source temp = new Source();
            temp.setId(c.getInt(c.getColumnIndex(Source.ID)));
            temp.setName(c.getString(c.getColumnIndex(Source.NAME)));
            temp.setLink(c.getString(c.getColumnIndex(Source.LINK)));
            temp.setTop_value(c.getInt(c.getColumnIndex(Source.TOP_VALUE)));
            temp.setBottom_value(c.getInt(c.getColumnIndex(Source.BOTTOM_VALUE)));
            temp.setVauvau(c.getInt(c.getColumnIndex(Source.VAU)));
            temp.setColor(c.getString(c.getColumnIndex(Source.COLOR)));
            s.add(temp);
            c.moveToNext();
        }
        c.close();
        return s;
    }
    //endregion

    //region deleters
    public void deleteExcludedUser(String user){
        int n = context.getContentResolver().delete(CP.URI_EXCLUDE, ExcludeUsers.USER+"=?", new String[]{user});
    }

    public void deleteSource(long id){
        int n = context.getContentResolver().delete(CP.URI_SOURCE, Source.ID+"=?", new String[]{""+id});
    }
    public void deleteResult(long resultId ){
        int n = context.getContentResolver().delete(CP.URI_RESULT, Result.ID+"=?", new String[]{Long.toString(resultId)});
    }
    //endregion

    //region other
    public boolean isOpen(){
        return false;
    }

    public void close(){
        Log.d(TAG, "close()");
    }

    public void open(){Log.d(TAG, "open()");}

    public Result cursorToResult(Cursor c){
        Result r = new Result();
        r.setIs_viewed(c.getInt(c.getColumnIndex(Result.IS_VIEWED)));
        //r.setSource_id(sourceID);
        r.setSource_id(c.getLong(c.getColumnIndex(Result.SOURCE_ID)));
        r.setPrice(c.getInt(c.getColumnIndex(Result.PRICE)));
        r.setPhone_number(c.getString(c.getColumnIndex(Result.PHONE_NUMBER)));
        r.setContent(c.getString(c.getColumnIndex(Result.CONTENT)));
        r.setTitle(c.getString(c.getColumnIndex(Result.TITLE)));
        r.setOriginalLink(c.getString(c.getColumnIndex(Result.ORIGINAL_LINK)));
        r.setLink(c.getString(c.getColumnIndex(Result.LINK)));
        r.setSeller(c.getString(c.getColumnIndex(Result.SELLER)));
        r.setTime(c.getLong(c.getColumnIndex(Result.TIME)));
        r.setId(c.getLong(c.getColumnIndex(Result.ID)));
        r.setTable(c.getString(c.getColumnIndex(Result.TABLE)));
        r.setFavorite(c.getInt(c.getColumnIndex(Result.FAVORITE)));
        return r;
    }
    //endregion
}
