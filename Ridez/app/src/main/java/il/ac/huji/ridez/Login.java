package il.ac.huji.ridez;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Button;
import android.app.ProgressDialog;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;

public class Login extends ActionBarActivity {
    EditText email;
    EditText password;
    EditText userName;
    Button signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        email = (EditText) findViewById(R.id.emailEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
        userName = (EditText) findViewById(R.id.userNameEditText);
        signUp = (Button) findViewById (R.id.signInButton);
//        signUp.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                final ProgressDialog pd = ProgressDialog.show(Login.this, "Please wait ...", "Signing up to the system ...", true);
//                pd.setCancelable(false);
//                ParseUser user = new ParseUser();
//                user.setUsername("my name");
//                user.setPassword("my pass");
//                user.setEmail("email@example.com");
//
//// other fields can be set just like with ParseObject
//                user.put("phone", "650-253-0000");
//
//                user.signUpInBackground(new SignUpCallback() {
//                    public void done(ParseException e) {
//                        if (e == null) {
//                            // Hooray! Let them use the app now.
//                            pd.dismiss();
//                            Toast.makeText(getApplicationContext(), "You have successfully signed up",
//                                    Toast.LENGTH_LONG).show();
//                            finish();
//                        } else {
//                            // Sign up didn't succeed. Look at the ParseException
//                            // to figure out what went wrong
//                            pd.dismiss();
//                            Toast.makeText(getApplicationContext(), "We have encountered a problem. Please retry",
//                                    Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//            }
//
//        });
    }

    public void launchRingDialog(View view) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(Login.this, "Please wait ...", "Downloading Image ...", true);
        ringProgressDialog.setCancelable(true);


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
