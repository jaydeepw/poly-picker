poly-picker
===========

Android library project for selecting/capturing multiple images from the device.


Why?
==========

* Most of the apps we develop require to fetch images from camera or gallery.
* Android does not provide multi-selection of images out of the box until [API 18](http://developer.android.com/reference/android/content/Intent.html#EXTRA_ALLOW_MULTIPLE).
* There are no libraries that help me multi-choose images from both camera and gallery with beautiful UX.

Features
==========
* Allows taking pictures from camera as well.
* Multi-selection of images from gallery.
* Ability to select/capture images upto a specified limit.
* Preview thumbnails of selected images.
* No dependency.

Result
==========
<img src="https://github.com/jaydeepw/poly-picker/blob/develop/pp-animation.gif" />

Getting started
==========

Add camera permissions and required features to your AndroidManifest.xml

```xml

<uses-feature android:name="android.hardware.camera" />
<uses-feature android:name="android.hardware.camera.autofocus" />

<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

Declare the PolyPicker activity in your AndroidManifest.xml

```xml

<activity
            android:name="nl.changer.polypicker.ImagePickerActivity"
            android:configChanges="mcc|mnc|touchscreen|orientation|uiMode|screenSize|keyboardHidden" />
```

Start the PolyPicker activity and get the result back.

```java

private void getImages() {
	Intent intent = new Intent(mContext, ImagePickerActivity.class);
	intent.putExtra(ImagePickerActivity.EXTRA_SELECTION_LIMIT, 3);	// allow only upto 3 images to be selected.
	startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
}

@Override
protected void onActivityResult(int requestCode, int resuleCode, Intent intent) {
	super.onActivityResult(requestCode, resuleCode, intent);

	if (resuleCode == Activity.RESULT_OK) {
		if (requestCode == INTENT_REQUEST_GET_IMAGES) {
			Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            
            if(parcelableUris == null) {
            	return;
            }

            // show images using uris returned.
		}
	}
}

```

Contributing
=========================

Please fork this repository and contribute back using
[pull requests](https://github.com/jaydeepw/poly-picker/pulls).

Please follow Android code [style guide](https://source.android.com/source/code-style.html)

Developed by
============

 * Jaydeep Wagh - <jaydeep.w@gmail.com>
 * Twitter - [Jaydeep_W](https://twitter.com/Jaydeep_W)

Credits
==========
This project is inspired by and modified from an existing project mentioned below.

[android-multiple-image-picker](https://github.com/giljulio/android-multiple-image-picker)


Alternative projects
==========
* [android-multiple-image-picker](https://github.com/giljulio/android-multiple-image-picker)
* [MultipleImagePick](https://github.com/luminousman/MultipleImagePick)