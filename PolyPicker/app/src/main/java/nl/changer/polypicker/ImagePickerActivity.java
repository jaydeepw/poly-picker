package nl.changer.polypicker;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import nl.changer.polypicker.model.Image;
import nl.changer.polypicker.utils.ImageInternalFetcher;

public class ImagePickerActivity extends Activity implements ActionBar.TabListener {

    private static final String TAG = ImagePickerActivity.class.getSimpleName();

    /**
     * Key to persist the list when saving the state of the activity.
     */
    private static final String KEY_LIST = "nl.changer.polypicker.savedinstance.key.list";

    /**
     * Returns the parcelled image uris in the intent with this extra.
     */
    public static final String EXTRA_IMAGE_URIS = "nl.changer.changer.nl.polypicker.extra.selected_image_uris";

    /**
     * Integer extra to limit the number of images that can be selected. By default the user can
     * select infinite number of images.
     */
    public static final String EXTRA_SELECTION_LIMIT = "nl.changer.changer.nl.polypicker.extra.selection_limit";

    /**
     * Single source. Allows taking image from only one source at a time.
     */
    public static final String EXTRA_SOURCE = "nl.changer.changer.nl.polypicker.extra.image_source";

    /**
     * Source camera. Allows taking image only using camera when passed in intent as
     * {@link nl.changer.polypicker.ImagePickerActivity#EXTRA_SOURCE}
     */
    public static final int SOURCE_CAMERA = 1;

    private Set<Image> mSelectedImages;
    private LinearLayout mSelectedImagesContainer;
    private TextView mSelectedImageEmptyMessage;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public ImageInternalFetcher mImageFetcher;

    private Button mCancelButtonView, mDoneButtonView;

    private int mMaxSelectionsAllowed = Integer.MAX_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pp);

        mSelectedImagesContainer = (LinearLayout) findViewById(R.id.selected_photos_container);
        mSelectedImageEmptyMessage = (TextView) findViewById(R.id.selected_photos_empty);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mCancelButtonView = (Button) findViewById(R.id.action_btn_cancel);
        mDoneButtonView = (Button) findViewById(R.id.action_btn_done);

        mSelectedImages = new HashSet<Image>();
        mImageFetcher = new ImageInternalFetcher(this, 500);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mCancelButtonView.setOnClickListener(onFinishGettingImages);
        mDoneButtonView.setOnClickListener(onFinishGettingImages);

        mMaxSelectionsAllowed = getIntent().getIntExtra(EXTRA_SELECTION_LIMIT, Integer.MAX_VALUE);

        setupActionBar();
        if (savedInstanceState != null) {
            populateUi(savedInstanceState);
        }
    }

    private void populateUi(Bundle savedInstanceState) {
        ArrayList<Image> list = savedInstanceState.getParcelableArrayList(KEY_LIST);

        if (list != null) {
            for (Image image : list) {
                addImage(image);
            }
        }
    }

    /**
     * Sets up the action bar, adding view page indicator.
     */
    private void setupActionBar() {
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
        }
    }

    public boolean addImage(Image image) {

        if (mSelectedImages == null) {
            // this condition may arise when the activity is being
            // restored when sufficient memory is available. onRestoreState()
            // will be called.
            mSelectedImages = new HashSet<Image>();
        }

        if (mSelectedImages.size() == mMaxSelectionsAllowed) {
            Toast.makeText(this, mMaxSelectionsAllowed + " images selected already", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (mSelectedImages.add(image)) {
                View rootView = LayoutInflater.from(ImagePickerActivity.this).inflate(R.layout.list_item_selected_thumbnail, null);
                ImageView thumbnail = (ImageView) rootView.findViewById(R.id.selected_photo);
                rootView.setTag(image.mUri);
                mImageFetcher.loadImage(image.mUri, thumbnail);
                mSelectedImagesContainer.addView(rootView, 0);

                int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
                thumbnail.setLayoutParams(new FrameLayout.LayoutParams(px, px));

                if (mSelectedImages.size() >= 1) {
                    mSelectedImagesContainer.setVisibility(View.VISIBLE);
                    mSelectedImageEmptyMessage.setVisibility(View.GONE);
                }
                return true;
            }
        }

        return false;
    }

    public boolean removeImage(Image image) {
        if (mSelectedImages.remove(image)) {
            for (int i = 0; i < mSelectedImagesContainer.getChildCount(); i++) {
                View childView = mSelectedImagesContainer.getChildAt(i);
                if (childView.getTag().equals(image.mUri)) {
                    mSelectedImagesContainer.removeViewAt(i);
                    break;
                }
            }

            if (mSelectedImages.size() == 0) {
                mSelectedImagesContainer.setVisibility(View.GONE);
                mSelectedImageEmptyMessage.setVisibility(View.VISIBLE);
            }
            return true;
        }
        return false;
    }

    public boolean containsImage(Image image) {
        return mSelectedImages.contains(image);
    }

    private View.OnClickListener onFinishGettingImages = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.action_btn_done) {

                Uri[] uris = new Uri[mSelectedImages.size()];
                int i = 0;
                for (Image img : mSelectedImages) {
                    uris[i++] = img.mUri;
                }

                Intent intent = new Intent();
                intent.putExtra(EXTRA_IMAGE_URIS, uris);
                setResult(Activity.RESULT_OK, intent);
            } else if (view.getId() == R.id.action_btn_cancel) {
                setResult(Activity.RESULT_CANCELED);
            }
            finish();
        }
    };

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                // TODO: user meaningful constants instead of merely integers
                // to make this code more readable.
                case 0:
                    return new CwacCameraFragment();
                case 1:
                    return new GalleryFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.take_photo);
                case 1:
                    return getString(R.string.gallery);
            }
            return null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // preserve already taken images on configuration changes like
        // screen rotation or activity run out of memory.
        // HashSet cannot be saved, so convert to list and then save.
        ArrayList<Image> list = new ArrayList<Image>(mSelectedImages);
        outState.putParcelableArrayList(KEY_LIST, list);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        populateUi(savedInstanceState);
    }

    @Override
    public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
        // Auto-generated method stub
    }

    @Override
    public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
        // Auto-generated method stub
    }
}