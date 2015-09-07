package il.ac.huji.ridez;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import il.ac.huji.ridez.contentClasses.RidezGroup;

import java.util.ArrayList;
import java.util.List;

public class ridezApp extends Application {
    ProgressDialog pd;
    public static boolean loadedGroups;
    @Override
    public void onCreate() {
        super.onCreate();
        loadedGroups = false;
        // Enable Local Datastore.
        DB.initialize(this);
        ParseCrashReporting.enable(this);
        ParseObject.registerSubclass(RidezGroup.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, BuildConfig.PARSE_ID, BuildConfig.PARSE_KEY);
        if (ParseUser.getCurrentUser() != null) {
            DB.setIsLoggedIn(true);
            ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (parseUser != null) {
                        parseUser.pinInBackground();
                    }
                }
            });
            Log.d("PARSE", "Logged in as " + ParseUser.getCurrentUser().getUsername());
            // Associate the device with a user
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("user", ParseUser.getCurrentUser());
            installation.saveInBackground();
            ParseQuery<RidezGroup> query = ParseQuery.getQuery("Group");
            query.whereEqualTo("users", ParseUser.getCurrentUser());
            query.orderByAscending("name");
            // execute the query
            query.findInBackground(new FindCallback<RidezGroup>() {
                public void done(List<RidezGroup> groupList, ParseException e) {
                    if (e == null) {
                        DB.setGroups(groupList);
                    } else {
                        Log.d("PARSE", "error getting groups");
                    }
                    loadedGroups = true;

                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "please login!!!", Toast.LENGTH_LONG).show();
        }
    }
}
