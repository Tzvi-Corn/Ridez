package il.ac.huji.ridez;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.parse.ParseUser;


public class MainMenuActivity extends ActionBarActivity {
    Button request;
    Button offer;
    Button myGroups;
    Button myRides;
    Button register;
    Button login;
    Button logout;


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
                Intent requestActivity = new Intent(MainMenuActivity.this, OfferRequestRideActivity.class);
                requestActivity.putExtra("isRequest", true);
                // currentContext.startActivity(activityChangeIntent);

                MainMenuActivity.this.startActivity(requestActivity);
            }
        });
        offer = (Button)findViewById(R.id.buttonOfferRide);
        UIHelper.buttonEffect(offer);
        offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send me to offer screen
                Intent offerActivity = new Intent(MainMenuActivity.this, OfferRequestRideActivity.class);
                offerActivity.putExtra("isRequest", false);
                MainMenuActivity.this.startActivity(offerActivity);
            }
        });
        myGroups = (Button)findViewById(R.id.buttonMyGroups);
        UIHelper.buttonEffect(myGroups);
        myGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send me to myGroups screen
                startActivity(new Intent(getApplicationContext(), MyGroupsActivity.class));
            }
        });
        myRides = (Button)findViewById(R.id.buttonMyRides);
        UIHelper.buttonEffect(myRides);
        myRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send me to myRides screen
                startActivity(new Intent(getApplicationContext(), MyRidez.class));
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
                alertDialogBuilder.setTitle("Approve logout");
                // set dialog message
                alertDialogBuilder.setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("logout", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DB.setIsLoggedIn(false);
                                DB.emptyGroups();
                                ParseUser.logOutInBackground();
                                loggedIn();
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
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
//        Thread thread = new Thread(new Runnable(){
//            @Override
//            public void run() {
//                try {
//                    //Your code goes here
//                    double firstTime = GoogleDirectionsHelper.getDuration(31.7769849, 35.1925251, 31.8364605, 35.263231);
//                    double secondTime = GoogleDirectionsHelper.getDuration(31.7769849,35.1925251,31.8154942,35.2484252, 31.8273655,35.2456281, 31.8364605,35.263231);
//                    Log.v("v", "the first time is: " + firstTime + "the second time: " + secondTime);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

       // thread.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
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
