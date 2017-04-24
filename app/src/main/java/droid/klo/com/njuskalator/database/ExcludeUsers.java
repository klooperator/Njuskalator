package droid.klo.com.njuskalator.database;

import android.content.ContentValues;

/**
 * Created by prpa on 4/17/17.
 */

public class ExcludeUsers {
    //region variables
    public static final String ID = "_ID";
    public static final String USER = "user";

    private String user;


    //table
    public static final String TABLE_EXCLUDE_NAME = "t_exclude";

    //all columns
    public static final String[] excludedUsersColumns = {ID, USER};

    //create statements
    public static final String CREATE_TABLE_EXCLUDE =
            "CREATE TABLE " + TABLE_EXCLUDE_NAME + " ( " +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    USER + " TEXT" +
                    ");";

    //URIs
    //public static final Uri CONTENT_URI = CP.BASE_CONTENT_URI.buildUpon().appendPath(TABLE_EXCLUDE_NAME).build();
    //endregion

    public ExcludeUsers(String user){
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ContentValues getContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(USER, this.user);
        return cv;
    }
}
