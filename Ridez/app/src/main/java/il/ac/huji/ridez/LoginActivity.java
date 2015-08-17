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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

import il.ac.huji.ridez.sqlHelpers.GroupInfo;

public class LoginActivity extends ActionBarActivity {
    EditText password;
    EditText userName;
    Button signIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        password = (EditText) findViewById(R.id.loginPasswordEditText);
        userName = (EditText) findViewById(R.id.loginUserNameEditText);
        signIn = (Button) findViewById (R.id.signInButton);
        signIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ProgressDialog pd = ProgressDialog.show(LoginActivity.this, "Please wait ...", "Signing in to the system ...", true);
                pd.setCancelable(false);
                final String userText = userName.getText().toString();
                final String passwordText = password.getText().toString();
                if (userText.isEmpty() || passwordText.isEmpty()) {
                    pd.dismiss();
                    showError("Please fill all fields!");
                    return;
                }
                ParseUser.logInInBackground(userText, passwordText, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null) {
                            // save username & password
                            Context context = getApplicationContext();
                            SharedPreferences pref = context.getSharedPreferences(getString(R.string.pref_username), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("username", userText);
                            editor.putString("password", passwordText);
                            editor.apply();
                            // Hooray! Let them use the app now.
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), "You have successfully signed in",
                                    Toast.LENGTH_LONG).show();
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
                                            ParseObject group = groupList.get(i);
                                            String name = group.getString("name");
                                            String description = group.getString("description");
                                            ParseFile icon = group.getParseFile("icon");
                                            String id = group.getObjectId();
                                            byte[] iconData = null;
                                            try {
                                                iconData = icon.getData();
                                            } catch (Exception ex) {

                                            }
                                            Bitmap bitmap = null;
                                            if (iconData != null) {
                                                bitmap = BitmapFactory.decodeByteArray(iconData, 0, iconData.length);
                                            }
                                            tempList.add(new GroupInfo(name, description, bitmap, id));
                                        }
                                        DB.setGroups(tempList);
                                    } else {
                                        Log.d("PARSE", "error getting groups");
                                    }
                                    finish();
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Please login!!!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            private void showError(String errorString) {
                new AlertDialog.Builder(LoginActivity.this)
                        .setMessage(errorString)
                        .setTitle("Login failed")
                        .setCancelable(true)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
