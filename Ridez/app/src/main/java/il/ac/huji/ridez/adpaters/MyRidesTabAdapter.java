package il.ac.huji.ridez.adpaters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import il.ac.huji.ridez.FutureRidesFragment;
import il.ac.huji.ridez.PastRidesFragment;

public class MyRidesTabAdapter extends FragmentPagerAdapter {

    public MyRidesTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new FutureRidesFragment();

            case 1:
                // Games fragment activity
                return new PastRidesFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}