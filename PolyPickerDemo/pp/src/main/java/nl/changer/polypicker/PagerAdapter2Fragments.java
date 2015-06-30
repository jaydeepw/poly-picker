package nl.changer.polypicker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

/**
 * Created by jay on 21/4/15.
 */
public class PagerAdapter2Fragments extends FragmentStatePagerAdapter {

    /**
     * Number of tabs to be show. Change this value when a tab is added/removed
     */
    private static final int TAB_COUNT = 2;
    private static final int TAB_CAMERA = 0;
    private static final int TAB_GALLERY = 1;

    public PagerAdapter2Fragments(FragmentManager fm) {
        super(fm);
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

    @Override
    public int getCount() {
        return TAB_COUNT;
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