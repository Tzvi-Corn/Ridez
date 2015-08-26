package il.ac.huji.ridez.adpaters;
import java.util.ArrayList;

import il.ac.huji.ridez.R;
import il.ac.huji.ridez.contentClasses.PotentialMatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PotentialMatchAdapter extends BaseAdapter {
    private ArrayList<PotentialMatch> matchArrayList;

    private LayoutInflater mInflater;

    public PotentialMatchAdapter(Context context, ArrayList<PotentialMatch> results) {
        matchArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return matchArrayList.size();
    }

    public Object getItem(int position) {
        return matchArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.possiblematchcell, null);
            holder = new ViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.cellDate);
            holder.orig = (TextView) convertView
                    .findViewById(R.id.cellFromLocation);
            holder.dest = (TextView) convertView.findViewById(R.id.cellToLocation);
            holder.email = (TextView) convertView.findViewById(R.id.cellEmail);
            holder.fullName = (TextView) convertView.findViewById(R.id.cellFullName);
            holder.cellTitle = (TextView) convertView.findViewById(R.id.cellTitle);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PotentialMatch pm = matchArrayList.get(position);
        if (pm.iAmRequester) {
            holder.email.setText(holder.email.getText().toString() + pm.offerUserEmail);
            holder.fullName.setText(holder.fullName.getText().toString() +( pm.offerFullName == null ? "": pm.offerFullName));
            holder.dest.setText(holder.dest.getText().toString() + pm.offerToAddress);
            holder.orig.setText(holder.orig.getText().toString() + pm.offerFromAddress);
            holder.date.setText(holder.date.getText().toString() + pm.offerDate.toString());
        }
        else {
            holder.cellTitle.setText("Passenger");
            holder.email.setText(holder.email.getText().toString() + pm.requestUserEmail);
            holder.fullName.setText(holder.fullName.getText().toString() + (pm.requestFullName  == null ? "": pm.requestFullName));
            holder.dest.setText(holder.dest.getText().toString() + pm.requestToAddress);
            holder.orig.setText(holder.orig.getText().toString() + pm.requestFromAddress);
            holder.date.setText(holder.date.getText().toString() + pm.requestdate.toString());
        }

        return convertView;
    }

    static class ViewHolder {
        TextView date;
        TextView orig;
        TextView dest;
        TextView fullName;
        TextView email;
        TextView cellTitle;
    }
}