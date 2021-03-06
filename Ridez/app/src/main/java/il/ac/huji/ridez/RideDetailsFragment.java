package il.ac.huji.ridez;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import il.ac.huji.ridez.contentClasses.PotentialMatch;

public class RideDetailsFragment extends Fragment {
    TextView pickup;
    TextView destination;
    TextView date;
    TextView time;
    TextView numPassengers;
    Activity activity;
    ListView listOfPossibles;
    ListView listOfGroups;
    ListView groupListView;
    ArrayList<PotentialMatch> tempList;
    View rootView;
    ArrayList<String> groups;
    ProgressDialog pd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.activity_ride_details, container, false);
        groupListView = (ListView) rootView.findViewById(R.id.groupListView);
        activity = getActivity();
        pd = ProgressDialog.show(getActivity(), getString(R.string.pleaseWait), getString(R.string.loadinYourData), true);
        groups = new ArrayList<>();
        String id = getActivity().getIntent().getExtras().getString("rideId");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Ride");
        query.include("from").include("to");
        query.getInBackground(id, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, com.parse.ParseException e) {
                if (e == null) {
                    ParseRelation groupsRelation = object.getRelation("groups");
                    ParseQuery gq = groupsRelation.getQuery();
                    gq.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            for (ParseObject group : list) {
                                groups.add(group.getString("name"));
                            }
                            groupListView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, groups));
                        }

                    });

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(object.getDate("date").getTime());
                    double timeInterval = object.getDouble("timeInterval");
                    cal.add(Calendar.MINUTE, (int)(-timeInterval));
                    Date startDate = cal.getTime();
                    cal.add(Calendar.MINUTE, 2 * (int) timeInterval);
                    Date endDate = cal.getTime();

                    date.setText(Toolbox.dateToLongDateString(startDate));
                    time.setText(Toolbox.dateToTimeString(startDate) + " to" + Toolbox.dateToTimeString(endDate));
                    pickup.setText(object.getParseObject("from").getString("address"));
                    destination.setText(object.getParseObject("to").getString("address"));
                    numPassengers.setText(Integer.toString(object.getInt("passengers")));
                } else {
                    // something went wrong
                    Log.v("v", "Oh boy");
                }
                pd.dismiss();
            }
        });
        //get ride with this specific id
        pickup = (TextView) rootView.findViewById(R.id.rideOrigin);
        destination = (TextView) rootView.findViewById(R.id.rideDestination);
        date = (TextView) rootView.findViewById(R.id.rideDateTextView);
        time = (TextView) rootView.findViewById(R.id.rideTimeTextView);
        numPassengers = (TextView) rootView.findViewById(R.id.ridePassengersNumTextview);

        return rootView;
    }
}
