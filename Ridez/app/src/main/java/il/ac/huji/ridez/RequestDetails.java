package il.ac.huji.ridez;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.EditText;
import java.util.Date;



public class RequestDetails extends ActionBarActivity {
    EditText originEditText;
    EditText destinationEditText;
    TextView numOfPassengers;
    TextView dateAndTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        originEditText = (EditText) findViewById(R.id.originEditText);
        destinationEditText = (EditText) findViewById(R.id.destinationEditText);
        numOfPassengers = (TextView) findViewById(R.id.passengersNumTextview);
        dateAndTime = (TextView) findViewById(R.id.dateAndTimeTextView);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            originEditText.setText(extras.getString("origin"));
            destinationEditText.setText(extras.getString("destination"));
            Date date = new Date();
            date.setTime(extras.getLong("date"));
            dateAndTime.setText(dateAndTime.getText() + date.toString());
            int numOfPass = extras.getInt("amount");
            numOfPassengers.setText(numOfPassengers.getText().toString() + numOfPass);
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
