package il.ac.huji.ridez;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import il.ac.huji.ridez.R;
import il.ac.huji.ridez.adpaters.MemberAdapter;
import il.ac.huji.ridez.adpaters.RidezAdapter;
import il.ac.huji.ridez.contentClasses.RidezGroup;
import il.ac.huji.ridez.sqlHelpers.GroupInfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GroupMembersFragment extends Fragment {
    int index;
    RidezGroup myGroup;
    GroupDetailsActivity activity;
    ListView memberListview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_group_members, container, false);
        activity = (GroupDetailsActivity) getActivity();
        memberListview = (ListView) rootView.findViewById(R.id.memberListView);
        index = activity.getGroupIndex();
        myGroup = DB.getGroups().get(index);
        if (!myGroup.isMembersReady()) {
            myGroup.updateMembersInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                        alertDialog.setTitle("Server problem");
                        alertDialog.setMessage("Problem getting updated member list");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                    Map <String, RidezGroup.Member> members =  myGroup.getMembers();
                    setListView(members);
                }
            });
        } else {
            setListView(myGroup.getMembers());
        }
        return rootView;
    }
    private void setListView(Map <String, RidezGroup.Member> members) {
        ArrayList<RidezGroup.Member> memberList = new ArrayList<RidezGroup.Member>();
        ParseUser me = ParseUser.getCurrentUser();
        Boolean isAdmin = false;
        for (Map.Entry<String,RidezGroup.Member> entry : members.entrySet()) {
            memberList.add(entry.getValue());
            if (me.getObjectId().equals(entry.getValue().id)) {
                isAdmin = true;
            }
        }
        MemberAdapter adapter = new MemberAdapter(activity, memberList, myGroup, isAdmin);
        memberListview.setAdapter(adapter);
    }
}
