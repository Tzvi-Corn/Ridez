package il.ac.huji.ridez;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.Timer;
import java.util.TimerTask;

public class RegistrationActivity extends ActionBarActivity {
    EditText email;
    EditText password;
    EditText fullName;
    EditText phoneNum;
    Button signUp;
    BroadcastReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        email = (EditText) findViewById(R.id.emailEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
        fullName = (EditText) findViewById(R.id.fullNameEditText);
        phoneNum = (EditText) findViewById(R.id.phoneNumEditText);
        signUp = (Button) findViewById (R.id.signUpButton);
        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ProgressDialog pd = ProgressDialog.show(RegistrationActivity.this, "Please wait ...", "Verifying yur phone number...", true, false);
                final String fullnameText = fullName.getText().toString();
                final String passwordText = password.getText().toString();
                final String emailText = email.getText().toString();
                final String phoneNumText = phoneNum.getText().toString();
                if (emailText.isEmpty() || fullnameText.isEmpty() || passwordText.isEmpty() || phoneNumText.isEmpty()) {
                    pd.dismiss();
                    showError("Please fill all fields!");
                    return;
                }
                final Timer timer = new Timer();
                final String verificationCode = Long.toHexString(Double.doubleToLongBits(Math.random()));
                final String msgText = "Ridez verification code: " + verificationCode;
                smsReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Bundle extras = intent.getExtras();

                        if (extras == null)
                            return;

                        Object[] pdus = (Object[]) extras.get("pdus");
                        if (pdus != null) {
                            SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdus[0]);
                            String origNumber = msg.getOriginatingAddress();
                            String msgBody = msg.getMessageBody();
                            if (msgText.equals(msgBody)) {
                                timer.cancel();
                                context.unregisterReceiver(smsReceiver);
                                abortBroadcast();
                                registerUser(pd, emailText, passwordText, fullnameText, phoneNumText);
                            }
                        }
                    }
                };
                IntentFilter smsIntentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
                smsIntentFilter.setPriority(Integer.MAX_VALUE);
                RegistrationActivity.this.registerReceiver(smsReceiver, smsIntentFilter);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        final AlertDialog.Builder alert = new AlertDialog.Builder(RegistrationActivity.this);

                        final EditText edittext = new EditText(RegistrationActivity.this);
                        alert.setMessage("Cannot verify your phone number. Please insert the code manually:");
                        alert.setTitle("Phone number verification");

                        alert.setView(edittext);

                        alert.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String codeText = edittext.getText().toString();
                                if (verificationCode.equals(codeText)) {
                                    dialog.dismiss();
                                    pd.show();
                                    registerUser(pd, emailText, passwordText, fullnameText, phoneNumText);
                                } else {
                                    showError("The verification code is not match. Try again");
                                    edittext.setText("");
                                }
                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });

                        alert.show();
                        showError("Cannot verify your phone number. Try again");
                        RegistrationActivity.this.unregisterReceiver(smsReceiver);
                    }
                }, 60 * 1000);
                SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(phoneNumText, null, msgText, null, null);
            }

        });
    }

    private void showError(String errorString) {
        new AlertDialog.Builder(RegistrationActivity.this)
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

    private void registerUser(final ProgressDialog pd, final String emailText, final String passwordText, final String fullnameText, final String phoneNumText) {
        pd.setMessage("Phone number verified. Signing up to the system...");
        final ParseUser user = new ParseUser();
        user.setUsername(emailText);
        user.setPassword(passwordText);
        user.setEmail(emailText);
        user.put("fullname", fullnameText);
        user.put("phoneNum", phoneNumText);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    ParseUser.logInInBackground(emailText, passwordText, new LogInCallback() {
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
                                            if (e == null) {
                                                // Hooray! Let them use the app now.
                                                pd.dismiss();
                                                Toast.makeText(getApplicationContext(), "You have successfully signed up",
                                                        Toast.LENGTH_LONG).show();
                                                finish();
                                            } else {
                                                pd.dismiss();
                                                showError(e.getMessage());
                                            }
                                        }
                                    });
                                } catch (ParseException e1) {
                                    pd.dismiss();
                                    showError(e1.getMessage());
                                }
                            } else {
                                pd.dismiss();
                                showError(e.getMessage());
                            }
                        }
                    });
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    pd.dismiss();
                    showError(e.getMessage());
                }
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
