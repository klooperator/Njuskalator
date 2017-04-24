package droid.klo.com.njuskalator.database;

import android.content.ContentValues;
import android.util.Log;

/**
 * Created by prpa on 4/17/17.
 */

public class Result {
    //region variabels
    private static final String TAG = "Result";
    private long id;
    private long source_id;
    private String title;
    private String content;
    private String table;
    private String phone_number;
    private long time;
    private int price;
    private String seller;
    private String link;
    private String originalLink;
    private int is_viewed;
    private String isVau;



    //table
    public static final String TABLE_RESULTS_NAME = "t_results";

    //TABLE_RESULTS columns
    public static final String ID = "_ID"; //int autoincrement
    public static final String SOURCE_ID = "_source_id"; //int foreign key
    public static final String PHONE_NUMBER = "result_phone_number"; // text
    public static final String TITLE = "result_title";//text
    public static final String CONTENT = "result_content"; //text
    public static final String PRICE = "result_price"; //int
    public static final String SELLER = "seller";//text
    public static final String TIME = "result_time"; //text
    public static final String ORIGINAL_LINK = "original_link";
    public static final String LINK = "link";
    public static final String IS_VIEWED = "is_viewed";
    public static final String IS_VAU = "is_vau";
    public static final String TABLE = "description_table";

    public static final String[] resultColumns = {ID,SOURCE_ID, PHONE_NUMBER, TITLE, CONTENT,PRICE,SELLER,TIME, ORIGINAL_LINK, LINK, IS_VIEWED, IS_VAU, TABLE};

    //create statements
    public static final String CREATE_TABLE_RESULTS =
            "CREATE TABLE " + TABLE_RESULTS_NAME + " ( " +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SOURCE_ID + " INTEGER, " +
                    TITLE + " TEXT NOT NULL, " +
                    CONTENT + " TEXT, " +
                    SELLER + " TEXT, " +
                    PHONE_NUMBER + " TEXT NOT NULL, " +
                    PRICE + " INTEGER, " +
                    TIME + " INTEGER, " +
                    ORIGINAL_LINK + " TEXT, "+
                    LINK + " TEXT, " +
                    IS_VIEWED + " INTEGER, " +
                    IS_VAU + " TEXT, " +
                    TABLE + " TEXT, " +
                    "FOREIGN KEY (" + SOURCE_ID + ") REFERENCES " + Source.TABLE_SOURCE_NAME + "(" +Source.ID + ")" +
                    ");";
    //endregion

    //public static final Uri CONTENT_URI = CP.BASE_CONTENT_URI.buildUpon().appendPath(TABLE_RESULTS_NAME).build();

    //region getters setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSource_id() {
        return source_id;
    }

    public void setSource_id(long source_id) {
        this.source_id = source_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getOriginalLink() {
        return originalLink;
    }

    public void setOriginalLink(String originalLink) {
        this.originalLink = originalLink;
    }

    public int getIs_viewed() {
        return is_viewed;
    }

    public void setIs_viewed(int is_viewed) {
        this.is_viewed = is_viewed;
    }

    public String getIsVau() {
        return isVau;
    }

    public void setIsVau(String isVau) {
        this.isVau = isVau;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public ContentValues getContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(SOURCE_ID,this.source_id);
        cv.put(PHONE_NUMBER,this.phone_number);
        cv.put(TITLE,this.title);
        cv.put(CONTENT,this.content);
        cv.put(PRICE,this.price);
        cv.put(SELLER,this.seller);
        Log.d(TAG, "Time declared in Resault class: " + this.time);
        cv.put(TIME,this.time);
        cv.put(ORIGINAL_LINK,this.originalLink);
        cv.put(LINK,this.link);
        cv.put(IS_VIEWED,this.is_viewed);
        cv.put(IS_VAU,this.isVau);
        cv.put(TABLE, this.table);
        return cv;
    }


    //endregion
}
