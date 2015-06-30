package nl.changer.polypicker;

import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;

/**
 * Created by jay on 3/5/15.
 */
public class Config {

    private int mTabBackgroundColor;
    private int mTabSelectionIndicatorColor;
    private int mSelectionLimit;
    private int mCameraButtonColor;

    /**
     *
     * @param tabBgColor
     * @param selectionLimit Pass -1 to not limit the selection of photos.
     */
    public Config(int tabBgColor, int tabSelectionIndicatorColor, int selectionLimit, int cameraButtonColor) {
        mTabBackgroundColor = tabBgColor;
        mTabSelectionIndicatorColor = tabSelectionIndicatorColor;

        if (selectionLimit != -1) {
            mSelectionLimit = selectionLimit;
        }

        mCameraButtonColor = cameraButtonColor;
    }

    public int getCameraButtonColor() {
        return mCameraButtonColor;
    }

    public int getTabBackgroundColor() {
        return mTabBackgroundColor;
    }

    public int getTabSelectionIndicatorColor() {
        return mTabSelectionIndicatorColor;
    }

    public int getSelectionLimit() {
        return mSelectionLimit;
    }

    /**
     * Build a new {@link Config}.
     * <p/>
     */
    public static class Builder {
        private int mTabBackgroundColor = R.color.white;
        private int mTabSelectionIndicatorColor = R.color.orange;
        private int mSelectionLimit = Integer.MAX_VALUE;
        private int mCameraButtonColor = R.color.orange;

        /**
         * Tab strip color
         */
        public Builder setTabBackgroundColor(@ColorRes int colorRes) {
            if (colorRes == 0 || colorRes == -1) {
                throw new IllegalArgumentException("Invalid value for color");
            }

            mTabBackgroundColor = colorRes;
            return this;
        }

        /**
         * Sets selected tab indicator color.
         */
        public Builder setTabSelectionIndicatorColor(@ColorRes int colorRes) {
            if (colorRes == 0 || colorRes == -1) {
                throw new IllegalArgumentException("Invalid value for tab selection indicator color");
            }

            mTabSelectionIndicatorColor = colorRes;
            return this;
        }

        /**
         * Limit the number of images that can be selected. By default the user can
         * select infinite number of images.
         * @param selectionLimit
         */
        public Builder setSelectionLimit(int selectionLimit) {
            mSelectionLimit = selectionLimit;
            return this;
        }

        /**
         * Configure camera button color.
         * @param buttonColorResId
         */
        public Builder setCameraButtonColor(@ColorRes int buttonColorResId) {
            if (buttonColorResId == 0 || buttonColorResId == -1) {
                throw new IllegalArgumentException("Invalid value for camera button color");
            }

            this.mCameraButtonColor = buttonColorResId;
            return this;
        }

        /**
         * Create the {@link Config} instances.
         */
        public Config build() {
            return new Config(mTabBackgroundColor, mTabSelectionIndicatorColor, mSelectionLimit, mCameraButtonColor);
        }
    }
}
