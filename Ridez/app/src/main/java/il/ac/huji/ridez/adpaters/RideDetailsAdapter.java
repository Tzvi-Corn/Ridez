package il.ac.huji.ridez.adpaters;
import java.util.ArrayList;
import java.util.List;

import il.ac.huji.ridez.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class RideDetailsAdapter extends BaseAdapter {
    protected ArrayList<String[]> ridezArrayList;

    private LayoutInflater mInflater;
    private Context mContext;
    private boolean isFutureRide, isMyRide;
    ProgressDialog pd;
    public static int DATE = 0, ORIGIN = 1, DESTINATION = 2, KIND = 3, ID = 4;

    public RideDetailsAdapter(Context context, ArrayList<String[]> results, boolean futureRide, boolean myRide) {
        ridezArrayList = results;
        mInflater = LayoutInflater.from(context);
        mContext = context;
        isFutureRide = futureRide;
        isMyRide = myRide;
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

    public View getView(int pos, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final int position = pos;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.rides_cell, null);
            holder = new ViewHolder();
            holder.user = (TextView) convertView.findViewById(R.id.rideUsername);
            holder.date = (TextView) convertView.findViewById(R.id.rideDate);
            holder.orig = (TextView) convertView.findViewById(R.id.rideOrigin);
            holder.dest = (TextView) convertView.findViewById(R.id.rideDestination);
            holder.kind = (TextView) convertView.findViewById(R.id.rideKind);
            holder.rideAccepted = (ImageButton) convertView.findViewById(R.id.rideAccepted);
            if (isFutureRide) {

//                holder.rideAccepted.setVisibility(View.VISIBLE);
//                holder.rideAccepted.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                        builder.setTitle(R.string.ride_accepted_dialog_title);
//                        builder.setMessage(R.string.ride_accepted_dialog_messgae);
//                        builder.setCancelable(false);
//                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                String rideId = ridezArrayList.get(position)[4];
//
//                            }
//                        });
//                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                    }
//                });
            }

            holder.deleteRide = (ImageButton) convertView.findViewById(R.id.deleteRide);
            if(isMyRide){
                holder.deleteRide.setVisibility(View.VISIBLE);
                holder.deleteRide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle(R.string.delete_ride);
                        builder.setCancelable(false);
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((Activity)mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd = ProgressDialog.show(mContext, mContext.getString(R.string.pleaseWait), mContext.getString(R.string.savingYourOffer), true);
                                    }
                                });
                                String rideId = ridezArrayList.get(position)[ID];
                                final String kind = ridezArrayList.get(position)[KIND];

                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Ride");
                                query.getInBackground(rideId, new GetCallback<ParseObject>() {
                                    public void done(ParseObject ride, ParseException e) {
                                        if (e == null) {
                                            ParseQuery<ParseObject> q = ParseQuery.getQuery("potentialMatches");
                                            if (kind.equals(mContext.getString(R.string.asPassenger))) {
                                                q.whereEqualTo("request", ride);
                                            } else {
                                                q.whereEqualTo("request", ride);
                                            }
                                            q.findInBackground(new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List<ParseObject> matchesList, ParseException e) {
                                                    for( ParseObject match : matchesList) {
                                                        match.deleteInBackground();
                                                    }
                                                }
                                            });

                                            ride.deleteInBackground();
                                        } else {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                ridezArrayList.remove(position);
                                notifyDataSetChanged();
                                ((Activity)mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.create().show();
                    }
                });
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.date.setText(ridezArrayList.get(position)[DATE]);
        holder.orig.setText(ridezArrayList.get(position)[ORIGIN]);
        holder.dest.setText(ridezArrayList.get(position)[DESTINATION]);
        holder.kind.setText(ridezArrayList.get(position)[KIND]);
        return convertView;
    }

    static class ViewHolder {
        TextView user;
        TextView date;
        TextView orig;
        TextView dest;
        TextView kind;
        ImageButton rideAccepted;
        ImageButton deleteRide;
    }
}