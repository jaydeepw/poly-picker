package nl.changer.polypicker;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import nl.changer.polypicker.model.Image;
import nl.changer.polypicker.utils.ImageInternalFetcher;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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


public class ImagePickerActivity extends FragmentActivity implements ActionBar.TabListener {

    private static final String TAG = ImagePickerActivity.class.getSimpleName();

    public static final String EXTRA_IMAGE_URIS = "nl.changer.polypicker.extra.selected_image_uris";
    
    /***
     * Integer extra to limit the number of images that can be selected. 
     * By default the user can select infinite number of images.
     */
    public static final String EXTRA_SELECTION_LIMIT = "nl.changer.polypicker.extra.selection_limit";

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

        setContentView(R.layout.activity_main);

        mSelectedImagesContainer = (LinearLayout) findViewById(R.id.selected_photos_container);
        mSelectedImageEmptyMessage = (TextView)findViewById(R.id.selected_photos_empty);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mCancelButtonView = (Button) findViewById(R.id.action_btn_cancel);
        mDoneButtonView = (Button) findViewById(R.id.action_btn_done);

        mSelectedImages = new HashSet<Image>();
        mImageFetcher = new ImageInternalFetcher(this, 500);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mCancelButtonView.setOnClickListener(onFinishGettingImages);
        mDoneButtonView.setOnClickListener(onFinishGettingImages);
        
        mMaxSelectionsAllowed = getIntent().getIntExtra(EXTRA_SELECTION_LIMIT, Integer.MAX_VALUE);

        setupActionBar();
    }

    /**
     * Sets up the action bar, adding view page indicator
     ***/
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
    	
    	if(mSelectedImages.size() == mMaxSelectionsAllowed) {
    		Toast.makeText(this, mMaxSelectionsAllowed + " images selected already", Toast.LENGTH_SHORT).show();
    		return false;
    	} else {
    		if(mSelectedImages.add(image)){
                View rootView = LayoutInflater.from(ImagePickerActivity.this).inflate(R.layout.list_item_selected_thumbnail, null);
                ImageView thumbnail = (ImageView) rootView.findViewById(R.id.selected_photo);
                rootView.setTag(image.mUri);
                mImageFetcher.loadImage(image.mUri, thumbnail);
                mSelectedImagesContainer.addView(rootView, 0);

                int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
                thumbnail.setLayoutParams(new FrameLayout.LayoutParams(px, px));

                if(mSelectedImages.size() == 1) {
                    mSelectedImagesContainer.setVisibility(View.VISIBLE);
                    mSelectedImageEmptyMessage.setVisibility(View.GONE);
                }
                return true;
            }
    	}
    	
        return false;
    }

    public boolean removeImage(Image image) {
        if(mSelectedImages.remove(image)) {
            for(int i = 0; i < mSelectedImagesContainer.getChildCount(); i++) {
                View childView = mSelectedImagesContainer.getChildAt(i);
                if(childView.getTag().equals(image.mUri)){
                    mSelectedImagesContainer.removeViewAt(i);
                    break;
                }
            }

            if(mSelectedImages.size() == 0) {
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
            // cannot use switch statement since ADT 14 -.-
            if(view.getId() == R.id.action_btn_done){

                Uri[] uris = new Uri[mSelectedImages.size()];
                int i = 0;
                for(Image img : mSelectedImages) {
                	uris[i++] = img.mUri;	
                }

                Intent intent = new Intent();
                intent.putExtra(EXTRA_IMAGE_URIS, uris);
                setResult(Activity.RESULT_OK, intent);
            } else if(view.getId() == R.id.action_btn_cancel) {
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
            switch(position){
                case 0:
                    return new CameraFragment();
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
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "Take a photo";	//getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return "Gallery";	//getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
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
