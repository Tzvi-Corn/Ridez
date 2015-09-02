package il.ac.huji.ridez.adpaters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import il.ac.huji.ridez.PotentialMatchFragment;
import il.ac.huji.ridez.RideDetails;

public class RideDetailsAdapter extends FragmentPagerAdapter {

    public RideDetailsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new RideDetails();
            case 1:
                // Games fragment activity
                return new PotentialMatchFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}