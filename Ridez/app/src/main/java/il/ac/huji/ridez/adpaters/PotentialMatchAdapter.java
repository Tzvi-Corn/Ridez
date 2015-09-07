package il.ac.huji.ridez.adpaters;
import java.util.ArrayList;

import il.ac.huji.ridez.R;
import il.ac.huji.ridez.contentClasses.PotentialMatch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

public class PotentialMatchAdapter extends BaseAdapter {
    private final Context mContext;
    private ArrayList<PotentialMatch> matchArrayList;

    private LayoutInflater mInflater;

    public PotentialMatchAdapter(Context context, ArrayList<PotentialMatch> results) {
        matchArrayList = results;
        mInflater = LayoutInflater.from(context);
        mContext = context;
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
//            holder.checkBox = (CheckBox) convertView.findViewById(R.id.matchCheckBox);
            holder.callButton = (ImageButton) convertView.findViewById(R.id.callPotentialRide);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final PotentialMatch pm = matchArrayList.get(position);
//        holder.checkBox.setChecked(pm.isConfirmed);
//        holder.checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
//            PotentialMatch p = pm;
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView,
//                                         boolean isChecked) {
//
//                // Toast.makeText(CheckBoxCheckedDemo.this, &quot;Checked =&gt; &quot;+isChecked, Toast.LENGTH_SHORT).show();
//                p.isConfirmed = isChecked;
//
//            }
//        });
        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:0123456789"));
                mContext.startActivity(intent);
            }
        });
        if (!pm.iAmRequester) {
            holder.cellTitle.setText("Passenger");
        }
            holder.email.setText("Email: " + pm.userEmail);
            holder.fullName.setText("Full name: " + (pm.fullName  == null ? "": pm.fullName));
            holder.dest.setText("Going to: " + pm.toAddress);
            holder.orig.setText("Leaving from: " + pm.fromAddress);
            holder.date.setText("Date and Time: " + pm.date.toString());

        return convertView;
    }

    static class ViewHolder {
        TextView date;
        TextView orig;
        TextView dest;
        TextView fullName;
        TextView email;
        TextView cellTitle;
//        CheckBox checkBox;
        ImageButton callButton;
    }
}