package il.ac.huji.ridez.adpaters;
import java.util.ArrayList;

import il.ac.huji.ridez.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RidezAdapter extends BaseAdapter {
    protected ArrayList<String[]> ridezArrayList;

    private LayoutInflater mInflater;

    public RidezAdapter(Context context, ArrayList<String[]> results) {
        ridezArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return ridezArrayList.size();
    }

    public Object getItem(int position) {
        return ridezArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.rides_cell, null);
            holder = new ViewHolder();
            holder.user = (TextView) convertView.findViewById(R.id.rideUsername);
            holder.date = (TextView) convertView.findViewById(R.id.rideDate);
            holder.orig = (TextView) convertView.findViewById(R.id.rideOrigin);
            holder.dest = (TextView) convertView.findViewById(R.id.rideDestination);
            holder.kind = (TextView) convertView.findViewById(R.id.rideKind);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.date.setText(ridezArrayList.get(position)[0]);
        holder.orig.setText(ridezArrayList.get(position)[1]);
        holder.dest.setText(ridezArrayList.get(position)[2]);
        holder.kind.setText(ridezArrayList.get(position)[3]);
        return convertView;
    }

    static class ViewHolder {
        TextView user;
        TextView date;
        TextView orig;
        TextView dest;
        TextView kind;
    }
}