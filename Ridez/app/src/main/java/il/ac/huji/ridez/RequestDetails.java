package il.ac.huji.ridez;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import il.ac.huji.ridez.contentClasses.RidezGroup;


public class RequestDetails extends ActionBarActivity {
    TextView originTextView;
    TextView destinationTextView;
    TextView dateTextView;
    TextView timeTextView;
    TextView numberPassengersTextView;
    ListView groupsListView;
    ListView matchesListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        originTextView = (TextView) findViewById(R.id.rideOrigin);
        destinationTextView = (TextView) findViewById(R.id.rideDestination);
        dateTextView = (TextView) findViewById(R.id.rideDateTextView);
        timeTextView = (TextView) findViewById(R.id.rideTimeTextView);
        numberPassengersTextView = (TextView) findViewById(R.id.ridePassengersNumTextview);

        groupsListView = (ListView) findViewById(R.id.groupListView);
        matchesListView = (ListView) findViewById(R.id.ridePotentialMatchesListView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            originTextView.append(extras.getString("origin"));
            destinationTextView.append(extras.getString("destination"));
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(extras.getLong("date"));
            int timeInterval = extras.getInt("timeInterval");
            cal.add(Calendar.MINUTE, -timeInterval);
            Date startDate = cal.getTime();
            cal.add(Calendar.MINUTE, 2 * timeInterval);
            ArrayList<String> groups = extras.getStringArrayList("groups");
            groupsListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, groups));

            Date endDate = cal.getTime();
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yy");
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");

            dateTextView.append(sdf1.format(startDate));
            timeTextView.append(sdf2.format(startDate) + " to" + sdf2.format(endDate));
            int numOfPass = extras.getInt("amount");
            numberPassengersTextView.append(Integer.toString(numOfPass));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_request_details, menu);
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
