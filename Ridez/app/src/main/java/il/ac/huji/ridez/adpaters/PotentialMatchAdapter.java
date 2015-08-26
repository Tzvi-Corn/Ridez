package il.ac.huji.ridez.adpaters;
import java.util.ArrayList;

import il.ac.huji.ridez.R;
import il.ac.huji.ridez.contentClasses.PotentialMatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.matchCheckBox);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final PotentialMatch pm = matchArrayList.get(position);
        holder.checkBox.setChecked(pm.isConfirmed);
        holder.checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            PotentialMatch p = pm;
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                // Toast.makeText(CheckBoxCheckedDemo.this, &quot;Checked =&gt; &quot;+isChecked, Toast.LENGTH_SHORT).show();
                p.isConfirmed = isChecked;

            }
        });
        if (pm.iAmRequester) {
            holder.email.setText("Email: " + pm.offerUserEmail);
            holder.fullName.setText("Full name: " + ( pm.offerFullName == null ? "": pm.offerFullName));
            holder.dest.setText("Going to: " + pm.offerToAddress);
            holder.orig.setText("Leaving from: " + pm.offerFromAddress);
            holder.date.setText("Date and Time: " + pm.offerDate.toString());
        }
        else {
            holder.cellTitle.setText("Passenger");
            holder.email.setText("Email: " + pm.requestUserEmail);
            holder.fullName.setText("Full name: " + (pm.requestFullName  == null ? "": pm.requestFullName));
            holder.dest.setText("Going to: " + pm.requestToAddress);
            holder.orig.setText("Leaving from: " + pm.requestFromAddress);
            holder.date.setText("Date and Time: " + pm.requestdate.toString());
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
        CheckBox checkBox;
    }
}