package il.ac.huji.ridez.adpaters;
import java.util.ArrayList;
import java.util.Map;

import il.ac.huji.ridez.DB;
import il.ac.huji.ridez.R;
import il.ac.huji.ridez.UIHelper;
import il.ac.huji.ridez.contentClasses.RidezGroup;

import android.app.Activity;
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

import com.parse.ParseException;
import com.parse.ParseUser;

public class MemberAdapter extends BaseAdapter {
    private ArrayList<RidezGroup.Member> memberArrayList;
    RidezGroup myGroup;
    Context mContext;

    private LayoutInflater mInflater;
    private Boolean isAdmin = false;

    public MemberAdapter(Context context, ArrayList<RidezGroup.Member> results, RidezGroup group, Boolean isAdmin) {
        memberArrayList = results;
        mInflater = LayoutInflater.from(context);
        this.isAdmin = isAdmin;
        myGroup = group;
        mContext = context;
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

            convertView = mInflater.inflate(R.layout.membercell, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.memberName);
            holder.email = (TextView) convertView.findViewById(R.id.memberEmail);
            holder.permissions = (TextView) convertView.findViewById(R.id.isAdmin);
            deleteButton = (ImageButton) convertView.findViewById(R.id.deleteMember);
            adminButton = (ImageButton) convertView.findViewById(R.id.adminizeMember);

            String listUserMail = memberArrayList.get(position).email;
            String appUserMail = ParseUser.getCurrentUser().getEmail();
            final boolean isAppUser = (listUserMail.equalsIgnoreCase(appUserMail));

            if (isAdmin) {
                UIHelper.buttonEffect(deleteButton);
                UIHelper.buttonEffect(adminButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    RidezGroup.Member m = memberArrayList.get(position);

                    @Override
                    public void onClick(View v) {
                        deleteDialog(m, isAppUser);
                    }
                });
                adminButton.setOnClickListener(new View.OnClickListener() {
                    RidezGroup.Member m = memberArrayList.get(position);

                    @Override
                    public void onClick(View v) {
                        adminDialog(m);
                    }
                });
                String a = holder.email.getText().toString();
                String b = ParseUser.getCurrentUser().getEmail();
                if (ParseUser.getCurrentUser().getEmail().equalsIgnoreCase(memberArrayList.get(position).email)) {
                    adminButton.setVisibility(View.GONE);
                    deleteButton.setImageResource(R.drawable.exit);
                    deleteButton.setBackgroundColor(mContext.getResources().getColor(R.color.background));
                } else {
                    if (memberArrayList.get(position).isAdmin) {
                        adminButton.setVisibility(View.GONE);
                        deleteButton.setVisibility(View.GONE);
                    }
                }
            } else {
                deleteButton.setVisibility(View.GONE);
                adminButton.setVisibility(View.GONE);
            }
            convertView.setTag(holder);

        holder.name.setText(memberArrayList.get(position).name);
        holder.email.setText(memberArrayList.get(position).email);
        if (memberArrayList.get(position).isAdmin) {
            holder.permissions.setText(R.string.admin);
        }
        return convertView;
    }

    private boolean adminDialog(final RidezGroup.Member m) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.set_admin_dialog_header);
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m.isAdmin = true;
                myGroup.setAdmin(m, true);
                try {
                    myGroup.save();
                } catch (Exception ex) {
                    Log.v("v", "vdsgs");
                }
                notifyDataSetChanged();
            }
        });
        builder.create().show();
        return true;
    }

    static class ViewHolder {
        TextView name;
        TextView email;
        TextView permissions;
    }

    private boolean deleteDialog(final RidezGroup.Member m, final boolean isAppUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(isAppUser ? R.string.leave_group_dialog_header :R.string.delete_member_dialog_header);
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton(R.string.delete_member_dialog_yes_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                memberArrayList.remove(m);
                myGroup.removeUser(m.parseUser);
                try {
                    myGroup.save();
                    if (isAppUser) {
                        ((Activity) mContext).finish();
                    }
                } catch (Exception ex) {
                    Log.v("v", "vdsgs");
                }
                notifyDataSetChanged();
            }
        });
        builder.create().show();
        return true;
    }
}