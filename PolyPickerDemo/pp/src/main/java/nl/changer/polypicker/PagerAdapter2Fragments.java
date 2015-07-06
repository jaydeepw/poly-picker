package nl.changer.polypicker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jay on 21/4/15.
 */
public class PagerAdapter2Fragments extends FragmentPagerAdapter {

    /**
     * Number of tabs to be show. Change this value when a tab is added/removed
     */
    private static final int TAB_COUNT = 2;
    private static final int TAB_CAMERA = 0;
    private static final int TAB_GALLERY = 1;

    public PagerAdapter2Fragments(FragmentManager fm) {
        super(fm);
    }

    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();

    public void addFragment(Fragment fragment, String title) {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {

            case TAB_CAMERA:
                CwacCameraFragment profileInfoFragment = new CwacCameraFragment();
                CwacCameraFragment.setConfig(ImagePickerActivity.getConfig());
                return profileInfoFragment;
            case TAB_GALLERY:
                GalleryFragment pnrFragment = new GalleryFragment();
                return pnrFragment;
        }

        return null;
    }

    /*@Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }*/

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }

    /**
     On a call to NotifyDataSetChanged it checks if the item position is POSITION_UNCHANGED
     or POSITION_NONE if we return NONE here based on some criteria it reloads only that specific
     fragment
     */
    @Override
    public int getItemPosition(Object object) {
        return POSITION_UNCHANGED;
    }
}