package nl.changer.polypicker.model;

import android.net.Uri;

/**
 * Created by Gil on 04/03/2014.
 */
public class Image {

    public Uri mUri;
    public int mOrientation;

    public Image(Uri uri, int orientation){
        mUri = uri;
        mOrientation = orientation;
    }

}
