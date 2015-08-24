package il.ac.huji.ridez;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import il.ac.huji.ridez.R;
import il.ac.huji.ridez.adpaters.MemberAdapter;
import il.ac.huji.ridez.adpaters.RidezAdapter;
import il.ac.huji.ridez.contentClasses.RidezGroup;
import il.ac.huji.ridez.sqlHelpers.GroupInfo;
import il.ac.huji.ridez.sqlHelpers.RideInfo;

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
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


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
                            rides.add(new String[]{user.getEmail(), date.toString(), "From " + from + " To " + to});
                        }

                    }
                    Context context = getActivity();
                    groupRidesListView.setAdapter(new RidezAdapter(context, rides));
                } else {
                    Log.d("PARSE", "error getting groups");
                }
            }
        });

        return rootView;
    }
}
