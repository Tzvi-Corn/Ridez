package il.ac.huji.ridez;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;

import java.io.IOException;
import java.util.Calendar;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.Toast;
import java.util.List;
import java.util.ArrayList;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Date;
import java.util.Locale;

import il.ac.huji.ridez.GoogleDirections.GoogleDirectionsHelper;
import il.ac.huji.ridez.GoogleDirections.GoogleMapsAPIGeocode;
import il.ac.huji.ridez.contentClasses.RidezGroup;


public class RequestRideActivity extends ActionBarActivity {
    EditText origin;
    EditText destination;
    int numOfPassengers = 0;
    int taskCounter = 0;
    int totalAmount = 0;
    ProgressDialog pd;
    static final long ONE_MINUTE_IN_MILLIS=60000;//millisecs
    //    Button passengerButton1;
//    Button passengerButton2;
//    Button passengerButton3;
//    Button passengerButton4;
    Intent requestDetails;
    double olatitude = 0;
    double olongitude = 0;
    double dlatitude = 0;
    double dlongitude = 0;
    Date date;
    ArrayList<RidezGroup> groups;
final Calendar c = Calendar.getInstance();
   int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);
    int mHour = c.get(Calendar.HOUR_OF_DAY);
    int mMinute = c.get(Calendar.MINUTE);
    List<String> groupsList;
    Button saveRequestButton;
    Button setDateButton;
    Button setTimeButton;
    Button  setAmountButton;
    Boolean btn1 = false;
    Boolean btn2 = false;
    Boolean btn3 = false;
    Boolean btn4 = false;
    TextView dateTextView;
    TextView timeTextView;
    int ourYear;
    int ourMonth;
    int ourDay;
    int ourHour;
    int ourMinute;
    ListView groupsListView;
    NumberPicker np;
    Date requestDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_ride);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dateTextView = (TextView)findViewById(R.id.dateTextView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        final AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.requestDestination);
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                String str = (String) adapterView.getItemAtPosition(position);
                Toast.makeText(RequestRideActivity.this, str, Toast.LENGTH_SHORT).show();
                autoCompView.setText(str);
            }
        });
        final AutoCompleteTextView autoCompViewOrigin = (AutoCompleteTextView) findViewById(R.id.requestOrigin);
        autoCompViewOrigin.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompViewOrigin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                String str = (String) adapterView.getItemAtPosition(position);
                Toast.makeText(RequestRideActivity.this, str, Toast.LENGTH_SHORT).show();
                autoCompViewOrigin.setText(str);
            }
        });

        np = (NumberPicker) findViewById(R.id.amountNumberPicker);
        np.setMinValue(1);
        np.setMaxValue(5);
        np.setValue(1);
        setDateButton = (Button) findViewById(R.id.dateButton);
        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(RequestRideActivity.this,
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
        setTimeButton = (Button) findViewById(R.id.timeButton);
        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpd = new TimePickerDialog(RequestRideActivity.this,
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


        saveRequestButton = (Button) findViewById(R.id.saveRequestButton);
        saveRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if date is not null, all other fields valid
                //and then register the request in parse server;
                if (dateTextView.getText().toString().startsWith("No") || timeTextView.getText().toString().startsWith("No")) {
                    showError("Please fill in all the data, and then proceed", "Missing Data");
                    return;
                }
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(0);
                cal.set(ourYear, ourMonth, ourDay, ourHour, ourMinute, 0);
                date = cal.getTime();

                //create request on server
                //save to db
                requestDetails = new Intent(RequestRideActivity.this, RequestDetails.class);
                int len = groupsListView.getCount();
                SparseBooleanArray checked = groupsListView.getCheckedItemPositions();
                int checkedCounter = 0;
                groups = new ArrayList<>();
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
                olatitude = 0;
                olongitude = 0;
                dlatitude = 0;
                dlongitude = 0;
                try {
                    if (Geocoder.isPresent()) {
                        List<Address> address = geoCoder.getFromLocationName(autoCompViewOrigin.getText().toString(), 1);
                        olatitude = address.get(0).getLatitude();
                        olongitude = address.get(0).getLongitude();
                        List<Address> address2 = geoCoder.getFromLocationName(autoCompView.getText().toString(), 1);
                        dlatitude = address2.get(0).getLatitude();
                        dlongitude = address2.get(0).getLongitude();
                    } else {
                        double[] originGeo = GoogleMapsAPIGeocode.getLatLongFromAddress(autoCompViewOrigin.getText().toString());
                        olatitude = originGeo[0];
                        olongitude = originGeo[1];
                        double[] destGeo = GoogleMapsAPIGeocode.getLatLongFromAddress(autoCompView.getText().toString());
                        dlatitude = destGeo[0];
                        dlongitude = destGeo[1];
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                requestDetails.putExtra("origin", autoCompViewOrigin.getText().toString());
                requestDetails.putExtra("destination", autoCompView.getText().toString());
                requestDetails.putExtra("date", date.getTime());
                requestDetails.putExtra("isRequest", true);
                requestDetails.putExtra("amount", np.getValue());

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
                newRide.put("request", true);
                newRide.put("passengers", np.getValue());
                newRide.put("user", ParseUser.getCurrentUser());
                newRide.put("timeInterval", 60);
                ParseRelation<RidezGroup> checked_groups = newRide.getRelation("groups");
                for (int i = 0; i < groups.size(); ++i) {
                    checked_groups.add(groups.get(i));
                }
                pd = ProgressDialog.show(RequestRideActivity.this, "Please wait ...", "Saving your request in our systems", true);

                newRide.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        // Log.d(TAG, "new group!!");
                        for (int i = 0; i < groups.size(); ++i) {
                            totalAmount = groups.size();
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR);
                            int mMonth = c.get(Calendar.MONTH);
                            int mDay = c.get(Calendar.DAY_OF_MONTH);
                            int mHour = c.get(Calendar.HOUR_OF_DAY);
                            int mMinute = c.get(Calendar.MINUTE);
                            c.set(mYear, mMonth, mDay, mHour, mMinute);
                            c.add(Calendar.DATE, -1);
                            Date d = c.getTime();
                            requestDetails.putExtra("rideId", newRide.getObjectId());
                            RidezGroup g = groups.get(i);
                            ParseQuery<ParseObject> q = ParseQuery.getQuery("Ride");
                            q.whereEqualTo("groups", ParseObject.createWithoutData("Group", g.getObjectId()));
                            q.whereEqualTo("request", false);
                            q.whereGreaterThan("date", d);
                            q.include("from");
                            q.include("to");
                            q.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> rideList, ParseException e) {
                                    final List<ParseObject> rideList2 = rideList;
                                    if (e == null) {
                                        Thread thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                for (ParseObject ride: rideList2) {
                                                    Date offerDate = ride.getDate("date");
                                                    int timeInterval = (int) ride.getDouble("timeInterval");
                                                    long t= offerDate.getTime();
                                                    Date afterAddingMins=new Date(t + (timeInterval * ONE_MINUTE_IN_MILLIS));
                                                    Date afterMinusMinutes = new Date(t - (timeInterval * ONE_MINUTE_IN_MILLIS));
                                                    long requestT = date.getTime();
                                                    Date afterAddingMinsRequest=new Date(requestT + (timeInterval * ONE_MINUTE_IN_MILLIS));
                                                    Date afterMinusMinutesRequest = new Date(requestT - (timeInterval * ONE_MINUTE_IN_MILLIS));
                                                    ParseGeoPoint fromGeo = ride.getParseObject("from").getParseGeoPoint("point");
                                                    ParseGeoPoint toGeo = ride.getParseObject("to").getParseGeoPoint("point");
                                                    if ((!afterMinusMinutesRequest.after(afterAddingMins)) && (!afterMinusMinutes.after(afterAddingMinsRequest))) {
                                                        double hisOldDuration = GoogleDirectionsHelper.getDuration(fromGeo.getLatitude(), fromGeo.getLongitude(), toGeo.getLatitude(), toGeo.getLongitude()) / 60;
                                                        double newDuration = GoogleDirectionsHelper.getDuration(fromGeo.getLatitude(), fromGeo.getLongitude(), olatitude, olongitude , dlatitude, dlongitude, toGeo.getLatitude(), toGeo.getLongitude())/60;
                                                        if (newDuration - hisOldDuration < 15) {
                                                            ParseObject pm = new ParseObject("potentialMatch");
                                                            pm.put("isConfirmed", false);
                                                            ParseRelation<ParseObject> offerRelation = pm.getRelation("offer");
                                                            ParseRelation<ParseObject> requestRelation = pm.getRelation("request");
                                                            offerRelation.add(ride);
                                                            requestRelation.add(newRide);
                                                            try {
                                                                pm.save();
                                                            } catch (Exception ex) {
                                                                Log.v("v", "fewlvkm");
                                                            }
                                                        }

                                                    }
                                                }
                                                increaseCounter();
                                            }
                                        });
                                        thread.start();
                                    }   else {
                                        Log.d("PARSE", "error getting matching rides");
                                        increaseCounter();
                                    }
                                }
                            });
                        }

                    }


                });
            }
        });
        groupsListView = (ListView) findViewById(R.id.groupListView);
        groupsListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        groupsList = new ArrayList<>();
        for (int i = 0; i < DB.getGroups().size(); ++i) {
            groupsList.add(DB.getGroups().get(i).getName());
        }
        groupsListView.setAdapter(new ArrayAdapter<>(this,
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
        new AlertDialog.Builder(RequestRideActivity.this)
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


    private synchronized void increaseCounter() {
        taskCounter++;
        if (taskCounter == totalAmount) {
            pd.dismiss();
            RequestRideActivity.this.startActivity(requestDetails);
            RequestRideActivity.this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_request_ride, menu);
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
