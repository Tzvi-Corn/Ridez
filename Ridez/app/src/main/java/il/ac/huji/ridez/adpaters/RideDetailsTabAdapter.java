package il.ac.huji.ridez.adpaters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import il.ac.huji.ridez.PotentialMatchFragment;
import il.ac.huji.ridez.RideDetailsFragment;

public class RideDetailsTabAdapter extends FragmentPagerAdapter {
    boolean isOne;
    public RideDetailsTabAdapter(FragmentManager fm) {
        super(fm);
        isOne = false;
    }

    public RideDetailsTabAdapter(FragmentManager fm, boolean isOne) {

        super(fm);
        this.isOne = isOne;
    }
    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new RideDetailsFragment();
            case 1:
                // Games fragment activity
                return new PotentialMatchFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        if (isOne) {
            return 1;
        }
        return 2;
    }

}