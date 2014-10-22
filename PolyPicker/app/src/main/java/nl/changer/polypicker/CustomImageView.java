package changer.nl.polypicker;

import nl.changer.polypicker.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Gil on 05/03/2014.
 */
public class CustomImageView extends ImageView {

    private static final String TAG = CustomImageView.class.getSimpleName();
    private static boolean mMatchHeightToWidth;
    private static boolean mMatchWidthToHeight;

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomView,
                0, 0);

        try {
            mMatchHeightToWidth = a.getBoolean(R.styleable.CustomView_matchHeightToWidth, false);
            mMatchWidthToHeight = a.getBoolean(R.styleable.CustomView_matchWidthToHeight, false);
        } finally {
            a.recycle();
        }
    }



    //Squares the thumbnail
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec){
        if(mMatchHeightToWidth){
            setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
        } else if(mMatchWidthToHeight){
            setMeasuredDimension(heightMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}
