package il.ac.huji.ridez;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import java.util.Calendar;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.NumberPicker.OnValueChangeListener;

import java.util.Date;


public class OfferRideActivity extends ActionBarActivity {
    EditText origin;
    EditText destination;
    int numOfPassengers = 0;
    final Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);
    int mHour = c.get(Calendar.HOUR_OF_DAY);
    int mMinute = c.get(Calendar.MINUTE);

    Button saveRequestButton;
    Button setDateButton;
    Button setTimeButton;
    Button  setAmountButton;

    TextView dateTextView;
    TextView timeTextView;
    int ourYear;
    int ourMonth;
    int ourDay;
    int ourHour;
    int ourMinute;
    ListView groupsListView;
    NumberPicker np;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_ride);
        origin = (EditText) findViewById(R.id.originOffering);
        destination = (EditText) findViewById(R.id.destinationOffering);
        dateTextView = (TextView)findViewById(R.id.dateTextViewOffering);
        timeTextView = (TextView) findViewById(R.id.timeTextViewOffering);




        np = (NumberPicker) findViewById(R.id.amountNumberPickerOffering);
        np.setMinValue(1);
        np.setMaxValue(5);
        np.setValue(1);
        setDateButton = (Button) findViewById(R.id.dateButtonOffering);
        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(OfferRideActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                ourYear = year;
                                ourMonth = monthOfYear;
                                ourDay = dayOfMonth;
                                dateTextView.setText(new StringBuilder()
                                        // Month is 0 based, just add 1
                                        .append(ourDay).append("-").append(ourMonth + 1).append("-")
                                        .append(ourYear).append(" "));
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });
        setTimeButton = (Button) findViewById(R.id.timeButtonOffering);
        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpd = new TimePickerDialog(OfferRideActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                ourHour = hourOfDay;
                                ourMinute = minute;
                                timeTextView.setText(new StringBuilder()
                                        // Month is 0 based, just add 1
                                        .append(ourHour).append(":").append(ourMinute));
                            }
                        }, mHour, mMinute, false);
                tpd.show();
            }
        });


        saveRequestButton = (Button) findViewById(R.id.saveRequestButtonOffering);
        saveRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if date is not null, all other fields valid
                //and then register the request in parse server;
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(0);
                cal.set(ourYear, ourMonth, ourDay, ourHour, ourMinute, 0);
                Date date = cal.getTime();

                //create request on server
                //save to db
                Intent requestDetails = new Intent(OfferRideActivity.this, RequestDetails.class);
                requestDetails.putExtra("origin", origin.getText().toString());
                requestDetails.putExtra("destination", destination.getText().toString());
                requestDetails.putExtra("date", date.getTime());
                requestDetails.putExtra("amount", np.getValue());
                OfferRideActivity.this.startActivity(requestDetails);
                OfferRideActivity.this.finish();

            }
        });
        groupsListView = (ListView) findViewById(R.id.groupListView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_offer_ride, menu);
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
