package nl.changer.polypicker;

/**
 * Created by jay on 3/5/15.
 */
public class Config {

    private int mStripColor;
    private int mSelectionLimit;

    /**
     *
     * @param stripColor
     * @param selectionLimit Pass -1 to not limit the selection of photos.
     */
    public Config(int stripColor, int selectionLimit) {
        mStripColor = stripColor;

        if (selectionLimit != -1) {
            mSelectionLimit = selectionLimit;
        }
    }

    public int getStripColor() {
        return mStripColor;
    }

    public int getSelectionLimit() {
        return mSelectionLimit;
    }

    /**
     * Build a new {@link Config}.
     * <p/>
     */
    public static class Builder {
        private int mStripColor;
        private int mSelectionLimit = Integer.MAX_VALUE;

        /**
         * Tab strip color
         */
        public Builder setStripColor(int colorRes) {
            if (colorRes == 0 || colorRes == -1) {
                throw new IllegalArgumentException("Invalid value for color");
            }

            mStripColor = colorRes;
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
            return new Config(mStripColor, mSelectionLimit);
        }
    }
}
