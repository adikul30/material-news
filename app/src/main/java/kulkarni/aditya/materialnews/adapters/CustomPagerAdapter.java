package kulkarni.aditya.materialnews.adapters;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import kulkarni.aditya.materialnews.fragments.BlogFragment;
import kulkarni.aditya.materialnews.fragments.PinnedFragment;
import kulkarni.aditya.materialnews.fragments.UnreadFragment;

/**
 * Created by adicool on 28/5/17.
 */

public class CustomPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> titles = new ArrayList<>();

    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        if (fragments.isEmpty()) {
            return 0;
        } else {
            return fragments.size();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        fragments.add(fragment);
        titles.add(title);
    }
}
