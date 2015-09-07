package il.ac.huji.ridez;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

import il.ac.huji.ridez.adpaters.RideDetailsTabAdapter;


public class RideDetailsActivity extends FragmentActivity implements
        ActionBar.TabListener  {
    private final static int RIDE_DETAILES_TAB = 0, RIDE_POTENTIAL_MATCHES_TAB = 1;
    TextView originTextView;
    TextView destinationTextView;
    TextView dateTextView;
    TextView timeTextView;
    TextView numberPassengersTextView;
    ListView groupsListView;
    ListView matchesListView;
    private ViewPager viewPager;
    private RideDetailsTabAdapter mAdapter;
    private ActionBar actionBar;
    public String rideId;
    private boolean isRequest;

    // Tab titles
    private String[] tabs = new String[2];
    public String getRideId() {
        return rideId;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView(R.layout.ridedetailsfragmented);
        rideId = getIntent().getExtras().getString("rideId");
        isRequest = getIntent().getExtras().getBoolean("isRequest");
        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new RideDetailsTabAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        tabs[0] = getString(R.string.rideDetails);
        tabs[1]= getString(R.string.potentiaMatches);
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
//        if (getIntent().getIntExtra("fromPush", 0) == PushReceiver.MATCH_ACT) { TODO - open the potential match detailes
//            viewPager.setCurrentItem(RIDE_POTENTIAL_MATCHES_TAB);
//        }
        String matchId = "";
        matchId = getIntent().getExtras().getString("matchId");
        if (matchId != null && !matchId.isEmpty()) {
            getActionBar().setSelectedNavigationItem(1);
        }


    }

    @Override
    public void onBackPressed() {
        String matchId = "";
        matchId = getIntent().getExtras().getString("matchId");
        if (matchId != null && !matchId.isEmpty()) {
            Intent mainActivity = new Intent(RideDetailsActivity.this, MainMenuActivity.class);
            RideDetailsActivity.this.startActivity(mainActivity);
            this.finish();
        } else {
            super.onBackPressed();
        }
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
