package il.ac.huji.ridez;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import il.ac.huji.ridez.adpaters.GroupRidesAdapter;
import il.ac.huji.ridez.contentClasses.RidezGroup;

import android.support.v4.app.Fragment;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class GroupRidesFragment extends Fragment {
    int index;
    RidezGroup myGroup;
    ArrayList<String[]> rides;
    ListView groupRidesListView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_group_rides, container, false);
        GroupDetailsActivity activity = (GroupDetailsActivity) getActivity();
        groupRidesListView = (ListView) rootView.findViewById(R.id.groupRidesListView);
        index = activity.getGroupIndex();
        myGroup = DB.getGroups().get(index);
        rides = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Ride");
        // Include the post data with each comment
        query.include("from");
        query.include("to");
        query.include("user");
        // suppose we have a author object, for which we want to get all books
        query.whereEqualTo("groups", myGroup);
        // execute the query
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> rideList, ParseException e) {
                if (e == null) {

                    for (int i = 0; i < rideList.size(); ++i) {
                        ParseObject ride = rideList.get(i);
                        ParseObject fromO = ride.getParseObject("from");
                        String from = fromO.getString("address");
                        String to = ride.getParseObject("to").getString("address");
                        ParseUser user = ride.getParseUser("user");
                        Boolean isRequest = ride.getBoolean("request");
                        Date date = ride.getDate("date");
                        if (date.getTime() > System.currentTimeMillis() && isRequest) {
                            rides.add(new String[]{Toolbox.dateToShortDateAndTimeString(date), from ,to, isRequest ? "As Passenger" : "As Driver", user.getEmail()});
                        }

                    }
                    Context context = getActivity();
                    groupRidesListView.setAdapter(new GroupRidesAdapter(context, rides));
                } else {
                    Log.d("PARSE", "error getting groups");
                }
            }
        });

        return rootView;
    }
}
