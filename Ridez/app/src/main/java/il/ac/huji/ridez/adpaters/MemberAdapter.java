package il.ac.huji.ridez.adpaters;
import java.util.ArrayList;

import il.ac.huji.ridez.DB;
import il.ac.huji.ridez.R;
import il.ac.huji.ridez.UIHelper;
import il.ac.huji.ridez.contentClasses.RidezGroup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MemberAdapter extends BaseAdapter {
    private ArrayList<RidezGroup.Member> memberArrayList;
    RidezGroup myGroup;

    private LayoutInflater mInflater;
    private Boolean isAdmin = false;

    public MemberAdapter(Context context, ArrayList<RidezGroup.Member> results, RidezGroup group, Boolean isAdmin) {
        memberArrayList = results;
        mInflater = LayoutInflater.from(context);
        this.isAdmin = isAdmin;
        myGroup = group;
    }

    public int getCount() {
        return memberArrayList.size();
    }

    public Object getItem(int position) {
        return memberArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ImageButton deleteButton;
        ImageButton adminButton;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.membercell, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.memberName);
            holder.email = (TextView) convertView.findViewById(R.id.memberEmail);
            holder.permissions = (TextView) convertView.findViewById(R.id.isAdmin);
            deleteButton = (ImageButton) convertView.findViewById(R.id.deleteMember);
            adminButton = (ImageButton) convertView.findViewById(R.id.adminizeMember);
            if (isAdmin) {
                UIHelper.buttonEffect(deleteButton);
                UIHelper.buttonEffect(adminButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    RidezGroup.Member m = memberArrayList.get(position);
                    @Override
                    public void onClick(View v) {
                       memberArrayList.remove(m);
                        notifyDataSetChanged();
                        myGroup.removeUser(m.parseUser);
                        try {
                            myGroup.save();
                        } catch (Exception ex) {
                            Log.v("v", "vdsgs");
                        }
                    }
                });
                adminButton.setOnClickListener(new View.OnClickListener() {
                    RidezGroup.Member m = memberArrayList.get(position);
                    @Override
                    public void onClick(View v) {
                        myGroup.setAdmin(m, true);
                        try {
                            myGroup.save();
                        } catch (Exception ex) {
                            Log.v("v", "vdsgs");
                        }
                        notifyDataSetChanged();
                    }
                });
            } else {
                deleteButton.setVisibility(View.GONE);
                adminButton.setVisibility(View.GONE);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(memberArrayList.get(position).name);
        holder.email.setText(memberArrayList.get(position).email);
        if (memberArrayList.get(position).isAdmin) {
            holder.permissions.setText(R.string.admin);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView email;
        TextView permissions;
    }
}