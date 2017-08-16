package droid.klo.com.njuskalator.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.Set;

import droid.klo.com.njuskalator.database.content_provider.CP;

/**
 * Created by prpa on 4/29/17.
 */

public class NjusPreferences implements SharedPreferences {

    public static final String STRING = "string";
    public static final String INTEGER = "int";
    public static final String LONG = "long";
    public static final String BOOLEAN = "bool";
    public static final int PREFS = 5;

    public static final String PREFERENCES_FILE = "mf_crawl";

    private Context context;

    public NjusPreferences(Context c){
        this.context = c;
    }

    @Override
    public Map<String, ?> getAll() {
        return null;
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        Cursor c = context.getContentResolver().query(CP.URI_PREFERENCES, new String[]{key}, STRING, null,null);
        if(c==null)return defValue;
        else{
            c.moveToFirst();
            String s = c.getString(c.getColumnIndex(key));
            c.close();
            return s;
        }
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return null;
    }

    @Override
    public int getInt(String key, int defValue) {
        Cursor c = context.getContentResolver().query(CP.URI_PREFERENCES, new String[]{key}, INTEGER, null,null);
        if(c==null)return defValue;
        else{
            c.moveToFirst();
            int t = c.getInt(c.getColumnIndex(key));
            c.close();
            return t;
        }
    }

    @Override
    public long getLong(String key, long defValue) {
        Cursor c = context.getContentResolver().query(CP.URI_PREFERENCES, new String[]{key}, LONG, null,null);
        if(c==null)return defValue;
        else{
            c.moveToFirst();
            long t = c.getLong(c.getColumnIndex(key));
            c.close();
            return t;
        }
    }

    @Override
    public float getFloat(String key, float defValue) {
        return 0;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        Cursor c = context.getContentResolver().query(CP.URI_PREFERENCES, new String[]{key}, BOOLEAN, null,null);
        if(c==null)return defValue;
        else{
            c.moveToFirst();
            boolean t;
            if(c.getInt(c.getColumnIndex(key))==1)t = true;
            else t = false;
            c.close();
            return t;
        }
    }

    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public Editor edit() {
        return new Editorator(this.context);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    private class Editorator implements Editor{

        private ContentValues cv;
        private Context context;

        public Editorator(Context c){
            this.context = c;
            this.cv = new ContentValues();
        }

        @Override
        public Editor putString(String key, @Nullable String value) {
            cv.put(key, value);
            return this;
        }

        @Override
        public Editor putStringSet(String key, @Nullable Set<String> values) {
            return this;
        }

        @Override
        public Editor putInt(String key, int value) {
            cv.put(key, value);
            return this;
        }

        @Override
        public Editor putLong(String key, long value) {
            cv.put(key, value);
            return this;
        }

        @Override
        public Editor putFloat(String key, float value) {
            cv.put(key, value);
            return this;
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            cv.put(key, value);
            return this;
        }

        @Override
        public Editor remove(String key) {

            return this;
        }

        @Override
        public Editor clear() {

            return this;
        }

        @Override
        public boolean commit() {
            apply();
            return true;
        }

        @Override
        public void apply() {
        this.context.getContentResolver().insert(CP.URI_PREFERENCES,cv);
        }
    }
}
