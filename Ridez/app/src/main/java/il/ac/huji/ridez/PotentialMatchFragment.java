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
ListView pListView;
    ArrayList<PotentialMatch> tempList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.potentialmatches, container, false);
        pListView = (ListView) rootView.findViewById(R.id.ridePotentialMatchesListView);
        id = getActivity().getIntent().getExtras().getString("rideId");
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
                String matchIdFromParse = getActivity().getIntent().getExtras().getString("matchId");

                if (e == null) {
                    tempList = new ArrayList<>();
                    int indexToScrollTo = -1;
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
                        if (matchIdFromParse != null && !matchIdFromParse.isEmpty()) {
                            if (matchIdFromParse.equals(potentialMatch.id)) {
                                indexToScrollTo = i;
                            }
                        }
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
                        potentialMatch.offerPhoneNum = rideOffer.getString("phoneNum");
                        potentialMatch.requestPhoneNum = rideRequest.getString("phoneNum");
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
