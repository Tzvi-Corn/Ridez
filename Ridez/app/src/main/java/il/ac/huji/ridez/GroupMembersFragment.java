
package il.ac.huji.ridez;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import il.ac.huji.ridez.adpaters.MemberAdapter;
import il.ac.huji.ridez.contentClasses.RidezGroup;

import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Map;


public class GroupMembersFragment extends Fragment {
    int index;
    RidezGroup myGroup;
    GroupDetailsActivity activity;
    ListView memberListview;
    ImageButton button;
    ProgressDialog pd;
    public boolean isAdmin = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_group_members, container, false);
        activity = (GroupDetailsActivity) getActivity();
        activity.setFragment(this);
        button = (ImageButton) rootView.findViewById(R.id.buttonShowCustomDialog);
        pd = ProgressDialog.show(getActivity(), "Please wait ...", "Loading your data", true);

        // add button listener
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
        ArrayList<RidezGroup.Member> memberList = new ArrayList<>();
        ParseUser me = ParseUser.getCurrentUser();
        for (Map.Entry<String,RidezGroup.Member> entry : members.entrySet()) {
            memberList.add(entry.getValue());
            if (me.getObjectId().equals(entry.getValue().id)) {
                isAdmin = entry.getValue().isAdmin;

            }
        }
        if (!isAdmin) {
            button.setVisibility(View.GONE);
        }
        MemberAdapter adapter = new MemberAdapter(activity, memberList, myGroup, isAdmin);
        memberListview.setAdapter(adapter);
        pd.dismiss();
    }

    public void add_member() {
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.addmember);
        dialog.setTitle("Add a friend");
        dialog.setCancelable(true);
        ArrayList<String> emailAddressCollection = new ArrayList<>();

        ContentResolver cr = getActivity().getContentResolver();

        Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null, null, null);

        while (emailCur.moveToNext())
        {
            String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            emailAddressCollection.add(email);
        }
        emailCur.close();

        String[] emailAddresses = new String[emailAddressCollection.size()];
        emailAddressCollection.toArray(emailAddresses);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, emailAddresses);
        final AutoCompleteTextView textView = (AutoCompleteTextView) dialog.findViewById(R.id.searchGroupMember);
        textView.setAdapter(adapter2);
        Button okButton = (Button) dialog.findViewById(R.id.dialogOkButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myGroup.addUserInBackground(textView.getText().toString(), getActivity(), new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null) {
                            myGroup.saveInBackground();
                            setListView(myGroup.getMembers());
                        } else {
                            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
        Button cancelButton = (Button) dialog.findViewById(R.id.dialogCancButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });



        dialog.show();
    }
}
