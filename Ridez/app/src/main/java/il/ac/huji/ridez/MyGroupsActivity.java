package il.ac.huji.ridez;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import il.ac.huji.ridez.adpaters.GroupsListArrayAdapter;
import il.ac.huji.ridez.sqlHelpers.GroupInfo;


public class MyGroupsActivity extends ActionBarActivity {
    final static String GROUP_NAME = "name";
    private static int RESULT_NEW_GROUP = 1;
    private ListView groupsListView;
    private GroupsListArrayAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);
        ImageButton newGroup = (ImageButton) findViewById(R.id.buttonCreateNewGroup);
        groupsListView = (ListView) findViewById(R.id.listMyGroups);
        groupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), GroupDetailsActivity.class);
                GroupInfo item = (GroupInfo) parent.getItemAtPosition(position);
//                intent.putExtra(GROUP_NAME, item.getInfo());
                startActivity(intent);
            }
        });
        newGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NewGroupActivity.class);
                startActivityForResult(i, RESULT_NEW_GROUP);
            }
        });
        adapter = new GroupsListArrayAdapter(this, DB.getGroups());
        groupsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
        query.whereEqualTo("users", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> groupList, ParseException e) {
                if (e == null) {
                    List<GroupInfo> tempList = new ArrayList<>();
                    for (int i = 0; i < groupList.size(); ++i) {
                        ParseObject group = groupList.get(i);
                        String name = group.getString("name");
                        String description = group.getString("description");
                        ParseFile icon = group.getParseFile("icon");
                        String id = group.getObjectId();
                        byte[] iconData = null;
                        try {
                            iconData = icon.getData();
                        } catch (Exception ex) {

                        }
                        Bitmap bitmap = null;
                        if (iconData != null) {
                            bitmap = BitmapFactory.decodeByteArray(iconData, 0, iconData.length);
                        }
                        tempList.add(new GroupInfo(name, description, bitmap, id));
                    }
                    if (tempList.size() != DB.getGroups().size()) {
                        DB.setGroups(tempList);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                adapter = new GroupsListArrayAdapter(MyGroupsActivity.this, DB.getGroups());
                                groupsListView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        });

                    } else {
                        for (int i = 0; i < tempList.size(); ++i) {
                            GroupInfo newInfo = tempList.get(i);
                            GroupInfo oldInfo = DB.getGroups().get(i);
                            if (!(newInfo.getName().equals(oldInfo.getName())) || !(newInfo.getDescription().equals(oldInfo.getDescription()))) {
                                DB.setGroups(tempList);
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        adapter = new GroupsListArrayAdapter(MyGroupsActivity.this, DB.getGroups());
                                        groupsListView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                break;
                            }
                            if (newInfo.getIcon() != null && !(newInfo.getIcon().sameAs(oldInfo.getIcon()))) {
                                DB.setGroups(tempList);
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        adapter = new GroupsListArrayAdapter(MyGroupsActivity.this, DB.getGroups());
                                        groupsListView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                break;
                            }
                            if (newInfo.getIcon() == null && oldInfo.getIcon() != null) {
                                DB.setGroups(tempList);
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        adapter = new GroupsListArrayAdapter(MyGroupsActivity.this, DB.getGroups());
                                        groupsListView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                break;
                            }
                        }
                    }

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
           this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_NEW_GROUP && resultCode == RESULT_OK && data != null) {
            GroupInfo newgroupItem = new GroupInfo(data.getStringArrayExtra(GROUP_NAME));
            DB.addGroup(newgroupItem);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();

    }
}
