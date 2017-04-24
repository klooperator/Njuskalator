package droid.klo.com.njuskalator.database;

/**
 * Created by prpa on 4/22/17.
 */

public class Links {

    //region Variables
    public static final String LINK = "crawled_link";
    public static final String TIME = "crawled_time";
    public static final String SOURCE_ID = "source_id";
    public static final String[] linkColumns = {LINK,TIME, SOURCE_ID};

    public static final String TABLE_LINK_NAME = "t_links";

    //create statements
    public static final String CREATE_TABLE_RESULTS =
            "CREATE TABLE " + TABLE_LINK_NAME + " ( " +
                    LINK + " TEXT NOT NULL, " +
                    TIME + " INTEGER, " +
                    SOURCE_ID + " INTEGER" +
                    ");";
}
