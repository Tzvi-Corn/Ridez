package il.ac.huji.ridez;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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

import java.util.Date;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import il.ac.huji.ridez.contentClasses.RidezGroup;


public class OfferRideActivity extends ActionBarActivity {
    EditText origin;
    EditText destination;
    Intent requestDetails;
    int numOfPassengers = 0;
    List<String> groupsList;
    final Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);
    int mHour = c.get(Calendar.HOUR_OF_DAY);
    int mMinute = c.get(Calendar.MINUTE);
    Button saveRequestButton;
    Button setDateButton;
    Button setStartTimeButton;
    Button setEndTimeButton;
    Button  setAmountButton;
    TextView dateTextView;
    TextView startTimeTextView;
    TextView endTimeTextView;
    int ourYear;
    int ourMonth;
    int ourDay;
    int startHour;
    int startMinute;
    int endHour;
    int endMinute;
    ListView groupsListView;
    NumberPicker np;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_ride);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //origin = (EditText) findViewById(R.id.originOffering);
        //destination = (EditText) findViewById(R.id.destinationOffering);
        final AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                String str = (String) adapterView.getItemAtPosition(position);
                Toast.makeText(OfferRideActivity.this, str, Toast.LENGTH_SHORT).show();
                autoCompView.setText(str);
            }
        });
        final AutoCompleteTextView autoCompViewOrigin = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewOrigin);
        autoCompViewOrigin.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompViewOrigin.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                String str = (String) adapterView.getItemAtPosition(position);
                Toast.makeText(OfferRideActivity.this, str, Toast.LENGTH_SHORT).show();
                autoCompViewOrigin.setText(str);
            }
        });
        dateTextView = (TextView)findViewById(R.id.dateTextViewOffering);
        startTimeTextView = (TextView) findViewById(R.id.startTimeTextViewOffering);
        endTimeTextView = (TextView) findViewById(R.id.endTimeTextViewOffering);




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
        setStartTimeButton = (Button) findViewById(R.id.startTimeButtonOffering);
        setStartTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpd = new TimePickerDialog(OfferRideActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String minuteS = "";
                                startHour = hourOfDay;
                                startMinute = minute;
                                if (startMinute < 10) {
                                    minuteS = "0" + startMinute;
                                } else {
                                    minuteS = Integer.toString(startMinute);
                                }
                                startTimeTextView.setText(new StringBuilder()
                                        // Month is 0 based, just add 1
                                        .append(startHour).append(":").append(minuteS));
                            }
                        }, mHour, mMinute, false);
                tpd.show();
            }
        });
        setEndTimeButton = (Button) findViewById(R.id.endTimeButtonOffering);
        setEndTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpd = new TimePickerDialog(OfferRideActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String minuteS = "";
                                endHour = hourOfDay;
                                endMinute = minute;
                                if (endMinute < 10) {
                                    minuteS = "0" + endMinute;
                                } else {
                                    minuteS = Integer.toString(endMinute);
                                }
                                endTimeTextView.setText(new StringBuilder()
                                        // Month is 0 based, just add 1
                                        .append(endHour).append(":").append(minuteS));
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
                if (dateTextView.getText().toString().startsWith("No") || startTimeTextView.getText().toString().startsWith("No")  || endTimeTextView.getText().toString().startsWith("No")) {
                    showError("Please fill in all the data, and then proceed", "Missing Data");
                    return;
                }
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(0);
                cal.set(ourYear, ourMonth, ourDay, startHour, startMinute, 0);
                Date date = cal.getTime();
                //create request on server
                //save to db
                int timeInterval = 0;
                if (startHour < endHour || (startHour == endHour && startMinute <=endMinute)) {
                    timeInterval = ((endHour - startHour) * 60 + endMinute - startMinute)/2;
                } else {
                    timeInterval = ((24 - startHour + endHour) * 60  - endMinute + startMinute)/2;
                }
                cal.add(Calendar.MINUTE, timeInterval);
                date = cal.getTime();
                requestDetails = new Intent(OfferRideActivity.this, RequestDetails.class);
                int len = groupsListView.getCount();
                SparseBooleanArray checked = groupsListView.getCheckedItemPositions();
                int checkedCounter = 0;
                ArrayList<RidezGroup> groups = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    if (checked.get(i)) {
                        checkedCounter++;
                        groups.add(DB.getGroups().get(i));
                    }
                }
                if (checkedCounter == 0) {
                    showError("Please fill in all the data, and then proceed", "Missing Data");
                    return;
                }
                if (autoCompViewOrigin.getText().toString().isEmpty() || autoCompView.getText().toString().isEmpty()) {
                    showError("Please fill in all the data, and then proceed", "Missing Data");
                    return;
                }
                Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                double olatitude = 0;
                double olongitude = 0;
                double dlatitude = 0;
                double dlongitude = 0;
                try {
                    List<Address> address = geoCoder.getFromLocationName(autoCompViewOrigin.getText().toString(), 1);
                    olatitude = address.get(0).getLatitude();
                    olongitude = address.get(0).getLongitude();
                    List<Address> address2 = geoCoder.getFromLocationName(autoCompView.getText().toString(), 1);
                    dlatitude = address2.get(0).getLatitude();
                    dlongitude = address2.get(0).getLongitude();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }

                requestDetails.putExtra("origin", autoCompViewOrigin.getText().toString());
                requestDetails.putExtra("destination", autoCompView.getText().toString());
                requestDetails.putExtra("date", date.getTime());
                requestDetails.putExtra("amount", np.getValue());
                requestDetails.putExtra("isRequest", false);
                requestDetails.putExtra("timeInterval", timeInterval);
                ArrayList<String> groupsStr = new ArrayList<String>();
                for (RidezGroup i : groups) {
                    groupsStr.add(i.getName());
                }
                requestDetails.putExtra("groups",groupsStr);


                final ParseObject newRide = new ParseObject("Ride");
                ParseObject origin = new ParseObject("Place");
                origin.put("address", autoCompViewOrigin.getText().toString());
                ParseGeoPoint originGeo = new ParseGeoPoint(olatitude, olongitude);
                origin.put("point", originGeo);
                ParseObject destination = new ParseObject("Place");
                destination.put("address", autoCompView.getText().toString());
                ParseGeoPoint destinationGeo = new ParseGeoPoint(dlatitude, dlongitude);
                destination.put("point", destinationGeo);
                newRide.put("from", origin);
                newRide.put("to", destination);
                newRide.put("date", date);
                newRide.put("request", false);
                newRide.put("passengers", np.getValue());
                newRide.put("user", ParseUser.getCurrentUser());
                newRide.put("timeInterval", timeInterval);
                ParseRelation<RidezGroup> checked_groups = newRide.getRelation("groups");
                for (int i = 0; i < groups.size(); ++i) {
                    checked_groups.add(groups.get(i));
                }
                final ProgressDialog pd = ProgressDialog.show(OfferRideActivity.this, "Please wait ...", "Saving your offer in our systems", true);

                newRide.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        // Log.d(TAG, "new group!!");
                        pd.dismiss();
                        OfferRideActivity.this.startActivity(requestDetails);
                        OfferRideActivity.this.finish();
                    }
                });


            }
        });
        groupsListView = (ListView) findViewById(R.id.offerGroupListView);
        groupsListView.setChoiceMode(groupsListView.CHOICE_MODE_MULTIPLE);
        groupsList = new ArrayList<String>();
        for (int i = 0; i < DB.getGroups().size(); ++i) {
            groupsList.add(DB.getGroups().get(i).getName());
        }
        groupsListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, groupsList));
        groupsListView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
    }

    private void showError(String errorString, String errorTitle) {
        new AlertDialog.Builder(OfferRideActivity.this)
                .setMessage(errorString)
                .setTitle(errorTitle)
                .setCancelable(true)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
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
