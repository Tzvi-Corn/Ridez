package il.ac.huji.ridez;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import il.ac.huji.ridez.contentClasses.RidezGroup;

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
        if (username != "" && password != "") {
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (e == null) {
                        Log.d("PARSE", "Logged in as " + parseUser.getUsername());
                    } else {
                        Toast.makeText(getApplicationContext(), "please login!!!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
