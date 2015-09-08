package il.ac.huji.ridez;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Map;

import il.ac.huji.ridez.adpaters.GroupsArrayAdapter;
import il.ac.huji.ridez.contentClasses.RidezGroup;


public class MyGroupsActivity extends ActionBarActivity {
    final static String GROUP_NAME = "name";
    private static int RESULT_NEW_GROUP = 1;
    private ListView groupsListView;
    private TextView noGroupsTextView;
    private GroupsArrayAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_groups);
        groupsListView = (ListView) findViewById(R.id.listMyGroups);
        groupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyGroupsActivity.this, GroupDetailsActivity.class);
                RidezGroup item = (RidezGroup) parent.getItemAtPosition(position);
                intent.putExtra("groupIndex", DB.getGroups().indexOf(item));
                startActivity(intent);
            }
        });
        noGroupsTextView = (TextView) findViewById(R.id.noGroupsTextView);
        newGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DB.isLoggedIn()) {
                    showError(getString(R.string.createGroupNotLoggedIn), getString(R.string.pleaseLogin));
                    return;
                }
                Intent i = new Intent(getApplicationContext(), NewGroupActivity.class);
                startActivityForResult(i, RESULT_NEW_GROUP);
            }
        });
        groupsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyGroupsActivity.this);
                builder.setTitle(R.string.leave_group);
                builder.setCancelable(false);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RidezGroup group = (RidezGroup) parent.getItemAtPosition(position);
                        group.removeUser(ParseUser.getCurrentUser());
                        try {
                            group.save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        DB.getGroups().remove(position);
                        adapter.notifyDataSetChanged();
                        Map<String, RidezGroup.Member> members = group.getMembers();
                        if (members.isEmpty()) {
                            group.deleteInBackground();
                            return;
                        }

                        boolean otherAdminExist = false;
                        for (RidezGroup.Member i : members.values()) {
                            if (i.isAdmin) {
                                otherAdminExist = true;
                                break;
                            }
                        }
                        if (!otherAdminExist) {
                            group.setAdmin(members.values().iterator().next(), true);
                            try {
                                group.save();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                builder.create().show();
                return true;
            }
        });



        adapter = new GroupsArrayAdapter(this, DB.getGroups());
        groupsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_NEW_GROUP && resultCode == RESULT_OK) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ParseQuery<RidezGroup> query = ParseQuery.getQuery("Group");
        query.whereEqualTo("users", ParseUser.getCurrentUser());
        query.orderByAscending("name");
        query.findInBackground(new FindCallback<RidezGroup>() {
            public void done(List<RidezGroup> groupList, ParseException e) {
                if (e == null) {
                    DB.setGroups(groupList);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (DB.getGroups().size() == 0) {
                                groupsListView.setVisibility(View.GONE);
                                noGroupsTextView.setVisibility(View.VISIBLE);
                            } else {
                                groupsListView.setVisibility(View.VISIBLE);
                                noGroupsTextView.setVisibility(View.GONE);
                                adapter = new GroupsArrayAdapter(MyGroupsActivity.this, DB.getGroups());
                                groupsListView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });

                } else {
                    Log.d("PARSE", "error getting groups");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_group) {
            Intent i = new Intent(getApplicationContext(), NewGroupActivity.class);
            startActivityForResult(i, RESULT_NEW_GROUP);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_NEW_GROUP && resultCode == RESULT_OK) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    private void showError(String errorString, String errorTitle) {
        final String errTitle = errorTitle;
        final String errMessage = errorString;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(MyGroupsActivity.this)
                        .setMessage(errMessage)
                        .setTitle(errTitle)
                        .setCancelable(true)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
            }
        });

    }
}
