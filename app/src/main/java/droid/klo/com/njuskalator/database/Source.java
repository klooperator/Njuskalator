package droid.klo.com.njuskalator.database;

import android.content.ContentValues;

/**
 * Created by prpa on 4/17/17.
 */

public class Source {
    //region variabels
    private long id;
    private String name;
    private String link;
    private int top_value;
    private int bottom_value;
    private int vauvau;
    private String color;


    //table
    public static final String TABLE_SOURCE_NAME = "t_source";

    //TABLE_SOURCE columns
    public static final String ID = "_ID";
    public static final String NAME = "source_name"; //text
    public static final String LINK = "source_link"; //text
    public static final String TOP_VALUE = "source_top_value"; //int
    public static final String BOTTOM_VALUE = "source_bottom_value"; //int
    public static final String VAU = "vauvau";
    public static final String COLOR = "color";

    public static final String[] sourceColumns = {ID,NAME, LINK, TOP_VALUE, BOTTOM_VALUE, VAU, COLOR};

    //create statements
    public static String CREATE_TABLE_SOURCE =
            "CREATE TABLE " + TABLE_SOURCE_NAME + "( " +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NAME + " TEXT NOT NULL, " +
                    LINK + " TEXT NOT NULL, " +
                    COLOR + " TEXT NOT NULL, " +
                    BOTTOM_VALUE + " INTEGER, " +
                    TOP_VALUE + " INTEGER, " +
                    VAU + " INTEGER" +
                    ");";

    //URIs
    //public static final Uri CONTENT_URI = CP.BASE_CONTENT_URI.buildUpon().appendPath(TABLE_SOURCE_NAME).build();
    //endregion

    //region Constructors
    public Source(){}
    public Source(String name, String link, int top_value, int bottom_value, int vauau, String color){
        this.name=name;
        this.link=link;
        this.top_value=top_value;
        this.bottom_value=bottom_value;
        this.vauvau=vauau;
        this.color = color;
    }
    public Source(String name, String link){
        this.name=name;
        this.link=link;
        this.top_value=-1;
        this.bottom_value=-1;
        this.vauvau=0;
    }
    //endregion

    //region getters setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getTop_value() {
        return top_value;
    }

    public void setTop_value(int top_value) {
        this.top_value = top_value;
    }

    public int getBottom_value() {
        return bottom_value;
    }

    public void setBottom_value(int bottom_value) {
        this.bottom_value = bottom_value;
    }

    public int getVauvau() {
        return vauvau;
    }

    public void setVauvau(int vauvau) {
        this.vauvau = vauvau;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ContentValues getContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(NAME, this.name);
        cv.put(BOTTOM_VALUE, this.bottom_value);
        cv.put(TOP_VALUE, this.top_value);
        cv.put(LINK, this.link);
        cv.put(COLOR,this.color);
        cv.put(VAU, this.vauvau);
        return cv;
    }

    //endregion
}

