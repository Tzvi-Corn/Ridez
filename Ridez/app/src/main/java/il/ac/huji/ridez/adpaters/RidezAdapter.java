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
    private static ArrayList<String[]> ridezArrayList;

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
            convertView = mInflater.inflate(R.layout.ridescell, null);
            holder = new ViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.rideDate);
            holder.origDest = (TextView) convertView
                    .findViewById(R.id.rideOriginAndDestination);
            holder.kind = (TextView) convertView.findViewById(R.id.rideKind);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.date.setText(ridezArrayList.get(position)[0]);
        holder.origDest.setText(ridezArrayList.get(position)[1]);
        holder.kind.setText(ridezArrayList.get(position)[2]);

        return convertView;
    }

    static class ViewHolder {
        TextView date;
        TextView origDest;
        TextView kind;
    }
}