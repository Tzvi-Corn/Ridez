package il.ac.huji.ridez.adpaters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import il.ac.huji.ridez.FutureRides;
import il.ac.huji.ridez.GroupMembersFragment;
import il.ac.huji.ridez.GroupRidesFragment;
import il.ac.huji.ridez.PastRidesFragment;

/**
 * Created by Tzvi on 18/08/2015.
 */
public class groupDetailsTabAdapter extends FragmentPagerAdapter {

    public groupDetailsTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new GroupMembersFragment(/*groupIndex*/);
            case 1:
                // Games fragment activity
                return new GroupRidesFragment(/*groupIndex*/);
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }
}
