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
import android.view.Window;
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
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import il.ac.huji.ridez.adpaters.RidezGroupArrayAdapter;
import il.ac.huji.ridez.contentClasses.RidezGroup;
import il.ac.huji.ridez.sqlHelpers.GroupInfo;


public class MyGroupsActivity extends ActionBarActivity {
    final static String GROUP_NAME = "name";
    private static int RESULT_NEW_GROUP = 1;
    private ListView groupsListView;
    private RidezGroupArrayAdapter adapter;



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
                RidezGroup item = (RidezGroup) parent.getItemAtPosition(position);
                intent.putExtra("groupIndex", DB.getGroups().indexOf(item));
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
        adapter = new RidezGroupArrayAdapter(this, DB.getGroups());
        groupsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
                            adapter = new RidezGroupArrayAdapter(MyGroupsActivity.this, DB.getGroups());
                            groupsListView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
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
        if (requestCode == RESULT_NEW_GROUP && resultCode == RESULT_OK) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();

    }
}
