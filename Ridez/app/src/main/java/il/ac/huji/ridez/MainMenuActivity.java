package il.ac.huji.ridez;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.*;

import com.parse.ParseUser;

import java.util.Timer;
import java.util.TimerTask;


public class MainMenuActivity extends ActionBarActivity {
    Button request;
    Button offer;
    Button myGroups;
    Button myRides;
    Button register;
    Button login;
    Button logout;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_menu);
        request = (Button) findViewById(R.id.buttonRequestRide);
        UIHelper.buttonEffect(request);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send me to request screen
                Intent requestActivity = new Intent(MainMenuActivity.this, NewRideActivity.class);
                requestActivity.putExtra("isRequest", true);
                if (ridezApp.loadedGroups || !DB.isLoggedIn()) {
                    // currentContext.startActivity(activityChangeIntent);
                    MainMenuActivity.this.startActivity(requestActivity);
                } else {
                    pd = ProgressDialog.show(MainMenuActivity.this, getString(R.string.pleaseWait), getString(R.string.loadingPersonalData), true);
                    final Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if (ridezApp.loadedGroups) {
                                pd.dismiss();
                                timer.cancel();
                                Intent requestActivity = new Intent(MainMenuActivity.this, NewRideActivity.class);
                                requestActivity.putExtra("isRequest", true);
                                MainMenuActivity.this.startActivity(requestActivity);
                            }
                        }
                    }, 1000, 1000);
                }
            }
        });
        offer = (Button)findViewById(R.id.buttonOfferRide);
        UIHelper.buttonEffect(offer);
        offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send me to offer screen
                Intent offerActivity = new Intent(MainMenuActivity.this, NewRideActivity.class);
                offerActivity.putExtra("isRequest", false);
                if (ridezApp.loadedGroups || !DB.isLoggedIn()) {
                    MainMenuActivity.this.startActivity(offerActivity);
                } else {
                    pd = ProgressDialog.show(MainMenuActivity.this, getString(R.string.pleaseWait), getString(R.string.loadingPersonalData), true);
                    final Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if (ridezApp.loadedGroups) {
                                pd.dismiss();
                                timer.cancel();
                                Intent offerActivity = new Intent(MainMenuActivity.this, NewRideActivity.class);
                                offerActivity.putExtra("isRequest", false);
                                MainMenuActivity.this.startActivity(offerActivity);
                            }
                        }
                    }, 1000, 1000);
                }
            }
        });
        myGroups = (Button)findViewById(R.id.buttonMyGroups);
        UIHelper.buttonEffect(myGroups);
        myGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ridezApp.loadedGroups || !DB.isLoggedIn()) {
                    //send me to myGroups screen
                    startActivity(new Intent(getApplicationContext(), MyGroupsActivity.class));
                } else {
                    pd = ProgressDialog.show(MainMenuActivity.this, getString(R.string.pleaseWait), getString(R.string.loadingPersonalData), true);
                    final Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if (ridezApp.loadedGroups) {
                                pd.dismiss();
                                timer.cancel();
                                startActivity(new Intent(getApplicationContext(), MyGroupsActivity.class));

                            }
                        }
                    }, 1000, 1000);
                }

            }
        });
        myRides = (Button)findViewById(R.id.buttonMyRides);
        UIHelper.buttonEffect(myRides);
        myRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send me to myRides screen
                startActivity(new Intent(getApplicationContext(), MyRidezActivity.class));
            }
        });
        register = (Button)findViewById(R.id.buttonRegister);
        UIHelper.buttonEffect(register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send me to register screen
                Intent registrationActivity = new Intent(MainMenuActivity.this, RegistrationActivity.class);
                MainMenuActivity.this.startActivity(registrationActivity);
            }
        });
        login = (Button)findViewById(R.id.buttonLogin);
        UIHelper.buttonEffect(login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send me to register screen
                Intent loginActivity = new Intent(MainMenuActivity.this, LoginActivity.class);
                MainMenuActivity.this.startActivity(loginActivity);
            }
        });
        logout = (Button)findViewById(R.id.buttonLogOut);
        UIHelper.buttonEffect(logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainMenuActivity.this);
                alertDialogBuilder.setTitle(R.string.approveLogout);
                // set dialog message
                alertDialogBuilder.setMessage(R.string.areYousureLogout)
                        .setCancelable(false)
                        .setPositiveButton(R.string.Logout, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DB.setIsLoggedIn(false);
                                DB.emptyGroups();
                                ParseUser.logOutInBackground();
                                loggedIn();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        loggedIn();
    }

        private void loggedIn () {
            if(DB.isLoggedIn()) {
                login.setVisibility(View.GONE);
                register.setVisibility(View.GONE);
                logout.setVisibility(View.VISIBLE);
            } else {
                login.setVisibility(View.VISIBLE);
                register.setVisibility(View.VISIBLE);
                logout.setVisibility(View.GONE);
            }
        }
    @Override
    protected void onResume() {
        super.onResume();
        loggedIn();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }
}
