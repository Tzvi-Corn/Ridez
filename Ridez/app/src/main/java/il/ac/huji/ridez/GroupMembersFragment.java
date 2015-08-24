
package il.ac.huji.ridez;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
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
    ImageButton button;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_group_members, container, false);
        activity = (GroupDetailsActivity) getActivity();
        button = (ImageButton) rootView.findViewById(R.id.buttonShowCustomDialog);

        // add button listener
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

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
                final AutoCompleteTextView textView = (AutoCompleteTextView) dialog.findViewById(R.id.searchGruopMember);
                textView.setAdapter(adapter2);
                Button okButton = (Button) dialog.findViewById(R.id.dialogOkButton);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //add user
                        ParseUser.getQuery().whereEqualTo("email", textView.getText().toString()).getFirstInBackground((new GetCallback<ParseUser>() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (e == null) {

                                    if (user == null) {
                                        // this user is not in parse
                                       // waiting_members.add(emailText);
                                    } else {
                                       myGroup.addUser(user, false);
                                        myGroup.saveInBackground();
                                       setListView(myGroup.getMembers());
                                    }
                                } else {
                                    //do
                                }

                                dialog.dismiss();
                            }

                            private String emailText;
                            private GetCallback<ParseUser> setEmail(String email) {
                                emailText = email;
                                return this;
                            }
                        }).setEmail(textView.getText().toString()));
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
        });
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
                isAdmin = entry.getValue().isAdmin;

            }
        }
        if (!isAdmin) {
            button.setVisibility(View.GONE);
        }
        MemberAdapter adapter = new MemberAdapter(activity, memberList, myGroup, isAdmin);
        memberListview.setAdapter(adapter);
    }
}
