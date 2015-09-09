package il.ac.huji.ridez;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import il.ac.huji.ridez.adpaters.RideDetailsAdapter;

import android.support.v4.app.Fragment;
import android.widget.AdapterView;
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

public class PastRidesFragment extends Fragment {
    ListView pastListView;
    TextView noPastRidesTextView;
    ArrayList<String[]> rides;
    ProgressDialog pd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.pastrides, container, false);
        pastListView = (ListView) rootView.findViewById(R.id.pastListView);
        noPastRidesTextView = (TextView) rootView.findViewById(R.id.noPastRidesTextView);
        pd = ProgressDialog.show(getActivity(), getString(R.string.pleaseWait), getString(R.string.loadinYourData), true);
        pastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity().getApplicationContext(), RideDetailsActivity.class);
                intent.putExtra("rideId", rides.get(position)[4]);
                intent.putExtra("isRequest", rides.get(position)[3].equals(getString(R.string.asPassenger)));
                intent.putExtra("isPast", true);
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
                        ParseObject fromO = ride.getParseObject("from");
                        String from = fromO.getString("address");
                        String to = ride.getParseObject("to").getString("address");
                        String id = ride.getObjectId();
                        Boolean isRequest = ride.getBoolean("request");
                        Date date = ride.getDate("date");
                        if (date.getTime() < System.currentTimeMillis()) {
                            rides.add(new String[]{Toolbox.dateToShortDateAndTimeString(date), from ,to, isRequest ? getString(R.string.asPassenger) : getString(R.string.asDriver), id});
                        }

                    }
                    if (rides.size() == 0) {
                        pastListView.setVisibility(View.GONE);
                        noPastRidesTextView.setVisibility(View.VISIBLE);
                    } else {
                        pastListView.setVisibility(View.VISIBLE);
                        noPastRidesTextView.setVisibility(View.GONE);
                        Context context = getActivity();
                        pastListView.setAdapter(new RideDetailsAdapter(context, rides));
                    }
                } else {
                    Log.d("PARSE", "error getting groups");
                }
                pd.dismiss();
            }
        });
        Context context = getActivity();
        if (context != null) {
            RideDetailsAdapter adapter = new RideDetailsAdapter(context, rides);
            pastListView.setAdapter(adapter);
            return rootView;
        }
        return rootView;
    }
}