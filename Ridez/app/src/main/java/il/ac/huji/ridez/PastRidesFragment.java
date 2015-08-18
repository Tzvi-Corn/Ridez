package il.ac.huji.ridez;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import il.ac.huji.ridez.R;
import il.ac.huji.ridez.adpaters.RidezAdapter;
import il.ac.huji.ridez.sqlHelpers.GroupInfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PastRidesFragment extends Fragment {
    ListView pastListView;
    ArrayList<String[]> rides;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.pastrides, container, false);
        pastListView = (ListView) rootView.findViewById(R.id.pastListView);
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
                        if (date.getTime() < System.currentTimeMillis()) {
                            rides.add(new String[]{date.toString(), "From " + from + " To " + to, isRequest ? "As Passenger" : "As Driver"});
                        }

                    }
                    Context context = getActivity();
                    pastListView.setAdapter(new RidezAdapter(context, rides));
                } else {
                    Log.d("PARSE", "error getting groups");
                }
            }
        });
        Context context = getActivity();
        if (context != null) {
            RidezAdapter adapter = new RidezAdapter(context, rides);
            pastListView.setAdapter(adapter);
            return rootView;
        }
       return rootView;
    }
}