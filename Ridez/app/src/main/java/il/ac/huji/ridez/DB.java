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
import java.util.Collections;
import java.util.List;

import il.ac.huji.ridez.contentClasses.RidezGroup;

public class DB {
    private static final String TAG = "DB";
    private static List<RidezGroup> groups;
    private static boolean isLoggedIn = false;

    private DB() {}

    public static void initialize(Context context){
        groups = new ArrayList<>();
    }

    public static void addGroupInBackground(final RidezGroup group, final SaveCallback callback) {
        groups.add(0, group);
        Collections.sort(groups);
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
    public static void emptyGroups() {
        groups.clear();
    }
    public static boolean isLoggedIn() {
        return isLoggedIn;
    }

    public static void setIsLoggedIn(boolean logged) {
        isLoggedIn = logged;
    }

}
