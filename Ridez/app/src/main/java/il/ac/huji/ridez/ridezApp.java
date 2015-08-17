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
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "8VFSK81d3JofZNkzQ1V9pWWGxYFiQEaSk57HM8BR", "lhGtlfFbe2AAd3KFhF3kpj75PP37UkYHEbK1NTiM");
        if (username != "" && password != "") {
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (e == null) {
                        Log.d("PARSE", "Logged in as " + parseUser.getUsername());
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
                        // Include the post data with each comment
                        // suppose we have a author object, for which we want to get all books
                        query.whereEqualTo("users", parseUser);
                        // execute the query
                        query.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> groupList, ParseException e) {
                                if (e == null) {
                                    List<GroupInfo> tempList = new ArrayList<GroupInfo>();
                                    for (int i = 0; i < groupList.size(); ++i) {
                                        ParseObject group =  groupList.get(i);
                                        String name = group.getString("name");
                                        String description = group.getString("description");
                                        ParseFile icon = group.getParseFile("icon");
                                        byte[] iconData = null;
                                        try {
                                            iconData = icon.getData();
                                        } catch (Exception ex) {

                                        }
                                        Bitmap bitmap = null;
                                        if (iconData != null) {
                                            bitmap = BitmapFactory.decodeByteArray(iconData, 0, iconData.length);
                                        }
                                        tempList.add(new GroupInfo(name, description, bitmap));
                                    }
                                    DB.setGroups(tempList);
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
