package kulkarni.aditya.materialnews.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import kulkarni.aditya.materialnews.Fragments.Blog;
import kulkarni.aditya.materialnews.Fragments.Pinned;
import kulkarni.aditya.materialnews.Fragments.Unread;

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
            case 0 : return new Unread();
            case 1 : return new Blog();
            case 2 : return new Pinned();

            default: return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
