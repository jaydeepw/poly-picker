package nl.changer.polypicker;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import nl.changer.polypicker.utils.DebugLog;

/**
 * Created by rahul on 15/4/15.
 */
public class SlidingTabText extends SlidingTabLayout {

    public SlidingTabText(Context context) {
        super(context);
    }

    public SlidingTabText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlidingTabText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private String[] mTabText;

    public void setTabTitles(String[] titles) {
        if (mViewPager != null) {
            if (titles.length != mViewPager.getChildCount()) {
                throw new IllegalArgumentException("Titles and View pager count mismatch. Expected " + mViewPager.getChildCount()
                        + "Found " + mTabText.length);
            }
        }
        mTabText = titles;
    }

    public void setTabStripColor(int color) {
        mTabStrip.setBackgroundResource(color);
    }

    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    @Override
    public void setViewPager(ViewPager viewPager) {
        mTabStrip.removeAllViews();

        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.setOnPageChangeListener(new InternalViewPagerListener());
            if (mTabText == null || mTabText.length == 0) {
                int size = mViewPager.getAdapter().getCount();
                mTabText = new String[size];
                for (int i = 0; i < size; i++) {
                    mTabText[i] = Integer.toString(i);
                }
            }

            if (viewPager.getAdapter().getCount() != mTabText.length) {
                throw new IllegalArgumentException("Titles and View pager count mismatch. Expected " + viewPager.getChildCount()
                 + " Found " + mTabText.length);
            }
            populateTabStrip();
        }
    }

    @Override
    protected void populateTabStrip() {
        final PagerAdapter adapter = mViewPager.getAdapter();
        final View.OnClickListener tabClickListener = new TabClickListener();

        for (int i = 0; i < adapter.getCount(); i++) {
            View tabView = null;
            View tabViewDescendant;
            TextView tabTitleView = null;
            ImageView tabImageView = null;

            if (mTabViewLayoutId != 0) {
                // If there is a custom tab view layout id set, try and inflate it
                tabView = LayoutInflater.from(getContext()).inflate(mTabViewLayoutId, mTabStrip,
                        false);
                tabViewDescendant = tabView.findViewById(mTabViewImageViewId);
                if (tabViewDescendant instanceof TextView) {
                    tabTitleView = (TextView) tabViewDescendant;
                } else if (tabViewDescendant instanceof  ImageView) {
                    tabImageView = (ImageView) tabViewDescendant;
                }

                DebugLog.v("#populateTabStrip: tab_view_layout is not null");
            }

            if (tabView == null) {
                DebugLog.v("#populateTabStrip: tab view is null");
                tabView = createDefaultTabView(getContext());
            }

            if (tabTitleView == null && ImageView.class.isInstance(tabView)) {
//                DebugLog.v("#populateTabStrip: tabtitleView is null");
                tabImageView = (ImageView) tabView;
                // tabImageView.setImageResource(R.drawable.asterix);
            } else if (tabTitleView == null && TextView.class.isInstance(tabTitleView)) {
                tabTitleView = (TextView) tabView;
            }
            if (tabTitleView != null) {
                tabTitleView.setText(mTabText[i]);
            }

            tabView.setOnClickListener(tabClickListener);
            mTabStrip.addView(tabView);
        }
    }
}