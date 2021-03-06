package il.ac.huji.ridez;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import il.ac.huji.ridez.adpaters.RideDetailsAdapter;

import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FutureRidesFragment extends Fragment {
    ListView futureListView;
    TextView noRidesTextView;
    ArrayList<String[]> rides;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.futurerides, container, false);
        futureListView = (ListView) rootView.findViewById(R.id.futureListView);
        noRidesTextView = (TextView) rootView.findViewById(R.id.noFutureRidesTextView);
        futureListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity().getApplicationContext(), RideDetailsActivity.class);
                intent.putExtra("rideId", rides.get(position)[RideDetailsAdapter.ID]);
                intent.putExtra("isRequest", rides.get(position)[RideDetailsAdapter.KIND].equals(getString(R.string.asPassenger)));
                startActivity(intent);
            }
        });
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
                        String from = ride.getParseObject("from").getString("address");
                        String to = ride.getParseObject("to").getString("address");
                        String id = ride.getObjectId();
                        Boolean isRequest = ride.getBoolean("request");
                        Date date = ride.getDate("date");
                        if (date.getTime() >= System.currentTimeMillis()) {
                            rides.add(new String[]{Toolbox.dateToShortDateAndTimeString(date, ride.getDouble("timeInterval")), from, to, isRequest ? getString(R.string.asPassenger) : getString(R.string.asDriver), id});
                        }
                    }
                    if (rides.size() == 0) {
                        futureListView.setVisibility(View.GONE);
                        noRidesTextView.setVisibility(View.VISIBLE);
                    } else {
                        futureListView.setVisibility(View.VISIBLE);
                        noRidesTextView.setVisibility(View.GONE);
                        Context context = getActivity();
                        futureListView.setAdapter(new RideDetailsAdapter(context, rides, true, true));
                    }
                } else {
                    Log.d("PARSE", "error getting rides");
                }
            }
        });
        Context context = getActivity();
        if (context != null) {
            RideDetailsAdapter adapter = new RideDetailsAdapter(context, rides, true, true);
            futureListView.setAdapter(adapter);
            return rootView;
        }

        return rootView;
    }
}