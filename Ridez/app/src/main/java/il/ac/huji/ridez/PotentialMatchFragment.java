package il.ac.huji.ridez;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import il.ac.huji.ridez.adpaters.PotentialMatchAdapter;
import il.ac.huji.ridez.contentClasses.PotentialMatch;

public class PotentialMatchFragment extends Fragment {
    View rootView;
    String id;
    Boolean isRequest;
    ListView pListView;
    ArrayList<PotentialMatch> tempList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.potentialmatches, container, false);
        pListView = (ListView) rootView.findViewById(R.id.ridePotentialMatchesListView);
        id = getActivity().getIntent().getExtras().getString("rideId");
        isRequest = getActivity().getIntent().getExtras().getBoolean("isRequest");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("potentialMatch");
        if (isRequest) {
            query.whereEqualTo("request", ParseUser.createWithoutData("Ride", id));
        } else {
            query.whereEqualTo("offer", ParseUser.createWithoutData("Ride", id));
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> possibleMatchList, com.parse.ParseException e) {
                String matchIdFromParse = getActivity().getIntent().getExtras().getString("matchId");

                if (e == null) {
                    tempList = new ArrayList<>();
                    int indexToScrollTo = -1;
                    for (int i = 0; i < possibleMatchList.size(); ++i) {
                        ParseRelation<ParseObject> offerRelation =  possibleMatchList.get(i).getRelation("offer");
                        ParseRelation<ParseObject> requestRelation =  possibleMatchList.get(i).getRelation("request");
                        ParseObject ride;
                        try {
                            if (isRequest) {
                                ride = offerRelation.getQuery().include("user").include("from").include("to").getFirst();
                            } else {
                                ride = requestRelation.getQuery().include("user").include("from").include("to").getFirst();
                            }
                        } catch (Exception ex) {
                            Log.v("v", "wcweb");
                            continue;
                        }
                        ParseUser otherUser;
                        otherUser = ride.getParseUser("user");
                        PotentialMatch potentialMatch = new PotentialMatch();
                        potentialMatch.id = possibleMatchList.get(i).getObjectId();
                        if (matchIdFromParse != null && !matchIdFromParse.isEmpty()) {
                            if (matchIdFromParse.equals(potentialMatch.id)) {
                                indexToScrollTo = i;
                            }
                        }
                        if (otherUser == null) {
                            continue;
                        }
                        try {
                            potentialMatch.fullName = otherUser.getString("fullname");
                            potentialMatch.userEmail = otherUser.getString("email");
                            potentialMatch.phoneNum = otherUser.getString("phoneNum");
                        } catch (Exception ex) {
                            Log.v("v", "cewc");
                        }
                        potentialMatch.isConfirmed = possibleMatchList.get(i).getBoolean("isConfirmed");
                        potentialMatch.iAmRequester = isRequest;
                        ParseObject from = null;
                        ParseObject to = null;
                        try {
                            from = ride.getParseObject("from");
                            to = ride.getParseObject("to");
                        } catch (Exception ex) {
                            Log.v("v", "Oh boy");
                        }
                        if (from == null || to == null) {
                            continue;
                        }
                        potentialMatch.fromAddress = from.getString("address");
                        potentialMatch.toAddress = to.getString("address");
                        potentialMatch.date = ride.getDate("date");
                        tempList.add(potentialMatch);
                    }
                    PotentialMatchAdapter adapter = new PotentialMatchAdapter(getActivity(), tempList);
                    pListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    if (indexToScrollTo >=0) {
                        pListView.setSelection(indexToScrollTo)
                        ;
                    }



                } else {
                    Log.d("PARSE", "error getting possibles");
                }
            }
        });
        return rootView;
    }



}
