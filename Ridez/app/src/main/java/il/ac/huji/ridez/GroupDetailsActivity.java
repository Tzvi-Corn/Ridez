package il.ac.huji.ridez;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import il.ac.huji.ridez.adpaters.groupDetailsTabAdapter;


public class GroupDetailsActivity extends FragmentActivity implements ActionBar.TabListener {
    private ViewPager viewPager;
    private groupDetailsTabAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "Members", "Future Ride Requests" };
    int index;
    public int getGroupIndex() {
        return index;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView(R.layout.activity_group_details);
        // Initilization
        viewPager = (ViewPager) findViewById(R.id.groupDetailsPager);
        actionBar = getActionBar();

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
        mAdapter = new groupDetailsTabAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

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

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }
}
