package il.ac.huji.ridez;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

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
        signUp = (Button) findViewById (R.id.signUpButton);
        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ProgressDialog pd = ProgressDialog.show(Login.this, "Please wait ...", "Signing up to the system ...", true);
                pd.setCancelable(false);
                String userText = userName.getText().toString();
                String passwordText = password.getText().toString();
                String emailText = email.getText().toString();
                if (emailText.isEmpty() || userText.isEmpty() || passwordText.isEmpty()) {
                    pd.dismiss();
                    showError("Please fill all fields!");
                    return;
                }
                ParseUser user = new ParseUser();
                user.setUsername(userText);
                user.setPassword(passwordText);
                user.setEmail(emailText);
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // Hooray! Let them use the app now.
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), "You have successfully signed up",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong
                            pd.dismiss();
                            showError(e.getMessage());
                        }
                    }
                });
            }

            private void showError(String errorString) {
                new AlertDialog.Builder(Login.this)
                        .setMessage(errorString)
                        .setTitle("Registration failed")
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
