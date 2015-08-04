package il.ac.huji.ridez.sqlHelpers;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.util.Log;

/**
 * Created by Zahi on 04/08/2015.
 */
public class SQLGroupsHelper extends SQLiteOpenHelper {

    public static final String TABLE_GROUPS = "groups";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_GROUP_NAME = "groupName";
    public static final String COLUMN_GROUP_DESCRIPTION = "groupDescription";
    public static final String COLUMN_GROUP_ICON = "groupIconPath";

    private static final String DATABASE_NAME = "groups.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_GROUPS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_GROUP_NAME
            + " text not null, "+ COLUMN_GROUP_DESCRIPTION
            + " text not null, "+ COLUMN_GROUP_ICON
            + " BLOB not null);";

    public SQLGroupsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLGroupsHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        onCreate(db);
    }
}
