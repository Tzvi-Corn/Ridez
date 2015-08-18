package il.ac.huji.ridez;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import il.ac.huji.ridez.R;
import il.ac.huji.ridez.adpaters.RidezAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FutureRides extends Fragment {
    ListView futureListView;
    ArrayList<String[]> rides;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.futurerides, container, false);
        futureListView = (ListView) rootView.findViewById(R.id.futureListView);
        rides = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Ride");
        // Include the post data with each comment
        query.include("from");
        query.include("to");
        // suppose we have a author object, for which we want to get all books
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        // execute the query
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> rideList, ParseException e) {
                if (e == null) {

                    for (int i = 0; i < rideList.size(); ++i) {
                        ParseObject ride = rideList.get(i);
                        ParseObject fromO = ride.getParseObject("from");
                        String from = fromO.getString("address");
                        String to = ride.getParseObject("to").getString("address");
                        Boolean isRequest = ride.getBoolean("request");
                        Date date = ride.getDate("date");
                        if (date.getTime() >= System.currentTimeMillis()) {
                            rides.add(new String[]{date.toString(), "From " + from + " To " + to, isRequest ? "As Passenger" : "As Driver"});
                        }

                    }
                    Context context = getActivity();
                    futureListView.setAdapter(new RidezAdapter(context, rides));
                } else {
                    Log.d("PARSE", "error getting groups");
                }
            }
        });
        Context context = getActivity();
        if (context != null) {
            RidezAdapter adapter = new RidezAdapter(context, rides);
            futureListView.setAdapter(adapter);
            return rootView;
        }
        return rootView;
    }
}