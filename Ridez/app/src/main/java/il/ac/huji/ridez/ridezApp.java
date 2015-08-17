package il.ac.huji.ridez;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import il.ac.huji.ridez.contentClasses.RidezGroup;

import java.util.ArrayList;
import java.util.List;

import il.ac.huji.ridez.sqlHelpers.GroupInfo;

public class ridezApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        DB.initialize(this);
        Context context = getApplicationContext();
        SharedPreferences pref = context.getSharedPreferences(getString(R.string.pref_username), Context.MODE_PRIVATE);
        String username = pref.getString("username", "");
        String password = pref.getString("password", "");
        ParseObject.registerSubclass(RidezGroup.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "8VFSK81d3JofZNkzQ1V9pWWGxYFiQEaSk57HM8BR", "lhGtlfFbe2AAd3KFhF3kpj75PP37UkYHEbK1NTiM");
        if (!"".equals(username) && !"".equals(password)) {
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (e == null) {
                        Log.d("PARSE", "Logged in as " + parseUser.getUsername());
                        ParseQuery<RidezGroup> query = ParseQuery.getQuery("Group");
                        // Include the post data with each comment
                        // suppose we have a author object, for which we want to get all books
                        query.whereEqualTo("users", parseUser);
                        // execute the query
                        query.findInBackground(new FindCallback<RidezGroup>() {
                            public void done(List<RidezGroup> groupList, ParseException e) {
                                if (e == null) {
                                    DB.setGroups(groupList);
                                } else {
                                    Log.d("PARSE", "error getting groups");
                                }
                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), "please login!!!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
