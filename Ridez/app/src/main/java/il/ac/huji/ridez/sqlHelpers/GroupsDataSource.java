package il.ac.huji.ridez.sqlHelpers;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

/**
 * Created by Zahi on 04/08/2015.
 */
public class GroupsDataSource {

    // Database fields
    private SQLiteDatabase db;
    private SQLGroupsHelper dbHelper;
    private String[] allColumns = { SQLGroupsHelper.COLUMN_ID, SQLGroupsHelper.COLUMN_GROUP_NAME,
            SQLGroupsHelper.COLUMN_GROUP_DESCRIPTION, SQLGroupsHelper.COLUMN_GROUP_ICON};

    public GroupsDataSource(Context context) {
        dbHelper = new SQLGroupsHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public GroupInfo createGroupInfo(String name, String description, Bitmap icon ) {
        ContentValues values = new ContentValues();
        values.put(SQLGroupsHelper.COLUMN_GROUP_NAME, name);
        values.put(SQLGroupsHelper.COLUMN_GROUP_DESCRIPTION, description);

        if (icon != null) {
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            icon.compress(Bitmap.CompressFormat.PNG, 50, bs);
            values.put(SQLGroupsHelper.COLUMN_GROUP_ICON, bs.toByteArray());
        } else {
            values.put(SQLGroupsHelper.COLUMN_GROUP_ICON, "");
        }

        long insertId = db.insert(SQLGroupsHelper.TABLE_GROUPS, null, values);
        Cursor cursor = db.query(SQLGroupsHelper.TABLE_GROUPS, allColumns, SQLGroupsHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        GroupInfo newGroupInfo = cursorToGroupInfo(cursor);
        cursor.close();
        return newGroupInfo;
    }

    public void deleteGroupInfo(GroupInfo GroupInfo) {
        long id = GroupInfo.getId();
        System.out.println("GroupInfo deleted with id: " + id);
        db.delete(SQLGroupsHelper.TABLE_GROUPS, SQLGroupsHelper.COLUMN_ID + " = " + id, null);
    }

    public List<GroupInfo> getAllGroupInfos() {
        List<GroupInfo> GroupInfos = new ArrayList<>();

        Cursor cursor = db.query(SQLGroupsHelper.TABLE_GROUPS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            GroupInfo GroupInfo = cursorToGroupInfo(cursor);
            GroupInfos.add(GroupInfo);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return GroupInfos;
    }

    private GroupInfo cursorToGroupInfo(Cursor cursor) {
        GroupInfo GroupInfo = new GroupInfo(cursor.getString(1), cursor.getString(2), cursor.getBlob(3));
        GroupInfo.setId(cursor.getLong(0));
        return GroupInfo;
    }
}
