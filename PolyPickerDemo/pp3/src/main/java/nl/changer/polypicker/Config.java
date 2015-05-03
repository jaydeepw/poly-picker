package nl.changer.polypicker;

/**
 * Created by jay on 3/5/15.
 */
public class Config {

    private int mTabBackgroundColor;
    private int mTabSelectionIndicatorColor;
    private int mSelectionLimit;

    /**
     *
     * @param tabBgColor
     * @param selectionLimit Pass -1 to not limit the selection of photos.
     */
    public Config(int tabBgColor, int tabSelectionIndicatorColor, int selectionLimit) {
        mTabBackgroundColor = tabBgColor;
        mTabSelectionIndicatorColor = tabSelectionIndicatorColor;

        if (selectionLimit != -1) {
            mSelectionLimit = selectionLimit;
        }
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

        /**
         * Tab strip color
         */
        public Builder setTabBackgroundColor(int colorRes) {
            if (colorRes == 0 || colorRes == -1) {
                throw new IllegalArgumentException("Invalid value for color");
            }

            mTabBackgroundColor = colorRes;
            return this;
        }

        /**
         * Sets selected tab indicator color.
         */
        public Builder setTabSelectionIndicatorColor(int colorRes) {
            if (colorRes == 0 || colorRes == -1) {
                throw new IllegalArgumentException("Invalid value for color");
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
         * Create the {@link Config} instances.
         */
        public Config build() {
            return new Config(mTabBackgroundColor, mTabSelectionIndicatorColor, mSelectionLimit);
        }
    }
}
