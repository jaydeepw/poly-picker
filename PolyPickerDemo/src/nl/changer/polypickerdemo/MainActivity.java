package nl.changer.polypickerdemo;

import java.util.HashSet;

import nl.changer.polypicker.ImagePickerActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends FragmentActivity {

	private static final String TAG = MainActivity.class.getSimpleName();
	
	private static final int INTENT_REQUEST_GET_IMAGES = 13;
	
	private Context mContext;
	
	private ViewGroup mSelectedImagesContainer;
	private ViewGroup mSelectedImagesNone;
	HashSet<Uri> mMedia = new HashSet<Uri>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mContext = MainActivity.this;
		
		View getImages = findViewById(R.id.get_images);

		getImages.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getImages();
			}
		});
	}

	private void getImages() {
		Intent intent = new Intent(mContext, ImagePickerActivity.class);
		startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
	}

	@Override
	protected void onActivityResult(int requestCode, int resuleCode, Intent intent) {
		super.onActivityResult(requestCode, resuleCode, intent);

		if (resuleCode == Activity.RESULT_OK) {
			if (requestCode == INTENT_REQUEST_GET_IMAGES) {
				Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.TAG_IMAGE_URI);
                
                if(parcelableUris ==null) {
                	return;
                }

                // Java doesn't allow array casting, this is a little hack
                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);
                
                if(uris != null) {
                	for (Uri uri : uris) {
                		Log.i(TAG, " uri: " + uri);
                		mMedia.add(uri);
					}
                }
			}
		}
	}
}
