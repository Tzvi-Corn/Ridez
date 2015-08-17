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
import java.util.ArrayList;
import java.util.List;

import il.ac.huji.ridez.contentClasses.RidezGroup;
import il.ac.huji.ridez.sqlHelpers.GroupInfo;
import il.ac.huji.ridez.sqlHelpers.GroupsDataSource;
import il.ac.huji.ridez.sqlHelpers.RideInfo;
import il.ac.huji.ridez.sqlHelpers.SQLGroupsHelper;

public class DB {
    private static final String TAG = "DB";
//    private static GroupsDataSource datasource;
    private static List<RidezGroup> groups;
    private static List<RideInfo> ridesHistory;

    private DB() {}

    public static void initialize(Context context){
//        SQLGroupsHelper helper = new SQLGroupsHelper(context);
        context.deleteDatabase("groups.db");    //TODO delete after works
//        datasource = new GroupsDataSource(context);
//        datasource.open();
//        groups = datasource.getAllGroupInfos();
        groups = new ArrayList<>();
    }

    public static void addGroupInBackground(final RidezGroup group, final SaveCallback callback) {
        groups.add(0, group);
        group.uploadLocalIconInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    group.saveInBackground(callback);
                } else {
                    callback.done(e);
                }
            }
        });
    }

    public static List<RidezGroup> getGroups() {
        return groups;
    }
    public static void setGroups(List<RidezGroup> list) {
        groups = list;
    }
}
