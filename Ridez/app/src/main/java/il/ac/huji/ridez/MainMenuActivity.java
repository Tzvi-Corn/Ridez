package il.ac.huji.ridez;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.content.Intent;


public class MainMenuActivity extends ActionBarActivity {
    Button request;
    Button offer;
    Button myGroups;
    Button myRides;
    Button register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        request = (Button) findViewById(R.id.buttonRequestRide);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send me to request screen
                Intent requestActivity = new Intent(MainMenuActivity.this, RequestRideActivity.class);

                // currentContext.startActivity(activityChangeIntent);

                MainMenuActivity.this.startActivity(requestActivity);
            }
        });
        offer = (Button)findViewById(R.id.buttonOfferRide);
        offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send me to offer screen
            }
        });
        myGroups = (Button)findViewById(R.id.buttonMyGroups);
        myGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send me to myGroups screen
            }
        });
        myRides = (Button)findViewById(R.id.buttonMyRides);
        myRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send me to myRides screen
            }
        });
        register = (Button)findViewById(R.id.buttonRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send me to register screen
                Intent loginActivity = new Intent(MainMenuActivity.this, Login.class);

                // currentContext.startActivity(activityChangeIntent);

                MainMenuActivity.this.startActivity(loginActivity);
            }
        });
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
