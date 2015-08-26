package il.ac.huji.ridez;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import il.ac.huji.ridez.adpaters.PotentialMatchAdapter;
import il.ac.huji.ridez.adpaters.RidezGroupArrayAdapter;
import il.ac.huji.ridez.contentClasses.PotentialMatch;
import il.ac.huji.ridez.contentClasses.RidezGroup;

public class RideDetails extends ActionBarActivity {
    TextView pickup;
    TextView destination;
    TextView dateAndTime;
    TextView numPassengers;
    ListView listOfPossibles;
    ListView listOfGroups;
    ProgressDialog pd;
    ArrayList<PotentialMatch> tempList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_details);
        Button saveChangesButton = (Button) findViewById(R.id.saveChangesButton);
        UIHelper.buttonEffect(saveChangesButton);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        final ProgressDialog pd = ProgressDialog.show(RideDetails.this, "Please Wait", "Saving changes", true);
                        for (PotentialMatch pmatch : tempList) {
                            ParseObject pma = ParseObject.createWithoutData("potentialMatch", pmatch.id);
                            pma.put("isConfirmed", pmatch.isConfirmed);
                            try {
                                pma.save();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        pd.dismiss();
                    }
                });

            }
        });
        pd = ProgressDialog.show(RideDetails.this, "Please wait ...", "Getting your ride details", true);
        String id = getIntent().getExtras().getString("rideId");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Ride");
        query.include("from").include("to");
        query.getInBackground(id, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, com.parse.ParseException e) {
                if (e == null) {
                    pickup.setText("Pickup: " + object.getParseObject("from").getString("address"));
                    destination.setText("Destination: " +  object.getParseObject("to").getString("address"));
                    numPassengers.setText("Number of passengers: " + object.getInt("passengers"));
                    dateAndTime.setText("Date and Time: " + object.getDate("date"));
                } else {
                    // something went wrong
                    Log.v("v", "Oh boy");
                }
            }
        });
        //get ride with this specific id
        pickup = (TextView) findViewById(R.id.ridePickup);
        destination = (TextView) findViewById(R.id.rideDestination);
        dateAndTime = (TextView) findViewById(R.id.rideDateAndTimeTextView);
        numPassengers = (TextView) findViewById(R.id.ridePassengersNumTextview);
        listOfPossibles = (ListView) findViewById(R.id.possibleConnectionsListView);
        listOfPossibles.setOnTouchListener(new ListView.OnTouchListener() {
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
        //listOfPossibles.setHeaderDividersEnabled(true);
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header, listOfPossibles, false);
        listOfPossibles.addHeaderView(header, null, false);
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("potentialMatch");
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("potentialMatch");
        query1.whereEqualTo("offer", ParseUser.createWithoutData("Ride", id));
        query2.whereEqualTo("request", ParseUser.createWithoutData("Ride", id));
        ArrayList<ParseQuery<ParseObject>> ql = new ArrayList<>();
        ql.add(query1);
        ql.add(query2);
        ParseQuery<ParseObject> query3 = ParseQuery.or(ql);
        query3.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> possibleMatchList, com.parse.ParseException e) {
                if (e == null) {
                    tempList = new ArrayList<>();
                    for (int i = 0; i < possibleMatchList.size(); ++i) {
                        ParseRelation<ParseObject> offerRelation =  possibleMatchList.get(i).getRelation("offer");
                        ParseRelation<ParseObject> requestRelation =  possibleMatchList.get(i).getRelation("request");
                        ParseObject rideOffer = null;
                        ParseObject rideRequest = null;
                        try {
                            rideOffer = offerRelation.getQuery().include("user").include("from").include("to").getFirst();
                            rideRequest = requestRelation.getQuery().include("user").include("from").include("to").getFirst();
                        } catch (Exception ex) {
                            Log.v("v", "wcweb");
                            continue;
                        }
                        ParseUser offerUser = null;
                        ParseObject testUser = rideOffer.getParseObject("user");
                        offerUser = rideOffer.getParseUser("user");
                        ParseUser requestUser = null;
                        requestUser = rideRequest.getParseUser("user");
                        PotentialMatch potentialMatch = new PotentialMatch();
                        potentialMatch.id = possibleMatchList.get(i).getObjectId();
                        if (offerUser == null || requestUser == null) {
                            continue;
                        }
                        try {
                            potentialMatch.offerFullName = offerUser.getString("fullname");
                        } catch (Exception ex) {
                            Log.v("v", "cewc");
                        }
                        potentialMatch.offerUserEmail = offerUser.getString("email");
                        potentialMatch.requestFullName = requestUser.getString("fullname");
                        potentialMatch.requestUserEmail = requestUser.getString("email");
                        potentialMatch.isConfirmed = possibleMatchList.get(i).getBoolean("isConfirmed");
                        ParseObject offerFrom = null;
                        ParseObject offerTo = null;
                        ParseObject requestFrom = null;
                        ParseObject requestTo = null;
                        try {
                            offerFrom = rideOffer.getParseObject("from");
                            offerTo = rideOffer.getParseObject("to");
                            requestFrom = rideRequest.getParseObject("from");
                            requestTo = rideRequest.getParseObject("to");
                        } catch (Exception ex) {
                            Log.v("v", "Oh boy");
                        }
                        if (offerFrom == null || offerTo == null || requestFrom == null || requestTo == null) {
                            continue;
                        }
                        potentialMatch.offerFromAddress = offerFrom.getString("address");
                        potentialMatch.offerToAddress = offerTo.getString("address");
                        potentialMatch.requestFromAddress = requestFrom.getString("address");
                        potentialMatch.requestToAddress = requestTo.getString("address");
                        potentialMatch.offerDate = rideOffer.getDate("date");
                        potentialMatch.requestdate = rideRequest.getDate("date");
                        tempList.add(potentialMatch);
                    }
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            PotentialMatchAdapter adapter = new PotentialMatchAdapter(RideDetails.this, tempList);
                            listOfPossibles.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            pd.dismiss();
                        }
                    });

                } else {
                    pd.dismiss();
                    Log.d("PARSE", "error getting possibles");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ride_details, menu);
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
