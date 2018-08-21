package kulkarni.aditya.materialnews.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by adicool on 28/5/17.
 */

public class CustomPagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;

    public CustomPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 : return new UnreadFragment();
            case 1 : return new BlogFragment();
            case 2 : return new PinnedFragment();

            default: return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
