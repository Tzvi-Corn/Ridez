package il.ac.huji.ridez;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.List;

import il.ac.huji.ridez.sqlHelpers.GroupInfo;
import il.ac.huji.ridez.sqlHelpers.GroupsDataSource;
import il.ac.huji.ridez.sqlHelpers.RideInfo;
import il.ac.huji.ridez.sqlHelpers.SQLGroupsHelper;

/**
 * Created by Zahi on 04/08/2015.
 */
public class DB {
    private static final String TAG = "DB";
    private static GroupsDataSource datasource;
    private static List<GroupInfo> groups;
    private static List<RideInfo> ridesHistory;

    private DB() {}

    public static void initialize(Context context){
//        SQLGroupsHelper helper = new SQLGroupsHelper(context);
        context.deleteDatabase("groups.db");    //TODO delete after works
        datasource = new GroupsDataSource(context);
        datasource.open();
        groups = datasource.getAllGroupInfos();
    }

    public static void addGroup(GroupInfo group){
        groups.add(0, group);
        datasource.createGroupInfo(group.getName(), group.getDescription(), group.getIcon());
        final ParseObject newGroup = new ParseObject("Group");
        newGroup.put("name", group.getName());
        newGroup.put("description", group.getDescription());
        ParseRelation<ParseUser> users = newGroup.getRelation("users");
        users.add(ParseUser.getCurrentUser());
        ParseRelation<ParseUser> admins = newGroup.getRelation("admins");
        admins.add(ParseUser.getCurrentUser());
        Bitmap icon = group.getIcon();
        final ParseFile iconFile;
        if (icon != null) {
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            icon.compress(Bitmap.CompressFormat.PNG, 50, bs);
            iconFile = new ParseFile("icon.PNG", bs.toByteArray());
        } else {
            iconFile = new ParseFile("icon.PNG", "".getBytes());
        }
        iconFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                newGroup.put("icon", iconFile);
                newGroup.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d(TAG, "new group!!");
                    }
                });
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer integer) {
                Log.d(TAG, "progress:" + integer);
            }
        });
    }

    public static List<GroupInfo> getGroups() {
        return groups;
    }
    public static void setGroups(List<GroupInfo> list) {
        groups = list;
    }
}
