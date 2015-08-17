package il.ac.huji.ridez;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;

public class PastRidesFragment extends Fragment {
    ListView pastListView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.pastrides, container, false);
        pastListView = (ListView) rootView.findViewById(R.id.pastListView);
        ArrayList<String[]> rides = new ArrayList<>();
        rides.add(new String[]{"12/4/2014", "from Gedera To Jerusalem", "As passenger"});
        rides.add(new String[]{"12/5/2014", "from Bet Shemesh To Jerusalem", "As passenger"});
        rides.add(new String[]{"12/4/2017", "from Gedera To Modiin", "As passenger"});
        rides.add(new String[]{"11/4/2012", "from Gedera To New york", "As driver"});
        rides.add(new String[]{"12/9/2014", "from Gedera To The moon", "As passenger"});
        rides.add(new String[]{"12/5/2034", "from Gedera To Har Habayit", "As driver"});
        rides.add(new String[]{"7/4/2014", "from Yerucham To Jerusalem", "As passenger"});
        rides.add(new String[]{"11/4/2014", "from Gedera To Jerusalem", "As passenger"});
        Context context = getActivity();
        if (context != null) {
            RidezAdapter adapter = new RidezAdapter(context, rides);
            pastListView.setAdapter(adapter);
            return rootView;
        }
       return rootView;
    }
}