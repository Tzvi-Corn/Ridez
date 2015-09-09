package il.ac.huji.ridez.adpaters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import il.ac.huji.ridez.R;

/**
 * Created by Zahi on 03/09/2015.
 */
public class GroupRidesAdapter extends RideDetailsAdapter {
    public GroupRidesAdapter(Context context, ArrayList<String[]> results) {
        super(context, results, false, false);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);
        ((ViewHolder) convertView.getTag()).user.setText(ridezArrayList.get(position)[4]);
        convertView.findViewById(R.id.rideUserLayout).setVisibility(View.VISIBLE);
        return convertView;
    }
}
