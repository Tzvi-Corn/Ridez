package il.ac.huji.ridez;

import android.app.Dialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

import il.ac.huji.ridez.adpaters.groupDetailsTabAdapter;
import il.ac.huji.ridez.contentClasses.RidezGroup;


public class GroupDetailsActivity extends ActionBarActivity implements ActionBar.TabListener {
    private ViewPager viewPager;
    private ActionBar actionBar;
    private groupDetailsTabAdapter mAdapter;
    private MenuItem addMember;
    private int curTab;
    // Tab titles
    private String[] tabs = new String[2];
    int index;
    private boolean isAdmin;
    private RidezGroup group;
    private GroupMembersFragment fragment;
    public int getGroupIndex() {
        return index;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        // Initilization
        viewPager = (ViewPager) findViewById(R.id.groupDetailsPager);
        actionBar = getSupportActionBar();

        String addedGroupId = getIntent().getExtras().getString("groupAdded");
        if (addedGroupId != null) {
            DB.refreshGroups();
            index = DB.getPositionFromId(addedGroupId);
            if(index == -1) {
                Log.v("AddedGroup", "relieved notification, but no info from parse.");
                Toast.makeText(getApplicationContext(), R.string.added_to_group_error_msg, Toast.LENGTH_LONG).show();
            }
        } else {
            index = getIntent().getExtras().getInt("groupIndex");
        }
        group = DB.getGroups().get(index);
        group.updateMembersInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                RidezGroup.Member m = group.getMembers().get(ParseUser.getCurrentUser().getEmail());
                isAdmin = DB.getGroups().get(index).getMembers().get(ParseUser.getCurrentUser().getEmail()).isAdmin;
                updateIcon();
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(DB.getGroups().get(index).getName());
        }
        mAdapter = new groupDetailsTabAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        tabs[0] = getString(R.string.members);
        tabs[1] = getString(R.string.futureRideRequests);
        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    private void updateIcon() {
        if (addMember != null) {
            if (curTab == 0 && isAdmin) {
                addMember.setVisible(true);
            } else {
                addMember.setVisible(false);
            }
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        curTab = tab.getPosition();
        updateIcon();
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_view, menu);
        addMember = (MenuItem) menu.findItem(R.id.action_add_new_member);
        updateIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_new_member) {
            if (fragment != null) {
                fragment.add_member();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setFragment (GroupMembersFragment fragment) {
        this.fragment = fragment;
    }

}
