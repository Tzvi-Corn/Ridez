package il.ac.huji.ridez;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

import il.ac.huji.ridez.contentClasses.RidezGroup;

public class LoginActivity extends ActionBarActivity {
    EditText password;
    EditText userName;
    Button signIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        password = (EditText) findViewById(R.id.loginPasswordEditText);
        userName = (EditText) findViewById(R.id.loginEmailEditText);
        signIn = (Button) findViewById (R.id.signInButton);
        signIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ProgressDialog pd = ProgressDialog.show(LoginActivity.this, getString(R.string.pleaseWait), getString(R.string.signingIn), true);
                pd.setCancelable(false);
                final String userText = userName.getText().toString();
                final String passwordText = password.getText().toString();
                if (userText.isEmpty() || passwordText.isEmpty()) {
                    pd.dismiss();
                    showError(getString(R.string.fill_all_fields));
                    return;
                }
                ParseUser.logInInBackground(userText, passwordText, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null) {
                            // save parseUser
                            try {
                                parseUser.pin();
                                // Associate the device with a user
                                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                installation.put("user", ParseUser.getCurrentUser());
                                installation.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        // Hooray! Let them use the app now.
                                        pd.dismiss();
                                        Toast.makeText(getApplicationContext(), R.string.succesfullSignIn,
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            } catch (ParseException e1) {
                                Toast.makeText(getApplicationContext(), R.string.loginFailed, Toast.LENGTH_LONG).show();
                                pd.dismiss();
                            }
                            ParseQuery<RidezGroup> query = ParseQuery.getQuery("Group");
                            query.whereEqualTo("users", parseUser);
                            query.orderByAscending("name");
                            // execute the query
                            query.findInBackground(new FindCallback<RidezGroup>() {
                                public void done(List<RidezGroup> groupList, ParseException e) {
                                    if (e == null) {
                                        DB.setGroups(groupList);
                                        DB.setIsLoggedIn(true);
                                        ridezApp.loadedGroups = true;
                                    } else {
                                        Log.d("PARSE", "error getting groups");
                                    }
                                    finish();
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.loginFailed, Toast.LENGTH_LONG).show();
                            pd.dismiss();
                        }
                    }
                });
            }

            private void showError(String errorString) {
                new AlertDialog.Builder(LoginActivity.this)
                        .setMessage(errorString)
                        .setTitle(R.string.loginFailed)
                        .setCancelable(true)
                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
            }

        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }
}
